package tn.forsa.app.engine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tn.forsa.app.model.EligibilityResult;
import tn.forsa.app.model.Opportunity;
import tn.forsa.app.model.Student;

/**
 * Role: Rule-based eligibility evaluator for Wijhti / Forsa.
 * Evaluates formal published criteria separately from personal fit (skills, projects, destination)
 * and calculates deadline countdowns using minSdk-24 compatible Calendar math.
 */
public class EligibilityEngine {

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * Evaluates a student's formal eligibility and personal fit against a published opportunity.
     */
    public static EligibilityResult evaluate(Student student, Opportunity opportunity) {
        EligibilityResult result = new EligibilityResult();
        ArrayList<String> reasons = new ArrayList<>();
        ArrayList<String> fitNotes = new ArrayList<>();

        if (opportunity == null) {
            result.setStatus(EligibilityResult.Status.INSUFFICIENT_INFO);
            reasons.add("Opportunité invalide.");
            result.setReasons(reasons);
            return result;
        }

        // Always compute personal fit notes separately from formal eligibility
        buildPersonalFitNotes(student, opportunity, fitNotes);
        result.setPersonalFitNotes(fitNotes);

        // 1. Expired opportunities are never shown as eligible/open
        if (isExpired(opportunity)) {
            result.setStatus(EligibilityResult.Status.NOT_ELIGIBLE);
            reasons.add("Opportunité clôturée (date limite dépassée : " + opportunity.getDeadline() + ").");
            result.setReasons(reasons);
            result.setCompatibilityPercent(0);
            return result;
        }

        if (student == null) {
            result.setStatus(EligibilityResult.Status.INSUFFICIENT_INFO);
            reasons.add("Passeport étudiant manquant. Complète ton profil pour lancer l'analyse.");
            result.setReasons(reasons);
            result.setCompatibilityPercent(0);
            return result;
        }

        // 2. Special consortium case (e.g. Erasmus Mundus): always POTENTIALLY_ELIGIBLE
        if (opportunity.isVariableConsortiumRules()) {
            reasons.add("Les critères exacts varient selon chaque consortium Erasmus Mundus.");
            reasons.add("Vérifie le niveau d'anglais exigé (IELTS/TOEFL), les lettres de recommandation et la lettre de motivation.");
            if (student.isNeedsFunding()) {
                reasons.add("Couverture financière : vérifie si la bourse complète EMJM est ouverte pour ton pays.");
            }
            int fitPercent = computeConsortiumReadinessPercent(student);
            result.setStatus(EligibilityResult.Status.POTENTIALLY_ELIGIBLE);
            result.setReasons(reasons);
            result.setCompatibilityPercent(fitPercent);
            return result;
        }

        // 3. Check missing required student fields (INSUFFICIENT_INFO)
        ArrayList<String> missingFields = new ArrayList<>();
        boolean needsDegreeCheck = !opportunity.getAcceptedDegrees().isEmpty();
        String spec = safeTrim(student.getSpecialization());
        String exactTitle = safeTrim(student.getExactDegreeTitle());

        if (needsDegreeCheck && spec.isEmpty() && exactTitle.isEmpty()) {
            missingFields.add("Spécialité et intitulé exact du diplôme");
        }
        if (opportunity.getMinMsd() > 0 && student.getMsd() < 0) {
            missingFields.add("Moyennes Bac, L1 et L2 (nécessaires pour calculer la MSD)");
        }
        if (opportunity.getMaxAge() > 0 && student.getAgeOnSept2027() <= 0) {
            missingFields.add("Âge au 01/09/2027");
        }

        // Track published criteria met vs total published criteria
        int totalCriteria = 0;
        int metCriteria = 0;
        boolean hasHardRejection = false;
        boolean needsConfirmation = false;

        // Criterion A: Degree / specialization matching
        if (needsDegreeCheck) {
            totalCriteria++;
            int degreeMatch = checkDegreeMatch(student, opportunity.getAcceptedDegrees());
            if (degreeMatch == 2) {
                metCriteria++;
                reasons.add("Diplôme accepté : \"" + exactTitle + "\" correspond à la liste publiée.");
            } else if (degreeMatch == 1) {
                // Loose match (e.g. MDW belongs to Technologies de l'Informatique, exact title unconfirmed)
                needsConfirmation = true;
                reasons.add("Spécialité \"" + spec + "\" proche des diplômes acceptés, mais l'intitulé officiel exact n'est pas encore confirmé — à confirmer avec ton établissement.");
            } else if (degreeMatch == 0 && !(spec.isEmpty() && exactTitle.isEmpty())) {
                hasHardRejection = true;
                reasons.add("Diplôme non listé : ton profil (" + displayDegree(student)
                        + ") ne figure pas dans la liste publiée (" + joinList(opportunity.getAcceptedDegrees()) + ").");
            }
        }

        // Criterion B: Minimum MSD threshold
        if (opportunity.getMinMsd() > 0) {
            totalCriteria++;
            double msd = student.getMsd();
            if (msd >= 0) {
                if (msd >= opportunity.getMinMsd()) {
                    metCriteria++;
                    reasons.add(String.format(Locale.US,
                            "MSD conforme : %.2f / 20 (minimum requis : %.2f / 20).",
                            msd, opportunity.getMinMsd()));
                } else {
                    hasHardRejection = true;
                    reasons.add(String.format(Locale.US,
                            "MSD insuffisante : %.2f / 20 (minimum requis : %.2f / 20).",
                            msd, opportunity.getMinMsd()));
                }
            }
        }

        // Criterion C: Maximum age threshold
        if (opportunity.getMaxAge() > 0) {
            totalCriteria++;
            int age = student.getAgeOnSept2027();
            if (age > 0) {
                if (age <= opportunity.getMaxAge()) {
                    metCriteria++;
                    reasons.add("Âge conforme : " + age + " ans au 01/09/2027 (maximum : " + opportunity.getMaxAge() + " ans).");
                } else {
                    hasHardRejection = true;
                    reasons.add("Âge dépassé : " + age + " ans au 01/09/2027 (maximum autorisé : " + opportunity.getMaxAge() + " ans).");
                }
            }
        }

        // Criterion D: No repeats / no interruption rule
        if (opportunity.isRequiresNoRepeat()) {
            totalCriteria++;
            if (student.getYearsRepeated() == 0 && !student.isHadInterruption()) {
                metCriteria++;
                reasons.add("Parcours sans redoublement ni interruption respecté (0 année redoublée).");
            } else {
                hasHardRejection = true;
                reasons.add("Critère sans redoublement/interruption non respecté : "
                        + student.getYearsRepeated() + " redoublement(s), interruption = "
                        + (student.isHadInterruption() ? "oui" : "non")
                        + " (requis : 0 redoublement, aucune interruption).");
            }
        }

        // Criterion E: Institution nomination requirement
        if (opportunity.isRequiresInstitutionNomination()) {
            totalCriteria++;
            needsConfirmation = true;
            reasons.add("Nomination officielle par ton établissement requise — à confirmer avec ton établissement.");
        }

        // General open criteria (e.g. CIVP, Junior Dev, Freelance with no numeric barriers)
        if (totalCriteria == 0) {
            totalCriteria = 1;
            metCriteria = 1;
            reasons.add("Critères formels ouverts aux diplômés de l'enseignement supérieur (BAC+3).");
        }

        int compatibilityPercent = (int) Math.round((metCriteria * 100.0) / totalCriteria);
        result.setCompatibilityPercent(compatibilityPercent);

        // Final status priority:
        // 1) Missing required data -> INSUFFICIENT_INFO (unless already hard-rejected? Prompt checks missing data first)
        if (!missingFields.isEmpty()) {
            reasons.add(0, "Informations manquantes dans ton Passeport : " + joinList(missingFields) + ".");
            result.setStatus(EligibilityResult.Status.INSUFFICIENT_INFO);
        } else if (hasHardRejection) {
            result.setStatus(EligibilityResult.Status.NOT_ELIGIBLE);
        } else if (needsConfirmation) {
            result.setStatus(EligibilityResult.Status.POTENTIALLY_ELIGIBLE);
        } else {
            result.setStatus(EligibilityResult.Status.ELIGIBLE);
        }

        result.setReasons(reasons);
        return result;
    }

    /**
     * Checks degree match:
     * 2 = exact match against acceptedDegrees
     * 1 = loose family match (e.g. MDW / IT specialization when exact title is unconfirmed)
     * 0 = no match
     */
    private static int checkDegreeMatch(Student student, ArrayList<String> acceptedDegrees) {
        String exactTitle = safeTrim(student.getExactDegreeTitle()).toLowerCase();
        String spec = safeTrim(student.getSpecialization()).toLowerCase();

        // First check exact degree title if provided
        if (!exactTitle.isEmpty()) {
            for (int i = 0; i < acceptedDegrees.size(); i++) {
                String accepted = acceptedDegrees.get(i).toLowerCase();
                if (exactTitle.contains(accepted) || accepted.contains(exactTitle)) {
                    return 2;
                }
            }
        }

        // Check loose match via specialization family (e.g. MDW / DSI / RSI -> Technologies de l'Informatique)
        if (!spec.isEmpty()) {
            boolean isItFamilySpec = spec.contains("mdw") || spec.contains("dsi")
                    || spec.contains("rsi") || spec.contains("sem")
                    || spec.contains("info") || spec.contains("dev");
            for (int i = 0; i < acceptedDegrees.size(); i++) {
                String accepted = acceptedDegrees.get(i).toLowerCase();
                if (spec.equals(accepted)) {
                    return 2;
                }
                if (isItFamilySpec && (accepted.contains("informatique") || accepted.contains("multimédia"))) {
                    return 1;
                }
            }
        }

        return 0;
    }

    /**
     * Keeps personal fit (skills, projects, PFE, destination) separate from formal eligibility.
     */
    private static void buildPersonalFitNotes(Student student, Opportunity opportunity, ArrayList<String> fitNotes) {
        if (student == null) {
            return;
        }

        String cat = opportunity.getCategory();
        if (Opportunity.CAT_JOB.equals(cat) || Opportunity.CAT_FREELANCE.equals(cat)) {
            int skillCount = student.getSkills() != null ? student.getSkills().size() : 0;
            fitNotes.add("Compétences techniques déclarées : " + skillCount
                    + (skillCount > 0 ? " (" + joinList(student.getSkills()) + ")" : "") + ".");

            if (student.isHasDeployedProject()) {
                fitNotes.add("Atout fort : tu as déjà au moins un projet déployé.");
            } else {
                fitNotes.add("À renforcer : déploie un projet concret (GitHub + démo en ligne).");
            }

            if (Opportunity.CAT_JOB.equals(cat)) {
                if (student.isHasPfe()) {
                    fitNotes.add("Expérience PFE validée / en cours : bon signal pour les recruteurs junior.");
                } else {
                    fitNotes.add("PFE non encore validé : mets ton PFE en valeur dès son démarrage.");
                }
            }
        } else if (Opportunity.CAT_SCHOLARSHIP_ABROAD.equals(cat)) {
            if ("Europe".equalsIgnoreCase(student.getPreferredDestination())) {
                fitNotes.add("Cohérent avec ta préférence de destination : Europe.");
            } else {
                fitNotes.add("Note personnelle : ta destination préférée actuelle est " + safeDisplay(student.getPreferredDestination()) + ".");
            }
            if (student.isNeedsFunding()) {
                fitNotes.add("Besoin de financement indiqué : cette piste inclut une bourse / aide financière.");
            }
        } else {
            if ("Tunisie".equalsIgnoreCase(student.getPreferredDestination())) {
                fitNotes.add("Cohérent avec ta préférence de destination : Tunisie.");
            }
            if (student.isHasDeployedProject()) {
                fitNotes.add("Projet déployé : utile pour le dossier de sélection et l'entretien du mastère.");
            }
        }
    }

    private static int computeConsortiumReadinessPercent(Student student) {
        int checks = 3;
        int met = 0;
        if (student.getMsd() >= 11.0 || student.getL1Average() >= 11.0) {
            met++;
        }
        if (student.getStudyLanguage() != null && student.getStudyLanguage().toLowerCase().contains("anglais")) {
            met++;
        }
        if (student.isHasDeployedProject() || student.isHasPfe()) {
            met++;
        }
        return (int) Math.round((met * 100.0) / checks);
    }

    /**
     * Computes days remaining from today until deadline (yyyy-MM-dd).
     * Returns Long.MAX_VALUE if no deadline is set.
     */
    public static long getDaysLeft(String deadline) {
        if (deadline == null || deadline.trim().isEmpty()) {
            return Long.MAX_VALUE;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        sdf.setLenient(false);
        try {
            Date targetDate = sdf.parse(deadline.trim());
            if (targetDate == null) {
                return Long.MAX_VALUE;
            }
            Calendar todayCal = Calendar.getInstance();
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);

            Calendar targetCal = Calendar.getInstance();
            targetCal.setTime(targetDate);
            targetCal.set(Calendar.HOUR_OF_DAY, 0);
            targetCal.set(Calendar.MINUTE, 0);
            targetCal.set(Calendar.SECOND, 0);
            targetCal.set(Calendar.MILLISECOND, 0);

            long diffMs = targetCal.getTimeInMillis() - todayCal.getTimeInMillis();
            return diffMs / (24L * 60L * 60L * 1000L);
        } catch (ParseException e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * Returns true if the deadline is strictly before today.
     */
    public static boolean isExpired(Opportunity opportunity) {
        if (opportunity == null) {
            return false;
        }
        long daysLeft = getDaysLeft(opportunity.getDeadline());
        return daysLeft != Long.MAX_VALUE && daysLeft < 0;
    }

    /**
     * Returns true if the opportunity is still open and deadline is <= 14 days away.
     */
    public static boolean isUrgent(Opportunity opportunity) {
        if (opportunity == null) {
            return false;
        }
        long daysLeft = getDaysLeft(opportunity.getDeadline());
        return daysLeft >= 0 && daysLeft <= 14;
    }

    private static String displayDegree(Student student) {
        String exact = safeTrim(student.getExactDegreeTitle());
        String spec = safeTrim(student.getSpecialization());
        if (!exact.isEmpty()) {
            return exact;
        }
        return spec.isEmpty() ? "non renseigné" : spec;
    }

    private static String safeTrim(String text) {
        return text == null ? "" : text.trim();
    }

    private static String safeDisplay(String text) {
        return (text == null || text.trim().isEmpty()) ? "non précisée" : text.trim();
    }

    private static String joinList(ArrayList<String> items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(items.get(i));
        }
        return sb.toString();
    }
}

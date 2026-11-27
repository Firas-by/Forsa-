package tn.forsa.app.engine;

import java.util.ArrayList;

import tn.forsa.app.model.ActionItem;
import tn.forsa.app.model.DocumentItem;
import tn.forsa.app.model.EligibilityResult;
import tn.forsa.app.model.Opportunity;
import tn.forsa.app.model.Student;

/**
 * Role: Generates 3 to 5 prioritized action steps ("Tes 90 prochains jours")
 * from simple, transparent if/else conditions on the student's Passport and opportunities.
 */
public class ActionPlanBuilder {

    public static ArrayList<ActionItem> buildPlan(Student student, ArrayList<Opportunity> opportunities) {
        ArrayList<ActionItem> actions = new ArrayList<>();

        if (student == null) {
            actions.add(new ActionItem(
                    1,
                    "Créer ton Passeport Étudiant",
                    "Renseigne tes moyennes (Bac, L1, L2), ton âge et ton diplôme pour activer l'analyse.",
                    "Jours 1–7",
                    ActionItem.TARGET_PASSPORT,
                    -1
            ));
            return actions;
        }

        // 1. Missing academic info in Passport
        boolean missingGradesOrAge = student.getMsd() < 0 || student.getAgeOnSept2027() <= 0;
        boolean missingExactTitle = student.getExactDegreeTitle() == null
                || student.getExactDegreeTitle().trim().isEmpty();

        if (missingGradesOrAge || missingExactTitle) {
            String detail = missingGradesOrAge
                    ? "Complète tes moyennes Bac/L1/L2 et ton âge au 01/09/2027 pour calculer la MSD officielle."
                    : "Ajoute l'intitulé exact de ton diplôme (ex : Technologies de l'Informatique) pour confirmer les mastères.";
            actions.add(new ActionItem(
                    1,
                    "Compléter ton Passeport Étudiant",
                    detail,
                    "Jours 1–7",
                    ActionItem.TARGET_PASSPORT,
                    -1
            ));
        }

        // 2. Urgent deadlines or institution nomination required
        for (int i = 0; i < opportunities.size(); i++) {
            Opportunity opp = opportunities.get(i);
            if (EligibilityEngine.isExpired(opp)) {
                continue;
            }
            long daysLeft = EligibilityEngine.getDaysLeft(opp.getDeadline());
            EligibilityResult result = EligibilityEngine.evaluate(student, opp);

            if (result.getStatus() == EligibilityResult.Status.NOT_ELIGIBLE) {
                continue;
            }

            if (opp.isRequiresInstitutionNomination()) {
                actions.add(new ActionItem(
                        1,
                        "Contacter ton établissement (nomination)",
                        opp.getTitle() + " exige une fiche de nomination signée (clôture le "
                                + opp.getDeadline() + "). Prends rendez-vous rapidement.",
                        "Jours 1–14",
                        ActionItem.TARGET_OPPORTUNITY,
                        opp.getId()
                ));
                break;
            } else if (daysLeft >= 0 && daysLeft <= 14) {
                actions.add(new ActionItem(
                        1,
                        "Échéance urgente : " + opp.getTitle(),
                        "Il reste seulement " + daysLeft + " jour(s) avant la date limite (" + opp.getDeadline() + ").",
                        "Urgent (J-" + daysLeft + ")",
                        ActionItem.TARGET_OPPORTUNITY,
                        opp.getId()
                ));
                break;
            }
        }

        // 3. Missing documents across open eligible / potentially eligible opportunities
        int totalMissingDocs = 0;
        int firstOppWithMissingDocs = -1;
        for (int i = 0; i < opportunities.size(); i++) {
            Opportunity opp = opportunities.get(i);
            if (EligibilityEngine.isExpired(opp)) {
                continue;
            }
            EligibilityResult res = EligibilityEngine.evaluate(student, opp);
            if (res.getStatus() == EligibilityResult.Status.ELIGIBLE
                    || res.getStatus() == EligibilityResult.Status.POTENTIALLY_ELIGIBLE) {
                ArrayList<DocumentItem> docs = opp.getDocumentItems();
                for (int j = 0; j < docs.size(); j++) {
                    if (DocumentItem.STATUS_MISSING.equals(docs.get(j).getStatus())) {
                        totalMissingDocs++;
                        if (firstOppWithMissingDocs == -1) {
                            firstOppWithMissingDocs = opp.getId();
                        }
                    }
                }
            }
        }
        if (totalMissingDocs > 0) {
            actions.add(new ActionItem(
                    2,
                    "Préparer tes pièces manquantes (" + totalMissingDocs + ")",
                    "Vérifie les checklists (relevés certifiés, CIN, lettres de recommandation) pour éviter les blocages de dernière minute.",
                    "Jours 7–30",
                    firstOppWithMissingDocs != -1 ? ActionItem.TARGET_OPPORTUNITY : ActionItem.TARGET_OPPORTUNITIES_LIST,
                    firstOppWithMissingDocs
            ));
        }

        // 4. Check if any Tunisian Master is currently eligible or potentially eligible
        boolean hasOpenMasterPath = false;
        for (int i = 0; i < opportunities.size(); i++) {
            Opportunity opp = opportunities.get(i);
            if (Opportunity.CAT_MASTER_TN.equals(opp.getCategory()) && !EligibilityEngine.isExpired(opp)) {
                EligibilityResult res = EligibilityEngine.evaluate(student, opp);
                if (res.getStatus() == EligibilityResult.Status.ELIGIBLE
                        || res.getStatus() == EligibilityResult.Status.POTENTIALLY_ELIGIBLE) {
                    hasOpenMasterPath = true;
                    break;
                }
            }
        }
        if (!hasOpenMasterPath) {
            actions.add(new ActionItem(
                    2,
                    "Activer le plan de secours Emploi / CIVP",
                    "Aucun mastère tunisien n'est confirmé éligible pour ton profil actuel : prépare ton inscription ANETI (CIVP) et tes candidatures junior.",
                    "Jours 15–45",
                    ActionItem.TARGET_OPPORTUNITIES_LIST,
                    -1
            ));
        }

        // 5. Portfolio / deployed project / PFE check
        if (!student.isHasDeployedProject()) {
            actions.add(new ActionItem(
                    2,
                    "Déployer 1 projet portfolio en ligne",
                    "Publie un projet fonctionnel (GitHub + hébergement live) pour renforcer tes candidatures emploi, freelance et mastères.",
                    "Jours 15–60",
                    ActionItem.TARGET_PASSPORT,
                    -1
            ));
        } else if (!student.isHasPfe()) {
            actions.add(new ActionItem(
                    3,
                    "Valoriser ton PFE & tes projets techniques",
                    "Documente tes projets (" + student.getSkills().size() + " compétences) dans un CV clair prêt pour les entretiens PFE / junior.",
                    "Jours 30–90",
                    ActionItem.TARGET_OPPORTUNITIES_LIST,
                    -1
            ));
        }

        // Ensure at least 3 actions exist
        if (actions.size() < 3) {
            actions.add(new ActionItem(
                    3,
                    "Échanger avec un conseiller d'orientation",
                    "Valide tes choix de mastères, bourses ou stages avec un conseiller du Bureau d'Orientation.",
                    "Jours 30–60",
                    ActionItem.TARGET_ADVISOR,
                    -1
            ));
        }
        if (actions.size() < 3) {
            actions.add(new ActionItem(
                    3,
                    "Vérifier les sources officielles (2027-2028)",
                    "Reconsulte best.rnu.tn et les portails ISET pour confirmer les dates définitives d'ouverture.",
                    "Jours 60–90",
                    ActionItem.TARGET_OPPORTUNITIES_LIST,
                    -1
            ));
        }

        // Cap at 5 prioritized actions
        while (actions.size() > 5) {
            actions.remove(actions.size() - 1);
        }

        return actions;
    }
}

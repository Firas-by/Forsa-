package tn.forsa.app.data;

import java.util.ArrayList;
import java.util.Arrays;

import tn.forsa.app.engine.EligibilityEngine;
import tn.forsa.app.model.Employee;
import tn.forsa.app.model.Opportunity;
import tn.forsa.app.model.Student;

/**
 * Role: Static in-memory data store holding all lists while the app is running.
 * PDF Requirement: Data stored ONLY in static ArrayLists (no SQLite, Room, or Firebase).
 */
public class DataStore {

    // Official list of guidance office departments
    public static final String[] DEPARTMENTS = {
            "Orientation Master",
            "Bourses & Mobilité",
            "Emploi & Stage",
            "Entrepreneuriat"
    };

    // Main list of guidance-office advisors ("employees")
    public static final ArrayList<Employee> employees = new ArrayList<>();

    // Seeded list of real/published opportunities
    public static final ArrayList<Opportunity> opportunities = new ArrayList<>();

    // Current student's Passport profile
    public static Student currentStudent = new Student();

    private static boolean initialized = false;
    private static int nextEmployeeId = 1;

    /**
     * Populates the initial sample data once per app launch.
     */
    public static void initIfNeeded() {
        if (initialized) {
            return;
        }
        initialized = true;

        seedDefaultStudent();
        seedOpportunities();

        // 4 required advisors from the specification
        addInitialEmployee("Ben Ali", "Ahmed", "ahmed.benali@univ.tn",
                "98 765 432", "Conseiller d'orientation", "Orientation Master", "15/09/2021");

        addInitialEmployee("Trabelsi", "Sarra", "sarra.trabelsi@univ.tn",
                "92 345 678", "Conseillère bourses & mobilité", "Bourses & Mobilité", "10/10/2020");

        addInitialEmployee("Gharbi", "Youssef", "youssef.gharbi@univ.tn",
                "93 111 222", "Coordinateur stages & emploi", "Emploi & Stage", "03/02/2022");

        addInitialEmployee("Khalfalah", "Meriem", "meriem.khalfalah@univ.tn",
                "95 555 444", "Conseillère entrepreneuriat", "Entrepreneuriat", "12/04/2023");

        // 2 additional realistic university advisors
        addInitialEmployee("Bouazizi", "Karim", "karim.bouazizi@univ.tn",
                "97 222 333", "Référent mastères & équivalences", "Orientation Master", "01/09/2019");

        addInitialEmployee("Mejri", "Ines", "ines.mejri@univ.tn",
                "94 888 777", "Chargée coopération internationale", "Bourses & Mobilité", "20/01/2023");
    }

    /**
     * Seeds the default student profile (ISET MDW student with technical skills).
     * Numeric grades and age remain unset (-1) until the student completes their Passport.
     */
    private static void seedDefaultStudent() {
        currentStudent = new Student();
        currentStudent.setName("Firas");
        currentStudent.setInstitution("ISET [Campus à compléter]");
        currentStudent.setSpecialization("MDW");
        currentStudent.setExactDegreeTitle("");
        currentStudent.setBacAverage(Student.UNSET_GRADE);
        currentStudent.setL1Average(Student.UNSET_GRADE);
        currentStudent.setL2Average(Student.UNSET_GRADE);
        currentStudent.setL3Average(Student.UNSET_GRADE);
        currentStudent.setAgeOnSept2027(Student.UNSET_AGE);
        currentStudent.setYearsRepeated(0);
        currentStudent.setHadInterruption(false);
        currentStudent.setHasDeployedProject(true);
        currentStudent.setHasPfe(false);
        currentStudent.setPreferredDestination("Tunisie");
        currentStudent.setNeedsFunding(true);
        currentStudent.setStudyLanguage("Français");

        ArrayList<String> skills = new ArrayList<>();
        skills.add("Angular");
        skills.add("FastAPI");
        skills.add("Flutter");
        skills.add("Unity/AR");
        skills.add("AI prototypes");
        currentStudent.setSkills(skills);
    }

    private static void addInitialEmployee(String nom, String prenom, String email,
                                           String telephone, String poste, String departement,
                                           String dateRecrutement) {
        employees.add(new Employee(
                nextEmployeeId++,
                nom,
                prenom,
                email,
                telephone,
                poste,
                departement,
                dateRecrutement
        ));
    }

    /**
     * Generates a unique ID for a newly added employee.
     */
    public static int getNextEmployeeId() {
        return nextEmployeeId++;
    }

    /**
     * Finds an employee by ID using a plain loop, or returns null if not found.
     */
    public static Employee findEmployeeById(int id) {
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            if (emp.getId() == id) {
                return emp;
            }
        }
        return null;
    }

    /**
     * Replaces an existing employee in the list with updated fields.
     */
    public static boolean updateEmployee(Employee updated) {
        if (updated == null) {
            return false;
        }
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == updated.getId()) {
                employees.set(i, updated);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes an employee by ID from the static ArrayList.
     */
    public static boolean deleteEmployeeById(int id) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == id) {
                employees.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the first advisor belonging to a given department (used in Phase 5).
     */
    public static Employee findAdvisorByDepartment(String department) {
        if (department != null) {
            for (int i = 0; i < employees.size(); i++) {
                Employee emp = employees.get(i);
                if (department.equals(emp.getDepartement())) {
                    return emp;
                }
            }
        }
        if (!employees.isEmpty()) {
            return employees.get(0);
        }
        return null;
    }

    /**
     * Returns the number of currently open (non-expired) opportunities.
     */
    public static int getOpenOpportunitiesCount() {
        int count = 0;
        for (int i = 0; i < opportunities.size(); i++) {
            if (!EligibilityEngine.isExpired(opportunities.get(i))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Finds an opportunity by ID using a plain loop, or returns null if not found.
     */
    public static Opportunity findOpportunityById(int id) {
        for (int i = 0; i < opportunities.size(); i++) {
            Opportunity opp = opportunities.get(i);
            if (opp.getId() == id) {
                return opp;
            }
        }
        return null;
    }

    /**
     * Seeds the 8 official / realistic post-Licence opportunities for Tunisia.
     * Uses lastVerified = 2026-11-27 and marks uncertain details with "TODO verify".
     */
    private static void seedOpportunities() {
        opportunities.clear();

        // a) Bourse du gouvernement tunisien 2027-2028 (Circulaire 50/26)
        opportunities.add(new Opportunity(
                1,
                "Bourse du gouvernement tunisien (Circulaire 50/26)",
                "Ministère de l'Enseignement Supérieur (BEST)",
                Opportunity.CAT_SCHOLARSHIP_ABROAD,
                new ArrayList<String>(),
                12.5,
                24,
                true,
                true,
                false,
                "2026-12-18",
                new ArrayList<>(Arrays.asList(
                        "Formulaire d'inscription en ligne (best.rnu.tn)",
                        "Copie du relevé de notes du Baccalauréat",
                        "Relevés de notes L1 et L2",
                        "Attestation d'inscription en 3ème année Licence (2026-2027)",
                        "Copie de la CIN",
                        "Fiche de nomination signée par l'établissement (dépôt physique le 21/12/2026)"
                )),
                "Bourse nationale complète selon circulaire 50/26 (montant selon pays d'accueil).",
                "https://www.best.rnu.tn",
                "2026-11-27",
                "2027-2028",
                "Inscription en ligne jusqu'au 2026-12-18 et dépôt du dossier physique jusqu'au 2026-12-21. Réservée aux étudiants inscrits en 3ème année Licence en 2026-2027. TODO verify: confirmer pièces exactes auprès de la scolarité."
        ));

        // b) ISET Sfax - Mastère pro Administration des Systèmes, Sécurité et Cloud Computing
        opportunities.add(new Opportunity(
                2,
                "Mastère pro Administration des Systèmes, Sécurité et Cloud Computing",
                "ISET Sfax",
                Opportunity.CAT_MASTER_TN,
                new ArrayList<>(Arrays.asList(
                        "Informatique et Multimédia",
                        "Technologies de l'Informatique"
                )),
                0.0,
                0,
                false,
                false,
                false,
                "2027-07-15",
                new ArrayList<>(Arrays.asList(
                        "Fiche de candidature en ligne imprimée",
                        "Copies certifiées des relevés de notes (Bac, L1, L2, L3)",
                        "Copie de la CIN",
                        "Attestation de réussite / diplôme (ou seulement après présélection selon session)"
                )),
                "Enseignement public tunisien (frais d'inscription universitaires standards).",
                "https://isetsf.rnu.tn",
                "2026-11-27",
                "2027-2028",
                "Mastère professionnel en systèmes, sécurité et cloud. TODO verify: dates exactes d'ouverture 2027-2028 et liste complète des licences équivalentes sur isetsf.rnu.tn."
        ));

        // c) ISET Sfax - Mastère pro Développement des Systèmes Informatiques et Réseaux
        opportunities.add(new Opportunity(
                3,
                "Mastère pro Développement des Systèmes Informatiques et Réseaux",
                "ISET Sfax",
                Opportunity.CAT_MASTER_TN,
                new ArrayList<>(Arrays.asList(
                        "Informatique et Multimédia",
                        "Technologies de l'Informatique"
                )),
                0.0,
                0,
                false,
                false,
                false,
                "2027-07-15",
                new ArrayList<>(Arrays.asList(
                        "Formulaire de candidature (max 3 choix de mastères)",
                        "Relevés de notes L1, L2 et L3",
                        "Copie du diplôme du Baccalauréat",
                        "Copie de la CIN",
                        "Dossier complet à déposer seulement après présélection"
                )),
                "Enseignement public tunisien (frais d'inscription universitaires standards).",
                "https://isetsf.rnu.tn",
                "2026-11-27",
                "2027-2028",
                "Règle ISET : maximum 3 choix de mastères autorisés. TODO verify: dates limites exactes de la campagne 2027-2028."
        ));

        // d) ISET Sousse - Mastère pro Cloud Computing et Développement des Applications Distribuées (CLOSED)
        opportunities.add(new Opportunity(
                4,
                "Mastère pro Cloud Computing et Développement des Applications Distribuées",
                "ISET Sousse",
                Opportunity.CAT_MASTER_TN,
                new ArrayList<>(Arrays.asList(
                        "Informatique et Multimédia",
                        "Technologies de l'Informatique"
                )),
                0.0,
                0,
                false,
                false,
                false,
                "2026-08-09",
                new ArrayList<>(Arrays.asList(
                        "Formulaire de candidature en ligne",
                        "Relevés de notes L1, L2, L3",
                        "Copie de la CIN"
                )),
                "Enseignement public tunisien (capacité indicative : 30 places).",
                "https://isetso.rnu.tn",
                "2026-11-27",
                "2026-2027",
                "Capacité publiée : 30 places. Clôturé le 2026-08-09 (affiché comme clôturé). TODO verify: ouverture de la prochaine session 2027-2028."
        ));

        // e) Erasmus Mundus (generic entry)
        opportunities.add(new Opportunity(
                5,
                "Bourses Erasmus Mundus Joint Masters (EMJM)",
                "Commission Européenne — Consortiums EMJM",
                Opportunity.CAT_SCHOLARSHIP_ABROAD,
                new ArrayList<String>(),
                0.0,
                0,
                false,
                false,
                true,
                "2027-01-15",
                new ArrayList<>(Arrays.asList(
                        "Certificat d'anglais (IELTS / TOEFL / selon consortium)",
                        "2 lettres de recommandation académiques ou professionnelles",
                        "Lettre de motivation personnalisée",
                        "Question de couverture financière (bourse complète vs auto-financement)",
                        "Relevés de notes traduits + passeport"
                )),
                "Bourse EMJM pouvant couvrir frais de scolarité + allocation mensuelle + voyage (selon attribution).",
                "https://www.eacea.ec.europa.eu/scholarships/erasmus-mundus-catalogue_en",
                "2026-11-27",
                "2027-2028",
                "Les règles, seuils et dates varient selon chaque consortium. Vérifier chaque programme individuellement. TODO verify: date limite exacte par master ciblé."
        ));

        // f) CIVP - Contrat d'Initiation à la Vie Professionnelle
        opportunities.add(new Opportunity(
                6,
                "CIVP — Contrat d'Initiation à la Vie Professionnelle",
                "ANETI (Agence Nationale pour l'Emploi)",
                Opportunity.CAT_JOB,
                new ArrayList<String>(),
                0.0,
                0,
                false,
                false,
                false,
                "",
                new ArrayList<>(Arrays.asList(
                        "Attestation de réussite ou diplôme de Licence (BAC+3)",
                        "Inscription comme primo-demandeur d'emploi à l'ANETI",
                        "Copie de la CIN",
                        "RIB bancaire ou postal",
                        "Promesse / contrat CIVP signé avec l'entreprise d'accueil"
                )),
                "Indemnité mensuelle ANETI + complément employeur (durée 12 mois). ATTENTION : le CIVP n'est PAS un contrat permanent (CDI).",
                "https://www.emploi.nat.tn",
                "2026-11-27",
                "2026-2027",
                "Destiné aux primo-demandeurs d'emploi diplômés de l'enseignement supérieur. Important : ce n'est PAS un CDI. TODO verify: montant actualisé de l'indemnité ANETI."
        ));

        // g) Junior Full-Stack Developer
        opportunities.add(new Opportunity(
                7,
                "Développeur Full-Stack Junior (Web / Mobile / IA)",
                "Entreprises Tech & ESN en Tunisie",
                Opportunity.CAT_JOB,
                new ArrayList<String>(),
                0.0,
                0,
                false,
                false,
                false,
                "",
                new ArrayList<>(Arrays.asList(
                        "CV technique à jour (projets Angular / FastAPI / Flutter)",
                        "Portfolio GitHub avec au moins 1 projet déployé",
                        "Rapport ou démo du PFE",
                        "Lettre / profil LinkedIn complet"
                )),
                "Salaire junior selon entreprise (souvent combiné au dispositif CIVP en début de carrière).",
                "https://www.emploi.nat.tn",
                "2026-11-27",
                "2026-2027",
                "Adéquation évaluée séparément via tes compétences, projets déployés et PFE. TODO verify: offres ouvertes ciblées sur LinkedIn / Tanitjobs."
        ));

        // h) Freelance / Remote Developer path
        opportunities.add(new Opportunity(
                8,
                "Parcours Freelance / Développeur Remote Indépendant",
                "Statut Auto-Entrepreneur / Plateformes Remote",
                Opportunity.CAT_FREELANCE,
                new ArrayList<String>(),
                0.0,
                0,
                false,
                false,
                false,
                "",
                new ArrayList<>(Arrays.asList(
                        "Portfolio en ligne avec études de cas & démos déployées",
                        "2 à 3 projets complets (Angular / FastAPI / Flutter / Unity)",
                        "Profil professionnel (GitHub, LinkedIn, Malt / Upwork)",
                        "Renseignements sur le statut Auto-Entrepreneur (Tunisie)"
                )),
                "Revenus variables par mission ; démarches légales et facturation à encadrer (Auto-Entrepreneur / SUARL).",
                "https://www.autoentrepreneur.tn",
                "2026-11-27",
                "2026-2027",
                "Voie indépendante recommandée en parallèle ou en plan de secours si tu disposes de projets déployés. TODO verify: conditions fiscales du statut Auto-Entrepreneur."
        ));
    }
}

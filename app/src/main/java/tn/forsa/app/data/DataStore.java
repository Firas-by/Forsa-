package tn.forsa.app.data;

import java.util.ArrayList;

import tn.forsa.app.model.Employee;

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
}

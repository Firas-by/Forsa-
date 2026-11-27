package tn.forsa.app.model;

import java.io.Serializable;

/**
 * Role: Represents a guidance-office advisor ("Employee") in the Forsa application.
 * PDF Requirement: Employee.java model with private fields (id, nom, prenom, email,
 * telephone, poste, departement, dateRecrutement), constructor, getters, setters,
 * and Serializable implementation so objects can be passed through Intents.
 */
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String poste;
    private String departement;
    private String dateRecrutement;

    public Employee() {
    }

    public Employee(int id, String nom, String prenom, String email,
                    String telephone, String poste, String departement, String dateRecrutement) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.poste = poste;
        this.departement = departement;
        this.dateRecrutement = dateRecrutement;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getDateRecrutement() {
        return dateRecrutement;
    }

    public void setDateRecrutement(String dateRecrutement) {
        this.dateRecrutement = dateRecrutement;
    }

    /**
     * Helper method returning "Prenom Nom" for display in lists and details.
     */
    public String getNomComplet() {
        String first = prenom != null ? prenom.trim() : "";
        String last = nom != null ? nom.trim() : "";
        if (first.isEmpty()) {
            return last;
        }
        if (last.isEmpty()) {
            return first;
        }
        return first + " " + last;
    }

    /**
     * Helper method returning up to 2 uppercase initials for the circular avatar.
     */
    public String getInitiales() {
        StringBuilder sb = new StringBuilder();
        if (prenom != null && !prenom.trim().isEmpty()) {
            sb.append(Character.toUpperCase(prenom.trim().charAt(0)));
        }
        if (nom != null && !nom.trim().isEmpty()) {
            sb.append(Character.toUpperCase(nom.trim().charAt(0)));
        }
        if (sb.length() == 0) {
            return "?";
        }
        return sb.toString();
    }
}

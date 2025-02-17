package org.example;

public class Persons {
    private String pavadinimas;
    private String vardas;
    private String pavarde;

    public Persons(String pavadinimas, String vardas, String pavarde) {
        this.pavadinimas = pavadinimas;
        this.vardas = vardas;
        this.pavarde = pavarde;
    }

    public String getPavadinimas() {
        return pavadinimas;
    }

    public void setPavadinimas(String pavadinimas) {
        this.pavadinimas = pavadinimas;
    }

    public String getVardas() {
        return vardas;
    }

    public void setVardas(String vardas) {
        this.vardas = vardas;
    }

    public String getPavarde() {
        return pavarde;
    }

    public void setPavarde(String pavarde) {
        this.pavarde = pavarde;
    }

    @Override
    public String toString() {
        return "Persons{" +
                "pavadinimas='" + pavadinimas + '\'' +
                ", vardas='" + vardas + '\'' +
                ", pavarde='" + pavarde + '\'' +
                '}';
    }
}

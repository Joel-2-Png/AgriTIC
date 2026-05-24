package modele;

public class Parcelle {

    private String id;
    private String culture;
    private double humidite;
    private double temperature;
    private String etat;

    // Constructeur
    public Parcelle(String id, String culture, double humidite, double temperature) {

        this.id = id;
        this.culture = culture;
        this.humidite = humidite;
        this.temperature = temperature;

        // Détermination de l'état
        if (humidite < 20) {
            this.etat = "Très sèche";
        } else if (humidite < 30) {
            this.etat = "Sèche";
        } else {
            this.etat = "Normale";
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCulture() {
        return culture;
    }

    public double getHumidite() {
        return humidite;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getEtat() {
        return etat;
    }

    // toString
    @Override
    public String toString() {
        return "Parcelle{" +
                "id='" + id + '\'' +
                ", culture='" + culture + '\'' +
                ", humidite=" + humidite +
                ", temperature=" + temperature +
                ", etat='" + etat + '\'' +
                '}';
    }
}
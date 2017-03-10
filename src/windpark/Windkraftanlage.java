package windpark;

import java.util.Hashtable;

public class Windkraftanlage {
    private String name;
    private Integer aktStrom;
    private Integer blindstrom;
    private Integer windgesch;
    private Integer drehzahl;
    private Integer temp;
    private Integer blattpos;

    //Constructor
    public Windkraftanlage() {
        this.aktStrom = this.generate_random_number(10000000, 0); //megawatt
        this.blindstrom = 0; //Keinen Blindstrom, da sie einen Synchrongenerator benutzen
        this.windgesch = this.generate_random_number(50, 0); //m/s
        this.drehzahl = this.generate_random_number(100, 0); //rpm
        this.temp = this.generate_random_number(70, -20); //Grad Celsius
        this.blattpos= this.generate_random_number(360, 0); //Grad
    }

    public Windkraftanlage(String name, int aktStrom, int blindstrom, int windgesch, int drehzahl, int temp, int blattpos) {
        this.name = name;
        this.aktStrom = aktStrom;
        this.blindstrom = blindstrom;
        this.windgesch = windgesch;
        this.drehzahl = drehzahl;
        this.temp = temp;
        this.blattpos = blattpos;
    }

    //Get-Methode, die von dem Client (Parkrechner) aufgerufen wird
    public Hashtable<String, Integer> getData() {
        Hashtable<String, Integer> wka = new Hashtable<String, Integer>();
        wka.put("aktStrom", this.aktStrom);
        wka.put("blindstrom", this.blindstrom);
        wka.put("windgesch", this.windgesch);
        wka.put("drehzahl", this.drehzahl);
        wka.put("temp", this.temp);
        wka.put("blattpos", this.blattpos);
        return wka;
    }

    //Generates a random number
    private Integer generate_random_number(int max, int min) {
        return (min + (int) (Math.random() * ((max - min) + 1)));
    }
}

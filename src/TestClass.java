import windpark.Parkrechner;
import windpark.ServerApplication;
import zentralrechner.Zentralrechner;

import org.apache.log4j.BasicConfigurator;

import java.util.ArrayList;

public class TestClass {
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        String[] parkanlagen = {"Parkanlage1"};

        ArrayList<Integer> parks = new ArrayList<>();

        for(int i = 5050; i < 5051; i++) {
            new ServerApplication(i);
            parks.add(i);
        }
        Parkrechner pa1 = new Parkrechner(parks, parkanlagen[0]);

        parks.clear();

        try {
            Thread.sleep(100000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Zentralrechner zr = new Zentralrechner(parkanlagen);

        // Noch ein Parkrechner, zudem hat es auch das mit dem Meldungen, das alles fertig ist
        /*for(int i = 6060; i < 6061; i++) {
            new ServerApplication(i);
            parks.add(i);
        }
        Parkrechner pa2 = new Parkrechner(parks, parkanlagen[1]);*/


        /*try {
            zr.notifyAllParkrechner(true);
            pa1.getNotification();
            pa2.getNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}

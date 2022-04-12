package stellarnear.lost_ark_companion.Models;

import android.content.Context;

import stellarnear.lost_ark_companion.Activities.MainActivity;
import stellarnear.lost_ark_companion.Divers.TinyDB;

public class ExpeditionManager {

    private static ExpeditionManager instance = null;
    private final Context mC;
    private Expedition expedition = null;

    public ExpeditionManager(Context mC) {
        this.mC = mC;
    }

    public static ExpeditionManager getInstance(Context mC) {
        if (instance == null) {
            instance = new ExpeditionManager(mC);
        }
        return instance;
    }


    public Expedition initExpeditionFromDB() {
        try {
            expedition = loadFromDB();
        } catch (Exception e) {
            expedition = new Expedition("Calvasus");
            TimeChecker.getInstance(mC).checkCurrentTime();
            saveToDB();
        }
        if (expedition == null) {
            expedition = new Expedition("Calvasus");
            TimeChecker.getInstance(mC).checkCurrentTime();
            saveToDB();
        }
        return this.expedition;
    }

    private Expedition loadFromDB() {
        TinyDB tinyDB = new TinyDB(mC);
        Expedition expedition = tinyDB.getExpedition("save_expedition");
        return expedition;
    }

    public void saveToDB() {
        TinyDB tinyDB = new TinyDB(mC);
        tinyDB.putExpedition("save_expedition", MainActivity.expedition);
    }
}

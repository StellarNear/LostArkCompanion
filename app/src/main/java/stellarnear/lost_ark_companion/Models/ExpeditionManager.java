package stellarnear.lost_ark_companion.Models;

import android.content.Context;

import stellarnear.lost_ark_companion.Activities.MainActivity;
import stellarnear.lost_ark_companion.Divers.TinyDB;

public class ExpeditionManager {

    private static ExpeditionManager instance = null;
    private final Context mC;

    public ExpeditionManager(Context mC) {
        this.mC = mC;
    }

    public static ExpeditionManager getInstance(Context mC) {
        if (instance == null) {
            instance = new ExpeditionManager(mC);
        }
        return instance;
    }


    public Expedition getExpedition() {
        Expedition expeSaved = loadFromDB();
        if (expeSaved != null) {
            return expeSaved;
        } else {
            return new Expedition("Calvasus");
        }
    }

    public Expedition loadFromDB() {
        TinyDB tinyDB = new TinyDB(mC);
        Expedition expedition = tinyDB.getObject("save_expedition", Expedition.class);
        return expedition;
    }

    public void saveToDB() {
        TinyDB tinyDB = new TinyDB(mC);
        tinyDB.putObject("save_expedition", MainActivity.expedition);
    }
}

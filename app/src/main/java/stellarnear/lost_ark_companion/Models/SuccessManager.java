package stellarnear.lost_ark_companion.Models;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import stellarnear.lost_ark_companion.Activities.MainActivity;
import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

public class SuccessManager {

    private static final Tools tools = Tools.getTools();

    public SuccessManager() {

    }

    public static void reset(Context mC) {
        MainActivity.expedition.resetSuccessForCharacterId();
        ExpeditionManager.getInstance(mC).saveToDB();
    }

    public static void checkSuccess(Context mC) {
        //for migration if it wasn't there before
        if (MainActivity.expedition.getSuccessForCharacterId() == null) {
            reset(mC);
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (!settings.getBoolean("success_check", mC.getResources().getBoolean(R.bool.success_check_def))) {
            return;
        }

        boolean totalWin = true;
        List<String> toPlayNow = new ArrayList<>();
        for (Character c : MainActivity.expedition.getCharacters()) {
            if (!MainActivity.expedition.getSuccessForCharacterId().contains(c.getId())) {
                boolean successForChar = true;
                for (Task t : c.getTasks()) {
                    if (TimeChecker.getInstance(mC).isThatDay(t.getAppearance())) {
                        if (t.getOccurrence() != t.getCurrentDone()) {
                            successForChar = false;
                            break;
                        }
                    }
                }
                if (!successForChar) {
                    totalWin = false;
                } else {
                    MainActivity.expedition.getSuccessForCharacterId().add(c.getId());
                    ExpeditionManager.getInstance(mC).saveToDB();
                    toPlayNow.add(c.getName().substring(0, 1).toUpperCase() + c.getName().substring(1));
                }
            }
        }
        for (Task t : MainActivity.expedition.getExpeditionTasks()) {
            if (TimeChecker.getInstance(mC).isThatDay(t.getAppearance())) {
                if (t.getOccurrence() != t.getCurrentDone()) {
                    totalWin = false;
                    break;
                }
            }
        }
        if (totalWin && !MainActivity.expedition.getSuccessForCharacterId().contains("GLOBAL_WIN")) {
            MainActivity.expedition.getSuccessForCharacterId().add("GLOBAL_WIN");
            ExpeditionManager.getInstance(mC).saveToDB();
            toPlayNow.add("GLOBAL_WIN");
        }

        if (toPlayNow.size() > 0) {
            if (toPlayNow.contains("GLOBAL_WIN")) {
                tools.playVideo(mC, "/raw/total_success");
                tools.customToast(mC, "Bravo ! You completed all tasks for your expedition ! ! !", "bottom");
            } else {
                tools.playVideo(mC, "/raw/char_success");
                tools.customToast(mC, "Nice, you completed all tasks for " + String.join(" and ", toPlayNow) + " !", "bottom");
            }
        }

    }
}

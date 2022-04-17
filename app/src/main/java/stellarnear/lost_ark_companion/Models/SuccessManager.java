package stellarnear.lost_ark_companion.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import stellarnear.lost_ark_companion.Activities.MainActivity;
import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

public class SuccessManager {

    private static ArrayList<String> hadSuccessForCharacterId = new ArrayList<>();
    private static final Tools tools = Tools.getTools();

    public SuccessManager() {

    }

    public static void reset() {
        hadSuccessForCharacterId = new ArrayList<>();
    }

    public static void checkSuccess(Context mC) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (!settings.getBoolean("success_check", mC.getResources().getBoolean(R.bool.success_check_def))) {
            return;
        }

        boolean totalWin = true;
        List<String> toPlayNow = new ArrayList<>();
        for (Character c : MainActivity.expedition.getCharacters()) {
            if (!hadSuccessForCharacterId.contains(c.getId())) {
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
                    break;
                } else {
                    hadSuccessForCharacterId.add(c.getId());
                    toPlayNow.add(c.getId());
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
        if (totalWin && !hadSuccessForCharacterId.contains("GLOBAL_WIN")) {
            hadSuccessForCharacterId.add("GLOBAL_WIN");
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

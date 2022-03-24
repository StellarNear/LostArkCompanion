package stellarnear.lost_ark_companion.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Locale;

import stellarnear.lost_ark_companion.Activities.MainActivity;
import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

import static java.time.temporal.ChronoUnit.DAYS;

public class TimeChecker {
    private static TimeChecker instance = null;
    private final Context mC;
    private final Tools tools = Tools.getTools();
    Expedition expedition = MainActivity.expedition;
    private final SharedPreferences settings;

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat formatterHour = new SimpleDateFormat("HH");
    private final SimpleDateFormat formatterDayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH);

    private TimeChecker(Context mC) {
        this.mC = mC;
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
    }

    public static TimeChecker getInstance(Context mC) {
        if (instance == null) {
            instance = new TimeChecker(mC);
        }
        return instance;
    }

    public boolean checkCurrentTime() {

        boolean needRefreshUi = false;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime storedDate = MainActivity.expedition.getStoredDate();

        long nReset = resetDiff(now, storedDate);

        if (nReset > 0) {
            if (nReset > 1) {
                int nWeekly = 0;
                int nDaily = 0;
                LocalDateTime previousDate = storedDate;
                for (LocalDateTime date = storedDate; date.isBefore(now); date = date.plusHours(1)) {
                    if (previousDate.getHour() < 11 && date.getHour() >= 11) {
                        if (date.getDayOfWeek().toString().equalsIgnoreCase("Thursday")) {
                            nWeekly++;
                            expedition.resetWeekly();
                        } else {
                            nDaily++;
                            expedition.resetDaily();
                        }
                        needRefreshUi = true;
                    }
                    previousDate = date;
                }
                tools.customToast(mC, "Since the last update we had " + nDaily + " daily reset and " + nWeekly + " weekly reset...");
            } else {
                if (now.getDayOfWeek().toString().equalsIgnoreCase("Thursday")) {
                    expedition.resetWeekly();
                    tools.customToast(mC, "We are Thursday !\nWeekly Reset");
                    needRefreshUi = true;
                } else {
                    expedition.resetDaily();
                    tools.customToast(mC, "Daily Reset");
                    needRefreshUi = true;
                }
            }
        } else if (settings.getBoolean("display_all_checks", mC.getResources().getBoolean(R.bool.display_all_checks_DEF))) {
            tools.customToast(mC, "No reset");

        }
        MainActivity.expedition.setStoredDate(now);
        ExpeditionManager.getInstance(mC).saveToDB();
        return needRefreshUi;
    }

    private long resetDiff(LocalDateTime now, LocalDateTime storedDate) {
        if (storedDate == null) {
            return 0;
        }

        long nReset = DAYS.between(storedDate, now) + (now.getHour() >= 11 ? 1 : 0) - (storedDate.getHour() >= 11 ? 1 : 0);
        return nReset;
    }


    public void cheatPassDay(int nDays) {
        LocalDateTime storedDate = MainActivity.expedition.getStoredDate();
        try {
            MainActivity.expedition.setStoredDate(storedDate.minusDays(nDays));
            ExpeditionManager.getInstance(mC).saveToDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

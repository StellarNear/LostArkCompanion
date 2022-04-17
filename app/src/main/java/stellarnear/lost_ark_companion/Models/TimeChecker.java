package stellarnear.lost_ark_companion.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import stellarnear.lost_ark_companion.Activities.MainActivity;
import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

import static java.time.temporal.ChronoUnit.DAYS;

public class TimeChecker {
    private static TimeChecker instance = null;
    private final Context mC;
    private final Tools tools = Tools.getTools();
    private final SharedPreferences settings;

    private final Expedition expedition = MainActivity.expedition;

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
        LocalDateTime nowWithHour = LocalDateTime.now(ZoneId.of("UTC+1"));
        LocalDate now = LocalDate.now(ZoneId.of("UTC+1"));

        if (nowWithHour.getHour() < 11) { //before 11 AM it's still the previous day
            now = now.minusDays(1);
        }

        if (MainActivity.expedition == null) {
            return true;
        }

        LocalDate storedDate = MainActivity.expedition.getStoredDate();
        if (storedDate == null) {
            MainActivity.expedition.setStoredDate(now);
            ExpeditionManager.getInstance(mC).saveToDB();
            return true;
        }

        long nReset = resetDiff(now, storedDate);

        if (settings.getBoolean("display_all_checks", mC.getResources().getBoolean(R.bool.display_all_checks_DEF))) {
            tools.customToast(mC, "Now : " + now.getDayOfMonth() + "/" + now.getMonthValue());
            tools.customToast(mC, "Stored : " + storedDate.getDayOfMonth() + "/" + storedDate.getMonthValue());
            tools.customToast(mC, "nReset : " + nReset);
        }
        if (nReset > 0) {
            if (nReset > 1) {
                int nWeekly = 0;
                int nDaily = 0;
                for (LocalDate date = storedDate; date.isBefore(now.plusDays(1)); date = date.plusDays(1)) { //the loop have to consider the today reset
                    if (date.getDayOfWeek().toString().equalsIgnoreCase("Thursday")) {
                        nWeekly++;
                        expedition.resetWeekly();
                    } else {
                        nDaily++;
                        expedition.resetDaily();
                    }
                    needRefreshUi = true;
                }
                SuccessManager.reset();
                tools.customToast(mC, "Since the last update we had " + nDaily + " daily reset and " + nWeekly + " weekly reset...");
            } else {
                if (now.getDayOfWeek().toString().equalsIgnoreCase("Thursday")) {
                    expedition.resetWeekly();
                    tools.customToast(mC, "We are Thursday !\nWeekly Reset");
                } else {
                    expedition.resetDaily();
                    tools.customToast(mC, "Daily Reset");
                }
                SuccessManager.reset();
                needRefreshUi = true;
            }
        } else if (settings.getBoolean("display_all_checks", mC.getResources().getBoolean(R.bool.display_all_checks_DEF))) {
            tools.customToast(mC, "No reset");
        }
        MainActivity.expedition.setStoredDate(now);
        ExpeditionManager.getInstance(mC).saveToDB();
        return needRefreshUi;
    }

    private long resetDiff(LocalDate now, LocalDate storedDate) {
        if (storedDate == null) {
            return 0;
        }
        return DAYS.between(storedDate, now);
    }


    public void cheatPassDay(int nDays) {
        LocalDate storedDate = MainActivity.expedition.getStoredDate();
        try {
            MainActivity.expedition.setStoredDate(storedDate.minusDays(nDays));
            ExpeditionManager.getInstance(mC).saveToDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isThatDay(List<String> appearance) {
        if (appearance == null || appearance.size() == 0) {
            return true;
        }
        LocalDate now = LocalDate.now(ZoneId.of("UTC+1"));
        LocalDateTime nowWithHour = LocalDateTime.now(ZoneId.of("UTC+1"));
        if (nowWithHour.getHour() < 11) { //before 11 AM it's still the previous day
            now = now.minusDays(1);
        }
        for (String day : appearance) {
            if (now.getDayOfWeek().toString().equalsIgnoreCase(day)) {
                return true;
            }
        }
        return false;
    }
}

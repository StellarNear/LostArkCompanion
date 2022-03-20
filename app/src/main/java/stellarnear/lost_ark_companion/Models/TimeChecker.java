package stellarnear.lost_ark_companion.Models;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import stellarnear.lost_ark_companion.Activities.MainActivity;
import stellarnear.lost_ark_companion.Divers.Tools;

public class TimeChecker {
    private static TimeChecker instance = null;
    Expedition expedition = MainActivity.expedition;
    private final Context mC;
    private final Tools tools = Tools.getTools();

    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatterHour = new SimpleDateFormat("HH");
    private SimpleDateFormat formatterDayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH);

    private TimeChecker(Context mC) {
        this.mC = mC;
    }

    public static TimeChecker getInstance(Context mC) {
        if (instance == null) {
            instance = new TimeChecker(mC);
        }
        return instance;
    }

    public void checkCurrentTime() {
        tools.customToast(mC, "Checking time");

        Date date = new Date();

        String storedDate = MainActivity.expedition.getStoredDate();
        if (!storedDate.equalsIgnoreCase(formatter.format(date)) && (Integer.parseInt(formatterHour.format(date)) >= 11)) {

            if (formatterDayOfTheWeek.format(date).equalsIgnoreCase("Thursday")) {
                expedition.resetWeekly();
                tools.customToast(mC,"We are Thursday !\nWeekly Reset");
            } else {
                expedition.resetDaily();
                tools.customToast(mC,"Daily Reset");
            }
            MainActivity.expedition.setStoredDate(formatter.format(date));
            ExpeditionManager.getInstance(mC).saveToDB();
        } else {
            tools.customToast(mC,"Nothing, changed still the same day");
        }
    }

    public void cheatPassDay(int nDays){
        String storedDate = MainActivity.expedition.getStoredDate();
        try {
           Date stored =  formatter.parse(storedDate);

            LocalDateTime ldt = LocalDateTime.ofInstant(stored.toInstant(), ZoneId.systemDefault()).minusDays(nDays);
            Date fakeDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

            MainActivity.expedition.setStoredDate(formatter.format(fakeDate));
            ExpeditionManager.getInstance(mC).saveToDB();
            checkCurrentTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



}

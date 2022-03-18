
package stellarnear.lost_ark_companion.Models;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import stellarnear.lost_ark_companion.Activities.MainActivity;
import stellarnear.lost_ark_companion.Divers.Tools;

public class TimeChecker {
	private Context mC;
	Expedition expedition = MainActivity.expedition;
	private Tools tools = Tools.getTools();

	private static TimeChecker instance=null;

	public static TimeChecker getInstance(Context mC) {
		if (instance == null) {
			instance = new TimeChecker(mC);
		}
		return instance;
	}

	private TimeChecker(Context mC){
		this.mC=mC;
	}


	public void checkCurrentTime() {
		tools.customToast(mC,"Checking time");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatterHour = new SimpleDateFormat("HH");
        Date date = new Date();
        ;
        //todo get from settings in phone the storedDate
        String storedDate = "";
        if (!storedDate.equalsIgnoreCase(formatter.format(date)) && (Integer.parseInt(formatterHour.format(date)) >= 11)) {
            SimpleDateFormat formatterDayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            if (formatterDayOfTheWeek.format(date).equalsIgnoreCase("Thursday")) {
                expedition.resetWeekly();
                Tools.toast("We are Thursday !\nWeekly Reset");
            } else {
                expedition.resetDaily();
                Tools.toast("Daily Reset");
            }
            storeDate(formatter.format(date));
        }
    }

    private void storeDate(String format) {

    }

}

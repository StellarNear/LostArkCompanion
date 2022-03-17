
package stellarnear.lost_ark_companion.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeChecker {
	Expedition expedition = MainActivity.expedition;

	public void checkCurrentTime(){
		Tools.shortToast("Checking time");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatterHour = new SimpleDateFormat("HH");
		Date date = new Date();
		;
		//todo get from settings in phone the storedDate
		String storedDate="";
		if(!storedDate.equalsIgnoreCase(formatter.format(date)) && (Integer.parseInt(formatterHour.format(date)) >= 11) ){
			SimpleDateFormat formatterDayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH);
			if(formatterDayOfTheWeek.format(date).equalsIgnoreCase("Thursday")){
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

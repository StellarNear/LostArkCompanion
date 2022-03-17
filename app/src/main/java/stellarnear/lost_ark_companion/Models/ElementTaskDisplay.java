package stellarnear.lost_ark_companion.Models;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

public class ElementTaskDisplay {

	public static LinearLayout getTaskElement(final Task task){
		LinearLayout tempalteTaskElement = inflate.from("task_element");
		//todo une ressource layout avec un background et dessus un layout Vertical
		// qui contient en haut un title Textview
		//et en bas un linear horizontal checkboxes

		if(getDrawable(task.getId())!=null){
			elemetempalteTaskElement.getFieldByID("background").setBackground(getDrawable(task.getId()));
		//todo un drawable avec background id et le nom dessus
		}

		TextView text=tempalteTaskElement.getFieldByID("title");
		text.setText(task.getName());

		LinearLayout checkboxes = tempalteTaskElement.getFieldByID("checkboxes"); //horizontal
		int nDone=0;
		for (int i=1;i<= task.getOccurance();i++){
			final CheckBox box = new CheckBox();
			if(nDone<task.getCurrentDone())){
				box.setChecked(true);
				box.setEnable(false);
				nDone++;
			}
			checkboxes.add(box);

			box.setOnClickListner(new EventListener() {
				@Override
				public void handleEvent(Event evt) {
					box.setEnable(false);
					task.addDone();
					ExpeditionManager.saveToDB();
				}
			});

			box.setOnLongClickListner(new EventListener() {
				@Override
				public void handleEvent(Event evt) {
					//confirmation Es tu sure d'annuler cette complétion ?
					//on confirm
					box.setEnable(true);
					box.setChecked(false);
					task.cancelOne();
					ExpeditionManager.saveToDB();
				}

			});


			)
		}

		return tempalteTaskElement;
	}

	private static Object getDrawable(String id) {
		return null;
	}

}

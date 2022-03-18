package stellarnear.lost_ark_companion.Models;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

public class ElementTaskDisplay {
    private final Context mC;
    private final Task task;

    public ElementTaskDisplay(Context mC, Task task) {
        this.mC = mC;
        this.task = task;
    }

    public LinearLayout getTaskElement(final Task task) {
        LayoutInflater inflater = LayoutInflater.from(mC);
        View templateTaskElement = inflater.inflate(R.layout.task_element, null);

        if (getDrawable(task.getId()) != null) {
            templateTaskElement.getFieldByID("task_background").setBackground(getDrawable(task.getId()));
            //todo un drawable avec background id et le nom dessus
        }

        TextView text = templateTaskElement.getFieldByID("task_name");
        text.setText(task.getName());

        LinearLayout checkboxes = templateTaskElement.getFieldByID("checkboxes"); //horizontal
        int nDone = 0;
        for (int i = 1; i <= task.getOccurance(); i++) {
            final CheckBox box = new CheckBox();
            if (nDone < task.getCurrentDone())){
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
                    //to correct missclick we allow undo on longpress on checkbox
                    //because we know that Bene can panic and do random clicks
                    new AlertDialog.Builder(mC)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle("Undo this ?")
                            .setMessage("Do you really want to undo this completion of " + task.getName() + " ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    box.setEnable(true);
                                    box.setChecked(false);
                                    task.cancelOne();
                                    ExpeditionManager.saveToDB();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }

            });


			)
        }

        return templateTaskElement;
    }

}

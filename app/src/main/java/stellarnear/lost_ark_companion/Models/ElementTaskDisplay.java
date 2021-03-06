package stellarnear.lost_ark_companion.Models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

public class ElementTaskDisplay implements ElementTask {
    private final Context mC;
    private final Tools tools = Tools.getTools();

    public ElementTaskDisplay(Context mC) {
        this.mC = mC;
    }

    public View getTaskElement(final Task task) {
        LayoutInflater inflater = LayoutInflater.from(mC);
        View templateTaskElement = inflater.inflate(R.layout.task_element, null);
        try {
            templateTaskElement.findViewById(R.id.task_background).setBackground(tools.getDrawable(mC, task.getDrawableId()));
        } catch (Exception e) {
            templateTaskElement.findViewById(R.id.task_background).setBackground(mC.getDrawable(R.drawable.mire_test));
        }
        TextView text = templateTaskElement.findViewById(R.id.task_name);
        text.setText(task.getName());
        LinearLayout checkboxes = templateTaskElement.findViewById(R.id.checkboxes_tasks); //horizontal
        buildCheckBoxList(checkboxes, task);

        return templateTaskElement;
    }

    private void buildCheckBoxList(LinearLayout checkboxes, Task task) {
        int nDone = 0;
        checkboxes.removeAllViews();
        for (int i = 1; i <= task.getOccurrence(); i++) {
            final CheckBox box = new CheckBox(mC);
            if (nDone < task.getCurrentDone()) {
                box.setChecked(true);
                nDone++;
            }
            checkboxes.addView(box, 0);

            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!box.isChecked()) {
                        new AlertDialog.Builder(mC)
                                .setIcon(R.drawable.ic_warning_black_24dp)
                                .setTitle("Undo this ?")
                                .setMessage("Do you really want to undo this completion of " + task.getName() + " ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        task.cancelOne();
                                        ExpeditionManager.getInstance(mC).saveToDB();
                                        buildCheckBoxList(checkboxes, task);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        box.setChecked(true);
                                    }
                                })
                                .show();
                    } else {
                        task.addDone();
                        ExpeditionManager.getInstance(mC).saveToDB();
                        SuccessManager.checkSuccess(mC);
                    }
                }
            });

            box.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(mC)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle("Manual edition")
                            .setMessage("You can undo one occurrence, or validate one by stronghold expedition for " + task.getName() + ".")
                            .setNegativeButton("Undo", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    task.cancelOne();
                                    ExpeditionManager.getInstance(mC).saveToDB();
                                }
                            })
                            .setPositiveButton("By boat", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    task.doneByBoat();
                                    ExpeditionManager.getInstance(mC).saveToDB();
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                    return true;
                }
            });
        }
    }
}

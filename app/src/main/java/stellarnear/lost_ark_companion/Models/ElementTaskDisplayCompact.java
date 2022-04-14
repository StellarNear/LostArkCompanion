package stellarnear.lost_ark_companion.Models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import stellarnear.lost_ark_companion.Divers.CircularProgressBar;
import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

public class ElementTaskDisplayCompact implements ElementTask {
    private final Context mC;
    private final Tools tools = Tools.getTools();

    public ElementTaskDisplayCompact(Context mC) {
        this.mC = mC;
    }

    public View getTaskElement(final Task task) {
        LayoutInflater inflater = LayoutInflater.from(mC);
        View templateTaskElement = inflater.inflate(R.layout.task_element_compact, null);
        try {
            templateTaskElement.findViewById(R.id.circular_task_icon).setBackground(tools.getDrawable(mC, task.getDrawableId()));
        } catch (Exception e) {
            templateTaskElement.findViewById(R.id.circular_task_icon).setBackground(mC.getDrawable(R.drawable.mire_test));
        }

        CircularProgressBar circular = templateTaskElement.findViewById(R.id.circular_progress_task);
        setCircular(circular, task);

        return templateTaskElement;
    }

    private void setCircular(CircularProgressBar circular, Task task) {
        circular.setMax(task.getOccurrence());
        setColor(circular, task);
        circular.setProgressWithAnimation(task.getCurrentDone());


        circular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.addDone();
                ExpeditionManager.getInstance(mC).saveToDB();
                setColor(circular, task);
                circular.setProgressWithAnimation(task.getCurrentDone());
                if ((task.getId().equalsIgnoreCase("chaos_dungeon") || task.getId().equalsIgnoreCase("guardian_raid") && task.getRest() >= 20)) { //sinon la barre bougera pas aucun interet à refresh
                    task.refreshRestBar(mC);
                }
            }
        });

        circular.setOnLongClickListener(new View.OnLongClickListener() {
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
                                setColor(circular, task);
                                circular.setProgressWithAnimation(task.getCurrentDone());
                            }
                        })
                        .setPositiveButton("By boat", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                task.doneByBoat();
                                ExpeditionManager.getInstance(mC).saveToDB();
                                setColor(circular, task);
                                circular.setProgressWithAnimation(task.getCurrentDone());
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

    private void setColor(CircularProgressBar circular, Task task) {
        double coef = (1.0 * task.getCurrentDone()) / (1.0 * task.getOccurrence());
        if (coef >= 0.75) {
            circular.setColor(mC.getColor(R.color.task_done));
        } else if (coef < 0.75 && coef >= 0.5) {
            circular.setColor(mC.getColor(R.color.task_abovehaldf));
        } else if (coef < 0.5 && coef >= 0.25) {
            circular.setColor(mC.getColor(R.color.task_underhalf));
        } else {
            circular.setColor(mC.getColor(R.color.task_notstarted));
        }
    }


}

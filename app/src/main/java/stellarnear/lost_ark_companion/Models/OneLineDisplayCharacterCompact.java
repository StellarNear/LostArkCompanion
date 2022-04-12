package stellarnear.lost_ark_companion.Models;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

public class OneLineDisplayCharacterCompact implements OneLineDisplay {

    private final Context mC;
    private final Tools tools = Tools.getTools();

    public OneLineDisplayCharacterCompact(Context context) {
        this.mC = context;
    }


    public View getOneLine(Character c) {

        LayoutInflater inflater = LayoutInflater.from(mC);
        View mainView = inflater.inflate(R.layout.character_line_compact, null);

        TextView name = mainView.findViewById(R.id.char_name);
        name.setText(c.getName().substring(0, 1).toUpperCase() + c.getName().substring(1));
        name.setTextColor(mC.getColor(R.color.colorPrimaryDark));

        TextView ilvl = mainView.findViewById(R.id.ilvl);
        if (c.getIlvl() > 0) {
            ilvl.setText("[" + c.getIlvl() + "]");
            ilvl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final EditText input = new EditText(mC);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    new AlertDialog.Builder(mC)
                            .setView(input)
                            .setTitle("Change ilvl for " + c.getName())
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    c.setIlvl(tools.toInt(input.getText().toString()));
                                    ExpeditionManager.getInstance(mC).saveToDB();
                                    ilvl.setText("[" + c.getIlvl() + "]");
                                    RefreshManager.getRefreshManager().triggerRefresh();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }).show();
                    return true;
                }
            });
        } else {
            ilvl.setVisibility(View.GONE);
        }

        ImageView work = mainView.findViewById(R.id.work_background);
        //si le fill color marche pas add   vectorDrawables.useSupportLibrary = true dans le defaultCOnfig gradle et

        //DrawableCompat.setTint(imageView.getDrawable(),ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

        work.setImageDrawable(tools.getDrawable(mC, c.getWorkId() + "_ico"));
        work.setColorFilter(ColorUtils.setAlphaComponent(mC.getColor(R.color.colorPrimary), 50), PorterDuff.Mode.MULTIPLY);

        // create layout avec


        // autre layout (progress bar)
        initProgressBar(mainView, R.id.chaos_back_bar, R.id.chaos_bar, c.getTaskByID("chaos_dungeon"));
        mainView.findViewById(R.id.chaos_bar_frame).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                askForOvewriteRest(c.getTaskByID("chaos_dungeon"), mainView);
                return true;
            }
        });
        initProgressBar(mainView, R.id.guardian_back_bar, R.id.guardian_bar, c.getTaskByID("guardian_raid"));
        mainView.findViewById(R.id.guardian_bar_frame).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                askForOvewriteRest(c.getTaskByID("guardian_raid"), mainView);
                return true;
            }
        });

        // Tasks
        LinearLayout tasks = mainView.findViewById(R.id.tasks_list_one_line_char_circular);
        ElementTaskDisplayCompact elementLiner = new ElementTaskDisplayCompact(mC);
        for (final Task task : c.getTasks()) {
            if (task.getAppearance() != null && !TimeChecker.getInstance(mC).isThatDay(task.getAppearance())) {
                continue;
            }

            final View taskElement = elementLiner.getTaskElement(task);

            taskElement.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(mC)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle("Remove this task ?")
                            .setMessage("Do you really want to delete " + task.getName() + " ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (task.getId().equalsIgnoreCase("chaos_dungeon") || task.getId().equalsIgnoreCase("guardian_raid")) {
                                        tools.customToast(mC, task.getName() + " can't be deleted (Chaos and Guardians are baseline) !");
                                    } else {
                                        c.getTasks().remove(task);
                                        ExpeditionManager.getInstance(mC).saveToDB();
                                        ((ViewGroup) taskElement.getParent()).removeView(taskElement);
                                        tools.customToast(mC, task.getName() + " removed for " + c.getName() + " !");
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                    return true;
                }
            });

            if (!task.getId().equalsIgnoreCase("guardian_raid") && !task.getId().equalsIgnoreCase("chaos_dungeon")) {
                taskElement.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                tasks.addView(taskElement);
            } else {
                if (task.getId().equalsIgnoreCase("guardian_raid")) {
                    ((LinearLayout) mainView.findViewById(R.id.main_guardian_lin)).addView(taskElement, 0);
                } else {
                    ((LinearLayout) mainView.findViewById(R.id.main_chaos_lin)).addView(taskElement, 0);
                }
            }
        }

        return mainView;

    }


    private void askForOvewriteRest(Task task, View mainView) {
        final EditText input = new EditText(mC);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(mC)
                .setView(input)
                .setTitle("Overwrite rest value for " + task.getName())
                .setIcon(android.R.drawable.ic_menu_help)
                .setPositiveButton("Overwrite", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        task.setRest(tools.toInt(input.getText().toString()));
                        ExpeditionManager.getInstance(mC).saveToDB();
                        task.refreshRestBar(mC);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }


    private void initProgressBar(View mainView, int idBackBar, int frontBar, Task task) {
        final ImageView image = mainView.findViewById(frontBar);
        image.post(new Runnable() {
            @Override
            public void run() {
                ImageView image = mainView.findViewById(frontBar);
                ImageView progress = mainView.findViewById(idBackBar);
                task.initRestBarUI(image, progress, mC);
            }
        });
    }


}

package stellarnear.lost_ark_companion.Models;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

public class OneLineDisplayCharacter implements OneLineDisplay {

    private final Context mC;
    private final Tools tools = Tools.getTools();

    public OneLineDisplayCharacter(Context context) {
        this.mC = context;
    }


    public View getOneLine(Character c) {

        LayoutInflater inflater = LayoutInflater.from(mC);
        View mainView = inflater.inflate(R.layout.character_line, null);

        TextView name = mainView.findViewById(R.id.char_name);
        name.setText(c.getName().substring(0, 1).toUpperCase() + c.getName().substring(1));

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
                                    ilvl.setText("[" + c.getIlvl() + "]");
                                    ExpeditionManager.getInstance(mC).saveToDB();
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

        work.setImageDrawable(tools.getDrawable(mC, c.getWorkId() + "_ico"));

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

        // linear horizontal avec chaque task element
        LinearLayout tasks = mainView.findViewById(R.id.tasks_list_one_line_char);
        ElementTaskDisplay elementLiner = new ElementTaskDisplay(mC);
        for (final Task task : c.getTasks()) {
            if (!TimeChecker.getInstance(mC).isThatDay(task.getAppearance())) {
                continue;
            }
            final View taskElement = elementLiner.getTaskElement(task);

            taskElement.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            tasks.addView(taskElement);
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

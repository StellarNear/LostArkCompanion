package stellarnear.lost_ark_companion.Models;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.lost_ark_companion.Activities.MainActivity;
import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

public class OneLineDisplayCharacter {

    private final Context mC;
    private Tools tools=Tools.getTools();

    public OneLineDisplayCharacter(Context context) {
        this.mC = context;
    }

    public View getOneLine(Character c) {

        LayoutInflater inflater = LayoutInflater.from(mC);
        View mainView = inflater.inflate(R.layout.character_line, null);

        TextView name = mainView.findViewById(R.id.char_name);
        name.setText(c.getName() +" ("+c.getIlvl()+")");

        ImageView work = mainView.findViewById(R.id.work_background);

        work.setImageDrawable(tools.getDrawable(mC,c.getWorkId()+"_ico"));

        // create layout avec


        // autre layout (progress bar)
        setProgressBar(mainView,R.id.chaos_back_bar, R.id.chaos_bar, c.getRestChaos());
        setProgressBar(mainView,R.id.guardian_back_bar, R.id.guardian_bar, c.getRestGuardian());

        // linear horizontal avec chaque task element
        LinearLayout tasks = mainView.findViewById(R.id.tasks_list_one_line_char);
		ElementTaskDisplay elementLiner = new ElementTaskDisplay(mC);
        for (final Task task : c.getTasks()) {
            final View taskElement = elementLiner.getTaskElement(task);

            taskElement.setLayoutParams(new LinearLayout.LayoutParams( 0,LinearLayout.LayoutParams.WRAP_CONTENT, 1));
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
                                    if(task.getId().equalsIgnoreCase("chaos_dungeon")|| task.getId().equalsIgnoreCase("guardian_raid")){
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
            tasks.addView(taskElement);
        }

        return mainView;

    }




    private void setProgressBar(View mainView, int idBackBar, int frontBar, int currentBarLevel) {
        final ImageView image = mainView.findViewById(frontBar);
        image.post(new Runnable() {
            @Override
            public void run() {
                ImageView progress = mainView.findViewById(idBackBar);
                ViewGroup.LayoutParams para = (ViewGroup.LayoutParams) progress.getLayoutParams();
                int oriWidth = image.getMeasuredWidth();
                int oriHeight = image.getMeasuredHeight();
                Double coef = (double) currentBarLevel / 100.0;
                if (coef < 0d) {
                    coef = 0d;
                } //pour les val
                if (coef > 1d) {
                    coef = 1d;
                }
                para.width = (int) (coef * oriWidth);
                para.height=oriHeight;
                progress.setLayoutParams(para);
                if (coef >= 0.75) {
                    progress.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_notok));
                } else if (coef < 0.75 && coef >= 0.5) {
                    progress.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_underhalf));
                } else if (coef < 0.5 && coef >= 0.25) {
                    progress.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_abovehalf));
                } else {
                    progress.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_ok));
                }
            }
        });
    }

}

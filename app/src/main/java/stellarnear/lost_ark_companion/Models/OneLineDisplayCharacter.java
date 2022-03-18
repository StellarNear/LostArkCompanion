package stellarnear.lost_ark_companion.Models;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.lost_ark_companion.R;

public class OneLineDisplayCharacter {

    private final Context mC;
    private Character c;

    public OneLineDisplayCharacter(Context context) {
        this.mC = context;
    }

    public View getOneLine(Character c) {

        LayoutInflater inflater = LayoutInflater.from(mC);
        View mainView = inflater.inflate(R.layout.character_line, null);

        TextView name = mainView.findViewById(R.id.char_name);
        name.setText(c.getName());

        ImageView work = mainView.findViewById(R.id.work_background);
        setImage(work, c.getWorkId());

        // create layout avec


        // autre layout (progress bar)
        setProgressBar(R.id.chaos_back_bar, R.id.chaos_bar, c.getRestChaos());
        setProgressBar(R.id.guardian_back_bar, R.id.guardian_bar, c.getRestGuardian());


        // linear horizontal avec chaque task element
        LinearLayout tasks = mainView.findViewById(R.id.tasks);
        for (final Task task : c.getTasks()) {
            final LinearLayout taskElement = ElementTaskDisplay.getTaskElement(task);
            taskElement.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {
                    c.getTasks().remove(task);
                    ExpeditionManager.saveToDB();
                    taskElement.getParent().remove(taskElement); //Ca marche ?
                    return true;
                }
            });

        }

        return mainView;

    }

    private void setImage(ImageView img, String id) {
        //todo seach if drawable match id
        img.setDrawable(draw);
    }

    private Object getDrawable(String id) {
        return null;
    }

    private void setProgressBar(String idBackBar, String frontBar, int currentBarLevel) {
        final ImageView image = dialogView.findViewById(frontBar);
        image.post(new Runnable() {
            @Override
            public void run() {
                ImageView progress = dialogView.findViewById(idBackBar);
                ViewGroup.LayoutParams para = (ViewGroup.LayoutParams) progress.getLayoutParams();
                int oriWidth = image.getMeasuredWidth();
                int oriHeight = image.getMeasuredHeight();

                Double coef = (double) currentBarLevel / 100.0;
                if (coef < 0d) {
                    coef = 0d;
                } //pour les hp negatif
                if (coef > 1d) {
                    coef = 1d;
                }
                para.width = (int) (coef * oriWidth);
                para.height = oriHeight;
                progress.setLayoutParams(para);
                if (coef >= 0.75) {
                    progress.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_health_ok));
                } else if (coef < 0.75 && coef >= 0.5) {
                    progress.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_health_abovehalf));
                } else if (coef < 0.5 && coef >= 0.25) {
                    progress.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_health_underhalf));
                } else {
                    progress.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_health_notok));
                }
            }
        });
        return image;
    }

}

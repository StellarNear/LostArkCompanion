package stellarnear.lost_ark_companion.Models;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import stellarnear.lost_ark_companion.R;

public class Task {

    private final boolean daily;
    private final boolean crossAccount;
    private String name;
    private final int occurance;
    private int currentDone;
    private final String id;
    private int rest = 0;

    //UI stuff
    private transient ImageView progressBarUI=null;
    private transient int oriWidthBarUI;
    private transient int oriHeightBarUI;


    public Task(boolean daily, boolean crossAccount, String name, int occurance) {
        this.daily = daily;
        this.crossAccount = crossAccount;
        this.name = name;
        this.occurance = occurance;
        this.currentDone = 0;
        this.id = name.replace(" ", "_").toLowerCase();
    }

    public Task(Task another) {
        this.daily = another.daily;
        this.crossAccount = another.crossAccount;
        this.name = another.name;
        this.occurance = another.occurance;
        this.currentDone = 0;
        this.id = another.name.replace(" ", "_").toLowerCase();
    }

    public String getId() {
        return id;
    }

    public int getOccurance() {
        return occurance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCrossAccount() {
        return crossAccount;
    }

    public boolean isDaily() {
        return daily;
    }


    public int getCurrentDone() {
        return this.currentDone;
    }

    public void addDone() {
        this.currentDone++;
        if (this.rest >= 20) {
            this.rest -= 20;
            if (this.rest < 0) {
                this.rest = 0;
            }
        }
        if (this.currentDone > this.occurance) {
            this.currentDone = this.occurance;
        }
    }

    public void reset() {
        this.rest += (this.occurance - this.currentDone) * 10;
        if (this.rest > 100) {
            this.rest = 100;
        }
        this.currentDone = 0;
    }

    public void cancelOne() {
        this.currentDone--;
        if (this.currentDone < 0) {
            this.currentDone = 0;
        }
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public void refreshRestBar(Context mC) {
        if(this.progressBarUI!=null) {
            ViewGroup.LayoutParams para = progressBarUI.getLayoutParams();
            Double coef = (double) this.rest / 100.0;
            if (coef < 0d) {
                coef = 0d;
            } //pour les val
            if (coef > 1d) {
                coef = 1d;
            }
            para.width = (int) (coef * oriWidthBarUI);
            para.height = oriHeightBarUI;
            progressBarUI.setLayoutParams(para);
            if (coef >= 0.75) {
                progressBarUI.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_notok));
            } else if (coef < 0.75 && coef >= 0.5) {
                progressBarUI.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_underhalf));
            } else if (coef < 0.5 && coef >= 0.25) {
                progressBarUI.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_abovehalf));
            } else {
                progressBarUI.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_ok));
            }
        }
    }

    public void initRestBarUI(View image , ImageView progress, Context mC) {
        this.progressBarUI=progress;
        ViewGroup.LayoutParams para = progressBarUI.getLayoutParams();
        this.oriWidthBarUI = image.getMeasuredWidth();
        this.oriHeightBarUI = image.getMeasuredHeight();
        Double coef = (double) this.rest / 100.0;
        if (coef < 0d) {
            coef = 0d;
        } //pour les val
        if (coef > 1d) {
            coef = 1d;
        }
        para.width = (int) (coef * oriWidthBarUI);
        para.height = oriHeightBarUI;
        progressBarUI.setLayoutParams(para);
        if (coef >= 0.75) {
            progressBarUI.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_notok));
        } else if (coef < 0.75 && coef >= 0.5) {
            progressBarUI.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_underhalf));
        } else if (coef < 0.5 && coef >= 0.25) {
            progressBarUI.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_abovehalf));
        } else {
            progressBarUI.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_ok));
        }
    }


}

package stellarnear.lost_ark_companion.Models;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import stellarnear.lost_ark_companion.R;

public class Task {

    private final boolean daily;
    private final boolean crossAccount;
    private final int occurrence;
    private final String id;
    private final String drawableId;
    private String name;
    private int currentDone;
    private int previousRest = 0;
    private int rest = 0;
    private List<String> appearance = null;

    //UI stuff
    private transient ImageView progressBarUI = null;
    private transient int oriWidthBarUI;
    private transient int oriHeightBarUI;
    private transient Context mC;


    public Task(boolean daily, boolean crossAccount, String name, int occurrence, String drawableId) {
        this.daily = daily;
        this.crossAccount = crossAccount;
        this.name = name;
        this.occurrence = occurrence;
        this.currentDone = 0;
        this.id = name.replace(" ", "_").toLowerCase();
        this.drawableId = drawableId;
    }

    public Task(Task another) {
        this.daily = another.daily;
        this.crossAccount = another.crossAccount;
        this.name = another.name;
        this.occurrence = another.occurrence;
        this.currentDone = 0;
        this.id = another.name.replace(" ", "_").toLowerCase();
        this.drawableId = another.drawableId;
        this.appearance = another.appearance;
    }

    public String getId() {
        return id;
    }

    public int getOccurrence() {
        return occurrence;
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

    public void doneByBoat() {
        this.currentDone++;
        if (this.currentDone > this.occurrence) {
            this.currentDone = this.occurrence;
        }
    }

    public void addDone() {
        this.currentDone++;
        this.previousRest = this.rest;
        if (this.rest >= 20) {
            this.rest -= 20;
            if (this.rest < 0) {
                this.rest = 0;
            }
        }
        if (this.currentDone > this.occurrence) {
            this.currentDone = this.occurrence;
        }

        if (this.progressBarUI != null && this.mC != null) {
            refreshRestBar();
        }
    }

    public void reset() {
        this.rest += (this.occurrence - this.currentDone) * 10;
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

        if (this.progressBarUI != null && this.mC != null) {
            this.rest = this.previousRest;
            refreshRestBar();
        }
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
        if (this.progressBarUI != null && this.mC != null) {
            refreshRestBar();
        }
    }

    public void refreshRestBar() {
        if (this.progressBarUI != null && this.mC != null) {
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

    public void initRestBarUI(View image, ImageView progress, Context mC) {
        this.mC = mC;
        this.progressBarUI = progress;
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

    public String getDrawableId() {
        if (this.drawableId == null) {
            return this.id + "_ico";
        } else {
            return this.drawableId;
        }
    }

    public List<String> getAppearance() {
        return appearance;
    }

    public Task setAppearance(List<String> days) {
        this.appearance = days;
        return this;
    }
}

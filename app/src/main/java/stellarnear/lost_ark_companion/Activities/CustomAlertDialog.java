package stellarnear.lost_ark_companion.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

public class CustomAlertDialog {
    private final AlertDialog.Builder dialogBuilder;
    private final Activity mA;
    private final Context mC;
    private final Tools tools = Tools.getTools();
    private AlertDialog alert;
    private boolean permanent = false;
    private boolean positiveButton = false;
    private boolean cancelButton = false;
    private String mode = "";
    private OnAcceptEventListener mListener;

    public CustomAlertDialog(Activity mA, Context mC, View view) {
        // Set the toast and duration
        this.mA = mA;
        this.mC = mC;
        if (mA != null) {
            dialogBuilder = new AlertDialog.Builder(mA, R.style.CustomDialog);
        } else {
            dialogBuilder = new AlertDialog.Builder(mC, R.style.CustomDialog);
        }
        dialogBuilder.setView(view);
    }

    public void showAlert() {
        if (alert == null) {
            alert = dialogBuilder.create();
        }
        alert.show();
        if (positiveButton) {
            applyStyleToOkButton();
        }
        if (cancelButton) {
            applyStyleToCancelButton();
        }
        setTimer();
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        if (mode.contains("width")) {
            width = LinearLayout.LayoutParams.MATCH_PARENT;
        }
        if (mode.contains("height")) {
            height = LinearLayout.LayoutParams.MATCH_PARENT;
        }
        alert.getWindow().setLayout(width, height);
        alert.getWindow().setGravity(Gravity.CENTER);
    }


    private void applyStyleToOkButton() {
        Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) button.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        positiveButtonLL.setMargins(mC.getResources().getDimensionPixelSize(R.dimen.general_margin), 0, 0, 0);
        button.setLayoutParams(positiveButtonLL);
        button.setTextColor(mC.getColor(R.color.colorBackground));
        button.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));
    }

    private void applyStyleToCancelButton() {
        Button button = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negativeButtonLL = (LinearLayout.LayoutParams) button.getLayoutParams();
        negativeButtonLL.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        button.setLayoutParams(negativeButtonLL);
        button.setTextColor(mC.getColor(R.color.colorBackground));
        button.setBackground(mC.getDrawable(R.drawable.button_cancel_gradient));
    }


    private void setTimer() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (settings.getBoolean("switch_autoclose_dialog", mC.getResources().getBoolean(R.bool.switch_autoclose_dialog_DEF)) && !permanent) {
            int duration = Tools.getTools().toInt(settings.getString("custom_alert_long_duration", String.valueOf(mC.getResources().getInteger(R.integer.custom_alert_long_duration_DEF))));
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissAlert();
                }
            }, duration);
        }
    }

    public void dismissAlert() {
        alert.dismiss();
    }

    public void clickToHide(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAlert();
            }
        });
    }

    public void setPermanent(boolean b) {
        this.permanent = b;
    }

    public void addConfirmButton(String txt) {
        dialogBuilder.setPositiveButton(txt, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onEvent();
                }
            }
        });
        positiveButton = true;
    }

    public void addCancelButton(String txt) {
        dialogBuilder.setNegativeButton(txt, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismissAlert();
            }
        });
        cancelButton = true;
    }

    public void setFill(String fill) {
        this.mode += fill;
    }

    public void setAcceptEventListener(OnAcceptEventListener eventListener) {
        mListener = eventListener;
    }

    public View getConfirmButton() {
        return alert.getButton(DialogInterface.BUTTON_POSITIVE);
    }

    public interface OnAcceptEventListener {
        void onEvent();
    }
}

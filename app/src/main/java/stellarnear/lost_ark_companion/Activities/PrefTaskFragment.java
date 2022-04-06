package stellarnear.lost_ark_companion.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.Models.ExpeditionManager;
import stellarnear.lost_ark_companion.Models.Task;
import stellarnear.lost_ark_companion.R;


public class PrefTaskFragment {

    private static ArrayList<String> allIcoRef;
    private final Activity mA;
    private final Context mC;
    private final Tools tools = Tools.getTools();
    private OnRefreshEventListener mListener;
    private String selectedIconId = null;

    public PrefTaskFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
    }

    public void chargeList(PreferenceCategory listCat, List<Task> list) {

        for (final Task task : list) {
            Preference pref = new Preference(mC);
            pref.setKey("task_" + task.getName());
            pref.setTitle(task.getName());

            String txt = task.getOccurance() + " time per " + (task.isDaily() ? "day" : "week") + " for " + (task.isCrossAccount() ? "the expedition" : "each character");
            pref.setSummary(txt);
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
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
                                        MainActivity.expedition.deleteTask(task);
                                        ExpeditionManager.getInstance(mC).saveToDB();
                                        mListener.onEvent();
                                        tools.customToast(mC, task.getName() + " deleted !");
                                    }

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                    return false;
                }
            });
            listCat.addPreference(pref);
        }
    }

    public void createTask() {
        LayoutInflater inflater = mA.getLayoutInflater();
        final View creationView = inflater.inflate(R.layout.custom_toast_task_creation, null);
        CustomAlertDialog creationTaskAlert = new CustomAlertDialog(mA, mC, creationView);

        final ImageButton icon = creationView.findViewById(R.id.icon_task_selector);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupSelectIcon(icon);
            }
        });

        creationTaskAlert.setPermanent(true);
        creationTaskAlert.addConfirmButton("Create");
        creationTaskAlert.addCancelButton("Cancel");
        creationTaskAlert.setAcceptEventListener(new CustomAlertDialog.OnAcceptEventListener() {
            @Override
            public void onEvent() {
                String name = ((EditText) creationView.findViewById(R.id.name_task_creation)).getText().toString();
                creationView.findViewById(R.id.daily_creation).isSelected();

                boolean daily = ((RadioButton) creationView.findViewById(R.id.daily_creation)).isChecked();
                boolean crossAccount = ((RadioButton) creationView.findViewById(R.id.cross_account_creation)).isChecked();
                String occuranceTxt = ((EditText) creationView.findViewById(R.id.occurance_creation)).getText().toString();

                if (name.equalsIgnoreCase("") || occuranceTxt.equalsIgnoreCase("") || selectedIconId == null) {
                    tools.customToast(mC, "You should fill all the fields and select an icon !");
                } else {
                    int occurance = Integer.parseInt(occuranceTxt);
                    Task task = new Task(daily, crossAccount, name, occurance, selectedIconId);
                    if (!taskAlreadyExist(task)) {
                        MainActivity.expedition.createTask(task);
                        ExpeditionManager.getInstance(mC).saveToDB();

                        mListener.onEvent();
                        tools.customToast(mC, task.getName() + " created !");
                    } else {
                        tools.customToast(mC, task.getName() + " already present !");
                    }
                }

            }
        });
        creationTaskAlert.showAlert();


        final EditText editName = creationView.findViewById(R.id.name_task_creation);
        editName.post(new Runnable() {
            public void run() {
                editName.setFocusableInTouchMode(true);
                editName.requestFocusFromTouch();
                InputMethodManager lManager = (InputMethodManager) mA.getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(editName, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void popupSelectIcon(ImageButton iconSelectButton) {


        LayoutInflater inflater = mA.getLayoutInflater();
        final View selectionIconView = inflater.inflate(R.layout.custom_icon_selection, null);
        CustomAlertDialog creationTaskAlert = new CustomAlertDialog(mA, mC, selectionIconView);

        creationTaskAlert.setPermanent(true);
        creationTaskAlert.addCancelButton("Cancel");
        GridLayout list = selectionIconView.findViewById(R.id.icons_list);

        if (allIcoRef == null || allIcoRef.size() < 1) {
            Field[] fields = R.drawable.class.getFields();
            allIcoRef = new ArrayList<>();
            for (int count = 0; count < fields.length; count++) {
                if (fields[count].getName().endsWith("_ico")) {
                    allIcoRef.add(fields[count].getName());
                }
            }
        }
        list.removeAllViews();
        for (String iconId : allIcoRef) {
            ImageView ico = new ImageView(mC);
            Drawable icoDraw = null;
            try {
                icoDraw = tools.getDrawable(mC, iconId);
            } catch (Exception e) {
                iconId = "mire_test";
                icoDraw = mC.getDrawable(R.drawable.mire_test);
            }
            ico.setImageDrawable(icoDraw);
            int margin = mC.getResources().getDimensionPixelSize(R.dimen.general_margin);
            ico.setPadding(margin, margin, margin, margin);
            tools.resize(ico, mC.getResources().getDimensionPixelSize(R.dimen.icon_task_selection));
            list.addView(ico);


            Drawable finalIcoDraw = icoDraw;
            String finalIconId = iconId;
            ico.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedIconId = finalIconId;
                    iconSelectButton.setBackground(finalIcoDraw);
                    creationTaskAlert.dismissAlert();
                }
            });
        }
        creationTaskAlert.showAlert();
    }


    private boolean taskAlreadyExist(Task task) {
        for (Task common : MainActivity.expedition.getCommonCharacterTasks()) {
            if (common.getId().equalsIgnoreCase(task.getId())) {
                return true;
            }
        }
        for (Task t : MainActivity.expedition.getExpeditionTasks()) {
            if (t.getId().equalsIgnoreCase(task.getId())) {
                return true;
            }
        }
        return false;
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }
}

package stellarnear.lost_ark_companion.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.Models.Character;
import stellarnear.lost_ark_companion.Models.ExpeditionManager;
import stellarnear.lost_ark_companion.Models.Task;
import stellarnear.lost_ark_companion.R;


public class PrefTaskFragment {

    private static ArrayList<String> allIcoRef = null;
    private final Activity mA;
    private final Context mC;
    private final Tools tools = Tools.getTools();
    private OnRefreshEventListener mListener;
    private String selectedIconId = null;
    private ImageButton iconSelect;
    private CustomAlertDialog selectIconDialog;

    private ArrayList<CheckBox> advanceAppearance = new ArrayList<>();
    private ArrayList<CheckBox> advanceCharacterLineCheckboxed = new ArrayList<>();

    public PrefTaskFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
    }

    public void chargeList(PreferenceCategory listCat, List<Task> list) {
        for (final Task task : list) {
            Preference pref = new Preference(mC);
            pref.setKey("task_" + task.getName());
            pref.setTitle(task.getName());

            String txt = task.getOccurance() + " time per " + (task.isDaily() ? "day" : "week") + " for " + (task.isCrossAccount() ? "the expedition" : "each character") + (task.getAppearance() != null && task.getAppearance().size() > 0 ? " only " + task.getAppearance().toString() : "");
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

    public void chargeCharList(PreferenceScreen listCatMain, List<Character> listChar) {
        for (Character character : listChar) {
            PreferenceCategory listCat = new PreferenceCategory(mC);
            listCat.setTitle(character.getId());
            listCatMain.addPreference(listCat);
            for (final Task task : character.getTasks()) {
                Preference pref = new Preference(mC);
                pref.setKey(character.getId() + "_task_" + task.getName());
                pref.setTitle(task.getName());

                String txt = task.getOccurance() + " time per " + (task.isDaily() ? "day" : "week") + (task.getAppearance() != null && task.getAppearance().size() > 0 ? " only " + task.getAppearance().toString() : "");
                pref.setSummary(txt);
                pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new AlertDialog.Builder(mC)
                                .setIcon(R.drawable.ic_warning_black_24dp)
                                .setTitle("Remove this task ?")
                                .setMessage("Do you really want to delete " + task.getName() + " for " + character.getId() + " ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (task.getId().equalsIgnoreCase("chaos_dungeon") || task.getId().equalsIgnoreCase("guardian_raid")) {
                                            tools.customToast(mC, task.getName() + " can't be deleted (Chaos and Guardians are baseline) !");
                                        } else {
                                            character.getTasks().remove(task);
                                            ExpeditionManager.getInstance(mC).saveToDB();
                                            mListener.onEvent();
                                            tools.customToast(mC, task.getName() + " deleted for " + character.getId() + " !");
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
    }


    public void createTask() {
        LayoutInflater inflater = mA.getLayoutInflater();
        final View creationView = inflater.inflate(R.layout.custom_toast_task_creation, null);
        CustomAlertDialog creationTaskAlert = new CustomAlertDialog(mA, mC, creationView);

        iconSelect = creationView.findViewById(R.id.icon_task_selector);
        iconSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupSelectIcon();
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

                    if (selectedDaysAppearance().size() > 0) {
                        task.setAppearance(selectedDaysAppearance());
                    }

                    if (selectedCharactersIds().size() > 0) {
                        if (!taskAlreadyExist(task, selectedCharactersIds())) {
                            MainActivity.expedition.createTaskForCharacters(task, selectedCharactersIds());
                            ExpeditionManager.getInstance(mC).saveToDB();

                            mListener.onEvent();
                            tools.customToast(mC, task.getName() + " created for " + selectedCharactersIds().toString());
                        } else {
                            tools.customToast(mC, task.getName() + " already present for character " + selectedCharactersIds().toString());
                        }

                    } else if (!taskAlreadyExist(task)) {
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

        ((CheckBox) creationView.findViewById(R.id.only_on_creation)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    advanceAppearance = new ArrayList<>();
                    creationView.findViewById(R.id.advance_days_scroll).setVisibility(View.VISIBLE);

                    List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
                    ((LinearLayout) creationView.findViewById(R.id.advance_days_line)).removeAllViews();
                    for (String day : days) {
                        CheckBox checkBox = new CheckBox(mC);
                        checkBox.setText(day);
                        ((LinearLayout) creationView.findViewById(R.id.advance_days_line)).addView(checkBox);
                        advanceAppearance.add(checkBox);
                    }
                } else {
                    creationView.findViewById(R.id.advance_days_scroll).setVisibility(View.GONE);
                }
            }
        });


        creationView.findViewById(R.id.only_for_creation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advanceCharacterLineCheckboxed = new ArrayList<>();
                creationView.findViewById(R.id.advance_character_scroll).setVisibility(View.VISIBLE);
                ((LinearLayout) creationView.findViewById(R.id.advance_character_line)).removeAllViews();
                for (Character character : MainActivity.expedition.getCharacters()) {
                    CheckBox checkBox = new CheckBox(mC);
                    checkBox.setText(character.getId());
                    ((LinearLayout) creationView.findViewById(R.id.advance_character_line)).addView(checkBox);
                    advanceCharacterLineCheckboxed.add(checkBox);
                }
            }
        });
        View.OnClickListener hideCharacterLine = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creationView.findViewById(R.id.advance_character_scroll).setVisibility(View.GONE);
            }
        };
        creationView.findViewById(R.id.per_character_creation).setOnClickListener(hideCharacterLine);
        creationView.findViewById(R.id.cross_account_creation).setOnClickListener(hideCharacterLine);
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


    private List<String> selectedCharactersIds() {
        List<String> ids = new ArrayList<>();
        for (CheckBox charCheck : advanceCharacterLineCheckboxed) {
            if (charCheck.isChecked()) {
                ids.add(charCheck.getText().toString());
            }
        }
        return ids;
    }

    private List<String> selectedDaysAppearance() {
        List<String> days = new ArrayList<>();
        for (CheckBox dayCheck : advanceAppearance) {
            if (dayCheck.isChecked()) {
                days.add(dayCheck.getText().toString());
            }
        }
        return days;
    }


    private void popupSelectIcon() {

        LayoutInflater inflater = mA.getLayoutInflater();
        final View selectionIconView = inflater.inflate(R.layout.custom_icon_selection, null);
        selectIconDialog = new CustomAlertDialog(mA, mC, selectionIconView);

        selectIconDialog.setPermanent(true);
        selectionIconView.findViewById(R.id.toast_list_backarrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectIconDialog.dismissAlert();
            }
        });

        GridLayout list = selectionIconView.findViewById(R.id.icons_list);
        selectionIconView.findViewById(R.id.android_icons).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAndroidIcons(list);
            }
        });

        initIconRef();

        loadIcons(list, allIcoRef);

        selectIconDialog.showAlert();
    }


    private void initIconRef() {
        if (allIcoRef == null || allIcoRef.size() < 1) {
            Field[] fields = R.drawable.class.getFields();
            allIcoRef = new ArrayList<>();
            for (int count = 0; count < fields.length; count++) {
                if (fields[count].getName().endsWith("_ico")) {
                    allIcoRef.add(fields[count].getName());
                }
            }
        }
    }

    private void loadIcons(GridLayout list, ArrayList<String> refsIds) {
        list.removeAllViews();

        for (String iconId : refsIds) {
            ImageView ico = new ImageView(mC);
            Drawable icoDraw = null;
            try {
                icoDraw = tools.getDrawable(mC, iconId);
            } catch (Exception e) {
                iconId = "mire_test";
                icoDraw = mC.getDrawable(R.drawable.mire_test);
            }
            if (icoDraw == null) {
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
                    iconSelect.setBackground(finalIcoDraw);
                    selectIconDialog.dismissAlert();
                }
            });
        }
    }


    private void loadAndroidIcons(GridLayout list) {
        Field[] fields = android.R.drawable.class.getFields();
        ArrayList<String> androidRefs = new ArrayList<>();
        for (int count = 0; count < fields.length; count++) {
            androidRefs.add(fields[count].getName());
        }
        loadIcons(list, androidRefs);
    }


    private boolean taskAlreadyExist(Task task) {
        for (Character character : MainActivity.expedition.getCharacters()) {
            for (Task common : character.getTasks()) {
                if (common.getId().equalsIgnoreCase(task.getId())) {
                    return true;
                }
            }
        }
        for (Task t : MainActivity.expedition.getExpeditionTasks()) {
            if (t.getId().equalsIgnoreCase(task.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean taskAlreadyExist(Task task, List<String> selectedCharactersIds) {
        for (String characterId : selectedCharactersIds) {
            Character character = MainActivity.expedition.getCharacterById(characterId);
            for (Task taskChar : character.getTasks()) {
                if (taskChar.getId().equalsIgnoreCase(task.getId())) {
                    return true;
                }
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

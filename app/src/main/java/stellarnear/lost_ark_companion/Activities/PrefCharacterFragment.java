package stellarnear.lost_ark_companion.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.Models.Character;
import stellarnear.lost_ark_companion.Models.ExpeditionManager;
import stellarnear.lost_ark_companion.R;


public class PrefCharacterFragment {

    private final Activity mA;
    private final Context mC;
    private final Tools tools = Tools.getTools();
    private OnRefreshEventListener mListener;

    public PrefCharacterFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
    }

    public void chargeList(PreferenceCategory otherList) {
        for (final Character c : MainActivity.expedition.getCharacters()) {
            Preference pref = new Preference(mC);
            pref.setKey("char_" + c.getName());
            pref.setTitle(c.getName());
            String txt = c.getWork() + " ilvl:" + c.getIlvl();

            pref.setSummary(txt);
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(mC)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle("Remove this character ?")
                            .setMessage("Do you really want to delete " + c.getName() + " ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.expedition.getCharacters().remove(c);
                                    ExpeditionManager.getInstance(mC).saveToDB();
                                    mListener.onEvent();
                                    tools.customToast(mC, c.getName() + " deleted !");
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
            otherList.addPreference(pref);
        }
    }

    public void createCharacter() {
        LayoutInflater inflater = mA.getLayoutInflater();
        final View creationView = inflater.inflate(R.layout.custom_toast_character_creation, null);
        CustomAlertDialog creationCharacterAlert = new CustomAlertDialog(mA, mC, creationView);

        creationCharacterAlert.setPermanent(true);
        creationCharacterAlert.addConfirmButton("Create");
        creationCharacterAlert.addCancelButton("Cancel");
        final Button work = creationView.findViewById(R.id.work_character_creation);
        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupSelectWork(work);
            }
        });

        creationCharacterAlert.setAcceptEventListener(new CustomAlertDialog.OnAcceptEventListener() {
            @Override
            public void onEvent() {
                String name = ((EditText) creationView.findViewById(R.id.name_character_creation)).getText().toString();
                String ilvl = ((EditText) creationView.findViewById(R.id.ilvl_character_creation)).getText().toString();
                String selectedWork = ((Button) creationView.findViewById(R.id.work_character_creation)).getText().toString();
                Character c = new Character(name, selectedWork, tools.toInt(ilvl));
                if (name.equalsIgnoreCase("") || selectedWork.contains("Choose")) {
                    tools.customToast(mC, "You should at least put a name select a class !");
                } else if (!charAlreadyExist(c)) {
                    MainActivity.expedition.createCharacter(c);
                    ExpeditionManager.getInstance(mC).saveToDB();

                    mListener.onEvent();
                    tools.customToast(mC, c.getName() + " created !");
                } else {
                    tools.customToast(mC, c.getName() + " already exists !");
                }

            }
        });
        creationCharacterAlert.showAlert();


        final EditText editName = creationView.findViewById(R.id.name_character_creation);
        editName.post(new Runnable() {
            public void run() {
                editName.setFocusableInTouchMode(true);
                editName.requestFocusFromTouch();
                InputMethodManager lManager = (InputMethodManager) mA.getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(editName, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void popupSelectWork(final Button work) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mA);
        builder.setTitle("Choose the class");
        // add a radio button list

        int checkedItem = -1;
        final List<String> works = Arrays.asList("Gunlancer", "Paladin", "SharpShooter", "Bard", "Berserker", "Spirit", "WarDancer", "DeathBlade", "Sorceress");
        Collections.sort(works);
        builder.setSingleChoiceItems((String[]) works.toArray(), checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                work.setText(works.get(which));
            }
        });

        builder.setPositiveButton("Ok", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean charAlreadyExist(Character c) {
        for (Character cExisting : MainActivity.expedition.getCharacters()) {
            if (cExisting.getId().equalsIgnoreCase(c.getId())) {
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

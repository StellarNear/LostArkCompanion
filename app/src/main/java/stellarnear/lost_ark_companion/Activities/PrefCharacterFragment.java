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
import android.widget.EditText;


public class PrefAllInventoryFragment {

    private Activity mA;
    private Context mC;
    private Tools tools=Tools.getTools();

    public PrefAllInventoryFragment(Activity mA,Context mC){
        this.mA=mA;
        this.mC=mC;
    }

    public void chargeList(PreferenceCategory otherList) {
        for (final Character char : MainActivity.expedition.getCharacters()) {
            Preference pref = new Preference(mC);
            pref.setKey("char_" + char.getName());
            pref.setTitle(char.getName());
            String txt = "ilvl:" + char.getIlvl();
            //todo add all charac
            pref.setSummary(txt);
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(mC)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle("Remove this character ?")
                            .setMessage("Do you really want to delete "+char.getName()+" ?" )
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.expedition.getCharacters().remove(char);
									ExpeditionManager.saveToDB();
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

    private void createCharacter() {
        LayoutInflater inflater = mA.getLayoutInflater();
        final View creationView = inflater.inflate(R.layout.custom_toast_equipment_creation, null);
        CustomAlertDialog creationEquipmentAlert = new CustomAlertDialog(mA,mC, creationView);


        creationView.findViewById(R.id.add_skill_create_item);
        creationEquipmentAlert.setPermanent(true);
        creationEquipmentAlert.addConfirmButton("Create");
        creationEquipmentAlert.addCancelButton("Cancel");
        creationEquipmentAlert.setAcceptEventListener(new CustomAlertDialog.OnAcceptEventListener() {
            @Override
            public void onEvent() {
                String name = ((EditText) creationView.findViewById(R.id.name_equipment_creation)).getText().toString();
                String ilvl = ((EditText) creationView.findViewById(R.id.value_equipment_creation)).getText().toString() + " po";
                String work = ((EditText) creationView.findViewById(R.id.descr_equipment_creation)).getText().toString();

                Character char = new Character(name, ilvl, work);
				MainActivity.expedition.createCharacter(char);
				ExpeditionManager.saveToDB();

                mListener.onEvent();
                tools.customToast(mC, equi.getName() + " created !");
            }
        });
        creationEquipmentAlert.showAlert();


        final EditText editName = ((EditText) creationView.findViewById(R.id.name_equipment_creation));
        editName.post(new Runnable() {
            public void run() {
                editName.setFocusableInTouchMode(true);
                editName.requestFocusFromTouch();
                InputMethodManager lManager = (InputMethodManager) mA.getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(editName, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }
}

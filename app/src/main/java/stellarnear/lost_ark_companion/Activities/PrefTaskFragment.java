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


public class PrefTaskFragment {

    private Activity mA;
    private Context mC;
    private Tools tools=Tools.getTools();

    public PrefTaskFragment(Activity mA,Context mC){
        this.mA=mA;
        this.mC=mC;
    }

    public void chargeList(PreferenceCategory listCat,List<Task> list) {
        for (final Task task : list) {
            Preference pref = new Preference(mC);
            pref.setKey("task_" + task.getName());
            pref.setTitle(task.getName());

			String txt = task.getOccurance()+" time per "+ task.isDaily()? "day":"week"+ " for "+task.isCrossAccount()?"the expedition":"each character";
            pref.setSummary(txt);
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(mC)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle("Remove this task ?")
                            .setMessage("Do you really want to delete "+task.getName()+" ?" )
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.expedition.deleteTask(task);
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
            listCat.addPreference(pref);
        }
    }

    private void createTask() {
        LayoutInflater inflater = mA.getLayoutInflater();
        final View creationView = inflater.inflate(R.layout.custom_toast_task_creation, null);
        CustomAlertDialog creationTaskAlert = new CustomAlertDialog(mA,mC, creationView);


        creationView.findViewById(R.id.add_skill_create_item);
        creationTaskAlert.setPermanent(true);
        creationTaskAlert.addConfirmButton("Create");
        creationTaskAlert.addCancelButton("Cancel");
        creationTaskAlert.setAcceptEventListener(new CustomAlertDialog.OnAcceptEventListener() {
            @Override
            public void onEvent() {
                String name = ((EditText) creationView.findViewById(R.id.name_task_creation)).getText().toString();

				bool daily = ((EditText) creationView.findViewById(R.id.daily_creation)).isSelected();
				bool crossAccount = ((EditText) creationView.findViewById(R.id.cross_account_creation)).isSelected();
				String occuranceTxt = ((EditText) creationView.findViewById(R.id.occurance_creation)).getText().toString();
				int occurance=Integer.parseInt(occuranceTxt);
                Task task = new Taks(daily,crossAccount, name, occurance);

				if(!taskAlreadyExist(task)){
					MainActivity.expedition.createTask(task);
					ExpeditionManager.saveToDB();

					mListener.onEvent();
					tools.customToast(mC, task.getName() + " created !");
				} else {
					tools.customToast(mC, task.getName() + " already present !");
				}

            }
        });
        creationTaskAlert.showAlert();


        final EditText editName = ((EditText) creationView.findViewById(R.id.name_task_creation));
        editName.post(new Runnable() {
            public void run() {
                editName.setFocusableInTouchMode(true);
                editName.requestFocusFromTouch();
                InputMethodManager lManager = (InputMethodManager) mA.getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(editName, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

	private bool taskAlreadyExist(Task task){
		for (Task common  : MainActivity.expedition.getCommonCharacterTasks()){
			if (common.getId().equalsIgnoreCase(task.getId())){
				return true;
			}
		}
		for (Task t : MainActivity.expedition.getExpeditionTasks()){
			if (t.getId().equalsIgnoreCase(task.getId())){
				return true;
			}
		}
		return false;
	}

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }
}

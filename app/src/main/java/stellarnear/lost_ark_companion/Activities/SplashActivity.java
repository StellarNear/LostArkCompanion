package stellarnear.lost_ark_companion.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import stellarnear.lost_ark_companion.Divers.Tools;


/**
 * Created by jchatron on 26/12/2017. updated with check download on 09/04/2020 and automatic download and install in 01/02/2021
 */


public class SplashActivity extends CustomActivity {
    public static final int progress_bar_type = 0;
    private final Tools tools = Tools.getTools();
    private ProgressDialog pDialog;

    @Override
    protected void doActivity() {
        startMainActivity();
    }

    @Override
    protected void onResumeActivity() {
        //nothing
    }

    @Override
    protected void onBackPressedActivity() {
        //nothing
    }

    @Override
    protected void onDestroyActivity() {
        //nothing
    }

    @Override
    protected boolean onOptionsItemSelectedActivity(MenuItem item) {
        return false;
    }

    @Override
    protected void onConfigurationChangedActivity() {
        //nothing
    }


    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}

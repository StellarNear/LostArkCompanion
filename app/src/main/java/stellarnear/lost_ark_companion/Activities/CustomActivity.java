package stellarnear.lost_ark_companion.Activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import stellarnear.lost_ark_companion.Log.CustomLog;
import stellarnear.lost_ark_companion.R;

public abstract class CustomActivity extends AppCompatActivity {
    protected transient CustomLog log = new CustomLog(this.getClass());
    protected transient SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (settings.getBoolean("switch_fullscreen_mode", getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_DEF))) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            super.onCreate(savedInstanceState);
            doActivity();
        } catch (Exception e) {
            log.fatal(this, "Error in activity : "+this.getLocalClassName(), e);
        }
    }

    abstract protected void doActivity() throws Exception;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            onResumeActivity();
        } catch (Exception e) {
            log.fatal(this,"Error in activity : "+ this.getLocalClassName(), e);
        }
    }

    protected abstract void onResumeActivity() throws Exception;

    @Override
    public void onBackPressed() {
        try {
            onBackPressedActivity();
        } catch (Exception e) {
            log.fatal(this, "Error in activity : "+this.getLocalClassName(), e);
        }
    }

    protected abstract void onBackPressedActivity() throws Exception;

    @Override
    protected void onDestroy() {
        try {
            onDestroyActivity();
        } catch (Exception e) {
            log.fatal(this, "Error in activity : "+this.getLocalClassName(), e);
        }
        super.onDestroy();
    }

    protected abstract void onDestroyActivity();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        boolean result = false;
        try {
            result = onOptionsItemSelectedActivity(item);
        } catch (Exception e) {
            log.fatal(this, "Error in activity : "+this.getLocalClassName(), e);
        }

        return result;
    }

    protected abstract boolean onOptionsItemSelectedActivity(MenuItem item) throws Exception;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        try {
            onConfigurationChangedActivity();
        } catch (Exception e) {
            log.fatal(this, "Error in activity : "+this.getLocalClassName(), e);
        }
    }

    protected abstract void onConfigurationChangedActivity();
}

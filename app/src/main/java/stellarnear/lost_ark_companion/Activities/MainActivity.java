package stellarnear.lost_ark_companion.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.Models.Expedition;
import stellarnear.lost_ark_companion.Models.ExpeditionManager;
import stellarnear.lost_ark_companion.Models.RefreshManager;
import stellarnear.lost_ark_companion.Models.TimeChecker;
import stellarnear.lost_ark_companion.R;


public class MainActivity extends AppCompatActivity {

    public static Expedition expedition = null;
    private FrameLayout mainFrameFrag;
    private SharedPreferences settings;
    private MainActivityFragment mainFrag = null;
    private TimeChecker timeChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.getBoolean("switch_fullscreen_mode", getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_DEF))) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);

        if (expedition == null) {
            Window window = getWindow();
            window.setStatusBarColor(getColor(R.color.start_back_color));
            expedition = ExpeditionManager.getInstance(getApplicationContext()).initExpeditionFromDB();
        }
    }

    private void buildMainPage() {
        setContentView(R.layout.activity_main);
        mainFrameFrag = findViewById(R.id.fragment_main_frame_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
        getSupportActionBar().show();
        startFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mainFrag != null) {
            RefreshManager.getRefreshManager().triggerRefresh();
        } else {
            buildMainPage();
        }
    }

    private void startFragment() {
        if (mainFrag == null) {
            mainFrag = new MainActivityFragment();
            timeChecker = TimeChecker.getInstance(getApplicationContext());
            int delay = Tools.getTools().toInt(settings.getString("checktimer_delay", String.valueOf(getApplicationContext().getResources().getInteger(R.integer.checktimer_delay_DEF))));

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (mainFrag.isAdded() && mainFrag.isVisible() && timeChecker.checkCurrentTime()) {
                        RefreshManager.getRefreshManager().triggerRefresh();
                    }
                    handler.postDelayed(this, 60 * delay * 1000);
                }
            }, 60 * delay * 1000);
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(mainFrameFrag.getId(), mainFrag);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        finish();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

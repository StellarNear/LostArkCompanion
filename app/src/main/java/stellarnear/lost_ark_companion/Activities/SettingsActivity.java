package stellarnear.lost_ark_companion.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import stellarnear.lost_ark_companion.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category pref shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity { //FOR ICON PICKER implements IconDialog.Callback {
    private SettingsFragment settingsFragment;

    //FOR ICON PICKER private IconPack iconPack=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.getBoolean("switch_fullscreen_mode", getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_DEF))) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsFragment = new SettingsFragment();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();

    }

    //
    // Handle what happens on up button
    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                settingsFragment.onUpButton();

                return true;
        }
        return true;
    }

    /*FOR ICON PICKER

    private void showPicker(){
        IconDialog dialog = (IconDialog) getSupportFragmentManager().findFragmentByTag("icon-dialog");
        IconDialogSettings.Builder builder = new IconDialogSettings.Builder();
        builder.setMaxSelection(1);
        IconDialog iconDialog = dialog != null ? dialog
                : IconDialog.newInstance(builder.build());

            iconDialog.show(getSupportFragmentManager(),"icon-dialog");
    }

    @Nullable
    @Override
    public IconPack getIconDialogIconPack() {
        return  iconPack != null ? iconPack : loadIconPack();
    }



    private IconPack loadIconPack() {
        // Create an icon pack loader with application context.
        IconPackLoader loader = new IconPackLoader(this);

        // Create an icon pack and load all drawables.
        if(iconPack==null) {
            iconPack = IconPackDefault.createDefaultIconPack(loader);
            iconPack.loadDrawables(loader.getDrawableLoader());
            iconPack = loader.load(R.xml.lost_ark_icons,R.xml.lost_ark_icons_tags, Collections.singletonList(Locale.ENGLISH),iconPack);
        }
        return iconPack;
    }

    @Override
    public void onIconDialogIconsSelected(@NonNull IconDialog dialog, @NonNull List<Icon> icons) {
        // Show a toast with the list of selected icon IDs.
        StringBuilder sb = new StringBuilder();
        for (Icon icon : icons) {
            sb.append(icon.getId());
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        Toast.makeText(this, "Icons selected: " + sb, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIconDialogCancelled() {}

     */
    @Override
    public void onBackPressed() {
        settingsFragment.onUpButton();
    }

    @Override
    protected void onDestroy() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        finish();
        super.onDestroy();
    }


}
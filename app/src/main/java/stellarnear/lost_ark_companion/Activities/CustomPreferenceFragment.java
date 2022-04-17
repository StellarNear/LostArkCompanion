package stellarnear.lost_ark_companion.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import stellarnear.lost_ark_companion.Log.CustomLog;


public abstract class CustomPreferenceFragment extends PreferenceFragment {
    public CustomLog log = new CustomLog(this.getClass());
    protected SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            onCreateFragment();
        } catch (Exception e) {
            log.fatal(getContext(), e.getMessage(), e);
        }
    }

    protected abstract void onCreateFragment();

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        try {
            onPreferenceTreeClickFragment(preferenceScreen, preference);
        } catch (Exception e) {
            log.fatal(getContext(), e.getMessage(), e);
        }
        return true;
    }

    protected abstract void onPreferenceTreeClickFragment(PreferenceScreen preferenceScreen, Preference preference) throws Exception;

    @Override
    public void onDestroy() {
        try {
            onDestroyFragment();
        } catch (Exception e) {
            log.fatal(getContext(), e.getMessage(), e);
        }
        super.onDestroy();
    }

    protected abstract void onDestroyFragment();
}

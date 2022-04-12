package stellarnear.lost_ark_companion.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

public class SettingsFragment extends CustomPreferenceFragment {
    private final List<String> histoPrefKeys = new ArrayList<>();
    private final List<String> histoTitle = new ArrayList<>();
    private final Tools tools = Tools.getTools();
    private Activity mA;
    private Context mC;
    private String currentPageKey;
    private String currentPageTitle;
    private PrefInfoScreenFragment prefInfoScreenFragment;
    private PrefCharacterFragment prefCharactersFragment;
    private PrefTaskFragment prefTaskFragment;

    @Override
    protected void onCreateFragment() {
        this.mA = getActivity();
        this.mC = getContext();
        addPreferencesFromResource(R.xml.pref);
        prefInfoScreenFragment = new PrefInfoScreenFragment(mA, mC);
        prefCharactersFragment = new PrefCharacterFragment(mA, mC);
        prefCharactersFragment.setRefreshEventListener(new PrefCharacterFragment.OnRefreshEventListener() {
            @Override
            public void onEvent() {
                navigate();
            }
        });
        prefTaskFragment = new PrefTaskFragment(mA, mC);
        prefTaskFragment.setRefreshEventListener(new PrefTaskFragment.OnRefreshEventListener() {
            @Override
            public void onEvent() {
                navigate();
            }
        });
        this.histoPrefKeys.add("pref");
        this.histoTitle.add(getResources().getString(R.string.setting_activity));
    }

    // will be called by SettingsActivity (Host Activity)
    public void onUpButton() {
        if (histoPrefKeys.get(histoPrefKeys.size() - 1).equalsIgnoreCase("pref") || histoPrefKeys.size() <= 1) // in top-level
        {
            Intent intent = new Intent(mA, MainActivity.class);// Switch to MainActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            mA.startActivity(intent);
        } else // in sub-level
        {
            currentPageKey = histoPrefKeys.get(histoPrefKeys.size() - 2);
            currentPageTitle = histoTitle.get(histoTitle.size() - 2);
            navigate();
            histoPrefKeys.remove(histoPrefKeys.size() - 1);
            histoTitle.remove(histoTitle.size() - 1);
        }
    }

    @Override
    protected void onPreferenceTreeClickFragment(PreferenceScreen preferenceScreen, Preference preference) throws Exception {
        if (preference.getKey().contains("pref_")) {
            histoPrefKeys.add(preference.getKey());
            histoTitle.add(preference.getTitle().toString());
        }

        if (preference.getKey().startsWith("pref")) {
            this.currentPageKey = preference.getKey();
            this.currentPageTitle = preference.getTitle().toString();
            navigate();
        } else {
            action(preference);
        }
              /*
        // Top level PreferenceScreen
        if (key.equals("top_key_0")) {         changePrefScreen(R.xml.pref_general, preference.getTitle().toString()); // descend into second level    }

        // Second level PreferenceScreens
        if (key.equals("second_level_key_0")) {        // do something...    }       */
    }

    @Override
    protected void onDestroyFragment() {

    }


    private void navigate() {
        if (currentPageKey.equalsIgnoreCase("pref")) {
            getPreferenceScreen().removeAll();
            addPreferencesFromResource(R.xml.pref);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentPageTitle);
        } else if (currentPageKey.contains("pref_")) {
            loadPage();
            switch (currentPageKey) {
                case "pref_characters":
                    PreferenceCategory list = (PreferenceCategory) findPreference("characters_list");
                    prefCharactersFragment.chargeList(list);
                    break;
                case "pref_tasks":
                    PreferenceCategory listExpeTask = (PreferenceCategory) findPreference("expedition_tasks");
                    prefTaskFragment.chargeList(listExpeTask, MainActivity.expedition.getExpeditionTasks());
                    PreferenceCategory listCharTask = (PreferenceCategory) findPreference("common_character_tasks");
                    prefTaskFragment.chargeList(listCharTask, MainActivity.expedition.getCommonCharacterTasks());
                    PreferenceCategory listCharDetail = (PreferenceCategory) findPreference("detail_character_tasks");
                    prefTaskFragment.chargeCharList(getPreferenceScreen(), MainActivity.expedition.getCharacters());
                    break;
            }
        }
    }

    private void loadPage() {
        getPreferenceScreen().removeAll();
        int xmlID = getResources().getIdentifier(currentPageKey, "xml", getContext().getPackageName());
        addPreferencesFromResource(xmlID);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentPageTitle);
    }

    private void action(Preference preference) throws Exception {
        switch (preference.getKey()) {
            case "infos":
                prefInfoScreenFragment.showInfo();
                break;
            case "create_task":
                prefTaskFragment.createTask();
                break;
            case "create_character":
                prefCharactersFragment.createCharacter();
                break;
        }
    }
}
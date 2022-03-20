package stellarnear.lost_ark_companion.Activities;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.Models.Character;
import stellarnear.lost_ark_companion.Models.ElementTaskDisplay;
import stellarnear.lost_ark_companion.Models.OneLineDisplayCharacter;
import stellarnear.lost_ark_companion.Models.Task;
import stellarnear.lost_ark_companion.Models.TimeChecker;
import stellarnear.lost_ark_companion.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    View returnFragView;
    private Tools tools=Tools.getTools();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }

        returnFragView = inflater.inflate(R.layout.fragment_main, container, false);

	    buildFrag();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(settings.getBoolean("test_mode", getContext().getResources().getBoolean(R.bool.test_mode_DEF))){
            returnFragView.findViewById(R.id.fake_test).setVisibility(View.VISIBLE);
            ((Button) returnFragView.findViewById(R.id.test_pass_day)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimeChecker.getInstance(getContext()).cheatPassDay(1);
                    tools.customToast(getContext(),"Passing one day...");
                    buildFrag();
                }
            });
            ((Button) returnFragView.findViewById(R.id.test_pass_2_days)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimeChecker.getInstance(getContext()).cheatPassDay(2);
                    tools.customToast(getContext(),"Passing two day...");
                    buildFrag();
                }
            });
        } else {
            returnFragView.findViewById(R.id.fake_test).setVisibility(View.GONE);
        }

        return returnFragView;
    }

    private void buildFrag() {
        LinearLayout expeLine = returnFragView.findViewById(R.id.expe_tasks);
        ElementTaskDisplay elementLiner = new ElementTaskDisplay(getContext());

        expeLine.removeAllViews();
        for(Task task : MainActivity.expedition.getExpeditionTasks()){
            expeLine.addView(elementLiner.getTaskElement(task));
        }

        LinearLayout grid = returnFragView.findViewById(R.id.characters_grid);
        int delay = 500;
        OneLineDisplayCharacter oneLiner = new OneLineDisplayCharacter(getContext());
        grid.removeAllViews();
        for (Character c : MainActivity.expedition.getCharacters()) {
            View line = oneLiner.getOneLine(c);
            grid.addView(line);
            Animation right = AnimationUtils.loadAnimation(getContext(), R.anim.infromright);
            right.setStartOffset(delay);
            line.startAnimation(right);
            delay += 500;
        }
    }

}
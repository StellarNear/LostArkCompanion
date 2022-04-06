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
import android.widget.LinearLayout;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.Models.Character;
import stellarnear.lost_ark_companion.Models.ElementTask;
import stellarnear.lost_ark_companion.Models.ElementTaskDisplay;
import stellarnear.lost_ark_companion.Models.ElementTaskDisplayCompact;
import stellarnear.lost_ark_companion.Models.OneLineDisplay;
import stellarnear.lost_ark_companion.Models.OneLineDisplayCharacter;
import stellarnear.lost_ark_companion.Models.OneLineDisplayCharacterCompact;
import stellarnear.lost_ark_companion.Models.RefreshManager;
import stellarnear.lost_ark_companion.Models.Task;
import stellarnear.lost_ark_companion.Models.TimeChecker;
import stellarnear.lost_ark_companion.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final Tools tools = Tools.getTools();
    private View returnFragView;
    private SharedPreferences settings;

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
        settings = PreferenceManager.getDefaultSharedPreferences(getContext());

        buildFrag();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (settings.getBoolean("test_mode", getContext().getResources().getBoolean(R.bool.test_mode_DEF))) {
            returnFragView.findViewById(R.id.fake_test).setVisibility(View.VISIBLE);
            returnFragView.findViewById(R.id.test_pass_day).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimeChecker.getInstance(getContext()).cheatPassDay(1);
                    tools.customToast(getContext(), "Passing one day...");
                }
            });
            returnFragView.findViewById(R.id.test_pass_2_days).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimeChecker.getInstance(getContext()).cheatPassDay(2);
                    tools.customToast(getContext(), "Passing two day...");
                }
            });
            returnFragView.findViewById(R.id.test_check_time).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tools.customToast(getContext(), "checking time...");
                    if (TimeChecker.getInstance(getContext()).checkCurrentTime()) {
                        RefreshManager.getRefreshManager().triggerRefresh();
                    }
                }
            });
        } else {
            returnFragView.findViewById(R.id.fake_test).setVisibility(View.GONE);
        }

        return returnFragView;
    }

    public void buildFrag() {
        LinearLayout expeLine = returnFragView.findViewById(R.id.expe_tasks);
        ElementTask elementLiner;
        if (settings.getBoolean("compact_mode", getContext().getResources().getBoolean(R.bool.compact_mode_DEF))) {
            elementLiner = new ElementTaskDisplayCompact(getContext());
        } else {
            elementLiner = new ElementTaskDisplay(getContext());
        }

        expeLine.removeAllViews();
        for (Task task : MainActivity.expedition.getExpeditionTasks()) {
            View elementTask = elementLiner.getTaskElement(task);
            elementTask.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            expeLine.addView(elementTask);
        }

        LinearLayout grid = returnFragView.findViewById(R.id.characters_grid);
        grid.post(new Runnable() {
            @Override
            public void run() {
                int delay = 50;

                OneLineDisplay oneLiner;

                if (settings.getBoolean("compact_mode", getContext().getResources().getBoolean(R.bool.compact_mode_DEF))) {
                    oneLiner = new OneLineDisplayCharacterCompact(getContext());
                } else {
                    oneLiner = new OneLineDisplayCharacter(getContext());
                }

                grid.removeAllViews();

                boolean needAnimation = !RefreshManager.getRefreshManager().wasAnimated();
                for (Character c : MainActivity.expedition.getCharacters()) {
                    View line = oneLiner.getOneLine(c);
                    grid.addView(line);

                    if (needAnimation) {
                        Animation right = AnimationUtils.loadAnimation(getContext(), R.anim.infromright);
                        right.setStartOffset(delay);
                        line.startAnimation(right);
                        delay += 500;
                    }
                }
                RefreshManager.getRefreshManager().isAnimated();
            }
        });

        RefreshManager.getRefreshManager().setRefreshEventListener(new RefreshManager.OnRefreshEventListener() {
            @Override
            public void onEvent() {
                buildFrag();
            }
        });
    }

}

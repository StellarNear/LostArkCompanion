package stellarnear.lost_ark_companion.Activities;

import android.app.Fragment;
import android.content.Context;
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
    private Context mC;

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
        mC = getContext();
        settings = PreferenceManager.getDefaultSharedPreferences(mC);

        buildFrag();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (settings.getBoolean("test_mode", mC.getResources().getBoolean(R.bool.test_mode_DEF))) {
            returnFragView.findViewById(R.id.fake_test).setVisibility(View.VISIBLE);
            returnFragView.findViewById(R.id.test_pass_day).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimeChecker.getInstance(mC).cheatPassDay(1);
                    tools.customToast(mC, "Passing one day...");
                }
            });
            returnFragView.findViewById(R.id.test_pass_2_days).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimeChecker.getInstance(mC).cheatPassDay(2);
                    tools.customToast(mC, "Passing two day...");
                }
            });
            returnFragView.findViewById(R.id.test_check_time).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tools.customToast(mC, "checking time...");
                    if (TimeChecker.getInstance(mC).checkCurrentTime()) {
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
        if (settings.getBoolean("compact_mode", mC.getResources().getBoolean(R.bool.compact_mode_DEF))) {
            elementLiner = new ElementTaskDisplayCompact(mC);
        } else {
            elementLiner = new ElementTaskDisplay(mC);
        }

        expeLine.removeAllViews();
        for (Task task : MainActivity.expedition.getExpeditionTasks()) {
            if (task.getAppearance() == null || TimeChecker.getInstance(mC).isThatDay(task.getAppearance())) {
                View elementTask = elementLiner.getTaskElement(task);
                elementTask.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                expeLine.addView(elementTask);
            }
        }

        LinearLayout grid = returnFragView.findViewById(R.id.characters_grid);
        grid.post(new Runnable() {
            @Override
            public void run() {
                int delay = 50;

                OneLineDisplay oneLiner;

                if (settings.getBoolean("compact_mode", mC.getResources().getBoolean(R.bool.compact_mode_DEF))) {
                    oneLiner = new OneLineDisplayCharacterCompact(mC);
                } else {
                    oneLiner = new OneLineDisplayCharacter(mC);
                }

                grid.removeAllViews();

                boolean needAnimation = !RefreshManager.getRefreshManager().wasAnimated();
                for (Character c : MainActivity.expedition.getCharacters()) {
                    View line = oneLiner.getOneLine(c);
                    grid.addView(line);

                    if (needAnimation) {
                        Animation right = AnimationUtils.loadAnimation(mC, R.anim.infromright);
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

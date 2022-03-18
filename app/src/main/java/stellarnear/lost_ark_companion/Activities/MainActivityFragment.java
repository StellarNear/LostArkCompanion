package stellarnear.lost_ark_companion.Activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;

import stellarnear.lost_ark_companion.Models.Character;
import stellarnear.lost_ark_companion.Models.OneLineDisplayCharacter;
import stellarnear.lost_ark_companion.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    View returnFragView;

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

        GridLayout grid = returnFragView.findViewById(R.id.characters_grid);
        int delay = 0;
        OneLineDisplayCharacter oneLiner = new OneLineDisplayCharacter(getContext());
        for (Character c : MainActivity.expedition.getCharacters()) {
            View line = oneLiner.getOneLine(c);
            grid.addView(line);
            Animation right = AnimationUtils.loadAnimation(getContext(), R.anim.infromright);
            right.setStartOffset(delay);
            line.startAnimation(right);
            delay += 0.5;
        }

        return returnFragView;
    }

}

package stellarnear.lost_ark_companion.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;


/**
 * Created by jchatron on 26/12/2017. updated with check download on 09/04/2020 and automatic download and install in 01/02/2021
 */


public class SplashActivity extends CustomActivity {
    public static final int progress_bar_type = 0;
    private final Tools tools = Tools.getTools();
    private ProgressDialog pDialog;

    @Override
    protected void doActivity() {

        Animation in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.infade);
        Animation out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.outfade);
        in.setStartOffset(500);
        out.setStartOffset(500);

        Animation inDel = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.infade);
        Animation outDel = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.outfade);

        setContentView(R.layout.splash);


        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                findViewById(R.id.splash_under1).startAnimation(outDel);
                findViewById(R.id.splash_above1).startAnimation(outDel);
                findViewById(R.id.splash_above2).startAnimation(inDel);
            }
        });

        inDel.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                startMainActivity();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                findViewById(R.id.splash_above1).startAnimation(in);
            }
        }, 500);

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

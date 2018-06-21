package su.leff.metronome.Activity;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;

import su.leff.metronome.Adapter.DrumSoundAdapter;
import su.leff.metronome.Class.DrumSound;
import su.leff.metronome.R;

import static java.security.AccessController.getContext;

public class Metronome extends AppCompatActivity {


    private SoundPool mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

    RecyclerView recyclerView;

    SoundPool soundPool;
    DrumSoundAdapter drumSoundAdapter;
    int soundToPlay;
    long delay;
    boolean status = false;
//    private AdView mAdView;

    final Handler handler = new Handler();
    final Handler handlerFab = new Handler();
    final Handler handlerDrum = new Handler();

    RippleBackground rippleBackgroundMinus;
    RelativeLayout rippleLayoutMinus;
    RippleBackground rippleBackgroundPlus;
    RelativeLayout rippleLayoutPlus;
    RippleBackground rippleBackgroundFab;
    FloatingActionButton fabPlayPause;

    RelativeLayout controls;

    MaterialEditText bpm_edt;

    int bpm = 140;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metronome);

        setTitle(getResources().getString(R.string.metronome));

        recyclerView = findViewById(R.id.RecyclerView);

        ArrayList<DrumSound> drumSounds = new ArrayList<>();

        drumSounds.add(new DrumSound("Drum Acoustic Hat", R.raw.drum_acoustic_hat_1, "Usual hat"));
        drumSounds.add(new DrumSound("Drum Acoustic Kick 1", R.raw.drum_acoustic_kick_1, "Usual kick"));
        drumSounds.add(new DrumSound("Drum Acoustic Kick 2", R.raw.drum_acoustic_kick_2, "Usual kick"));
        drumSounds.add(new DrumSound("Drum Acoustic Kick 3", R.raw.drum_acoustic_kick_3, "Usual kick"));
        drumSounds.add(new DrumSound("Drum Acoustic Snare", R.raw.drum_acoustic_snare_1, "Usual snare"));
        drumSounds.add(new DrumSound("Drum Acoustic Sonor Force Snare", R.raw.drum_acoustic_sonor_force_snare, "Usual snare"));
        drumSounds.add(new DrumSound("Drum Acoustic Zuljin Hat", R.raw.drum_acoustic_zuljin_hat_1, "Usual hat"));
        drumSounds.add(new DrumSound("Metronome Click", R.raw.drum_click, "Usual click"));
        drumSounds.add(new DrumSound("Drum Electric Hat", R.raw.drum_electric_hat_1, "Usual hat"));
        drumSounds.add(new DrumSound("Drum Electric Tom", R.raw.drum_electric_tom, "Electric Tom that sounds like a cheap bass drum"));
        drumSounds.add(new DrumSound("Drum Pearl Piccolo Side Snare", R.raw.drum_pearl_piccolo_side_snare_1, "wtf is this"));

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

//        mAdView = view.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        drumSoundAdapter = new DrumSoundAdapter(drumSounds, this, Metronome.this);

        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, OrientationHelper.VERTICAL, false));

        recyclerView.setAdapter(drumSoundAdapter);

        controls = findViewById(R.id.controls);

        rippleBackgroundMinus = findViewById(R.id.ripple_minus);
        rippleLayoutMinus = findViewById(R.id.minus_bpm);

        rippleBackgroundPlus = findViewById(R.id.ripple_plus);
        rippleLayoutPlus = findViewById(R.id.plus_bpm);

        rippleLayoutMinus.setOnClickListener(rippleMinusListener);
        rippleLayoutPlus.setOnClickListener(ripplePlusListener);

        bpm_edt = findViewById(R.id.edt_bpm);
        bpm_edt.addTextChangedListener(textWatcher);

        rippleBackgroundFab = findViewById(R.id.ripple_fab);
        fabPlayPause = findViewById(R.id.fab_play_pause);

        fabPlayPause.setOnClickListener(fabButtonListener);

        soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 1);
        soundToPlay = soundPool.load(this, R.raw.drum_acoustic_hat_1, 1);

//        launchSound();
    }

    public void updateSoundResource(int soundResource) {
        soundToPlay = soundPool.load(this, soundResource, 1);
    }

    long getDelay(int bpm) {
        return 60000 / bpm;
    }

    private void stopSound() {
        handlerDrum.removeCallbacksAndMessages(null);
    }

    private void launchSound() {
        thread.start();
    }

    private void updateBpmEditText() {
        bpm_edt.setText(String.valueOf(bpm));
    }

    private void getBpmFromEditText() {
        try {
            bpm = Integer.parseInt(String.valueOf(bpm_edt.getText()));
        } catch (NumberFormatException e) {
            Snackbar snackbar;
            if (bpm_edt.getText().length() > 0) {
                snackbar = Snackbar
                        .make(controls, getResources().getString(R.string.bpm_error), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    View.OnClickListener fabButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (status) {
                status = false;
                stopSound();
                runOnUiThread(punchFabPauseRipple);
                fabPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
            } else {
                status = true;
                launchSound();
                fabPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            }
        }
    };

    Runnable punchFab = new Runnable() {
        @Override
        public void run() {
            rippleBackgroundFab.startRippleAnimation();
        }
    };

    Runnable punchFabPauseRipple = new Runnable() {
        @Override
        public void run() {
            rippleBackgroundFab.stopRippleAnimation();
        }
    };

    Thread thread = new Thread() {
        @Override
        public void run() {
            runOnUiThread(punchFabPauseRipple);
            soundPool.play(soundToPlay, 1, 1, 1, 0, 1);
            runOnUiThread(punchFab);
            handlerDrum.postDelayed(thread, getDelay(bpm));

        }
    };

    View.OnClickListener ripplePlusListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            bpm_edt.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(bpm_edt.getWindowToken(), 0);
            bpm += 5;
            updateBpmEditText();
            rippleBackgroundPlus.startRippleAnimation();
            handler.postDelayed(runnablePlus, 200);
        }
    };

    final Runnable runnablePlus = new Runnable() {
        public void run() {
            rippleBackgroundPlus.stopRippleAnimation();
        }
    };

    View.OnClickListener rippleMinusListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            bpm_edt.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(bpm_edt.getWindowToken(), 0);
            if (bpm > 5) {
                bpm -= 5;
                updateBpmEditText();
                rippleBackgroundMinus.startRippleAnimation();
                handler.postDelayed(runnableMinus, 200);
            }
        }
    };

    final Runnable runnableMinus = new Runnable() {
        public void run() {
            rippleBackgroundMinus.stopRippleAnimation();
        }
    };

    View.OnClickListener rippleFabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            rippleBackgroundFab.startRippleAnimation();
            handlerFab.postDelayed(runnableFab, 200);
        }
    };

    final Runnable runnableFab = new Runnable() {
        @Override
        public void run() {
            rippleBackgroundFab.stopRippleAnimation();
        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getBpmFromEditText();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}

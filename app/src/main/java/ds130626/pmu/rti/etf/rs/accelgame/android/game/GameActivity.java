package ds130626.pmu.rti.etf.rs.accelgame.android.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import ds130626.pmu.rti.etf.rs.accelgame.R;
import ds130626.pmu.rti.etf.rs.accelgame.android.LevelLoader;
import ds130626.pmu.rti.etf.rs.accelgame.android.PixelConverter;
import ds130626.pmu.rti.etf.rs.accelgame.android.SimpleLevelLoader;
import ds130626.pmu.rti.etf.rs.accelgame.android.build.LevelWrapper;
import ds130626.pmu.rti.etf.rs.accelgame.android.score.DatabaseScoreLoader;
import ds130626.pmu.rti.etf.rs.accelgame.android.score.ScoreLoader;
import ds130626.pmu.rti.etf.rs.accelgame.android.score.ScoresActivity;
import ds130626.pmu.rti.etf.rs.accelgame.controller.GameEventListener;
import ds130626.pmu.rti.etf.rs.accelgame.controller.InstanceController;
import ds130626.pmu.rti.etf.rs.accelgame.model.GameInstance;

public class GameActivity extends AppCompatActivity implements GameEventListener, SensorEventListener, View.OnTouchListener {
    public static final String KEY_LEVEL_NAME = "ds130626.pmu.rti.etf.rs.accelgame.android.game.KEY_LEVEL_NAME";
    public static final String KEY_SAVED_INSTANCE_GAME = "ds130626.pmu.rti.etf.rs.accelgame.android.game.savedInstanceGame";
    public static final String KEY_SAVED_INSTANCE_VIEWPROPS = "ds130626.pmu.rti.etf.rs.accelgame.android.game.viewPropsSavedInstance";
    GameImageView view;
    GameInstance gameInstance;
    Map<Integer, Map<String, Object>> levelViewProperties;
    InstanceController controller;
    Filter filter;

    SensorManager sensorManager;
    Sensor accelerometer;

    ScoreLoader scoreLoader;


    GameSoundsController gameSoundsController;

    boolean applyFilter;
    boolean playSounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);

        view = (GameImageView) findViewById(R.id.gameView);

        LevelLoader loader = new SimpleLevelLoader(getApplicationContext());
        String levelName = getIntent().getStringExtra(KEY_LEVEL_NAME);
        LevelWrapper levelWrapper = loader.load(levelName);
        gameInstance = new GameInstance(levelWrapper.getLevel(), view, this, PreferenceManager.getDefaultSharedPreferences(this).getAll());
        levelViewProperties = levelWrapper.getLevelViewProperties();

        view.setOnTouchListener(this);
        scoreLoader = new DatabaseScoreLoader(getApplicationContext());
        controller = gameInstance.createController();

        view.setGameInstance(gameInstance, levelViewProperties);
        view.setPixelConverter(new PixelConverter(getResources().getDisplayMetrics().density));

        filter = new MovingAverage(3, 50);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        gameSoundsController = new GameSoundsController(this);

        applyFilter = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("key_filter_input", false);
        playSounds = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("key_game_sounds", true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.pause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    controller.acceptInput();
                }
            }, 500);
        }
    }

    @Override
    public void onGameOver(boolean outcome, long score) {
        if (outcome) {
            if (playSounds) gameSoundsController.playSound(R.raw.success);
            showInputNameDialog(score);
        } else {
            if (playSounds) gameSoundsController.playSound(R.raw.failure);
            showYouLoseDialog();
        }
    }

    private void showYouLoseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You lose!");
        builder.setMessage("Better luck next time!");
        builder.setCancelable(false);
        builder.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                controller.restart();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void showInputNameDialog(final long time) {
        final EditText editText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your time: " + String.format(Locale.getDefault(), "%.2f", time / 1000f));
        builder.setMessage("Your name:");
        builder.setView(editText);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.show();

        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                if (name.isEmpty()) {
                    editText.requestFocus();
                    Toast.makeText(GameActivity.this, "Input your name", Toast.LENGTH_SHORT).show();
                    return;
                }
                scoreLoader.put(time, name, getIntent().getStringExtra(KEY_LEVEL_NAME));
                Intent i = new Intent(GameActivity.this, ScoresActivity.class);
                i.putExtra(KEY_LEVEL_NAME, getIntent().getStringExtra(KEY_LEVEL_NAME));
                startActivity(i);
                finish();
                dialog.dismiss();
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GameActivity.this, ScoresActivity.class);
                i.putExtra(KEY_LEVEL_NAME, getIntent().getStringExtra(KEY_LEVEL_NAME));
                startActivity(i);
                finish();
                dialog.cancel();
            }
        });
    }

    @Override
    public void onObstacleHit() {
        if (playSounds) gameSoundsController.playSound(R.raw.sound_bounce);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.i("RAW", sensorEvent.values[0]+" "+sensorEvent.values[1]+" "+sensorEvent.values[2]);
        if (applyFilter) {
            filter.filter(sensorEvent.values);
        }
        controller.update(-sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], sensorEvent.timestamp / 1000000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        return true;
    }
}

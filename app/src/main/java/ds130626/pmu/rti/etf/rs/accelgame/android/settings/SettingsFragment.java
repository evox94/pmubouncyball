package ds130626.pmu.rti.etf.rs.accelgame.android.settings;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ds130626.pmu.rti.etf.rs.accelgame.R;
import ds130626.pmu.rti.etf.rs.accelgame.model.GameInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SensorEventListener {
    private SensorManager sensorManager;
    private List<Float> axAvg;
    private List<Float> ayAvg;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        axAvg = new ArrayList<>();
        ayAvg = new ArrayList<>();
        addPreferencesFromResource(R.xml.preference);
        Preference preference = getPreferenceManager().findPreference("ds130626.pmu.rti.etf.rs.accelgame.calibrate");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showCalibrationDialog();
                return true;
            }
        });
    }

    private void showCalibrationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Calibration");
        builder.setMessage("Lay the device flat on its back and then press the START button");
        builder.setPositiveButton("START", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        final AlertDialog dialog = builder.show();
        final Button pos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        final Button neg = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        neg.setVisibility(View.GONE);
        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(SettingsFragment.this, sensor, SensorManager.SENSOR_DELAY_GAME);
                ayAvg.clear();
                axAvg.clear();
                dialog.setMessage("Please wait...");
                (new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                unregisterListener();
                                float ax = calcAvg(axAvg);
                                float ay = calcAvg(ayAvg);
                                saveToSharedPrefs(ax,ay);
                                axAvg.clear();
                                ayAvg.clear();
                                dialog.setMessage("axOffset ="+ax+ " ayOffset="+ay);
                                pos.setVisibility(View.GONE);
                                neg.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                }).start();
            }
        });
    }

    private void saveToSharedPrefs(float ax, float ay) {
        SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
        editor.putFloat(GameInstance.Constants.KEY_AX_OFFSET, ax);
        editor.putFloat(GameInstance.Constants.KEY_AY_OFFSET, -ay);
        editor.apply();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        axAvg.add(sensorEvent.values[0]);
        ayAvg.add(sensorEvent.values[1]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void unregisterListener(){
        sensorManager.unregisterListener(this);
    }

    private float calcAvg(List<Float> list){
        float sum = 0;
        for(Float x: list){
            sum+=x;
        }
        return sum/list.size();
    }
}

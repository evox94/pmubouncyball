package ds130626.pmu.rti.etf.rs.accelgame.android.game;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ds130626.pmu.rti.etf.rs.accelgame.R;

/**
 * Created by smiljan on 2/9/17.
 */

public class GameSoundsController{
    private Context context;
    private SoundPool soundPool;
    private Map<Integer, Integer> sounds;

    public GameSoundsController(Context context){
        this.context = context;
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sounds = new Hashtable<>();
        sounds.put(R.raw.sound_bounce, soundPool.load(context, R.raw.sound_bounce, 0));
        sounds.put(R.raw.success, soundPool.load(context, R.raw.success, 0));
        sounds.put(R.raw.failure, soundPool.load(context, R.raw.failure, 0));
    }

    public void playSound(final int resourceId){
        if(resourceId == R.raw.sound_bounce){
            soundPool.play(sounds.get(resourceId),0.05f,0.05f,0,0,1);
        }else{
            soundPool.play(sounds.get(resourceId),1,1,0,0,1);
        }
    }

}

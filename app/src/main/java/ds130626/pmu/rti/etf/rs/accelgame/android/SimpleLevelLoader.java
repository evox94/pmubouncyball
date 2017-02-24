package ds130626.pmu.rti.etf.rs.accelgame.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import ds130626.pmu.rti.etf.rs.accelgame.android.build.LevelWrapper;

/**
 * Created by smiljan on 2/12/17.
 */

public class SimpleLevelLoader implements LevelLoader {
    Context context;

    public SimpleLevelLoader(Context context) {
        this.context = context;
    }

    @Override
    public void save(LevelWrapper wrapper) {
        File dir = context.getExternalFilesDir(null);
        File levelFile = new File(dir,wrapper.getLevel().getName()+".level");
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(levelFile, false));
            oos.writeObject(wrapper);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File levelImage = new File(dir, wrapper.getLevel().getName()+".png");
        try {
            OutputStream os = new FileOutputStream(levelImage,false);
            wrapper.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LevelWrapper load(String name) {
        File dir = context.getExternalFilesDir(null);
        File level = new File(dir, name+".level");
        LevelWrapper levelWrapper;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(level));
            levelWrapper =(LevelWrapper)ois.readObject();
            return levelWrapper;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Bitmap loadLevelPic(String name) {
        File dir = context.getExternalFilesDir(null);
        File levelPic = new File(dir, name+".png");
        return BitmapFactory.decodeFile(levelPic.getAbsolutePath());
    }

    @Override
    public void delete(String levelName) {
        File dir = context.getExternalFilesDir(null);
        File level = new File(dir, levelName+".level");
        File levelPic = new File(dir, levelName+".png");
        level.delete();
        levelPic.delete();
    }
}

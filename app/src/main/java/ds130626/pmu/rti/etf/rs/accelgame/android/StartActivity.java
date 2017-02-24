package ds130626.pmu.rti.etf.rs.accelgame.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ds130626.pmu.rti.etf.rs.accelgame.R;
import ds130626.pmu.rti.etf.rs.accelgame.android.build.LevelBuilderActivity;
import ds130626.pmu.rti.etf.rs.accelgame.android.build.LevelWrapper;
import ds130626.pmu.rti.etf.rs.accelgame.android.game.GameActivity;
import ds130626.pmu.rti.etf.rs.accelgame.android.score.DatabaseScoreLoader;
import ds130626.pmu.rti.etf.rs.accelgame.android.score.ScoreLoader;
import ds130626.pmu.rti.etf.rs.accelgame.android.score.ScoresActivity;
import ds130626.pmu.rti.etf.rs.accelgame.android.settings.SettingsActivity;
import ds130626.pmu.rti.etf.rs.accelgame.model.Level;

public class StartActivity extends AppCompatActivity {
    ListView levelListView;
    ScoreLoader scoreLoader;
    LevelLoader loader;
    LevelAdapter adapter;
    int deleteTries = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        scoreLoader = new DatabaseScoreLoader(getApplicationContext());
        loader = new SimpleLevelLoader(getApplicationContext());
        levelListView = (ListView) findViewById(R.id.levelListView);
        levelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LevelStub level = (LevelStub) adapterView.getItemAtPosition(i);
                startGame(level.getName());
            }
        });
        levelListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                LevelStub level = (LevelStub) adapterView.getItemAtPosition(i);
                deleteTries = 0;
                displayDialog(level.getName());
                return true;
            }
        });
    }

    private void refreshListView() {
        if(adapter!=null){
            for(LevelStub l : adapter.getLevels()){
                l.getBitmap().recycle();
            }
        }
        List<LevelStub> levels = getLevels();
        adapter = new LevelAdapter(this,R.layout.level_list_item,levels);
        levelListView.setAdapter(adapter);
    }

    private void deleteLevel(String levelName) {
        loader.delete(levelName);
        refreshListView();
        scoreLoader.deleteForLevel(levelName);
    }

    private void displayDialog(final String levelName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(levelName);
        builder.setMessage("What do you want to do?");
        builder.setPositiveButton("See scores", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
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
                Intent i = new Intent(StartActivity.this, ScoresActivity.class);
                i.putExtra(GameActivity.KEY_LEVEL_NAME, levelName);
                startActivity(i);
                dialog.dismiss();
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteTries == 0) {
                    Toast.makeText(StartActivity.this, "Press again to confirm", Toast.LENGTH_SHORT).show();
                    deleteTries++;
                    return;
                } else {
                    deleteLevel(levelName);
                    dialog.dismiss();
                }
                dialog.cancel();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshListView();
    }

    private List<LevelStub> getLevels() {
        List<LevelStub> levels = new ArrayList<>();
        File dir = getExternalFilesDir(null);
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.contains(".level");
            }
        });
        if(files!=null){
            for (File f : files) {
                levels.add(new LevelStub(f.getName().split("\\.")[0], loader.loadLevelPic(f.getName().split("\\.")[0])));
            }
        }
        return levels;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_menu, menu);
        MenuItem item = menu.findItem(R.id.itemNewLevel);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                newLevel();
                return true;
            }
        });
        item = menu.findItem(R.id.itemSettings);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(StartActivity.this, SettingsActivity.class);
                startActivity(i);
                return true;
            }
        });
        return true;
    }

    public void startGame(String levelName) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.KEY_LEVEL_NAME, levelName);
        startActivity(i);
    }

    public void newLevel() {
        Intent i = new Intent(this, LevelBuilderActivity.class);
        startActivity(i);
    }

    private static class LevelStub {
        private String name;
        private Bitmap bitmap;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LevelStub(String name, Bitmap bitmap) {
            this.name = name;
            this.bitmap = bitmap;
        }

        @Override
        public String toString() {
            return name;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

    public static class LevelAdapter extends ArrayAdapter<LevelStub> {
        List<LevelStub> levels;

        public LevelAdapter(Context context, int resource, List<LevelStub> objects) {
            super(context, resource);
            levels = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.level_list_item, null);
            }
            LevelStub level = levels.get(position);
            if (level != null) {
                ImageView imageView = (ImageView) v.findViewById(R.id.levelImageView);
                TextView textView = (TextView) v.findViewById(R.id.levelName);
                imageView.setImageBitmap(level.getBitmap());
                textView.setText(level.getName());
            }
            return v;
        }
        @Override
        public int getCount() {
            return levels.size();
        }

        @Nullable
        @Override
        public LevelStub getItem(int position) {
            return levels.get(position);
        }

        public List<LevelStub> getLevels() {
            return levels;
        }
    }
}

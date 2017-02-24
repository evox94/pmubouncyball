package ds130626.pmu.rti.etf.rs.accelgame.android.score;

import android.database.Cursor;
import android.opengl.EGLExt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ds130626.pmu.rti.etf.rs.accelgame.R;
import ds130626.pmu.rti.etf.rs.accelgame.android.game.GameActivity;

public class ScoresActivity extends AppCompatActivity {

    ListView scoresListView;
    ScoreLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        loader = new DatabaseScoreLoader(getApplicationContext());
        scoresListView = (ListView)findViewById(R.id.listViewScores);
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, new String[]{MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_PLAYER, MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_TIME},
                new int[]{android.R.id.text1, android.R.id.text2}, 0);
        cursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if(view.getId() == android.R.id.text1){
                    ((TextView)view).setText(cursor.getPosition()+1+". "+cursor.getString(cursor.getColumnIndex(MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_PLAYER)));
                    return true;
                }
                return false;
            }
        });
        scoresListView.setAdapter(cursorAdapter);
        TextView textView =(TextView)findViewById(R.id.levelName);
        String text = "Scoreboard for: "+getIntent().getStringExtra(GameActivity.KEY_LEVEL_NAME);
        textView.setText(text);
    }

    private void refresh(){
        CursorAdapter adapter = (CursorAdapter)scoresListView.getAdapter();
        adapter.changeCursor((Cursor)loader.getScores(getIntent().getStringExtra(GameActivity.KEY_LEVEL_NAME)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scores_menu, menu);
        MenuItem item = menu.findItem(R.id.itemClearThisLevel);
        item.setTitle("Clear for "+getIntent().getStringExtra(GameActivity.KEY_LEVEL_NAME));
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                loader.deleteForLevel(getIntent().getStringExtra(GameActivity.KEY_LEVEL_NAME));
                refresh();
                return true;
            }
        });

        item = menu.findItem(R.id.itemClearAll);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                loader.deleteAll();
                refresh();
                return true;
            }
        });
        return true;
    }
}

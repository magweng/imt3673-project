package com.imt3673.project.menu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.imt3673.project.database.AppDatabase;
import com.imt3673.project.main.R;

import java.util.ArrayList;

/**
 * LevelChooser activity
 * Holds a list with all the levels.
 */
public class LevelChooser extends AppCompatActivity {

    private LevelChooserListAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_chooser);


        // hide actionbar
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        ArrayList<LevelInfo> levels = new ArrayList<>();

        // Add this with a new level
        levels.add(new LevelInfo("Level 1","level1","00:10:00","00:20:00","00:40:00"));
        levels.add(new LevelInfo("Level 2","level2","00:30:00","00:40:00","01:00:00"));
        levels.add(new LevelInfo("Level 3","level3","00:20:00","00:40:00","01:00:00"));
        levels.add(new LevelInfo("Level 4","level4","00:20:00","00:40:00","01:00:00"));


        ListView levelListView = findViewById(R.id.lv_levels);
        this.listAdapter = new LevelChooserListAdapter(this, levels, AppDatabase.getAppDatabase(this));
        levelListView.setAdapter(this.listAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.listAdapter.notifyDataSetChanged();
    }
}

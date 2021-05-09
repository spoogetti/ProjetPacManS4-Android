package com.example.pac_man;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelSelectionActivity extends AppCompatActivity implements View.OnClickListener  {
    List<String> levelsList = new ArrayList<>();
    Map<String, Integer> nameIdLevelMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        ListView lv = findViewById(R.id.l_levels);

        levelsList.add("niveau1");
        levelsList.add("niveau2");
        levelsList.add("niveau3");

        nameIdLevelMap.put("niveau1", R.raw.niveau1);
        nameIdLevelMap.put("niveau2", R.raw.niveau2);
        nameIdLevelMap.put("niveau3", R.raw.niveau3);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                levelsList);

        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(onItemClickListener);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String levelRessourceName = ( (TextView) view ).getText().toString();
            int levelRessourceId = nameIdLevelMap.get(levelRessourceName);

            Intent gameActivity = new Intent(parent.getContext(), GameActivity.class);
            gameActivity.putExtra("levelRessourceId", levelRessourceId);
            startActivity(gameActivity);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_menu:
                Intent mainMenuIntent = new Intent(this, MainActivity.class);
                startActivity(mainMenuIntent);
                break;
            default:
                break;
        }
    }
}

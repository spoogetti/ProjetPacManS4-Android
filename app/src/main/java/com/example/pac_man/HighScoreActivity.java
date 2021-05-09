package com.example.pac_man;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HighScoreActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        SharedPreferences sharedPref = this.getSharedPreferences("highscores", Context.MODE_PRIVATE);
        String rawHighscores = sharedPref.getString("highscores", "");
        String[] splitedHighscores = rawHighscores.split(",");

        Arrays.sort(splitedHighscores, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                try{
                    int i1 = Integer.parseInt(s1);
                    int i2 = Integer.parseInt(s2);
                    if(i1 > i2) return -1;
                    else if(i1 < i2) return 1;
                    else return 0;
                } catch (Exception e) {}
                return -1;
            }
        });

        int topNhighscores = 5;
        if(topNhighscores > splitedHighscores.length)
            topNhighscores = splitedHighscores.length;
        String[] topScores = new String[topNhighscores];
        System.arraycopy(splitedHighscores, 0, topScores, 0, topNhighscores);

        ListView lView = findViewById(R.id.l_highscores);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                topScores);
        lView.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_menu:
                Intent mainMenuIntent = new Intent(this, MainActivity.class);
                startActivity(mainMenuIntent);
                break;
            case R.id.b_reset:
                SharedPreferences sharedPref = this.getSharedPreferences("highscores", Context.MODE_PRIVATE);
                sharedPref.edit().putString("highscores", "").apply();
                ListView lView = findViewById(R.id.l_highscores);
                List<String> emptyList = new ArrayList<>();
                ArrayAdapter arrayAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        emptyList);
                lView.setAdapter(arrayAdapter);
                break;
            default:
                break;
        }
    }
}

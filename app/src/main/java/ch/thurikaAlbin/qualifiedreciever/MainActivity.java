package ch.thurikaAlbin.qualifiedreciever;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {
    public static boolean isOnDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button darkModeBtn = findViewById(R.id.darkModeBtn);
        final Button scanBtn = findViewById(R.id.scanBtn);
        final Button generateBtn = findViewById(R.id.generateBtn);

        fillHistory();

        darkModeBtn.setOnClickListener(view -> {
            switchBetweenDarkAndLightMode();
        });

        scanBtn.setOnClickListener(view -> {
            switchToScan();
        });

        generateBtn.setOnClickListener(view -> {
            switchToGenerate();
        });
    }

    private void fillHistory(){
        final LinearLayout historyLayout = findViewById(R.id.layoutHistory);


        Intent intent = getIntent();

        TextView textView = new TextView(this);
        textView.setText(R.string.empty_history_text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        historyLayout.addView(textView);

        if (intent == null){
            Log.d("INTENT","intent is null");
            return;
        }
    }

    private void switchBetweenDarkAndLightMode(){
        if (isOnDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            isOnDarkMode = true;
            return;
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        isOnDarkMode = false;
    }

    private void switchToScan(){
        Log.d("SCAN_SWITCH","Trying to switch to scan");
        startActivity(new Intent(this,ScanActivity.class));
    }

    private void switchToGenerate(){
        Log.d("GENERATE_SWITCH","Trying to switch to generate");
        startActivity(new Intent(this,GenerateActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}



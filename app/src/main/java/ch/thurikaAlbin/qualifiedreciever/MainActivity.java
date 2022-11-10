package ch.thurikaAlbin.qualifiedreciever;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import ch.thurikaAlbin.qualifiedreciever.data.model.HistoryItem;

public class MainActivity extends AppCompatActivity {
    public static boolean isOnDarkMode = false;
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), this::handleResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton darkModeBtn = findViewById(R.id.darkModeBtn);
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

    private void fillHistory() {
        final LinearLayout historyLayout = findViewById(R.id.layoutHistory);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle == null) {
            Log.d("883", "No extras set in Intent");

            TextView textView = new TextView(this);
            textView.setText(R.string.empty_history_text);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            historyLayout.addView(textView);
            return;
        }

        try {
            HistoryItem historyItem = (HistoryItem) intent.getExtras().get("QR_CODE_ADD");

        } catch (ClassCastException e) {
            Log.e("EXCEPTION", e.getMessage());
        }

    }

    private void switchBetweenDarkAndLightMode() {
        if (isOnDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            isOnDarkMode = true;
            return;
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        isOnDarkMode = false;
    }

    private void switchToScan() {
        Log.d("SCAN_SWITCH", "Trying to switch to scan");

        ScanOptions options = new ScanOptions();
        options.setPrompt("Hold Camera in front of QR-Code");
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private void switchToGenerate() {
        Log.d("GENERATE_SWITCH", "Trying to switch to generate");
        startActivity(new Intent(this, GenerateActivity.class));
    }

    private void handleResult(ScanIntentResult result) {
        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            }).show();
        }
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



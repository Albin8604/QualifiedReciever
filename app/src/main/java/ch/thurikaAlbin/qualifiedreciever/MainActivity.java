package ch.thurikaAlbin.qualifiedreciever;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import ch.thurikaAlbin.qualifiedreciever.data.DataHandler;
import ch.thurikaAlbin.qualifiedreciever.data.model.HistoryItem;
import ch.thurikaAlbin.qualifiedreciever.data.model.QRCodeType;
import ch.thurikaAlbin.qualifiedreciever.qrCode.QRCodeGenerator;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    //todo save darkmode var
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
        Log.d("8878", DataHandler.getHistoryAsString());

        final LinearLayout historyLayout = findViewById(R.id.layoutHistory);


        historyLayout.removeAllViews();

        if (DataHandler.isHistoryEmpty()) {
            Log.d("883", "No extras set in Intent");

            TextView textView = new TextView(this);
            textView.setText(R.string.empty_history_text);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            historyLayout.addView(textView);
            return;
        }

        DataHandler.convertHistoryToLayout(this).forEach(historyLayout::addView);
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
        options.setBeepEnabled(false);

        barLauncher.launch(options);
    }

    private void switchToGenerate() {
        Log.d("GENERATE_SWITCH", "Trying to switch to generate");
        startActivity(new Intent(this, GenerateActivity.class));
    }

    private void handleResult(ScanIntentResult result) {
        try {

            if (result.getContents() != null) {
                HistoryItem historyItem = new HistoryItem();

                historyItem.setContent(result.getContents());
                historyItem.setType(QRCodeType.Scanned);
                historyItem.setQrCode(new QRCodeGenerator(result.getContents()).generateQRCodeImage());

                DataHandler.addHistoryItem(historyItem);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Result");
                builder.setMessage(result.getContents());
                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }).show();
            }

            fillHistory();
        } catch (WriterException e) {
            Log.e("EXCEPTION", e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        fillHistory();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}



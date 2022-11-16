package ch.thurikaAlbin.qualifiedreciever;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import ch.thurikaAlbin.qualifiedreciever.alert.AlertHelper;
import ch.thurikaAlbin.qualifiedreciever.data.DataHandler;
import ch.thurikaAlbin.qualifiedreciever.data.model.HistoryItem;
import ch.thurikaAlbin.qualifiedreciever.data.model.QRCodeType;
import ch.thurikaAlbin.qualifiedreciever.qrCode.QRCodeGenerator;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    public static final String HISTORY_KEY = "history";
    public static final String SHARED_PREFERENCES_NAME = "QualifiedReciever";
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), this::handleResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button scanBtn = findViewById(R.id.scanBtn);
        final Button generateBtn = findViewById(R.id.generateBtn);

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

        DataHandler.convertHistoryToLayout(this).forEach(historyItemLayout -> {
            historyLayout.addView(historyItemLayout);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 10, 0, 10);

            historyItemLayout.setLayoutParams(layoutParams);
        });
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
                String resultAsString = result.getContents();

                HistoryItem historyItem = new HistoryItem();

                historyItem.setContent(resultAsString);
                historyItem.setType(QRCodeType.Scanned);
                historyItem.setPreview(resultAsString);

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("", result.getContents());
                clipboardManager.setPrimaryClip(clipData);

                DataHandler.addHistoryItem(historyItem);

                AlertHelper.buildAndShowAlert(
                        this,
                        "Result",
                        result.getContents() + System.lineSeparator() + "Copied to clipboard",
                        "OK",
                        (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        }
                );
            }

            fillHistory();
        } catch (WriterException e) {
            Log.e("EXCEPTION", e.getMessage());
            AlertHelper.buildAndShowException(this, e);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DataHandler.isHistoryEmpty()){
            final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

            String jsonString = sharedPreferences.getString(HISTORY_KEY, "");

            DataHandler.convertJSONArrayToHistories(jsonString);
        }

        fillHistory();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("STOP", "MAIN STOPPED");

        final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(HISTORY_KEY, DataHandler.convertHistoryToJSONArray());
        myEdit.apply();
    }
}



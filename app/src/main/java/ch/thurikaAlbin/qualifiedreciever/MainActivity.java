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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Observable;
import java.util.Observer;

import ch.thurikaAlbin.qualifiedreciever.alert.AlertHelper;
import ch.thurikaAlbin.qualifiedreciever.data.DataManager;
import ch.thurikaAlbin.qualifiedreciever.data.model.HistoryItem;
import ch.thurikaAlbin.qualifiedreciever.data.model.QRCodeType;

/**
 * @author Thurika & Albin
 * @since 17.11.2022
 * Main activity
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements Observer {

    public static final String HISTORY_KEY = "history";
    public static final String SHARED_PREFERENCES_NAME = "QualifiedReciever";
    public static final int MARGIN_TOP_BOTTOM_HISTORY_ITEMS = 10;
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), this::handleResult);

    /**
     * Initializes current activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataManager.addObserver(this);

        final Button scanBtn = findViewById(R.id.scanBtn);
        final Button generateBtn = findViewById(R.id.generateBtn);

        scanBtn.setOnClickListener(view -> {
            switchToScan();
        });

        generateBtn.setOnClickListener(view -> {
            switchToGenerate();
        });
    }

    /**
     * Fills the history
     */
    private void fillHistory() {
        final LinearLayout historyLayout = findViewById(R.id.layoutHistory);

        historyLayout.removeAllViews();

        if (DataManager.getDataHandler().isHistoryEmpty()) {
            TextView textView = new TextView(this);
            textView.setText(R.string.empty_history_text);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            historyLayout.addView(textView);
            return;
        }

        DataManager.getDataHandler().convertHistoryToLayout(this).forEach(historyItemLayout -> {
            historyLayout.addView(historyItemLayout);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, MARGIN_TOP_BOTTOM_HISTORY_ITEMS, 0, MARGIN_TOP_BOTTOM_HISTORY_ITEMS);

            historyItemLayout.setLayoutParams(layoutParams);
        });
    }

    /**
     * Switches to camera for scanning the qr code
     */
    private void switchToScan() {
        Log.d("SCAN_SWITCH", "Trying to switch to scan");

        ScanOptions options = new ScanOptions();
        options.setPrompt("Hold Camera in front of QR-Code");
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        options.setBeepEnabled(false);

        barLauncher.launch(options);
    }

    /**
     * Switches to generate activity
     */
    private void switchToGenerate() {
        Log.d("GENERATE_SWITCH", "Trying to switch to generate");
        startActivity(new Intent(this, GenerateActivity.class));
    }

    /**
     * Handles the result of the scanned qr code
     * @param result result of the scanned qr code
     */
    private void handleResult(ScanIntentResult result) {

        if (result.getContents() != null) {
            String resultAsString = result.getContents();

            HistoryItem historyItem = new HistoryItem();

            historyItem.setContent(resultAsString);
            historyItem.setType(QRCodeType.Scanned);
            historyItem.setPreview(resultAsString);

            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("", result.getContents());
            clipboardManager.setPrimaryClip(clipData);

            DataManager.getDataHandler().addHistoryItem(historyItem);

            AlertHelper.buildAndShowAlert(
                    this,
                    "Result",
                    result.getContents() +
                            System.lineSeparator() +
                            System.lineSeparator() +
                            "Copied to clipboard"
            );
        }

        fillHistory();
    }

    /**
     * Updates the history
     */
    private void updateHistory(){
        if (DataManager.getDataHandler().isHistoryEmpty()) {
            final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

            String jsonString = sharedPreferences.getString(HISTORY_KEY, "");
            DataManager.getDataHandler().convertJSONArrayToHistories(jsonString);
        }

        fillHistory();
    }

    /**
     * Gets the History and sets it
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateHistory();
    }

    /**
     * Saves the History
     */
    @Override
    protected void onStop() {
        super.onStop();

        Log.d("STOP", "MAIN STOPPED");

        final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(HISTORY_KEY, DataManager.getDataHandler().convertHistoryToJSONArray());
        myEdit.apply();
    }

    /**
     * Updates history
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {
        fillHistory();
    }
}
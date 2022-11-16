package ch.thurikaAlbin.qualifiedreciever;

import static ch.thurikaAlbin.qualifiedreciever.MainActivity.HISTORY_KEY;
import static ch.thurikaAlbin.qualifiedreciever.MainActivity.SHARED_PREFERENCES_NAME;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.WriterException;

import ch.thurikaAlbin.qualifiedreciever.alert.AlertHelper;
import ch.thurikaAlbin.qualifiedreciever.data.DataHandler;
import ch.thurikaAlbin.qualifiedreciever.data.ImageHandler;
import ch.thurikaAlbin.qualifiedreciever.data.model.HistoryItem;
import ch.thurikaAlbin.qualifiedreciever.data.model.QRCodeType;
import ch.thurikaAlbin.qualifiedreciever.qrCode.QRCodeGenerator;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GenerateActivity extends AppCompatActivity {

    private static final String SSID_REPLACEMENT = "SSID";
    private static final String PASSWORD_REPLACEMENT = "PASSWORD";
    private static final String WIFI_QR_CODE_STRING_TEMPLATE = "WIFI:S:" +
            SSID_REPLACEMENT +
            ";T:WPA;P:" +
            PASSWORD_REPLACEMENT +
            ";;";
    private static final String URL_PREFIX = "https://";

    private HistoryItem currentItem = null;
    private Spinner generationTypeSpinner;
    private AppCompatButton generateQrCodeBtn;
    private ImageButton saveToGalleryBtn;
    private LinearLayout qrCodeLayout;
    private int selectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        generationTypeSpinner = findViewById(R.id.generationTypeChooser);
        generateQrCodeBtn = findViewById(R.id.generateQrCodeBtn);
        saveToGalleryBtn = findViewById(R.id.saveQrCodeToGallery);
        qrCodeLayout = findViewById(R.id.qrCodeLayout);

        initSpinner();

        generationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeContentOnTypeChanged(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        generateQrCodeBtn.setOnClickListener(view -> {
            if (isInputValid()) {
                try {
                    HistoryItem historyItem = buildItem();

                    DataHandler.addHistoryItem(historyItem);

                    ImageView qrCode = historyItem.convertQrCodeToImageView(this);

                    qrCodeLayout.removeAllViews();
                    qrCodeLayout.addView(qrCode);

                    configQRCode(qrCode);

                    return;
                } catch (Exception e) {
                    Log.e("EXCEPTION", e.getMessage());
                    AlertHelper.buildAndShowException(this, e);
                }
            }

            AlertHelper.buildAndShowAlert(
                    this,
                    "Error during generation of QR-Code",
                    "Invalid input for QR-Code, please edit your input"
            );
        });

        saveToGalleryBtn.setOnClickListener(view -> {
            if (currentItem != null) {
                new ImageHandler(new QRCodeGenerator(currentItem.getContent()).generateQRCodeImage(), getContentResolver(), this).saveImage();
            }
        });
    }

    private HistoryItem buildItem() throws WriterException {
        HistoryItem historyItem = new HistoryItem();

        historyItem.setContent(getInputValue());
        historyItem.setType(getInputType());
        historyItem.setPreview(getPreview());

        currentItem = historyItem;

        return historyItem;
    }

    private String getPreview() throws NullPointerException {
        String preview;
        if (selectedIndex == 0) {
            preview = ((TextInputLayout) findViewById(R.id.urlTextField)).getEditText().getText().toString();
        } else {
            preview = ((TextInputLayout) findViewById(R.id.ssidTextField)).getEditText().getText().toString();
        }

        if (preview.length() <= 15) {
            return preview;
        }
        return preview.substring(0, 14);
    }

    private void configQRCode(ImageView imageView) {
        imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    private QRCodeType getInputType() {
        if (selectedIndex == 0) {
            return QRCodeType.GeneratedUrl;
        } else {
            return QRCodeType.GeneratedWIFI;
        }
    }

    private String getInputValue() {
        if (selectedIndex == 0) {
            final TextInputLayout urlTextInput = findViewById(R.id.urlTextField);
            return URL_PREFIX + urlTextInput.getEditText().getText().toString();
        } else if (selectedIndex == 1) {
            final TextInputLayout ssidTextInput = findViewById(R.id.ssidTextField);
            final TextInputLayout passwordTextInput = findViewById(R.id.passwordTextField);
            String ssidInput = ssidTextInput.getEditText().getText().toString();
            String passwordInput = passwordTextInput.getEditText().getText().toString();

            return WIFI_QR_CODE_STRING_TEMPLATE
                    .replace(SSID_REPLACEMENT, ssidInput)
                    .replace(PASSWORD_REPLACEMENT, passwordInput);
        }

        return "";
    }

    private boolean isInputValid() {
        try {
            if (selectedIndex == 0) {
                Log.d("INPUT_VALIDATION","URL");
                final TextInputLayout urlTextInput = findViewById(R.id.urlTextField);
                String userInput = urlTextInput.getEditText().getText().toString();
                return !userInput.isEmpty();

            } else if (selectedIndex == 1) {
                Log.d("INPUT_VALIDATION","WIFI");

                final TextInputLayout ssidTextInput = findViewById(R.id.ssidTextField);
                final TextInputLayout passwordTextInput = findViewById(R.id.passwordTextField);
                String ssidInput = ssidTextInput.getEditText().getText().toString();
                String passwordInput = passwordTextInput.getEditText().getText().toString();
                return !ssidInput.isEmpty() && !passwordInput.isEmpty();
            }
            return false;

        } catch (NullPointerException e) {
            Log.e("EXCEPTION", e.getMessage());
            AlertHelper.buildAndShowException(this, e);
        }
        return false;
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generationTypeSpinner.setAdapter(adapter);
    }

    private void changeContentOnTypeChanged(int index) {
        selectedIndex = index;

        LinearLayout generationInputLayout = findViewById(R.id.generationInputLayout);
        generationInputLayout.removeAllViews();
        if (index == 0) {
            initURLFields(generationInputLayout);
        } else {
            initWIFIFields(generationInputLayout);
        }
    }

    private void initURLFields(LinearLayout parent) {
        TextInputLayout urlInputLayout = new TextInputLayout(this);
        TextInputEditText urlInputEditText = new TextInputEditText(this);
        urlInputLayout.setId(R.id.urlTextField);

        urlInputEditText.setHint(R.string.url_text_prompt);

        urlInputEditText.setMaxLines(1);
        urlInputEditText.setLines(1);
        urlInputEditText.setSingleLine();

        urlInputLayout.addView(urlInputEditText);
        parent.addView(urlInputLayout);
    }

    private void initWIFIFields(LinearLayout parent) {
        TextInputLayout ssidInputLayout = new TextInputLayout(this);
        TextInputEditText ssidInputEditText = new TextInputEditText(this);
        TextInputLayout passwordInputLayout = new TextInputLayout(this);
        TextInputEditText passwordInputEditText = new TextInputEditText(this);

        ssidInputLayout.addView(ssidInputEditText);
        passwordInputLayout.addView(passwordInputEditText);

        ssidInputEditText.setMaxLines(1);
        ssidInputEditText.setLines(1);
        ssidInputEditText.setSingleLine();

        passwordInputEditText.setMaxLines(1);
        passwordInputEditText.setLines(1);
        passwordInputEditText.setSingleLine();
        passwordInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ssidInputEditText.setHint(R.string.ssid_prompt_text);
        passwordInputEditText.setHint(R.string.password_prompt_text);

        parent.addView(ssidInputLayout);
        parent.addView(passwordInputLayout);

        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 10, 0, 10);

        ssidInputLayout.setLayoutParams(layoutParams);
        passwordInputLayout.setLayoutParams(layoutParams);

        ssidInputLayout.setId(R.id.ssidTextField);
        passwordInputLayout.setId(R.id.passwordTextField);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(HISTORY_KEY, DataHandler.convertHistoryToJSONArray());
        myEdit.apply();
    }
}
package ch.thurikaAlbin.qualifiedreciever;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static ch.thurikaAlbin.qualifiedreciever.MainActivity.HISTORY_KEY;
import static ch.thurikaAlbin.qualifiedreciever.MainActivity.SHARED_PREFERENCES_NAME;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ch.thurikaAlbin.qualifiedreciever.alert.AlertHelper;
import ch.thurikaAlbin.qualifiedreciever.data.DataManager;
import ch.thurikaAlbin.qualifiedreciever.data.ImageHandler;
import ch.thurikaAlbin.qualifiedreciever.data.model.HistoryItem;
import ch.thurikaAlbin.qualifiedreciever.data.model.QRCodeType;
import ch.thurikaAlbin.qualifiedreciever.qrCode.QRCodeGenerator;

/**
 * @author Thurika & Albin
 * @since 17.11.2022
 * Generate activity
 */
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
    public static final int INPUT_FIELDS_MARGIN_TOP_BOTTOM = 10;

    private HistoryItem currentItem = null;
    private Spinner generationTypeSpinner;
    private AppCompatButton generateQrCodeBtn;
    private ImageButton saveToGalleryBtn;
    private LinearLayout qrCodeLayout;
    private int selectedIndex = -1;

    /**
     * Initializes current activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        generationTypeSpinner = findViewById(R.id.generationTypeChooser);
        generateQrCodeBtn = findViewById(R.id.generateQrCodeBtn);
        saveToGalleryBtn = findViewById(R.id.saveQrCodeToGallery);
        qrCodeLayout = findViewById(R.id.qrCodeLayout);

        initSpinner();

        /**
         * If item changed on the dropdown
         */
        generationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeContentOnTypeChanged(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /**
         * If generate qr code button clicked
         */
        generateQrCodeBtn.setOnClickListener(view -> {
            if (isInputValid()) {
                try {
                    HistoryItem historyItem = buildItem();

                    DataManager.getDataHandler().addHistoryItem(historyItem);

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

        /**
         * Saves current generated qr code into the gallery
         * If permission is not granted, it asks for it
         */
        saveToGalleryBtn.setOnClickListener(view -> {
            if (currentItem != null) {
                new ImageHandler(
                        new QRCodeGenerator(currentItem.getContent()).generateQRCodeImage(),
                        getContentResolver(),
                        this
                ).saveImage();
            }
        });
    }

    /**
     * Gets the inputs of the different input fields and builds the current history item
     *
     * @return built history item
     */
    private HistoryItem buildItem() {
        HistoryItem historyItem = new HistoryItem();

        historyItem.setContent(getInputValue());
        historyItem.setType(getInputType());
        historyItem.setPreview(getPreview());

        currentItem = historyItem;

        return historyItem;
    }

    /**
     * Gets the preview out of the input fields
     *
     * @return preview
     * @throws NullPointerException
     */
    private String getPreview() throws NullPointerException {
        String preview;
        if (selectedIndex == 0) {
            preview = ((TextInputLayout) findViewById(R.id.urlTextField)).getEditText().getText().toString();
        } else {
            preview = ((TextInputLayout) findViewById(R.id.ssidTextField)).getEditText().getText().toString();
        }

        return preview;
    }

    /**
     * configs size of qr code
     *
     * @param imageView imageview of qr code
     */
    private void configQRCode(ImageView imageView) {
        imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    /**
     * gets current input type
     *
     * @return current input type
     */
    private QRCodeType getInputType() {
        if (selectedIndex == 0) {
            return QRCodeType.GeneratedUrl;
        } else {
            return QRCodeType.GeneratedWIFI;
        }
    }

    /**
     * Gets input values of current selected index
     *
     * @return input values
     */
    private String getInputValue() {
        try {
            if (selectedIndex == 0) {
                final TextInputLayout urlTextInput = findViewById(R.id.urlTextField);
                return URL_PREFIX + urlTextInput.getEditText().getText().toString();
            } else {
                final TextInputLayout ssidTextInput = findViewById(R.id.ssidTextField);
                final TextInputLayout passwordTextInput = findViewById(R.id.passwordTextField);
                String ssidInput = ssidTextInput.getEditText().getText().toString();
                String passwordInput = passwordTextInput.getEditText().getText().toString();

                return WIFI_QR_CODE_STRING_TEMPLATE
                        .replace(SSID_REPLACEMENT, ssidInput)
                        .replace(PASSWORD_REPLACEMENT, passwordInput);
            }
        } catch (NullPointerException e) {
            Log.d("EXCEPTION", e.getMessage());
            AlertHelper.buildAndShowException(this, e);
        }
        return "";
    }

    /**
     * Validates input
     *
     * @return is input valid
     */
    private boolean isInputValid() {
        try {
            if (selectedIndex == 0) {
                Log.d("INPUT_VALIDATION", "URL");
                final TextInputLayout urlTextInput = findViewById(R.id.urlTextField);
                String userInput = urlTextInput.getEditText().getText().toString();
                return !userInput.isEmpty();

            } else {
                Log.d("INPUT_VALIDATION", "WIFI");

                final TextInputLayout ssidTextInput = findViewById(R.id.ssidTextField);
                final TextInputLayout passwordTextInput = findViewById(R.id.passwordTextField);
                String ssidInput = ssidTextInput.getEditText().getText().toString();
                String passwordInput = passwordTextInput.getEditText().getText().toString();
                return !ssidInput.isEmpty() && !passwordInput.isEmpty();
            }
        } catch (NullPointerException e) {
            Log.e("EXCEPTION", e.getMessage());
            AlertHelper.buildAndShowException(this, e);
        }
        return false;
    }

    /**
     * Initializes the dropdown with the types defined in R.array.types_array
     */
    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generationTypeSpinner.setAdapter(adapter);
    }

    /**
     * Changes input field
     *
     * @param index index to be changed the fields to
     */
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

    /**
     * Initializes the fields needed for the URL
     *
     * @param parent parent of the URL fields
     */
    private void initURLFields(LinearLayout parent) {
        TextInputLayout urlInputLayout = new TextInputLayout(this);
        TextInputEditText urlInputEditText = new TextInputEditText(this);

        urlInputLayout.setId(R.id.urlTextField);

        urlInputLayout.addView(urlInputEditText);

        urlInputEditText.setHint(R.string.url_text_prompt);

        urlInputEditText.setMaxLines(1);
        urlInputEditText.setLines(1);
        urlInputEditText.setSingleLine();

        parent.addView(urlInputLayout);

        urlInputLayout.setLayoutParams(getLayoutParamsForInputFields());
    }

    /**
     * Initializes the fields needed for the WIFI
     *
     * @param parent parent of the WIFI fields
     */
    private void initWIFIFields(LinearLayout parent) {
        TextInputLayout ssidInputLayout = new TextInputLayout(this);
        TextInputEditText ssidInputEditText = new TextInputEditText(this);
        TextInputLayout passwordInputLayout = new TextInputLayout(this);
        TextInputEditText passwordInputEditText = new TextInputEditText(this);

        ssidInputLayout.setId(R.id.ssidTextField);
        passwordInputLayout.setId(R.id.passwordTextField);

        ssidInputLayout.addView(ssidInputEditText);
        passwordInputLayout.addView(passwordInputEditText);

        ssidInputEditText.setHint(R.string.ssid_prompt_text);
        passwordInputEditText.setHint(R.string.password_prompt_text);

        ssidInputEditText.setMaxLines(1);
        ssidInputEditText.setLines(1);
        ssidInputEditText.setSingleLine();

        passwordInputEditText.setMaxLines(1);
        passwordInputEditText.setLines(1);
        passwordInputEditText.setSingleLine();
        passwordInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        parent.addView(ssidInputLayout);
        parent.addView(passwordInputLayout);

        ssidInputLayout.setLayoutParams(getLayoutParamsForInputFields());
        passwordInputLayout.setLayoutParams(getLayoutParamsForInputFields());
    }

    /**
     * Gets layout params for the input fields
     *
     * @return layout params
     */
    public ViewGroup.LayoutParams getLayoutParamsForInputFields() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParams.setMargins(0, INPUT_FIELDS_MARGIN_TOP_BOTTOM, 0, INPUT_FIELDS_MARGIN_TOP_BOTTOM);
        return layoutParams;
    }

    /**
     * Saves generated qr code
     */
    @Override
    protected void onPause() {
        super.onPause();
        final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(HISTORY_KEY, DataManager.getDataHandler().convertHistoryToJSONArray());
        myEdit.apply();
    }
}
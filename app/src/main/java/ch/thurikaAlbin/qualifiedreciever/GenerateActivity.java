package ch.thurikaAlbin.qualifiedreciever;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import ch.thurikaAlbin.qualifiedreciever.alert.AlertHelper;
import ch.thurikaAlbin.qualifiedreciever.data.DataHandler;
import ch.thurikaAlbin.qualifiedreciever.data.model.HistoryItem;
import ch.thurikaAlbin.qualifiedreciever.data.model.QRCodeType;
import ch.thurikaAlbin.qualifiedreciever.qrCode.QRCodeGenerator;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GenerateActivity extends AppCompatActivity {

    private Spinner generationTypeSpinner;
    private AppCompatButton generateQrCodeBtn;
    private LinearLayout qrCodeLayout;
    private int selectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        generationTypeSpinner = findViewById(R.id.generationTypeChooser);
        generateQrCodeBtn = findViewById(R.id.generateQrCodeBtn);
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
                    Bitmap qrCodeImage = new QRCodeGenerator(getInputValue()).generateQRCodeImage();

                    HistoryItem historyItem = new HistoryItem();

                    historyItem.setContent(getInputValue());
                    historyItem.setType(getInputType());
                    historyItem.setQrCode(qrCodeImage);

                    DataHandler.addHistoryItem(historyItem);

                    ImageView qrCode = historyItem.convertQrCodeToImageView(this);

                    qrCodeLayout.removeAllViews();
                    qrCodeLayout.addView(qrCode);

                    configQRCode(qrCode);

                    return;
                } catch (WriterException e) {
                    Log.e("EXCEPTION", e.getMessage());
                }
            }

            AlertHelper.buildAndShowAlert(
                    this,
                    "Error during generation of QR-Code",
                    "Invalid input for QR-Code, please edit your input",
                    "Ok",
                    null
            );
        });
    }

    private void configQRCode(ImageView imageView){
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
            return urlTextInput.getEditText().getText().toString();
        } else if (selectedIndex == 1) {
            File file = new File("");
            StringBuilder stringBuilder = new StringBuilder();
            try {
                List<String> allLines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                allLines.forEach(line -> {
                    stringBuilder.append(line).append(System.lineSeparator());
                });
                return stringBuilder.toString();
            } catch (IOException e) {
                Log.e("EXCEPTION", e.getMessage());
            }
        }

        return "";
    }

    private boolean isInputValid() {
        try {
            if (selectedIndex == 0) {
                final TextInputLayout urlTextInput = findViewById(R.id.urlTextField);
                String userInput = urlTextInput.getEditText().getText().toString();
                if (userInput.isEmpty()) {
                    return false;
                }

            } else if (selectedIndex == 1) {

            } else {
                return false;
            }
            return true;
        } catch (NullPointerException e) {
            Log.e("EXCEPTION", e.getMessage());
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
            TextInputLayout textInputLayout = new TextInputLayout(this);
            TextInputEditText textInputEditText = new TextInputEditText(this);
            textInputLayout.setId(R.id.urlTextField);

//            textInputLayout.setBoxStrokeColor(R.color.mtrl_textinput_default_box_stroke_color);

            textInputEditText.setHint(R.string.url_text_prompt);

            textInputLayout.addView(textInputEditText);
            generationInputLayout.addView(textInputLayout);

        } else {
            AppCompatButton chooseFileBtn = new AppCompatButton(this);
            generationInputLayout.addView(chooseFileBtn);

//            chooseFileBtn.setId(R.id.chooseFileBtn);
//            chooseFileBtn.setBackground(R.drawable.rounded_corner);
            chooseFileBtn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            chooseFileBtn.setText(R.string.choose_file_text);

            chooseFileBtn.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("CHOOSE FILE BUTTON CLICKED");
                builder.setPositiveButton("OK", null);
                builder.show();
            });

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
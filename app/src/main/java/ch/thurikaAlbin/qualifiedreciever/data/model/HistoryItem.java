package ch.thurikaAlbin.qualifiedreciever.data.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.zxing.WriterException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import ch.thurikaAlbin.qualifiedreciever.R;
import ch.thurikaAlbin.qualifiedreciever.alert.AlertHelper;
import ch.thurikaAlbin.qualifiedreciever.data.ImageHandler;
import ch.thurikaAlbin.qualifiedreciever.qrCode.QRCodeGenerator;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HistoryItem {
    private static final String PATTERN = "dd.MM.yyyy HH:mm";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);
    public static final int PADDING = 15;
    public static final int PADDING_INFORMATION_LAYOUT = 25;

    private String id;
    private String content;
    private String preview;
    private QRCodeType type;
    private LocalDateTime timestamp;

    public HistoryItem() {
        id = UUID.randomUUID().toString();
        timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getQrCode() {
        return new QRCodeGenerator(getContent()).generateQRCodeImage();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) throws WriterException {
        this.content = content;
    }

    public QRCodeType getType() {
        return type;
    }

    public void setType(QRCodeType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        if (preview.length() > 50) {
            setPreview(preview.substring(0, 46)+"...");
        }
        this.preview = preview;
    }

    public String getFormattedTimestamp() {
        return getTimestamp().format(FORMATTER);
    }


    public ImageView convertQrCodeToImageView(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(getQrCode());
        return imageView;
    }

    @SuppressLint("ResourceAsColor")
    public LinearLayout convertToLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundResource(R.drawable.rounded_corner);

        ImageView imageView = convertQrCodeToImageView(context);

        linearLayout.addView(imageView);

        LinearLayout informationLayout = new LinearLayout(context);
        informationLayout.setOrientation(LinearLayout.VERTICAL);
        informationLayout.setPadding(
                PADDING_INFORMATION_LAYOUT,
                PADDING_INFORMATION_LAYOUT,
                PADDING_INFORMATION_LAYOUT,
                PADDING_INFORMATION_LAYOUT
        );

        TextView contentView = new TextView(context);
        contentView.setText(getPreview());

        TextView typeView = new TextView(context);
        typeView.setText(getType().getName());

        TextView timeStampView = new TextView(context);
        timeStampView.setText(getFormattedTimestamp());

        informationLayout.addView(contentView);
        informationLayout.addView(typeView);
        informationLayout.addView(timeStampView);

        linearLayout.addView(informationLayout);
        linearLayout.setPadding(PADDING, PADDING, PADDING, PADDING);

        linearLayout.setOnClickListener(view -> {
            new ImageHandler(
                    new QRCodeGenerator(getContent()).generateQRCodeImage(),
                    context.getContentResolver(),
                    context
            ).saveImage();
        });

        return linearLayout;
    }
}

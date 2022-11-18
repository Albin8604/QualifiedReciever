package ch.thurikaAlbin.qualifiedreciever.data.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import ch.thurikaAlbin.qualifiedreciever.R;
import ch.thurikaAlbin.qualifiedreciever.data.DataManager;
import ch.thurikaAlbin.qualifiedreciever.data.ImageHandler;
import ch.thurikaAlbin.qualifiedreciever.listener.OnSwipeTouchListener;
import ch.thurikaAlbin.qualifiedreciever.qrCode.QRCodeGenerator;

/**
 * @author Thurika & Albin
 * @since 17.11.2022
 * HistoryItem data class
 */
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

    /**
     * Constructor
     */
    public HistoryItem() {
        id = UUID.randomUUID().toString();
        timestamp = LocalDateTime.now();
    }

    /**
     * Getters & Setters
     */
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

    public void setContent(String content) {
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

    /**
     * If given preview ist too long, it gets shorten
     * @param preview given preview
     */
    public void setPreview(String preview) {
        if (preview.length() > 50) {
            setPreview(preview.substring(0, 46) + "...");
        }
        this.preview = preview;
    }

    /**
     * Returns timestamp formatted with the format "dd.MM.yyyy HH:mm" as a String
     * @return formatted timestamp
     */
    public String getFormattedTimestamp() {
        return getTimestamp().format(FORMATTER);
    }

    /**
     * Converts the QR Code into an ImageView
     * @param context Context on which the ImageView should be built on
     * @return created ImageView
     */
    public ImageView convertQrCodeToImageView(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(getQrCode());
        return imageView;
    }

    /**
     * Converts the HistoryItem into an Layout
     * @param context Context on which this Layout should be built on
     * @return layout of this HistoryItem
     */
    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
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

        linearLayout.setOnTouchListener(new OnSwipeTouchListener(context) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSwipeLeft() {
                DataManager.getDataHandler().removeHistoryItem(HistoryItem.this);
                Toast.makeText(context, "Deleted "+getPreview(), Toast.LENGTH_SHORT).show();
            }
        });

        return linearLayout;
    }

    //todo delete
    @Override
    public String toString() {
        return "HistoryItem{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", preview='" + preview + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                '}';
    }
}
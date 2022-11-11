package ch.thurikaAlbin.qualifiedreciever.data.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ch.thurikaAlbin.qualifiedreciever.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HistoryItem {
    private static final String PATTERN = "dd.MM.yyyy HH:mm";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    private Integer id;
    private Bitmap qrCode;
    private String content;
    private QRCodeType type;
    private LocalDateTime timestamp;

    public HistoryItem() {
        timestamp = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Bitmap getQrCode() {
        return qrCode;
    }

    public void setQrCode(Bitmap qrCode) {
        this.qrCode = qrCode;
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


        linearLayout.addView(convertQrCodeToImageView(context));

        LinearLayout informationLayout = new LinearLayout(context);
        informationLayout.setOrientation(LinearLayout.VERTICAL);

        TextView contentView = new TextView(context);
        contentView.setText(buildPreview());

        TextView typeView = new TextView(context);
        typeView.setText(getType().toString());

        TextView timeStampView = new TextView(context);
        timeStampView.setText(getFormattedTimestamp());

        informationLayout.addView(contentView);
        informationLayout.addView(typeView);
        informationLayout.addView(timeStampView);

        linearLayout.addView(informationLayout);

        return linearLayout;
    }

    private String buildPreview() {
        if (getType() == QRCodeType.GeneratedWIFI) {
            return "TODO";
        }
        if (getContent().length() > 15) {
            return getContent().substring(0, 15);
        }

        return getContent();
    }

    @Override
    public String toString() {
        return "HistoryItem{" +
                "id=" + id +
                ", qrCode=" + qrCode +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                '}';
    }
}

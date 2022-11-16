package ch.thurikaAlbin.qualifiedreciever.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.OutputStream;

import ch.thurikaAlbin.qualifiedreciever.alert.AlertHelper;

public class ImageHandler {
    private final Bitmap bitmap;
    private final ContentResolver contentResolver;
    private final Context context;

    public ImageHandler(Bitmap bitmap, ContentResolver contentResolver, Context context) {
        this.bitmap = bitmap;
        this.contentResolver = contentResolver;
        this.context = context;
    }

    public void saveImage() {
        try {
            String fileName = "qr_code_" + System.currentTimeMillis() + "_.jpg";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "qr_code");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/");
                values.put(MediaStore.MediaColumns.IS_PENDING, 1);
            } else {
                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                File file = new File(directory, fileName);
                values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
            }

            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            try (OutputStream output = contentResolver.openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            }

            AlertHelper.buildAndShowAlert(context,"Image","QR Code saved to Gallery");

        } catch (Exception e) {
            Log.d("onBtnSavePng", e.toString()); // java.io.IOException: Operation not permitted
        }
    }

}

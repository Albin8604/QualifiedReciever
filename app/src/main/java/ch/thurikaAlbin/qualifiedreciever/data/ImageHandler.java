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
import android.widget.Toast;

import java.io.File;
import java.io.OutputStream;

import ch.thurikaAlbin.qualifiedreciever.alert.AlertHelper;

/**
 * @author Thurika & Albin
 * @since 17.11.2022
 * Handler for the images
 */
public class ImageHandler {
    private final Bitmap bitmap;
    private final ContentResolver contentResolver;
    private final Context context;

    /**
     * Constructor
     *
     * @param bitmap          bitmap which should be used in this handler
     * @param contentResolver resolver on which this bitmap should be saved
     * @param context         Context on which this Image should be saved on
     */
    public ImageHandler(Bitmap bitmap, ContentResolver contentResolver, Context context) {
        this.bitmap = bitmap;
        this.contentResolver = contentResolver;
        this.context = context;
    }

    /**
     * Saves the bitmap (given in the constructor) into the devices gallery
     */
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

            Toast.makeText(context, "QR Code saved to Gallery", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.d("EXCEPTION", e.toString());
            AlertHelper.buildAndShowException(context, e);
        }
    }

}

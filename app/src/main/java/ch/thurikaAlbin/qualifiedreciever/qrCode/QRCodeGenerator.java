package ch.thurikaAlbin.qualifiedreciever.qrCode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * @author Thurika & Albin
 * @since 17.11.2022
 * QR Code Generator
 */
public class QRCodeGenerator {

    private static final int SIZE = 300;
    private static final String BACKGROUND_COLOR = "#3FBAC2";

    private final String content;

    /**
     * Constructor
     * @param content content which the qr code should be created of
     */
    public QRCodeGenerator(String content) {
        this.content = content;
    }

    /**
     * Generates the qr code
     * @return qr code as a bitmap
     */
    public Bitmap generateQRCodeImage() {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = barcodeWriter.encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE);
            return buildImage(bitMatrix);
        } catch (WriterException e) {
            Log.d("EXCEPTION",e.getMessage());
        }
        return null;
    }

    /**
     * Builds the image
     * @param bitMatrix bit matrix to build the bitmap on
     * @return built bitmap
     */
    private Bitmap buildImage(BitMatrix bitMatrix) {
        final int height = bitMatrix.getHeight();
        final int width = bitMatrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.parseColor(BACKGROUND_COLOR));
            }
        }
        return bmp;
    }
}
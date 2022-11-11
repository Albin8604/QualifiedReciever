package ch.thurikaAlbin.qualifiedreciever.qrCode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator {

    private static final int SIZE = 200;

    private final String content;

    public QRCodeGenerator(String content) {
        this.content = content;
    }

    public Bitmap generateQRCodeImage() throws WriterException {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix =
                barcodeWriter.encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE);

        return buildImage(bitMatrix);
    }

    private Bitmap buildImage(BitMatrix bitMatrix){
        final int height = bitMatrix.getHeight();
        final int width = bitMatrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bmp.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }
}

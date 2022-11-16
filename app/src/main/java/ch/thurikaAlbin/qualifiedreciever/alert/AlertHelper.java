package ch.thurikaAlbin.qualifiedreciever.alert;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class AlertHelper {
    public static void buildAndShowAlert(Context context, String title, String message, String positiveBtnText, DialogInterface.OnClickListener positiveButtonListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(positiveBtnText, positiveButtonListener);
        alertBuilder.show();
    }

    public static void buildAndShowAlert(Context context, String title, String message, String positiveBtnText) {
        buildAndShowAlert(context, title, message, positiveBtnText, (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
    }

    public static void buildAndShowAlert(Context context, String title, String message) {
        buildAndShowAlert(context, title, message, "OK");
    }

    public static void buildAndShowException(Context context, Exception e) {
        buildAndShowAlert(
                context,
                "Exception occured",
                e.getMessage()

        );
    }
}

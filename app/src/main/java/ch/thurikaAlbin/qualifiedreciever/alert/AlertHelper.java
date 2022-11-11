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
}

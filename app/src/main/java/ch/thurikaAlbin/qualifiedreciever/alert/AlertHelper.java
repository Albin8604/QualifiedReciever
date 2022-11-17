package ch.thurikaAlbin.qualifiedreciever.alert;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

/**
 * @author Thurika & Albin
 * @since 17.11.2022
 * Helper for creating alerts
 */
public class AlertHelper {
    /**
     * Builds and shows an Alert
     * @param context Context on which this Alert should be built on
     * @param title Title for this alert
     * @param message Message for this alert
     * @param positiveBtnText Text for the Positive button
     * @param positiveButtonListener Listener for the positive button
     */
    public static void buildAndShowAlert(Context context, String title, String message, String positiveBtnText, DialogInterface.OnClickListener positiveButtonListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(positiveBtnText, positiveButtonListener);
        alertBuilder.show();
    }

    /**
     * Builds and shows an Alert with some predefined values
     * @param context Context on which this Alert should be built on
     * @param title Title for this alert
     * @param message Message for this alert
     * @param positiveBtnText Text for the Positive button
     */
    public static void buildAndShowAlert(Context context, String title, String message, String positiveBtnText) {
        buildAndShowAlert(context, title, message, positiveBtnText, (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
    }

    /**
     * Builds and shows an Alert with some predefined values
     * @param context Context on which this Alert should be built on
     * @param title Title for this alert
     * @param message Message for this alert
     */
    public static void buildAndShowAlert(Context context, String title, String message) {
        buildAndShowAlert(context, title, message, "OK");
    }

    /**
     * Builds and shows an Alert for an Exception
     * @param context Context on which this Alert should be built on
     * @param e Exception to display
     */
    public static void buildAndShowException(Context context, Exception e) {
        buildAndShowAlert(
                context,
                "Exception occured",
                e.getMessage()

        );
    }
}

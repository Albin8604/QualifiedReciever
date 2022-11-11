package ch.thurikaAlbin.qualifiedreciever.data;

import android.content.Context;
import android.os.Build;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

import ch.thurikaAlbin.qualifiedreciever.data.model.HistoryItem;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DataHandler {
    private static final ArrayList<HistoryItem> HISTORY = new ArrayList<>();

    public static void addHistoryItem(HistoryItem historyItem) {
        HISTORY.add(historyItem);
    }

    public static String getHistoryAsString() {
        return Arrays.toString(DataHandler.HISTORY.toArray(new HistoryItem[0]));
    }

    public static boolean isHistoryEmpty() {
        return HISTORY.isEmpty();
    }

    public static List<LinearLayout> convertHistoryToLayout(Context context) {
        return HISTORY.stream().map(historyItem -> historyItem.convertToLayout(context)).collect(Collectors.toList());
    }
}

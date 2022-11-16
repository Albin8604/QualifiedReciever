package ch.thurikaAlbin.qualifiedreciever.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.WriterException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

import ch.thurikaAlbin.qualifiedreciever.data.model.HistoryItem;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DataHandler {
    private static final Gson GSON = new Gson();
    private static final List<HistoryItem> HISTORY = new ArrayList<>();

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

    public static String convertHistoryToJSONArray(){
        if (isHistoryEmpty()){
            return "";
        }
        String json = GSON.toJson(HISTORY);

        Log.d("JSON",json);

        return json;
    }

    public static void convertJSONArrayToHistories(String json){
        Log.d("JSON","to convert to objects"+json);

        if (json.isEmpty()){
            return;
        }
        Type listType = new TypeToken<List<HistoryItem>>() {}.getType();

        HISTORY.addAll(GSON.fromJson(json, listType));
    }
}
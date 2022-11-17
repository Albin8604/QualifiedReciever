package ch.thurikaAlbin.qualifiedreciever.data;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

import ch.thurikaAlbin.qualifiedreciever.data.model.HistoryItem;

/**
 * @author Thurika & Albin
 * @since 17.11.2022
 * Handler for the History
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class DataHandler extends Observable {
    private static final Gson GSON = new Gson();
    private static final List<HistoryItem> HISTORY = new ArrayList<>();

    /**
     * Adds a HistoryItem to the current History
     *
     * @param historyItem historyItem to be added
     */
    public void addHistoryItem(HistoryItem historyItem) {
        HISTORY.add(historyItem);
    }

    /**
     * Removes a HistoryItem of the current History
     *
     * @param id of item to be removed
     */
    public void removeHistoryItem(String id) {
        for (HistoryItem historyItem : HISTORY) {
            if (historyItem.getId().equals(id)){
                HISTORY.remove(historyItem);
                setChanged();
                notifyObservers();
                return;
            }
        }
    }

    /**
     * Checks if history is empty
     *
     * @return is history empty
     */
    public boolean isHistoryEmpty() {
        return HISTORY.isEmpty();
    }

    /**
     * Converts the whole history into layouts
     *
     * @param context Context on which the item layouts should be built on
     * @return list of the built layouts
     */
    public List<LinearLayout> convertHistoryToLayout(Context context) {
        return HISTORY.stream().map(historyItem -> historyItem.convertToLayout(context)).collect(Collectors.toList());
    }

    /**
     * Converts whole history into a JSON array
     *
     * @return JSON array as a string
     */
    public String convertHistoryToJSONArray() {
        if (isHistoryEmpty()) {
            return "";
        }
        String json = GSON.toJson(HISTORY);

        Log.d("JSON", json);

        return json;
    }

    /**
     * Converts JSON array into objects and adds them all to the history
     *
     * @param json json array as a String
     */
    public void convertJSONArrayToHistories(String json) {
        Log.d("JSON", "to convert to objects | " + json);

        if (json.isEmpty()) {
            return;
        }
        Type listType = new TypeToken<List<HistoryItem>>() {
        }.getType();

        HISTORY.addAll(GSON.fromJson(json, listType));
    }
}
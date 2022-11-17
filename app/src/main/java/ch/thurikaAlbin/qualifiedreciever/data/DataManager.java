package ch.thurikaAlbin.qualifiedreciever.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Observer;

/**
 * @author Thurika & Albin
 * @since 17.11.2022
 * Data manager
 * Created to be able to use observer pattern
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class DataManager {
    private static final DataHandler dataHandler = new DataHandler();

    public static DataHandler getDataHandler() {
        return dataHandler;
    }

    public static void addObserver(Observer observer){
        getDataHandler().addObserver(observer);
    }
}

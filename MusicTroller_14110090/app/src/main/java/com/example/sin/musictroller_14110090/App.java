package com.example.sin.musictroller_14110090;

import android.app.Application;
import android.content.Context;

/**
 * Created by Sin on 05/19/17.
 */

public class App extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}

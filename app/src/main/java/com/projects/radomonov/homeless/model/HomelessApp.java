package com.projects.radomonov.homeless.model;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Tom on 20.10.2017.
 */

public class HomelessApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

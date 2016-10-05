package whatsapp.android.com.whatsapp.application;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Created by ricar on 30/09/2016.
 */

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}

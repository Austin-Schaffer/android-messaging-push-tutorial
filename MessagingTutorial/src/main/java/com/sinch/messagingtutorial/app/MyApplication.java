package com.sinch.messagingtutorial.app;

import android.app.Application;
import com.parse.Parse;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "2rJ5seZ8DCMuzIyMIbrS33mg6GQ5enZQKpzAkaHS", "Roj2L3wOvzv1XYfq5u8oP7DM4cY3scBmiegPvaME");
    }
}

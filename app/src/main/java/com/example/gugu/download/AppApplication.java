package com.example.gugu.download;

import android.app.Application;

/**
 * Created by gugu on 2017/12/10.
 */

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationHolder.getInstance().setContext(this);
    }

}

package com.example.gugu.download;

import android.content.Context;

/**
 * Created by gugu on 2017/12/10.
 */

public class ApplicationHolder {
    public static ApplicationHolder instance = null;
    private Context context;
    private ApplicationHolder() {

    }

    public static ApplicationHolder getInstance() {
        if (instance == null) {
            instance = new ApplicationHolder();

        }
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

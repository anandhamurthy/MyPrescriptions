package com.myprescriptions;

import android.os.Environment;

public class Details {

    public static final String root = Environment.getExternalStorageDirectory().toString();
    public static final String app_folder = root+"/My prescriptions/";
}

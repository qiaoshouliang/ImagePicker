package com.qiaoshouliang.imagepicker.Application;

import android.app.Application;
import android.content.Context;

/**
 * Created by qiaoshouliang on 16/4/11.
 */
public class ImagePicker extends Application {
    public static ImagePicker context;
    public static Context getContext(){
        return context;
    }

}

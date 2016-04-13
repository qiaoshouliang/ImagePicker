package com.qiaoshouliang.imagepicker.Module;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by qiaoshouliang on 16/4/11.
 */
public class ImageHolder  {
    public String path;
    public Bitmap bitmap;
    public ImageView imageView;

    public ImageHolder(String path, Bitmap bitmap, ImageView imageView) {
        this.path = path;
        this.bitmap = bitmap;
        this.imageView = imageView;
    }
}

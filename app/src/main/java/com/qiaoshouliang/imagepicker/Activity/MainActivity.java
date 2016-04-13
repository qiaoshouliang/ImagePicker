package com.qiaoshouliang.imagepicker.Activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.qiaoshouliang.imagepicker.Module.ImageFloder;
import com.qiaoshouliang.imagepicker.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private List<ImageFloder> imageFloderList = new ArrayList<>();
    private String maxDir = "";
    private int maxSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getImages();
    }

    private void getImages() {


        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        final Set<String> dirPathSet = new HashSet<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String firstImagePath = null;
                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = MainActivity.this.getContentResolver();
                //TODO 研究一下这个怎么用
                Cursor cursor = contentResolver.query(imageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=?" + " or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                while (cursor.moveToNext()) {

                    String path = cursor.getString(
                            cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    Log.e("path: ", path);
                    if (firstImagePath == null)
                        firstImagePath = path;
                    File parentFile = new File(path).getParentFile();
                    //TODO 没有加上parentFile 为空的判断，如果有问题就加上判断
                    String dirPath = parentFile.getAbsolutePath();
                    if (dirPathSet.contains(dirPath)) {
                        continue;
                    } else {
                        dirPathSet.add(dirPath);
                    }
                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith("jpeg"))
                                return true;
                            return false;

                        }
                    }).length;

                    ImageFloder imageFloder = new ImageFloder();
                    imageFloder.setDir(dirPath);
                    imageFloder.setCount(picSize);
                    imageFloder.setFirstImagePath(firstImagePath);
                    imageFloderList.add(imageFloder);

                    if (picSize > maxSize){
                        maxSize = picSize;
                        maxDir = dirPath;
                    }
                }
            }
        }).start();


    }
}

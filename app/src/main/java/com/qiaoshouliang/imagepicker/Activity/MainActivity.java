package com.qiaoshouliang.imagepicker.Activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.qiaoshouliang.imagepicker.Module.ImageFloder;
import com.qiaoshouliang.imagepicker.R;
import com.qiaoshouliang.imagepicker.adapter.GridViewAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private List<ImageFloder> imageFloderList = new ArrayList<>();
    private File maxDir = null;
    private int maxSize = 0;

    //gridview
    private GridView gridView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x110) {
                data2View();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        getImages();
    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gv_images);

    }

    private void data2View() {
        List<String> imgList;
        //todo 这个地方要学习一下
        imgList = Arrays.asList(maxDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg")
                        || filename.endsWith(".JPG")
                        || filename.endsWith(".PNG")
                        || filename.endsWith(".png")
                        || filename.endsWith("jpeg")
                        || filename.endsWith(".JPEG"))
                    return true;
                return false;
            }
        }));
        gridView.setAdapter(new GridViewAdapter(this, imgList, maxDir.getAbsolutePath()));
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
                                    || filename.endsWith(".JPG")
                                    || filename.endsWith(".PNG")
                                    || filename.endsWith(".png")
                                    || filename.endsWith("jpeg")
                                    || filename.endsWith(".JPEG"))
                                return true;
                            return false;

                        }
                    }).length;

                    ImageFloder imageFloder = new ImageFloder();
                    imageFloder.setDir(dirPath);
                    imageFloder.setCount(picSize);
                    imageFloder.setFirstImagePath(firstImagePath);
                    imageFloderList.add(imageFloder);
                    Log.e("picSize", picSize+"");
                    Log.e("maxSize", maxSize+"");

                    if (picSize > maxSize) {
                        maxSize = picSize;
                        maxDir = parentFile;
                        Log.e("maxDir", maxDir.getAbsolutePath());
                    }
                }
                cursor.close();
                handler.sendEmptyMessage(0x110);
            }
        }).start();


    }
}

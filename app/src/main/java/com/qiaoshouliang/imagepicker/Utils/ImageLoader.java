package com.qiaoshouliang.imagepicker.Utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import java.util.LinkedList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by qiaoshouliang on 16/4/8.
 */
public class ImageLoader {
    private static ImageLoader imageLoader;
    //图片缓存
    private LruCache<String,Bitmap> lruCache;
    //线程池
    private ExecutorService threadPool;
    private static final int DEFAULT_THREAD_COUNT = 1;

    private Type type = Type.LIFO;

    private enum  Type{
        FIFO,LIFO

    }
    //
    private LinkedList<Runnable> taskQueue;

    private Thread poolThread;

    private Handler poolThreadHandler;

    private Handler UIHandler;

    private ImageLoader(int threadCount,Type type){
        init(threadCount,type);
    }

    private void init(int threadCount, Type type) {
        poolThread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                poolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                    }
                };
                Looper.loop();
            }
        };
        poolThread.start();

        //获取应用可用的最大内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory/8;

        lruCache = new LruCache<String, Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {

                //获得Bitmap的大小
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB_MR1)
                    return value.getByteCount();

                return value.getRowBytes()*value.getHeight();
            }
        };

        threadPool = Executors.newFixedThreadPool(threadCount);
    }

    /**
     * 实现单例模式
     * @return
     */
    public static ImageLoader getInstance(){

        if(imageLoader == null){
            synchronized (ImageLoader.class){
                if(imageLoader == null){
                    imageLoader = new ImageLoader(DEFAULT_THREAD_COUNT,Type.LIFO);
                }
            }

        }
        return imageLoader;
    }

}

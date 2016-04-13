package com.qiaoshouliang.imagepicker.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qiaoshouliang.imagepicker.Module.ImageHolder;
import com.qiaoshouliang.imagepicker.Module.ImageSize;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


/**
 * Created by qiaoshouliang on 16/4/8.
 */
public class ImageLoader {
    private static ImageLoader imageLoader;
    //图片缓存
    private LruCache<String, Bitmap> lruCache;
    //线程池
    private ExecutorService threadPool;
    private LinkedList<Runnable> taskQueue = new LinkedList<>();
    private static final int DEFAULT_THREAD_COUNT = 1;
    private Type type = Type.LIFO;

    private enum Type {
        FIFO, LIFO

    }

    private Thread poolThread;

    private Handler poolThreadHandler;

    private Handler UIHandler;

    private Semaphore poolThreadHandlerSemaphore = new Semaphore(0);
    private Semaphore threadPullSemaphore;

    private ImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    /**
     * 初始化操作
     *
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {

        threadPool = Executors.newFixedThreadPool(threadCount);
        //创建一个线程
        poolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                poolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //请求一个信号量
                        try {
                            threadPullSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        threadPool.execute(getTask());
                    }
                };
                //todo 记得打个log看看这个方法走了几遍
                // 多线程要记得判断该变量在其他线程中使用时，是否真的已经初始化完了，所以要用信号量来进行同步
                poolThreadHandlerSemaphore.release();
                Looper.loop();
            }
        };
        poolThread.start();

        //获取应用可用的最大内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        //创建一个lruCache
        lruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {

                //获得Bitmap的大小
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
                    return value.getByteCount();

                return value.getRowBytes() * value.getHeight();
            }
        };

        //todo 穿件UI线程的handler
        UIHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ImageHolder imageHolder = (ImageHolder) msg.obj;
                ImageView imageView = imageHolder.imageView;
                Bitmap bitmap = imageHolder.bitmap;
                imageView.setImageBitmap(bitmap);

            }
        };

        threadPullSemaphore = new Semaphore(threadCount);
    }

    /**
     * 根据获取图片的方式来获取Runnable
     *
     * @return
     */
    private Runnable getTask() {
        if (type == Type.LIFO)
            return taskQueue.removeLast();
        if (type == Type.FIFO)
            return taskQueue.removeFirst();

        return null;
    }

    /**
     * 实现单例模式
     *
     * @return
     */
    public static ImageLoader getInstance() {

        if (imageLoader == null) {
            synchronized (ImageLoader.class) {
                if (imageLoader == null) {
                    imageLoader = new ImageLoader(DEFAULT_THREAD_COUNT, Type.LIFO);
                }
            }

        }
        return imageLoader;
    }

    /****/
//TODO 漏洞很多，需要对照3-1初始化mUIHandler进行学习修改
    public void loadImage(final String path, final ImageView imageView) {

        Bitmap bitmap = getBitmapFromLruCache(path);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {

            addTasks(new Runnable() {
                @Override
                public void run() {
                    ImageSize size = getImageViewSize(imageView);
                    //压缩图片
                    Bitmap bitmap = decodeSampledBitmapFromPath(path, size.width, size.height);
                    //添加到缓存
                    addBitmapToLruCache(path, bitmap);

//                    imageView.setImageBitmap(bitmap);
                    Message message = new Message();
                    message.obj = new ImageHolder(path, bitmap, imageView);
                    UIHandler.sendMessage(message);

                    //任务执行完了之后释放信号量
                    threadPullSemaphore.release();
                }
            });

        }

    }

    /**
     * 将图片加入缓存
     *
     * @param path
     * @param bitmap
     */
    private void addBitmapToLruCache(String path, Bitmap bitmap) {

        lruCache.put(path, bitmap);
    }

    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = caculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);

        return bitmap;
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);

            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();

        ViewGroup.LayoutParams lp = imageView.getLayoutParams();

        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        /**
         * 获取宽度
         */
        int width = imageView.getWidth();
        if (width <= 0) {
            width = lp.width; //获取imageview在layout中声明的宽度
        }
        if (width <= 0) {

//                width = imageView.getMaxWidth();
            width = getFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            width = displayMetrics.widthPixels; //获取屏幕的宽度
        }

        int height = imageView.getHeight();
        if (height <= 0) {
            height = lp.height;
        }
        if (height <= 0) {

//                height = imageView.getMaxHeight();
            height = getFieldValue(imageView, "mMaxHeight");
        }
        if (width <= 0) {
            height = displayMetrics.heightPixels;
        }


        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    /**
     * 反射获取对象的私有变量
     *
     * @param object
     * @param fieldName
     * @return
     */

    private int getFieldValue(Object object, String fieldName) {
        int val = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            //todo 研究一下这个不加的话会出现什么情况
            field.setAccessible(true);

            int fieldVal = field.getInt(object);
            if (val > 0 && val < Integer.MAX_VALUE) {
                val = fieldVal;
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return val;
    }

    /**
     * 添加Tasks到队列中
     *
     * @param runnable
     */
    //TODO 不知道loadImage 是放到哪个线程中
    private synchronized void addTasks(Runnable runnable) {
        taskQueue.add(runnable);

        try {
            if (poolThreadHandler == null)
                poolThreadHandlerSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        poolThreadHandler.sendEmptyMessage(0x00);
    }

    private Bitmap getBitmapFromLruCache(String path) {

        return lruCache.get(path);
    }
/****/


}

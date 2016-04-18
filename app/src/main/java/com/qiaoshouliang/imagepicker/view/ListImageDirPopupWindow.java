package com.qiaoshouliang.imagepicker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.qiaoshouliang.imagepicker.Module.ImageFloder;
import com.qiaoshouliang.imagepicker.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaoshouliang on 16/4/15.
 */
public class ListImageDirPopupWindow extends PopupWindow {

    private Context context;
    private List<ImageFloder> imageFloderList;

    private ListView listView;

    private OnDirSelectedListener onDirSelectedListener;

    public ListImageDirPopupWindow(View contentView, int width, int height,
                                   List<ImageFloder> imageFloderList) {
        super(contentView, width, height);
        this.context = contentView.getContext();
        this.imageFloderList = imageFloderList;
        /**
         * 初始化View
         */
        initView(contentView);
        /**
         * 配置PopupWindow
         */
        configPopupWindow();

    }

    private void initView(View contentView) {

        listView = (ListView) contentView.findViewById(R.id.lv_image_dir);
        List<Map<String, Object>> adapterSrc = new ArrayList<>();
        for (int i = 0; i < imageFloderList.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("firstImage", BitmapFactory.decodeFile(imageFloderList.get(i).getFirstImagePath()));
            map.put("count", imageFloderList.get(i).getCount() + "");
            map.put("dir", imageFloderList.get(i).getDir());
            adapterSrc.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(context, adapterSrc, R.layout.list_image_dir_popupwindow_item,
                                                        new String[]{"firstImage", "count", "dir"},
                                                        new int[]{R.id.iv_first_image, R.id.tv_picture_count, R.id.tv_dir_name}
                                                        );

        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap){
                    ((ImageView) view).setImageBitmap((Bitmap) data);
                    return true;
                }else {
                    return false;
                }
            }
        });
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDirSelectedListener.selected(imageFloderList.get(position));
            }
        });
    }

    private void configPopupWindow() {
        /**
         * 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
         * 我觉得这里是API的一个bug
         */
        setBackgroundDrawable(new BitmapDrawable());

        setTouchable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO 应该研究一下Touch Event 是怎么传递的
                Log.e("Action", event.getAction() + "");
                if (event.getAction() == event.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }
    public void setOnDirSelectedListener(OnDirSelectedListener onDirSelectedListener){
        this.onDirSelectedListener =onDirSelectedListener;

    }

    public interface OnDirSelectedListener{
        public void selected(ImageFloder imageFloder);
    }

}

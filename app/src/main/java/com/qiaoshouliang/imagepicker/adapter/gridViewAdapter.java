package com.qiaoshouliang.imagepicker.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.qiaoshouliang.imagepicker.R;
import com.qiaoshouliang.imagepicker.Utils.ImageLoader;

import java.util.List;

/**
 * Created by qiaoshouliang on 16/4/13.
 */
public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private List imageList;
    private String parentPath;
    private LayoutInflater layoutInflater;

    public GridViewAdapter(Context context, List imageList, String parentPath) {
        this.context = context;
        this.imageList = imageList;
        this.parentPath = parentPath;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        Log.e("imageList.size()",imageList.size()+"");
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.grid_view_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_picture);
            viewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.ib_select);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
            viewHolder.imageView.setImageResource(R.drawable.pictures_no);
            viewHolder.imageButton.setImageResource(R.drawable.picture_unselected);

        ImageLoader.getInstance().loadImage(parentPath+"/"+imageList.get(position),viewHolder.imageView);

        return convertView;
    }

    private class ViewHolder {
        public ImageView imageView;
        public ImageButton imageButton;

    }
}

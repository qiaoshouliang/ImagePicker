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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by qiaoshouliang on 16/4/13.
 */
public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> imageList;
    private String parentPath;
    private LayoutInflater layoutInflater;
    /**
     * 存储已经选中的图片的全路径。
     */
    private static Set<String> selectedImage = new HashSet<>();

    public GridViewAdapter(Context context, List imageList, String parentPath) {
        this.context = context;
        this.imageList = imageList;
        this.parentPath = parentPath;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
//        Log.e("imageList.size()",imageList.size()+"");
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final String imagePath = parentPath + "/" + imageList.get(position);
//        Log.e("qq-------imagePath:  ",imagePath);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_view_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_picture);
            viewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.ib_select);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImage.contains(imagePath)) {

                    selectedImage.remove(imagePath);
                    finalViewHolder.imageButton.setImageResource(R.drawable.picture_unselected);
                    finalViewHolder.imageView.setColorFilter(null);
                } else {

                    selectedImage.add(imagePath);
                    finalViewHolder.imageButton.setImageResource(R.drawable.pictures_selected);
                    finalViewHolder.imageView.setColorFilter(0x77000000);
                }
            }
        });


        viewHolder.imageView.setImageResource(R.drawable.pictures_no);
        viewHolder.imageButton.setImageResource(R.drawable.picture_unselected);
        viewHolder.imageView.setColorFilter(null);

        if (selectedImage.contains(imagePath)) {
            viewHolder.imageButton.setImageResource(R.drawable.pictures_selected);
            viewHolder.imageView.setColorFilter(0x77000000);
        }

        ImageLoader.getInstance().loadImage(parentPath + "/" + imageList.get(position), viewHolder.imageView);

        return convertView;
    }

    private class ViewHolder {
        public ImageView imageView;
        public ImageButton imageButton;

    }
}

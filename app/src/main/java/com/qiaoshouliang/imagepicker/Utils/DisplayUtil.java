package com.qiaoshouliang.imagepicker.Utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.qiaoshouliang.imagepicker.Application.ImagePicker;

/**
 * Created by Victor on 2015-8-30.
 */
public class DisplayUtil {

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param density
     *            （DisplayMetrics类中属性density）
     * @return
     */

    public static int px2dip(float pxValue, float density) {
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param density
     *            （DisplayMetrics类中属性density）
     * @return
     */

    public static int dip2px(float dipValue, float density) {
        return (int) (dipValue * density + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */

    public static int px2sp(float pxValue, float fontScale) {
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */

    public static int sp2px(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    /** 改变字符串中个别字体大小
     * @param text
     * @param textSize 要改变的字体大小（sp）
     * @param isDip 字体单位是否是dip
     * @param start 开始位置
     * @param end 结束位置 （前包后不包）
     * @return
     */
    public static SpannableString changeTextSize(String text,int textSize,boolean isDip,int start,int end){
        SpannableString sp = new SpannableString(text);
        sp.setSpan(new AbsoluteSizeSpan(textSize,isDip),
                start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return sp;
    }

    /** 改变字符串中个别字体加粗
     * @param text
     * @param start 开始位置
     * @param end 结束位置 （前包后不包）
     * @return
     */
    public static SpannableString changeTextBold(String text,int start,int end){
        SpannableString sp = new SpannableString(text);
        sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        return sp;
    }

    /**
     * 根据ListView的子项目重新计算ListView的高度，然后把高度再作为LayoutParams设置给ListView
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public int screenWidth;

    /**
     * 屏幕高度
     */
    public static int screenHeight;
    /**
     * 屏幕密度
     */
    public float screenDensity;
    /**
     * 字体缩放比例
     */
    public float scaledDensity;
    /**
     * 获取高度、宽度、密度、缩放比例
     */

    private void getDisplayMetrics() {
        DisplayMetrics metric = ImagePicker.getContext().getResources().getDisplayMetrics();
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;
        screenDensity = metric.density;
        scaledDensity = metric.scaledDensity;
    }
}

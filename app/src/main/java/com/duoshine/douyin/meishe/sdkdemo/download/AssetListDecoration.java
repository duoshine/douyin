package com.duoshine.douyin.meishe.sdkdemo.download;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by wnw on 16-5-22.
 */
public class AssetListDecoration extends RecyclerView.ItemDecoration{

    private Context mContext;
    private Drawable mDivider;
    private int mOrientation;

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    /*
    * 通过获取系统属性中的listDivider来添加，在系统中的AppTheme中设置
    * Add by obtaining the listDivider in the system properties and set in AppTheme in the system
    * */
    public static final int[] ATRRS  = new int[]{
            android.R.attr.listDivider
    };

    public AssetListDecoration(Context context, int orientation) {
        this.mContext = context;
        final TypedArray ta = context.obtainStyledAttributes(ATRRS);
        this.mDivider = ta.getDrawable(0);
        ta.recycle();
        setOrientation(orientation);
    }

    /*
    * 设置屏幕的方向
    * Set screen orientation
    * */
    public void setOrientation(int orientation){
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST){
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL_LIST){
            drawVerticalLine(c, parent, state);
        }else {
            drawHorizontalLine(c, parent, state);

        }
    }

    /*
    * 绘制横线
    * Draw horizontal lines
    * */
    public void drawHorizontalLine(Canvas c, RecyclerView parent, RecyclerView.State state){
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        //添加了footer,不处理
        if(childCount<=1){
            return;
        }
        for (int i = 0; i < childCount-1; i++){
            final View child = parent.getChildAt(i);

            /*
            * 获得child的布局信息
            * Get child layout information
            * */
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }


    /*
    * 画竖线
    * Draw vertical lines
    * */
    public void drawVerticalLine(Canvas c, RecyclerView parent, RecyclerView.State state){
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);

            /*
             * 获得child的布局信息
             * Get child layout information
             * */
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == HORIZONTAL_LIST){
            /*
            * 画横线，就是往下偏移一个分割线的高度
            * Draw a horizontal line, which is offset by the height of a dividing line
            * */
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        }else {
            /*
            * 画竖线，就是往右偏移一个分割线的宽度
            * Draw a vertical line, which is offset by the width of a dividing line to the right
            * */
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}

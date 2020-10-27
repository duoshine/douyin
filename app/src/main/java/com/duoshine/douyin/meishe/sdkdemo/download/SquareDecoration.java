package com.duoshine.douyin.meishe.sdkdemo.download;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * 水平方向2个item的网格布局ItemDecoration
 */
public class SquareDecoration extends RecyclerView.ItemDecoration{

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if(position % 2 == 0){
            outRect.set(20, 10, 10, 10);
        }else{
            outRect.set(10, 10, 20, 10);
        }
        //super.getItemOffsets(outRect, view, parent, state);
    }
}

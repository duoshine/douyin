package com.duoshine.douyin.meishe.sdkdemo;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2018/11/15 13:37
 *
 * 平分一屏的宽度，间距的最大值不会超出均分给他的宽度减去item的宽度，参考：https://blog.csdn.net/lovext4098477/article/details/80419201
 * Split the width of a screen evenly, the maximum value of the spacing will not exceed the width given to him minus the width of the item,
 * reference: https://blog.csdn.net/lovext4098477/article/details/80419201
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int screenWidth;
    private int itemWidth;
    private List<Integer> spanRightList;

    public GridSpacingItemDecoration(Context context, int spanCount, int itemWidth) {
        this.spanCount = spanCount;
        this.itemWidth = itemWidth;
        screenWidth = ScreenUtils.getScreenWidth(context);
        spanRightList = new ArrayList<>();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int surplus = screenWidth - spanCount * itemWidth;
        if (surplus<=0){
            return;
        }
        int realSpace = (screenWidth - spanCount * itemWidth) / (spanCount + 1);
        int itemWidthInScreen = screenWidth / spanCount;
        int column = position % spanCount; // item column
        if (column == 0) {
            outRect.left = realSpace;
            outRect.right = itemWidthInScreen - itemWidth - outRect.left;
            spanRightList.add(outRect.right);
        } else if (column == (spanCount - 1)) {
            outRect.left = realSpace- spanRightList.get(column-1);
            outRect.right = itemWidthInScreen - itemWidth - outRect.left;
            spanRightList.add(outRect.right);
        } else {
            outRect.left = realSpace- spanRightList.get(column-1);
            outRect.right = itemWidthInScreen - itemWidth - outRect.left;
            spanRightList.add(outRect.right);
        }

    }
}
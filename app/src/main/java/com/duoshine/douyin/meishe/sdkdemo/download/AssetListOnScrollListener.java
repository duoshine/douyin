package com.duoshine.douyin.meishe.sdkdemo.download;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView滑动监听
 * RecyclerView sliding listener
 */

public abstract class AssetListOnScrollListener extends RecyclerView.OnScrollListener {

    // 用来标记是否正在向上滑动
    /*
    * 用来标记是否正在向上滑动
    * Used to mark whether it is sliding up
    * */
    private boolean isSlidingUpward = false;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            /*
            * 滑动停止时，获取最后一个完全显示的itemPosition
            * Get the last fully displayed itemPosition when the slide stops
            * */
            int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
            int itemCount = manager.getItemCount();

            /*
            * 判断是否滑动到了最后一个item，并且是向上滑动
            * Determine if the last item is swiped, and it is swiped up
            * */
            if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                /*
                * 加载更多
                * It loads more
                * */
                onLoadMore();
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        /*
        * 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
        * A dy value greater than 0 indicates that it is sliding up, and a value less than or equal to 0 indicates that it is stopping or sliding down.
        * */
        isSlidingUpward = dy > 0;
    }

    /**
     * 加载更多回调
     * Load more callbacks
     */
    public abstract void onLoadMore();
}

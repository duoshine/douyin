package com.duoshine.douyin.meishe.sdkdemo.edit.grallyRecyclerView;


import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by CaoZhiChao on 2018/5/31 11:31
 */
public class ItemTouchHelper extends  androidx.recyclerview.widget.ItemTouchHelper.Callback{
//public class ItemTouchHelper extends android.support.v7.widget.helper.ItemTouchHelper.Callback {
    private final ItemTouchListener mItemTouchListener;
    private final String TAG = "ItemTouchHelper";

    public ItemTouchHelper(ItemTouchListener listener) {
        this.mItemTouchListener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags =androidx.recyclerview.widget.ItemTouchHelper.LEFT | androidx.recyclerview.widget.ItemTouchHelper.RIGHT; //开启上下
        int swipeFlags = androidx.recyclerview.widget.ItemTouchHelper.START | androidx.recyclerview.widget.ItemTouchHelper.END;//开启左右
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
        mItemTouchListener.onItemMoved(viewHolder.getAdapterPosition(), viewHolder1.getAdapterPosition());
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }

    /**
     * Item被选中时候回调，actionState表示当前Item的状态
     * Callback when the item is selected, actionState indicates the current state of the item
     * *
     * * ItemTouchHelper.ACTION_STATE_IDLE
     * * ItemTouchHelper.ACTION_STATE_SWIPE
     * * ItemTouchHelper#ACTION_STATE_DRAG
     */

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) { // item被选中的操作
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 用户操作完毕或者动画完毕后会被调用
     * Will be called after user operation or animation is completed
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) { // 操作完毕后恢复
        recyclerView.getAdapter().notifyItemRangeChanged(0, recyclerView.getAdapter().getItemCount(), TAG);
        /*
        * 模拟移动，才能触发自动校准
        * Simulate movement to trigger automatic calibration
        * */
        recyclerView.smoothScrollBy(1, 0);
        super.clearView(recyclerView, viewHolder);
    }

}

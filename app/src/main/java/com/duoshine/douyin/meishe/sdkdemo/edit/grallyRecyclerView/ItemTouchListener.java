package com.duoshine.douyin.meishe.sdkdemo.edit.grallyRecyclerView;

/**
 * Created by CaoZhiChao on 2018/5/31 11:31
 */
public interface ItemTouchListener {
    /**
     * 上下拖拽时回调方法，adpater将两个位置的item调换位置
     * Callback method when dragging up and down, adpater swaps items in two positions
     * @param fromPosition
     * @param toPosition
     */
    void onItemMoved(int fromPosition, int toPosition);
    /*
    * 左右删除item
    * Delete items left and right
    * */
    void onItemDismiss(int position);
    /*
    * 清除所有的
    * Clear all
    * */
    void removeAll();
}

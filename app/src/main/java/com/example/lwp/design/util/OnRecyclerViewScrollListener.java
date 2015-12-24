package com.example.lwp.design.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * recycleView滚动到底部事件监听，实现滚动到底部监听
 * Created by heshen on 15/12/23.
 */
public abstract class OnRecyclerViewScrollListener extends RecyclerView.OnScrollListener implements OnBottomListener {
    public String TAG = getClass().getSimpleName();

    public enum LayoutManagerTypeEnum {
        LINEAR,
        GRID,
        STAGGERED_GRID

    }

    LayoutManagerTypeEnum layoutManagerTypeEnum;

    /**
     * 最后一个的位置 for StaggeredGridLayoutManager
     */
    int[] lastPositions;

    /**
     * 最后一个可见的item的位置
     */
    int lastVisibleItemPosition;
    /**
     * 当前滑动的状态
     */
    int currentScrollState = 0;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        LayoutManagerTypeEnum layoutManagerTypeEnum = setLayoutManagerType(layoutManager);
        switch (layoutManagerTypeEnum){
            case LINEAR:
                lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                break;
            case GRID:
                lastVisibleItemPosition =  ((GridLayoutManager)layoutManager).findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if(lastPositions == null){
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }

                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                break;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        currentScrollState = newState;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if(visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE
         && lastVisibleItemPosition >= totalItemCount -1){
            // 滚动到底部
            onBottom();
        }

    }

    private int findMax(int[] lastPositions) {

        int max = 0;
        for (int v:lastPositions){
           max = Math.max(max,v);
        }
        return max;
    }

    private LayoutManagerTypeEnum setLayoutManagerType(RecyclerView.LayoutManager layoutManager){
        if(layoutManagerTypeEnum == null){

            if(layoutManager instanceof GridLayoutManager) {
                layoutManagerTypeEnum = LayoutManagerTypeEnum.GRID;
            }else if(layoutManager instanceof LinearLayoutManager){
                layoutManagerTypeEnum = LayoutManagerTypeEnum.LINEAR;
            } else  if(layoutManager instanceof StaggeredGridLayoutManager){
                layoutManagerTypeEnum = LayoutManagerTypeEnum.STAGGERED_GRID;
            } else {
                throw new RuntimeException("unsupported layoutmanager used, Now only support LinearLayoutManager GridLayoutManager and StaggeredGridLayoutManager");
            }
        }
        return layoutManagerTypeEnum;
    }
}

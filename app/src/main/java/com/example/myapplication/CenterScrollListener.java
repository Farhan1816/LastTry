package com.example.myapplication;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CenterScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private boolean isScrolling = false;

    public CenterScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            isScrolling = true;
        } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            isScrolling = false;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (isScrolling) {
            int firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
            int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
            int visibleItemCount = lastVisibleItemPosition - firstVisibleItemPosition + 1;
            int totalItemCount = layoutManager.getItemCount();

            if (visibleItemCount < totalItemCount) {
                int middlePosition = firstVisibleItemPosition + visibleItemCount / 2;
                recyclerView.smoothScrollToPosition(middlePosition);
            }
        }
    }
}

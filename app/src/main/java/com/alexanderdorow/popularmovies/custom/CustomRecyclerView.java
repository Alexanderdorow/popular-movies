package com.alexanderdorow.popularmovies.custom;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public final class CustomRecyclerView
        extends RecyclerView {

    private static final String SAVED_INSTANCE_SUPER_STATE = "super_state";
    private static final String SAVED_LAYOUT_MANAGER = "layout_manager_state";

    private Parcelable layoutManagerSavedState;

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_INSTANCE_SUPER_STATE, super.onSaveInstanceState());
        bundle.putParcelable(SAVED_LAYOUT_MANAGER, this.getLayoutManager().onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            layoutManagerSavedState = bundle.getParcelable(SAVED_LAYOUT_MANAGER);
            state = bundle.getParcelable(SAVED_INSTANCE_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    private void restorePosition() {
        if (layoutManagerSavedState != null) {
            this.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
            layoutManagerSavedState = null;
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        restorePosition();
    }
}
package com.me.pa.utils;

import android.os.Handler;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class ConnectionErrorSheetController {
    private final BottomSheetBehavior<View> bottomSheetBehavior;
    private final Handler handler;
    private Runnable runnable;
    private boolean expanded = false;

    public ConnectionErrorSheetController(View view) {
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        handler = new Handler();
        runnable = () -> {

        };
    }

    public void expande() {
        if (!expanded) {
            expanded = true;
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            runnable = () -> {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                expanded = false;
            };
            handler.postDelayed(runnable, 2000);
        }
    }

    public void forceCollapse() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        expanded = false;
        handler.removeCallbacks(runnable);
    }

    public int getState() {
        return bottomSheetBehavior.getState();
    }
}

package com.mtramin.donethat.ui.animator;/*
 * Copyright (c) 2015 Daimler AG / Moovel GmbH
 *
 * All rights reserved
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemAnimator for a RecyclerView that will animate it's ViewHolders
 * <p>
 * Views will be animated in from the bottom
 * <p>
 * Only supports the add animation for now and is not suited for inserting single items.
 * Please only use it to add a complete list of items!
 */
public class RecyclerViewItemAnimator extends RecyclerView.ItemAnimator {

    private List<RecyclerView.ViewHolder> pendingViewHolders = new ArrayList<>();

    private float animationDuration;

    public RecyclerViewItemAnimator(Context context) {

        this.animationDuration = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
    }

    @Override
    public void runPendingAnimations() {
        for (RecyclerView.ViewHolder viewHolder : pendingViewHolders) {
            View target = viewHolder.itemView;

            target.setTranslationY(target.getMeasuredHeight() / 2);

            target.animate()
                    .setDuration((int) animationDuration)
                    .setInterpolator(new DecelerateInterpolator())
                    .alpha(1.0f)
                    .translationY(0)
                    .setStartDelay((long) (animationDuration * viewHolder.getAdapterPosition()) / 8)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            RecyclerView.ViewHolder holder = viewHolder;
                            pendingViewHolders.remove(holder);
                        }
                    });

        }
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setAlpha(0.0f);
        return pendingViewHolders.add(viewHolder);
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
        return false;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder viewHolder) {
    }

    @Override
    public void endAnimations() {
    }

    @Override
    public boolean isRunning() {
        return !pendingViewHolders.isEmpty();
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }
}
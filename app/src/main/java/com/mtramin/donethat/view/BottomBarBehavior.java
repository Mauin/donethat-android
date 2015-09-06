package com.mtramin.donethat.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.mtramin.donethat.R;

/**
 * {@link android.support.design.widget.CoordinatorLayout.Behavior} for a {@link View} at the bottom of a {@link RecyclerView}.
 * This bar will either be shown at the bottom of the screen (if there is still enough space).
 * or it will show up after the {@link RecyclerView} has scrolled all the way to the bottom.
 * <p>
 * Behaves similar to a {@link android.support.v7.widget.Toolbar} exits the screen once the {@link RecyclerView} scrolls
 */
public class BottomBarBehavior extends CoordinatorLayout.Behavior<View> {

    private boolean hasInitialLayoutPass = false;

    public BottomBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    /**
     * Hijacks the first layout passes to determine the optimum position for the bottom bar.
     */
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        // Only calculate start position if layout was done and only do it once
        if (!child.isLaidOut() || hasInitialLayoutPass) {
            return super.onLayoutChild(parent, child, layoutDirection);
        }

        hasInitialLayoutPass = true;

        int childHeight = child.getHeight();
        int parentHeight = parent.getHeight();

        RecyclerView recyclerView = findRecyclerView(parent, child);

        // Set bottom bar out of view
        calculateBottomBar(child, recyclerView, childHeight);

        // Check the position of the last item in the RecyclerView
        // Using this position we calculate if the bottom bar can be shown
        int itemCount = recyclerView.getAdapter().getItemCount();
        View lastChild = recyclerView.getLayoutManager().getChildAt(itemCount - 1);
        if (lastChild != null) {
            int bottomBarPadding = parent.getContext().getResources().getDimensionPixelSize(R.dimen.not_so_small_margin);
            int remainingSpace = parentHeight - lastChild.getBottom() - getToolbarHeight(parent) - bottomBarPadding;
            int translationY = childHeight - remainingSpace;
            calculateBottomBar(child, recyclerView, translationY);
        }

        return super.onLayoutChild(parent, child, layoutDirection);
    }

    private int getToolbarHeight(CoordinatorLayout parent) {
        View toolbar = parent.findViewById(R.id.toolbar);

        return toolbar == null ? 0 : toolbar.getHeight();
    }

    private RecyclerView findRecyclerView(CoordinatorLayout parent, View child) {
        RecyclerView target = null;
        for (View possibleTarget : parent.getDependencies(child)) {
            if (possibleTarget instanceof RecyclerView) {
                target = (RecyclerView) possibleTarget;
            }
        }

        if (target == null) {
            throw new IllegalStateException("BottomBarBehavior expects a RecyclerView as the child of the CoordinatorLayout");
        }

        return target;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        if (nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL) {
            return true;
        }
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final View child, final View target, final int dxConsumed, final int dyConsumed, final int dxUnconsumed, final int dyUnconsumed) {
        if (dyConsumed < 0) {
            addBottomBarOffset(child, target, dyConsumed);
        } else if (dyUnconsumed > 0) {
            addBottomBarOffset(child, target, dyUnconsumed);
        }
    }

    private void addBottomBarOffset(View child, View target, int scrollOffsetY) {
        float translationY = child.getTranslationY();
        translationY -= scrollOffsetY;

        calculateBottomBar(child, target, (int) translationY);
    }

    /**
     * Calculates the translation of the "BottomBar" View as well as a padding for the {@link RecyclerView}
     * to make it seem like it continues to scroll
     *
     * @param child        Child view
     * @param target       Target {@link RecyclerView}
     * @param translationY translation of the child
     */
    private void calculateBottomBar(View child, View target, int translationY) {
        int childHeight = child.getHeight();

        // Keep the translation in the range of the View height and set it to the child
        translationY = Math.max(0, Math.min(childHeight, translationY));
        child.setTranslationY(translationY);

        // Calculate and set padding for the target recyclerview
        int padding = childHeight - translationY;
        target.setPadding(target.getPaddingLeft(), target.getPaddingTop(), target.getPaddingRight(), padding);
    }
}

package com.bignerdranch.expandablerecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.bignerdranch.expandablerecyclerview.model.Parent;


/**
 * ViewHolder for a {@link Parent}
 * Keeps track of expanded state and holds callbacks which can be used to
 * trigger expansion-based events.
 *
 * @author Ryan Brooks
 * @version 1.0
 * @since 5/27/2015
 */
public class ParentViewHolder<P extends Parent<C>, C> extends RecyclerView.ViewHolder implements View.OnTouchListener {
    @Nullable
    private ParentViewHolderExpandCollapseListener mParentViewHolderExpandCollapseListener;
    private boolean mExpanded;
    private Context mContext;
    P mParent;
    ExpandableRecyclerAdapter mExpandableAdapter;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    /**
     * Empowers {@link com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter}
     * implementations to be notified of expand/collapse state change events.
     */
    interface ParentViewHolderExpandCollapseListener {

        /**
         * Called when a parent is expanded.
         *
         * @param flatParentPosition The index of the parent in the list being expanded
         */
        @UiThread
        void onParentExpanded(int flatParentPosition);

        /**
         * Called when a parent is collapsed.
         *
         * @param flatParentPosition The index of the parent in the list being collapsed
         */
        @UiThread
        void onParentCollapsed(int flatParentPosition);
    }

    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    @UiThread
    public ParentViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        mExpanded = false;
        mContext = context;
    }

    /**
     * @return the Parent associated with this ViewHolder
     */
    @UiThread
    public P getParent() {
        return mParent;
    }

    /**
     * Returns the adapter position of the Parent associated with this ParentViewHolder
     *
     * @return The adapter position of the Parent if it still exists in the adapter.
     * RecyclerView.NO_POSITION if item has been removed from the adapter,
     * RecyclerView.Adapter.notifyDataSetChanged() has been called after the last
     * layout pass or the ViewHolder has already been recycled.
     */
    @UiThread
    public int getParentAdapterPosition() {
        int flatPosition = getAdapterPosition();
        if (flatPosition == RecyclerView.NO_POSITION) {
            return flatPosition;
        }

        return mExpandableAdapter.getNearestParentPosition(flatPosition);
    }

    /**
     * Sets a {@link android.view.View.OnTouchListener} on the entire parent
     * As OnClickListener is been called even on swipe OnTouchListener is used
     * view to trigger expansion.
     */
    @UiThread
    public void setMainItemClickToExpand() {
        final GestureDetector
                gestureDetector = new GestureDetector(mContext, new SingleTapGestureDetector());
        itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    onParentClick();
                    return true;
                }
                return false;
            }
        });
    }

    private void onParentClick() {
        if (mExpanded) {
            collapseView();
        } else {
            expandView();
        }
    }

    /**
     * Returns expanded state for the {@link Parent}
     * corresponding to this {@link ParentViewHolder}.
     *
     * @return true if expanded, false if not
     */
    @UiThread
    public boolean isExpanded() {
        return mExpanded;
    }

    /**
     * Setter method for expanded state, used for initialization of expanded state.
     * changes to the state are given in {@link #onExpansionToggled(boolean)}
     *
     * @param expanded true if expanded, false if not
     */
    @UiThread
    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    /**
     * Callback triggered when expansion state is changed, but not during
     * initialization.
     * <p>
     * Useful for implementing animations on expansion.
     *
     * @param expanded true if view is expanded before expansion is toggled,
     *                 false if not
     */
    @UiThread
    public void onExpansionToggled(boolean expanded) {

    }

    /**
     * Setter for the {@link ParentViewHolderExpandCollapseListener} implemented in
     * {@link com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter}.
     *
     * @param parentViewHolderExpandCollapseListener The {@link ParentViewHolderExpandCollapseListener} to set on the {@link ParentViewHolder}
     */
    @UiThread
    void setParentViewHolderExpandCollapseListener(ParentViewHolderExpandCollapseListener parentViewHolderExpandCollapseListener) {
        mParentViewHolderExpandCollapseListener = parentViewHolderExpandCollapseListener;
    }

    /**
     * Used to determine whether a click in the entire parent {@link View}
     * should trigger row expansion.
     * <p>
     * If you return false, you can call {@link #expandView()} to trigger an
     * expansion in response to a another event or {@link #collapseView()} to
     * trigger a collapse.
     *
     * @return true to set an {@link android.view.View.OnClickListener} on the item view
     */
    @UiThread
    public boolean shouldItemViewClickToggleExpansion() {
        return true;
    }

    /**
     * Triggers expansion of the parent.
     */
    @UiThread
    protected void expandView() {
        setExpanded(true);
        onExpansionToggled(false);

        if (mParentViewHolderExpandCollapseListener != null) {
            mParentViewHolderExpandCollapseListener.onParentExpanded(getAdapterPosition());
        }
    }

    /**
     * Triggers collapse of the parent.
     */
    @UiThread
    protected void collapseView() {
        setExpanded(false);
        onExpansionToggled(true);

        if (mParentViewHolderExpandCollapseListener != null) {
            mParentViewHolderExpandCollapseListener.onParentCollapsed(getAdapterPosition());
        }
    }

    /**
     * Gesture Detector used to detect whether the Gesture is single tap or not
     */
    private class SingleTapGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }
    }
}

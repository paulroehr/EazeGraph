/**
 *
 *   Copyright (C) 2014 Paul Cech
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.eazegraph.lib.charts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.animation.ValueAnimator;

import org.eazegraph.lib.R;
import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.utils.Utils;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


/**
 * This is the main chart class and should be inherited by every graph. This class provides some general
 * methods and variables, which are needed and used by every type of chart.
 */
public abstract class BaseChart extends ViewGroup {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    protected BaseChart(Context context) {
        super(context);

        mLegendHeight   = Utils.dpToPx(DEF_LEGEND_HEIGHT);
        mLegendTextSize = Utils.dpToPx(DEF_LEGEND_TEXT_SIZE);
        mLegendColor    = DEF_LEGEND_COLOR;
        mAnimationTime  = DEF_ANIMATION_TIME;
        mShowDecimal    = DEF_SHOW_DECIMAL;
        mEmptyDataText  = DEF_EMPTY_DATA_TEXT;
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p/>
     * <p/>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see #View(android.content.Context, android.util.AttributeSet, int)
     */
    public BaseChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BaseChart,
                0, 0
        );

        try {

            mLegendHeight       = a.getDimension(R.styleable.BaseChart_egLegendHeight,     Utils.dpToPx(DEF_LEGEND_HEIGHT));
            mLegendTextSize     = a.getDimension(R.styleable.BaseChart_egLegendTextSize,   Utils.dpToPx(DEF_LEGEND_TEXT_SIZE));
            mAnimationTime      = a.getInt(R.styleable.BaseChart_egAnimationTime,          DEF_ANIMATION_TIME);
            mShowDecimal        = a.getBoolean(R.styleable.BaseChart_egShowDecimal,        DEF_SHOW_DECIMAL);
            mLegendColor        = a.getColor(R.styleable.BaseChart_egLegendColor,          DEF_LEGEND_COLOR);
            mEmptyDataText      = a.getString(R.styleable.BaseChart_egEmptyDataText);

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        if(mEmptyDataText == null) {
            mEmptyDataText = DEF_EMPTY_DATA_TEXT;
        }

    }

    /**
     * Returns the current height of the legend view
     * @return Legend view height
     */
    public float getLegendHeight() {
        return mLegendHeight;
    }

    /**
     * Sets and updates the height of the legend view.
     * @param _legendHeight The new legend view height.
     */
    public void setLegendHeight(float _legendHeight) {
        mLegendHeight = Utils.dpToPx(_legendHeight);

        if(getData().size() > 0)
            onDataChanged();
    }

    /**
     * Returns the text size which is used by the legend.
     * @return Size of the legend text.
     */
    public float getLegendTextSize() {
        return mLegendTextSize;
    }

    /**
     * Sets the size of the text which is displayed in the legend. (Interpreted as sp value)
     * @param _legendTextSize Size of the legend text.
     */
    public void setLegendTextSize(float _legendTextSize) {
        mLegendTextSize = Utils.dpToPx(_legendTextSize);
    }

    /**
     * Returns the animation time in milliseconds.
     * @return Animation time.
     */
    public int getAnimationTime() {
        return mAnimationTime;
    }

    /**
     * Sets the animation time in milliseconds.
     * @param _animationTime Animation time in milliseconds.
     */
    public void setAnimationTime(int _animationTime) {
        mAnimationTime = _animationTime;
    }

    public boolean isShowDecimal() {
        return mShowDecimal;
    }

    public void setShowDecimal(boolean _showDecimal) {
        mShowDecimal = _showDecimal;
        invalidate();
    }

    public int getLegendColor() {
        return mLegendColor;
    }

    public void setLegendColor(int _legendColor) {
        mLegendColor = _legendColor;
    }

    public String getEmptyDataText() {
        return mEmptyDataText;
    }

    public void setEmptyDataText(String _emptyDataText) {
        mEmptyDataText = _emptyDataText;
    }

    /**
     * Reloads the view and everything will be drawn again.
     */
    public void reloadView () {
        invalidateGlobal();
    }

    /**
     * Returns the datasets which are currently inserted.
     * @return the datasets
     */
    public abstract List<? extends BaseModel> getData();

    /**
     * Resets and clears the data object.
     */
    public abstract void clearChart();

    /**
     * Should be called when the dataset changed and the graph should update and redraw.
     * Graph implementations might overwrite this method to do more work than just call onDataChanged()
     */
    public void update() {
        onDataChanged();
    }

    /**
     * Starts the chart animation.
     */
    public void startAnimation() {
        if(mRevealAnimator != null) {
            mStartedAnimation = true;
            mRevealAnimator.setDuration(mAnimationTime).start();
        }
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w    Current width of this view.
     * @param h    Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mLeftPadding    = getPaddingLeft();
        mTopPadding     = getPaddingTop();
        mRightPadding   = getPaddingRight();
        mBottomPadding  = getPaddingBottom();

        mGraph.layout(mLeftPadding, mTopPadding, w - mRightPadding, (int) (h - mLegendHeight - mBottomPadding));
        mGraphOverlay.layout(mLeftPadding, mTopPadding, w - mRightPadding, (int) (h - mLegendHeight - mBottomPadding));
        mLegend.layout(mLeftPadding, (int) (h - mLegendHeight - mBottomPadding), w - mRightPadding, h - mBottomPadding);
    }

    /**
     * This is the main entry point after the graph has been inflated. Used to initialize the graph
     * and its corresponding members.
     */
    protected void initializeGraph() {
        mGraph = new Graph(getContext());
        addView(mGraph);

        mGraphOverlay = new GraphOverlay(getContext());
        addView(mGraphOverlay);

        mLegend = new Legend(getContext());
        addView(mLegend);
    }

    /**
     * Should be called after new data is inserted. Will be automatically called, when the view dimensions
     * has changed.
     */
    protected void onDataChanged() {
        invalidateGlobal();
    }

    /**
     * Invalidates graph and legend and forces them to be redrawn.
     */
    protected final void invalidateGlobal() {
        mGraph.invalidate();
        mGraphOverlay.invalidate();
        mLegend.invalidate();
    }

    protected final void invalidateGraph() {
        mGraph.invalidate();
    }

    protected final void invalidateGraphOverlay() {
        mGraphOverlay.invalidate();
    }

    protected final void invalidateLegend() {
        mLegend.invalidate();
    }

    // #############################################################################################
    //                          Override methods from view layers
    // ##############################################################################################

    protected void onGraphDraw(Canvas _Canvas) {

    }

    protected void onGraphOverlayDraw(Canvas _Canvas) {

    }

    protected void onLegendDraw(Canvas _Canvas) {

    }

    protected boolean onGraphOverlayTouchEvent(MotionEvent _Event) {
        return super.onTouchEvent(_Event);
    }

    protected void onGraphSizeChanged(int w, int h, int oldw, int oldh) {

    }

    protected void onGraphOverlaySizeChanged(int w, int h, int oldw, int oldh) {

    }

    protected void onLegendSizeChanged(int w, int h, int oldw, int oldh) {

    }

    //##############################################################################################
    // Graph
    //##############################################################################################
    protected class Graph extends View {
        /**
         * Simple constructor to use when creating a view from code.
         *
         * @param context The Context the view is running in, through which it can
         *                access the current theme, resources, etc.
         */
        private Graph(Context context) {
            super(context);
        }

        /**
         * Enable hardware acceleration (consumes memory)
         */
        public void accelerate() {
            Utils.setLayerToHW(this);
        }

        /**
         * Disable hardware acceleration (releases memory)
         */
        public void decelerate() {
            Utils.setLayerToSW(this);
        }

        /**
         * Implement this to do your drawing.
         *
         * @param canvas the canvas on which the background will be drawn
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (Build.VERSION.SDK_INT < 11) {
                mTransform.set(canvas.getMatrix());
                mTransform.preRotate(mRotation, mPivot.x, mPivot.y);
                canvas.setMatrix(mTransform);
            }

            onGraphDraw(canvas);
        }

        /**
         * This is called during layout when the size of this view has changed. If
         * you were just added to the view hierarchy, you're called with the old
         * values of 0.
         *
         * @param w    Current width of this view.
         * @param h    Current height of this view.
         * @param oldw Old width of this view.
         * @param oldh Old height of this view.
         */
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mGraphWidth = w;
            mGraphHeight = h;

            onGraphSizeChanged(w, h, oldw, oldh);
        }

        @Override
        public boolean performClick() {
            return super.performClick();
        }

        public void rotateTo(float pieRotation) {
            mRotation = pieRotation;
            if (Build.VERSION.SDK_INT >= 11) {
                setRotation(pieRotation);
            } else {
                this.invalidate();
            }
        }

        public void setPivot(float x, float y) {
            mPivot.x = x;
            mPivot.y = y;
            if (Build.VERSION.SDK_INT >= 11) {
                setPivotX(x);
                setPivotY(y);
            } else {
                this.invalidate();
            }
        }

        private float  mRotation  = 0;
        private Matrix mTransform = new Matrix();
        private PointF mPivot     = new PointF();

    }

    //##############################################################################################
    // GraphOverlay
    //##############################################################################################
    protected class GraphOverlay extends View {
        /**
         * Simple constructor to use when creating a view from code.
         *
         * @param context The Context the view is running in, through which it can
         *                access the current theme, resources, etc.
         */
        private GraphOverlay(Context context) {
            super(context);

        }

        /**
         * Enable hardware acceleration (consumes memory)
         */
        public void accelerate() {
            Utils.setLayerToHW(this);
        }

        /**
         * Disable hardware acceleration (releases memory)
         */
        public void decelerate() {
            Utils.setLayerToSW(this);
        }

        /**
         * Implement this to do your drawing.
         *
         * @param canvas the canvas on which the background will be drawn
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            onGraphOverlayDraw(canvas);
        }

        /**
         * This is called during layout when the size of this view has changed. If
         * you were just added to the view hierarchy, you're called with the old
         * values of 0.
         *
         * @param w    Current width of this view.
         * @param h    Current height of this view.
         * @param oldw Old width of this view.
         * @param oldh Old height of this view.
         */
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            onGraphOverlaySizeChanged(w, h, oldw, oldh);
        }

        /**
         * Implement this method to handle touch screen motion events.
         * <p/>
         * If this method is used to detect click actions, it is recommended that
         * the actions be performed by implementing and calling
         * {@link #performClick()}. This will ensure consistent system behavior,
         * including:
         * <ul>
         * <li>obeying click sound preferences
         * <li>dispatching OnClickListener calls
         * <li>handling {@link AccessibilityNodeInfo#ACTION_CLICK ACTION_CLICK} when
         * accessibility features are enabled
         * </ul>
         *
         * @param event The motion event.
         * @return True if the event was handled, false otherwise.
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return onGraphOverlayTouchEvent(event);
        }

        @Override
        public boolean performClick() {
            return super.performClick();
        }

    }

    //##############################################################################################
    // Legend
    //##############################################################################################
    protected class Legend extends View {
        /**
         * Simple constructor to use when creating a view from code.
         *
         * @param context The Context the view is running in, through which it can
         *                access the current theme, resources, etc.
         */
        private Legend(Context context) {
            super(context);
        }

        /**
         * Enable hardware acceleration (consumes memory)
         */
        public void accelerate() {
            Utils.setLayerToHW(this);
        }

        /**
         * Disable hardware acceleration (releases memory)
         */
        public void decelerate() {
            Utils.setLayerToSW(this);
        }

        /**
         * Implement this to do your drawing.
         *
         * @param canvas the canvas on which the background will be drawn
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            onLegendDraw(canvas);
        }

        /**
         * This is called during layout when the size of this view has changed. If
         * you were just added to the view hierarchy, you're called with the old
         * values of 0.
         *
         * @param w    Current width of this view.
         * @param h    Current height of this view.
         * @param oldw Old width of this view.
         * @param oldh Old height of this view.
         */
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mLegendWidth = w;
            mLegendHeight = h;

            onLegendSizeChanged(w, h, oldw, oldh);
        }
    }

    //##############################################################################################
    // Variables
    //##############################################################################################

    protected final static NumberFormat mFormatter = NumberFormat.getInstance(Locale.getDefault());

    public static final float   DEF_LEGEND_HEIGHT       = 58.f;
    public static final int     DEF_LEGEND_COLOR        = 0xFF898989;
    // will be interpreted as sp value
    public static final float   DEF_LEGEND_TEXT_SIZE    = 12.f;
    public static final int     DEF_ANIMATION_TIME      = 2000;
    public static final boolean DEF_SHOW_DECIMAL        = false;
    public static final String  DEF_EMPTY_DATA_TEXT     = "No Data available";

    protected Graph             mGraph;
    protected GraphOverlay      mGraphOverlay;
    protected Legend            mLegend;

    protected int               mHeight;
    protected int               mWidth;

    protected int               mGraphWidth;
    protected int               mGraphHeight;

    protected float             mLegendWidth;
    protected float             mLegendHeight;
    protected float             mLegendTextSize;
    protected int               mLegendColor;

    protected int               mLeftPadding;
    protected int               mTopPadding;
    protected int               mRightPadding;
    protected int               mBottomPadding;

    protected String            mEmptyDataText;

    protected float             mMaxFontHeight;
    protected float             mLegendTopPadding = Utils.dpToPx(4.f);

    protected boolean           mShowDecimal;

    protected ValueAnimator     mRevealAnimator     = null;
    protected float             mRevealValue        = 1.0f;
    protected int               mAnimationTime      = 1000;
    protected boolean           mStartedAnimation   = false;

}

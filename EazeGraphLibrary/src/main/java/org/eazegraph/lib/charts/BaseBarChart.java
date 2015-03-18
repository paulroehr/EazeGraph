/**
 *
 *   Copyright (C) 2015 Paul Cech
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
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.OverScroller;
import android.widget.Scroller;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import org.eazegraph.lib.R;
import org.eazegraph.lib.communication.IOnBarClickedListener;
import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.utils.Utils;

import java.util.List;

/**
 * The abstract class for every type of bar chart, which handles the general calculation for the bars.
 */
public abstract class BaseBarChart extends BaseChart {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public BaseBarChart(Context context) {
        super(context);

        mShowValues         = DEF_SHOW_VALUES;
        mBarWidth           = Utils.dpToPx(DEF_BAR_WIDTH);
        mBarMargin          = Utils.dpToPx(DEF_BAR_MARGIN);
        mFixedBarWidth      = DEF_FIXED_BAR_WIDTH;
        mScrollEnabled      = DEF_SCROLL_ENABLED;
        mVisibleBars        = DEF_VISIBLE_BARS;
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
    public BaseBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BaseBarChart,
                0, 0
        );

        try {

            mShowValues         = a.getBoolean(R.styleable.BaseBarChart_egShowValues,         DEF_SHOW_VALUES);
            mBarWidth           = a.getDimension(R.styleable.BaseBarChart_egBarWidth,         Utils.dpToPx(DEF_BAR_WIDTH));
            mBarMargin          = a.getDimension(R.styleable.BaseBarChart_egBarMargin,        Utils.dpToPx(DEF_BAR_MARGIN));
            mFixedBarWidth      = a.getBoolean(R.styleable.BaseBarChart_egFixedBarWidth,      DEF_FIXED_BAR_WIDTH);
            mScrollEnabled      = a.getBoolean(R.styleable.BaseBarChart_egEnableScroll,       DEF_SCROLL_ENABLED);
            mVisibleBars        = a.getInt(R.styleable.BaseBarChart_egVisibleBars,            DEF_VISIBLE_BARS);

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

    }

    /**
     * Returns the onBarClickedListener.
     * @return
     */
    public IOnBarClickedListener getOnBarClickedListener() {
        return mListener;
    }

    /**
     * Sets the onBarClickedListener
     * @param _listener The listener which will be set.
     */
    public void setOnBarClickedListener(IOnBarClickedListener _listener) {
        mListener = _listener;
    }

    /**
     * Returns the width of a bar.
     * @return
     */
    public float getBarWidth() {
        return mBarWidth;
    }

    /**
     * Sets the width of bars.
     * @param _barWidth Width of bars
     */
    public void setBarWidth(float _barWidth) {
        mBarWidth = _barWidth;
        onDataChanged();
    }

    /**
     * Checks if the bars have a fixed width or is dynamically calculated.
     * @return
     */
    public boolean isFixedBarWidth() {
        return mFixedBarWidth;
    }

    /**
     * Sets if the bar width should be fixed or dynamically caluclated
     * @param _fixedBarWidth True if it should be a fixed width.
     */
    public void setFixedBarWidth(boolean _fixedBarWidth) {
        mFixedBarWidth = _fixedBarWidth;
        onDataChanged();
    }

    /**
     * Returns the bar margin, which is set by user if the bar widths are calculated dynamically.
     * @return
     */
    public float getBarMargin() {
        return mBarMargin;
    }

    /**
     * Sets the bar margin.
     * @param _barMargin Bar margin
     */
    public void setBarMargin(float _barMargin) {
        mBarMargin = _barMargin;
        onDataChanged();
    }

    public boolean isScrollEnabled() {
        return mScrollEnabled;
    }

    public void setScrollEnabled(boolean _scrollEnabled) {
        mScrollEnabled = _scrollEnabled;
        onDataChanged();
    }

    public int getVisibleBars() {
        return mVisibleBars;
    }

    public void setVisibleBars(int _visibleBars) {
        mVisibleBars = _visibleBars;
        onDataChanged();
    }

    /**
     * Determines if the values of each data should be shown in the graph.
     * @param _showValues true to show values in the graph.
     */
    public void setShowValues(boolean _showValues) {
        mShowValues = _showValues;
        invalidateGlobal();
    }

    /**
     * Returns if the values are drawn on top of the bars.
     * @return True if they are drawn.
     */
    public boolean isShowValues() {
        return mShowValues;
    }

    public void setScrollToEnd() {
        mCurrentViewport.left = mContentRect.width() - mGraphWidth;
        mCurrentViewport.right = mContentRect.width();
        invalidateGlobal();
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

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
        // Check if the current graph is a VerticalBarChart and set the
        // availableScreenSize to the chartHeight
        mAvailableScreenSize = this instanceof VerticalBarChart ? mGraphHeight : mGraphWidth;

        if(getData().size() > 0) {
            onDataChanged();
        }
    }

    /**
     * This is the main entry point after the graph has been inflated. Used to initialize the graph
     * and its corresponding members.
     */
    @Override
    protected void initializeGraph() {
        super.initializeGraph();

        mGraphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGraphPaint.setStyle(Paint.Style.FILL);

        mLegendPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        mLegendPaint.setColor(mLegendColor);
        mLegendPaint.setTextSize(mLegendTextSize);
        mLegendPaint.setStrokeWidth(2);
        mLegendPaint.setStyle(Paint.Style.FILL);

        mMaxFontHeight = Utils.calculateMaxTextHeight(mLegendPaint, null);

        mGestureDetector = new GestureDetector(getContext(), mGestureListener);
        mScroller = new Scroller(getContext());

        mRevealAnimator = ValueAnimator.ofFloat(0, 1);
        mRevealAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRevealValue = (animation.getAnimatedFraction());
                mGraph.invalidate();
            }
        });
        mRevealAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mStartedAnimation = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        // The scroller doesn't have any built-in animation functions--it just supplies
        // values when we ask it to. So we have to have a way to call it every frame
        // until the fling ends. This code (ab)uses a ValueAnimator object to generate
        // a callback on every animation frame. We don't use the animated value at all.
        mScrollAnimator = ValueAnimator.ofFloat(0, 1);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                tickScrollAnimation();
                invalidateGlobal();
            }
        });

    }

    /**
     * Calculates the bar width and bar margin based on the _DataSize and settings and starts the boundary
     * calculation in child classes.
     * @param _DataSize Amount of data sets
     */
    protected void calculateBarPositions(int _DataSize) {

        int   dataSize = mScrollEnabled ? mVisibleBars : _DataSize;
        float barWidth = mBarWidth;
        float margin   = mBarMargin;

        if (!mFixedBarWidth) {
            // calculate the bar width if the bars should be dynamically displayed
            barWidth = (mAvailableScreenSize / _DataSize) - margin;
        } else {

            if(_DataSize < mVisibleBars) {
                dataSize = _DataSize;
            }

            // calculate margin between bars if the bars have a fixed width
            float cumulatedBarWidths = barWidth * dataSize;
            float remainingScreenSize = mAvailableScreenSize - cumulatedBarWidths;

            margin = remainingScreenSize / dataSize;
        }

        boolean isVertical = this instanceof VerticalBarChart;

        int calculatedSize = (int) ((barWidth * _DataSize) + (margin * _DataSize));
        int contentWidth   = isVertical ? mGraphWidth : calculatedSize;
        int contentHeight  = isVertical ? calculatedSize : mGraphHeight;

        mContentRect       = new Rect(0, 0, contentWidth, contentHeight);
        mCurrentViewport   = new RectF(0, 0, mGraphWidth, mGraphHeight);

        calculateBounds(barWidth, margin);
        mLegend.invalidate();
        mGraph.invalidate();
    }

    /**
     * The gesture listener, used for handling simple gestures such as double touches, scrolls,
     * and flings.
     */
    private final GestureDetector.SimpleOnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (mCurrentViewport.left + distanceX > mContentRect.left && mCurrentViewport.right + distanceX < mContentRect.right) {
                mCurrentViewport.left  += distanceX;
                mCurrentViewport.right += distanceX;
            }

            if (mCurrentViewport.top + distanceY > mContentRect.top && mCurrentViewport.bottom + distanceY < mContentRect.bottom) {
                mCurrentViewport.top    += distanceY;
                mCurrentViewport.bottom += distanceY;
            }

            invalidateGlobal();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            fling((int) -velocityX, (int) -velocityY);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            // The user is interacting with the pie, so we want to turn on acceleration
            // so that the interaction is smooth.
            if (!mScroller.isFinished()) {
                stopScrolling();
            }
            return true;
        }
    };

    private void fling(int velocityX, int velocityY) {

        mScroller.fling(
                (int) mCurrentViewport.left,
                (int) mCurrentViewport.top,
                velocityX,
                velocityY,
                0, mContentRect.width() - mGraphWidth,
                0, mContentRect.height() - mGraphHeight);

        // Start the animator and tell it to animate for the expected duration of the fling.
        mScrollAnimator.setDuration(mScroller.getDuration());
        mScrollAnimator.start();
    }

    private void tickScrollAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.computeScrollOffset();
            int currX = mScroller.getCurrX();
            int currY = mScroller.getCurrY();

            if (currX > mContentRect.left && currX + mGraphWidth < mContentRect.right) {
                mCurrentViewport.left  = currX;
                mCurrentViewport.right = currX + mGraphWidth;
            }

            if (currY > mContentRect.top && currY + mGraphHeight < mContentRect.bottom) {
                mCurrentViewport.top    = currY;
                mCurrentViewport.bottom = currY + mGraphHeight;
            }
        } else {
            mScrollAnimator.cancel();
        }
    }

    /**
     * Force a stop to all pie motion. Called when the user taps during a fling.
     */
    private void stopScrolling() {
        mScroller.forceFinished(true);
    }

    /**
     * Calculates the bar boundaries based on the bar width and bar margin.
     * @param _Width    Calculated bar width
     * @param _Margin   Calculated bar margin
     */
    protected abstract void calculateBounds(float _Width, float _Margin);

    /**
     * Callback method for drawing the bars in the child classes.
     * @param _Canvas The canvas object of the graph view.
     */
    protected abstract void drawBars(Canvas _Canvas);

    /**
     * Returns the list of data sets which hold the information about the legend boundaries and text.
     * @return List of BaseModel data sets.
     */
    protected abstract List<? extends BaseModel> getLegendData();

    protected abstract List<RectF> getBarBounds();

    // ---------------------------------------------------------------------------------------------
    //                          Override methods from view layers
    // ---------------------------------------------------------------------------------------------

    //region Override Methods
    @Override
    protected void onGraphDraw(Canvas _Canvas) {
        super.onGraphDraw(_Canvas);
        _Canvas.translate(-mCurrentViewport.left, -mCurrentViewport.top);
        drawBars(_Canvas);
    }

    @Override
    protected void onLegendDraw(Canvas _Canvas) {
        super.onLegendDraw(_Canvas);

        _Canvas.translate(-mCurrentViewport.left, 0);

        for (BaseModel model : getLegendData()) {
            if(model.canShowLabel()) {
                RectF bounds = model.getLegendBounds();
                _Canvas.drawText(model.getLegendLabel(), model.getLegendLabelPosition(), bounds.bottom - mMaxFontHeight, mLegendPaint);
                _Canvas.drawLine(
                        bounds.centerX(),
                        bounds.bottom - mMaxFontHeight * 2 - mLegendTopPadding,
                        bounds.centerX(),
                        mLegendTopPadding, mLegendPaint
                );
            }
        }
    }

    @Override
    protected boolean onGraphOverlayTouchEvent(MotionEvent _Event) {
        boolean result = mGestureDetector.onTouchEvent(_Event);

        switch (_Event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                result = true;

                if (mListener == null) {
                    // we're not interested in clicks on individual bars here
                    BaseBarChart.this.onTouchEvent(_Event);
                } else {
                    float newX = _Event.getX() + mCurrentViewport.left;
                    float newY = _Event.getY() + mCurrentViewport.top;
                    int   counter = 0;

                    for (RectF rectF : getBarBounds()) {
                        if (Utils.intersectsPointWithRectF(rectF, newX, newY)) {
                            mListener.onBarClicked(counter);
                            break; // no need to check other bars
                        }
                        counter++;
                    }
                }
                break;
        }

        return result;
    }

    //endregion

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = BaseBarChart.class.getSimpleName();

    // All float values are dp values and will be converted into px values in the constructor
    public static final boolean DEF_SHOW_VALUES         = true;
    public static final float   DEF_BAR_WIDTH           = 32.f;
    public static final boolean DEF_FIXED_BAR_WIDTH     = false;
    public static final float   DEF_BAR_MARGIN          = 12.f;
    public static final boolean DEF_SCROLL_ENABLED      = true;
    public static final int     DEF_VISIBLE_BARS        = 6;

    /**
     * The current viewport. This rectangle represents the currently visible chart domain
     * and range. The currently visible chart X values are from this rectangle's left to its right.
     * The currently visible chart Y values are from this rectangle's top to its bottom.
     * <p>
     * Note that this rectangle's top is actually the smaller Y value, and its bottom is the larger
     * Y value. Since the chart is drawn onscreen in such a way that chart Y values increase
     * towards the top of the screen (decreasing pixel Y positions), this rectangle's "top" is drawn
     * above this rectangle's "bottom" value.
     *
     * @see #mContentRect
     */
    protected RectF mCurrentViewport = new RectF();

    /**
     * The current destination rectangle (in pixel coordinates) into which the chart data should
     * be drawn. Chart labels are drawn outside this area.
     *
     * @see #mCurrentViewport
     */
    protected Rect mContentRect = new Rect();

    protected IOnBarClickedListener mListener = null;

    protected Paint           mGraphPaint;
    protected Paint           mLegendPaint;

    protected float           mBarWidth;
    protected boolean         mFixedBarWidth;
    protected float           mBarMargin;
    protected int             mAvailableScreenSize;

    protected boolean         mScrollEnabled;
    protected int             mVisibleBars;
    protected boolean         mShowValues;

    private GestureDetector   mGestureDetector;
    private Scroller          mScroller;
    private ValueAnimator     mScrollAnimator;

}

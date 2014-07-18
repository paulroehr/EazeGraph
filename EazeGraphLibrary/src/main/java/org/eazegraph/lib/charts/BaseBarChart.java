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

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import org.eazegraph.lib.R;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.models.StackedBarModel;
import org.eazegraph.lib.utils.Utils;

public abstract class BaseBarChart<K extends BaseModel> extends BaseChart {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public BaseBarChart(Context context) {
        super(context);

        mBarOutline = DEF_BAR_OUTLINE;
        mBarWidth = Utils.dpToPx(DEF_BAR_WIDTH);
        mBarMargin = Utils.dpToPx(DEF_BAR_MARGIN);
        mFixedBarWidth = DEF_FIXED_BAR_WIDTH;
        mFixedBarWidth = DEF_FIXED_BAR_HEIGHT;
        mBottomAxisColor = DEF_BOTTOM_AXIS_COLOR;
        mBottomAxisStroke = Utils.dpToPx(DEF_BOTTOM_AXIS_STROKE);

        initializeGraph();
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
     */
    public BaseBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BaseBarChart,
                0, 0
        );

        try {

            mBarOutline = a.getBoolean(R.styleable.BaseBarChart_egBarOutline, DEF_BAR_OUTLINE);
            mBarWidth = a.getDimension(R.styleable.BaseBarChart_egBarWidth, Utils.dpToPx(DEF_BAR_WIDTH));
            mBarHeight = a.getDimension(R.styleable.BaseBarChart_egBarHeight, Utils.dpToPx(DEF_BAR_HEIGHT));
            mBarMargin = a.getDimension(R.styleable.BaseBarChart_egBarMargin, Utils.dpToPx(DEF_BAR_MARGIN));
            mFixedBarWidth = a.getBoolean(R.styleable.BaseBarChart_egFixedBarWidth, DEF_FIXED_BAR_WIDTH);
            mFixedBarHeight = a.getBoolean(R.styleable.BaseBarChart_egFixedBarHeight, DEF_FIXED_BAR_HEIGHT);
            mBottomAxisColor = a.getColor(R.styleable.BaseBarChart_egBottomAxisColor, DEF_BOTTOM_AXIS_COLOR);
            mBottomAxisStroke = a.getDimension(R.styleable.BaseBarChart_egBottomAxisStroke, Utils.dpToPx(DEF_BOTTOM_AXIS_STROKE));

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        initializeGraph();
    }

    public boolean isBarOutline() {
        return mBarOutline;
    }

    public void setBarOutline(boolean _barOutline) {
        mBarOutline = _barOutline;
        invalidate();
    }

    public float getBarWidth() {
        return mBarWidth;
    }

    public void setBarWidth(float _barWidth) {
        mBarWidth = _barWidth;
        onDataChanged();
    }

    public boolean isFixedBarWidth() {
        return mFixedBarWidth;
    }

    public void setFixedBarWidth(boolean _fixedBarWidth) {
        mFixedBarWidth = _fixedBarWidth;
        onDataChanged();
    }


    public void setmFixedBarHeight(boolean _fixedBarHeight) {
        mFixedBarHeight = _fixedBarHeight;
        onDataChanged();
    }


    public float getBarMargin() {
        return mBarMargin;
    }

    public void setBarMargin(float _barMargin) {
        mBarMargin = _barMargin;
        onDataChanged();
    }

    public int getBottomAxisColor() {
        return mBottomAxisColor;
    }

    public void setBottomAxisColor(int _bottomAxisColor) {
        mBottomAxisColor = _bottomAxisColor;
        invalidate();
    }

    public float getBottomAxisStroke() {
        return mBottomAxisStroke;
    }

    public void setBottomAxisStroke(float _bottomAxisStroke) {
        mBottomAxisStroke = _bottomAxisStroke;
        invalidate();
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
        mWidth = w;
        mHeight = h;

        mGraph.layout(0, 0, w, (int) (h - mLegendHeight));
        mLegend.layout(0, (int) (h - mLegendHeight), w, h);

        if (getDataSize() > 0) {
            onDataChanged();
        }
    }

    @Override
    protected void initializeGraph() {

        mData = new ArrayList<K>();

        if (isInEditMode()) {
            setUpEditMode();
        }

        mGraphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGraphPaint.setStyle(Paint.Style.FILL);

        mLegendPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        mLegendPaint.setColor(DEF_LEGEND_COLOR);
        mLegendPaint.setTextSize(mLegendTextSize);
        mLegendPaint.setStrokeWidth(2);
        mLegendPaint.setStyle(Paint.Style.FILL);

        calculateMaxTextHeight(mLegendPaint);

        mGraph = new Graph(getContext());
        addView(mGraph);

        if (mUseLegend) {
            mLegend = new Legend(getContext());
            addView(mLegend);
        }

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

    }

    protected abstract void setUpEditMode();

    protected void calculateBarPositions(int _DataSize) {

        float barWidth = mBarWidth;
        float margin = mBarMargin;

        if (!mFixedBarWidth) {
            // calculate the bar width if the bars should be dynamically displayed
            barWidth = (mGraphWidth / _DataSize) - margin;
        } else {
            // calculate margin between bars if the bars have a fixed width
            float cumulatedBarWidths = barWidth * _DataSize;
            float remainingWidth = mGraphWidth - cumulatedBarWidths;
            margin = remainingWidth / _DataSize;
        }

        calculateBounds(barWidth, margin);
    }

    protected abstract void calculateBounds(float _Width, float _Margin);

    protected abstract void drawBars(Canvas _Canvas);

    public void clearChart() {
        mData.clear();
        onDataChanged();
    }

    public void addBar(K _Bar) {
        mData.add(_Bar);
        onDataChanged();
    }
    public void addBar(K _Bar, boolean notify) {
        mData.add(_Bar);
        if (notify) {
            onDataChanged();
        }
    }

    public void setData(List<K> _List) {
        mData = _List;
        onDataChanged();
    }

    public List<K> getData() {
        return mData;
    }

    protected List<K> getLegendData() {
        return mData;
    }

    protected int getDataSize() {
        return mData.size();
    }

    @Override
    protected void onDataChanged() {
        int size = 0;

        for (K k : mData) {
            if (!k.isIgnore()) {
                size++;
            }
        }

        calculateBarPositions(size);
        super.onDataChanged();
    }

    public BarChartListener getBarChartListener() {
        return barChartListener;
    }

    public void setBarChartListener(BarChartListener barChartListener) {
        this.barChartListener = barChartListener;
    }

    //##############################################################################################
    // Graph
    //##############################################################################################
    protected class Graph extends View {

        private float oldX;
        private float oldY;

        /**
         * Simple constructor to use when creating a view from code.
         *
         * @param context The Context the view is running in, through which it can
         *                access the current theme, resources, etc.
         */
        protected Graph(Context context) {
            super(context);
        }

        /**
         * Implement this to do your drawing.
         *
         * @param canvas the canvas on which the background will be drawn
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawBars(canvas);
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
            mGraphHeight = h - mTopPadding;
            mGraphWidth = w - mLeftPadding - mRightPadding;
        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
                oldX = event.getX();
                oldY = event.getY();

                boolean result = false;
                K item = null;
                for (K k : mData) {
                    RectF r;
                    if (k instanceof BarModel) {
                        r = ((BarModel) k).getBarBounds();
                        result = checkRectAndPoint(r, oldX, oldY);
                    } else if (k instanceof StackedBarModel) {
                        r = ((StackedBarModel) k).getBounds();
                        result = checkRectAndPoint(r, oldX, oldY);
                    }

                    if (result) {
                        item = k;
                        break;
                    }
                }

                if (result && barChartListener != null) {
                    barChartListener.onBarClick(BaseBarChart.this, mData.indexOf(item), item);
                }

                invalidate();
            }

            return true;
        }

        private boolean checkRectAndPoint(RectF r, float oldX, float oldY) {
            if (r == null) {
                return false;
            } else {
                return r.contains(oldX, oldY);
            }
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
         * Implement this to do your drawing.
         *
         * @param canvas the canvas on which the background will be drawn
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for (BaseModel model : getLegendData()) {
                if (model.canShowLabel()) {
                    drawModelLegend(canvas, model);
                }
            }
        }

        private void drawModelLegend(Canvas canvas, BaseModel model) {
            RectF bounds = model.getLegendBounds();
            canvas.drawText(model.getLegendLabel(),
                    model.getLegendLabelPosition(),
                    bounds.bottom - mMaxFontHeight, mLegendPaint);
            canvas.drawLine(
                    bounds.centerX(),
                    bounds.bottom - mMaxFontHeight * 2 - mLegendTopPadding,
                    bounds.centerX(),
                    mLegendTopPadding, mLegendPaint);
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
        }

    }

    @Override
    public void setUseLegend(boolean useLegend) {
        super.setUseLegend(useLegend);
        if (mLegend != null) {
            mLegend.setVisibility(useLegend ? View.VISIBLE : View.GONE);
        }

    }

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = BaseBarChart.class.getSimpleName();

    public interface BarChartListener<K> {
        void onBarClick(BaseBarChart view, int position, K model);
    }

    // All float values are dp values and will be converted into px values in the constructor
    public static final boolean DEF_BAR_OUTLINE = false;
    public static final float DEF_BAR_WIDTH = 32.f;
    public static final float DEF_BAR_HEIGHT = 32.f;
    public static final boolean DEF_FIXED_BAR_WIDTH = false;
    public static final boolean DEF_FIXED_BAR_HEIGHT = false;
    public static final float DEF_BAR_MARGIN = 12.f;
    public static final int DEF_BOTTOM_AXIS_COLOR = 0xFF121212;
    public static final float DEF_BOTTOM_AXIS_STROKE = 10.f;

    protected Graph mGraph;
    protected Legend mLegend;

    protected Paint mGraphPaint;
    protected Paint mLegendPaint;

    protected boolean mBarOutline;
    protected float mBarWidth;
    protected float mBarHeight;
    protected boolean mFixedBarWidth;
    protected boolean mFixedBarHeight;
    protected float mBarMargin;
    protected int mBottomAxisColor;
    protected float mBottomAxisStroke;
    protected List<K> mData;
    private BarChartListener barChartListener;
}

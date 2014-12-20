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
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import org.eazegraph.lib.R;
import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.models.LegendModel;
import org.eazegraph.lib.models.Point2D;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.eazegraph.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A LineChart which displays various line series with one value and the remaining information is
 * calculated dynamically. It is possible to draw normal and cubic lines.
 */
public class ValueLineChart extends BaseChart {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public ValueLineChart(Context context) {
        super(context);

        mUseCubic                     = DEF_USE_CUBIC;
        mLineStroke                   = Utils.dpToPx(DEF_LINE_STROKE);
        mFirstMultiplier              = DEF_FIRST_MULTIPLIER;
        mSecondMultiplier             = 1.0f - mFirstMultiplier;
        mXAxisStroke                  = Utils.dpToPx(DEF_X_AXIS_STROKE);
        mUseDynamicScaling            = DEF_USE_DYNAMIC_SCALING;
        mScalingFactor                = DEF_SCALING_FACTOR;
        mAxisTextColor                = DEF_AXIS_TEXT_COLOR;
        mAxisTextSize                 = Utils.dpToPx(DEF_AXIS_TEXT_SIZE);

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
     * @see #View(android.content.Context, android.util.AttributeSet, int)
     */
    public ValueLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ValueLineChart,
                0, 0
        );

        try {

            mUseCubic                     = a.getBoolean(R.styleable.ValueLineChart_egUseCubic, DEF_USE_CUBIC);
            mLineStroke                   = a.getDimension(R.styleable.ValueLineChart_egLineStroke, Utils.dpToPx(DEF_LINE_STROKE));
            mFirstMultiplier              = a.getFloat(R.styleable.ValueLineChart_egCurveSmoothness, DEF_FIRST_MULTIPLIER);
            mSecondMultiplier             = 1.0f - mFirstMultiplier;
            mXAxisStroke                  = a.getDimension(R.styleable.ValueLineChart_egXAxisStroke, Utils.dpToPx(DEF_X_AXIS_STROKE));
            mUseDynamicScaling            = a.getBoolean(R.styleable.ValueLineChart_egUseDynamicScaling, DEF_USE_DYNAMIC_SCALING);
            mScalingFactor                = a.getFloat(R.styleable.ValueLineChart_egScalingFactor, DEF_SCALING_FACTOR);
            mAxisTextColor                = a.getColor(R.styleable.ValueLineChart_egAxisTextColor, DEF_AXIS_TEXT_COLOR);
            mAxisTextSize                 = a.getDimension(R.styleable.ValueLineChart_egAxisTextSize, Utils.dpToPx(DEF_AXIS_TEXT_SIZE));

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        initializeGraph();
    }

    /**
     * Adds a new series to the graph.
     * @param _Series The series which should be added.
     */
    public void addSeries(ValueLineSeries _Series) {
        mSeries.add(_Series);
        onDataChanged();
    }

    /**
     * Resets and clears the data object.
     */
    @Override
    public void clearChart() {
        mSeries.clear();
    }

    /**
     * Adds a custom legend which should be displayed instead of the dynamic legend.
     * @param _Legend A list of LegendModels which will be displayed.
     */
    public void addLegend(List<LegendModel> _Legend) {
        mLegendList.addAll(_Legend);
        onLegendDataChanged();
    }

    public boolean isUseCustomLegend() {
        return mUseCustomLegend;
    }

    public void setUseCustomLegend(boolean _useCustomLegend) {
        mUseCustomLegend = _useCustomLegend;
        onLegendDataChanged();
    }

    /**
     * Checks if the graph is a cubic graph.
     * @return True if it's a cubic graph.
     */
    public boolean isUseCubic() {
        return mUseCubic;
    }

    /**
     * Sets the option if the graph should use a cubic spline interpolation or not.
     * @param _useCubic True if the graph should use cubic spline interpolation.
     */
    public void setUseCubic(boolean _useCubic) {
        mUseCubic = _useCubic;
        onDataChanged();
    }

    /**
     * Returns the size of the line stroke for every series.
     * @return Line stroke in px.
     */
    public float getLineStroke() {
        return mLineStroke;
    }

    /**
     * Sets the line stroke for every series.
     * @param _lineStroke Line stroke as a dp value.
     */
    public void setLineStroke(float _lineStroke) {
        mLineStroke = Utils.dpToPx(_lineStroke);
        invalidateGlobal();
    }

    /**
     * Returns the stroke size of the X-axis.
     * @return Stroke size in px.
     */
    public float getXAxisStroke() {
        return mXAxisStroke;
    }

    /**
     * Sets the stroke size of the X-axis.
     * @param _XAxisStroke Stroke size in dp.
     */
    public void setXAxisStroke(float _XAxisStroke) {
        mXAxisStroke = Utils.dpToPx(_XAxisStroke);
        invalidateGraphOverlay();
    }
        
    public boolean isUseDynamicScaling() {
        return mUseDynamicScaling;
    }

    public void setUseDynamicScaling(boolean _useDynamicScaling) {
        mUseDynamicScaling = _useDynamicScaling;
        onDataChanged();
    }

    public int getAxisTextColor() {
        return mAxisTextColor;
    }

    public void setAxisTextColor(int _axisTextColor) {
        mAxisTextColor = _axisTextColor;
        mAxisTextPaint.setColor(mAxisTextColor);
        invalidateGlobal();
    }

    public float getAxisTextSize() {
        return mAxisTextSize;
    }

    public void setAxisTextSize(float _axisTextSize) {
        mAxisTextSize = Utils.dpToPx(_axisTextSize);
        mAxisTextPaint.setTextSize(mAxisTextSize);
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

        mUsableGraphHeight = (int) (mGraphHeight - mGraphHeightPadding);

        onDataChanged();
        if(mUseCustomLegend) {
            onLegendDataChanged();
        }
    }

    /**
     * This is the main entry point after the graph has been inflated. Used to initialize the graph
     * and its corresponding members.
     */
    @Override
    protected void initializeGraph() {
        super.initializeGraph();

        mDrawMatrix.setValues(mDrawMatrixValues);

        mSeries     = new ArrayList<>();
        mLegendList = new ArrayList<>();

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mLineStroke);

        mLegendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendPaint.setColor(mLegendColor);
        mLegendPaint.setTextSize(mLegendTextSize);
        mLegendPaint.setStrokeWidth(mXAxisStroke);
        mLegendPaint.setStyle(Paint.Style.FILL);

        mAxisTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAxisTextPaint.setColor(mAxisTextColor);
        mAxisTextPaint.setTextSize(mAxisTextSize);
        mAxisTextPaint.setStrokeWidth(mXAxisStroke);
        mAxisTextPaint.setStyle(Paint.Style.FILL);

        mMaxFontHeight = Utils.calculateMaxTextHeight(mLegendPaint);

        mRevealAnimator = ValueAnimator.ofFloat(0, 1);
        mRevealAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRevealValue = animation.getAnimatedFraction();

                mDrawMatrix.reset();
                mDrawMatrix.setScale(1, 1.f * mRevealValue, 0, mGraphHeight - mNegativeOffset);

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

        if(this.isInEditMode()) {
            ValueLineSeries series1 = new ValueLineSeries();
            series1.setColor(0xFF63CBB0);

            series1.addPoint(new ValueLinePoint(1.4f));
            series1.addPoint(new ValueLinePoint(4.4f));
            series1.addPoint(new ValueLinePoint(2.4f));
            series1.addPoint(new ValueLinePoint(3.2f));
            series1.addPoint(new ValueLinePoint(2.6f));
            series1.addPoint(new ValueLinePoint(5.0f));
            series1.addPoint(new ValueLinePoint(3.5f));
            series1.addPoint(new ValueLinePoint(2.4f));
            series1.addPoint(new ValueLinePoint(0.4f));
            series1.addPoint(new ValueLinePoint(3.4f));
            series1.addPoint(new ValueLinePoint(2.5f));
            series1.addPoint(new ValueLinePoint(1.0f));
            series1.addPoint(new ValueLinePoint(4.2f));
            series1.addPoint(new ValueLinePoint(2.4f));
            series1.addPoint(new ValueLinePoint(3.6f));
            series1.addPoint(new ValueLinePoint(1.0f));
            series1.addPoint(new ValueLinePoint(2.5f));
            series1.addPoint(new ValueLinePoint(1.4f));
            addSeries(series1);
        }
    }

    /**
     * Should be called after new data is inserted. Will be automatically called, when the view dimensions
     * changed.
     *
     * Calculates various offsets and positions for different overlay features based on the graph settings.
     * After the calculation the Path is generated as a normal path or cubic path (Based on 'egUseCubic' attribute).
     */
    @Override
    protected void onDataChanged() {

        if(!mSeries.isEmpty()) {
            mMaxValue           = 0.f;
            mMinValue           = Float.MAX_VALUE;
            mNegativeValue      = 0.f;
            mNegativeOffset     = 0.f;
            mHasNegativeValues  = false;

            // calculate the maximum and minimum value present in data
            for (ValueLineSeries series : mSeries) {
                for (ValueLinePoint point : series.getSeries()) {

                    if (point.getValue() > mMaxValue)
                        mMaxValue = point.getValue();

                    if (point.getValue() < mNegativeValue)
                        mNegativeValue = point.getValue();

                    if (point.getValue() < mMinValue)
                        mMinValue = point.getValue();
                }
            }

            if(!mUseDynamicScaling) {
                mMinValue = 0;
            }
            else {
                mMinValue *= mScalingFactor;
                mMaxValue *= (1f - mScalingFactor) + 1f;
            }

            // check if values below zero were found
            if(mNegativeValue < 0) {
                mHasNegativeValues = true;
                mMaxValue += (mNegativeValue * -1);
                mMinValue = 0;
            }

            mAxisValues[0] = (float) ((mMaxValue - mMinValue) * 0.25) + mMinValue;
            mAxisValues[1] = (float) ((mMaxValue - mMinValue) * 0.50) + mMinValue;
            mAxisValues[2] = (float) ((mMaxValue - mMinValue) * 0.75) + mMinValue;

            float heightMultiplier  = mUsableGraphHeight / (mMaxValue - mMinValue);

            // calculate the offset
            if(mHasNegativeValues) {
                mNegativeOffset = (mNegativeValue * -1) * heightMultiplier;
            }

            for (ValueLineSeries series : mSeries) {

                int   seriesPointCount  = series.getSeries().size();

                // check if more than one point is available
                if (seriesPointCount <= 1) {
                    Log.w(LOG_TAG, "More than one point should be available!");
                }
                else {

                    float widthOffset = (float) mGraphWidth / (float) seriesPointCount;
                    widthOffset += widthOffset / seriesPointCount;
                    float currentOffset = 0;
                    series.setWidthOffset(widthOffset);

                    // used to store first point and set it later as ending point, if a graph fill is selected
                    float firstX = currentOffset;
                    float firstY = mGraphHeight - ((series.getSeries().get(0).getValue() - mMinValue) * heightMultiplier);

                    Path path = new Path();
                    path.moveTo(firstX, firstY);
                    series.getSeries().get(0).setCoordinates(new Point2D(firstX, firstY));

                    // If a cubic curve should be drawn then calculate cubic path
                    // If not then just draw basic lines
                    if (mUseCubic) {
                        Point2D P1 = new Point2D();
                        Point2D P2 = new Point2D();
                        Point2D P3 = new Point2D();

                        for (int i = 0; i < seriesPointCount - 1; i++) {

                            int i3 = (seriesPointCount - i) < 3 ? i + 1 : i + 2;
                            float offset2 = (seriesPointCount - i) < 3 ? mGraphWidth : currentOffset + widthOffset;
                            float offset3 = (seriesPointCount - i) < 3 ? mGraphWidth : currentOffset + (2*widthOffset);

                            P1.setX(currentOffset);
                            P1.setY(mGraphHeight - ((series.getSeries().get(i).getValue() - mMinValue) * heightMultiplier));

                            P2.setX(offset2);
                            P2.setY(mGraphHeight - ((series.getSeries().get(i + 1).getValue() - mMinValue) * heightMultiplier));
                            Utils.calculatePointDiff(P1, P2, P1, mSecondMultiplier);

                            P3.setX(offset3);
                            P3.setY(mGraphHeight - ((series.getSeries().get(i3).getValue() - mMinValue) * heightMultiplier));
                            Utils.calculatePointDiff(P2, P3, P3, mFirstMultiplier);

                            currentOffset += widthOffset;

                            series.getSeries().get(i + 1).setCoordinates(new Point2D(P2.getX(), P2.getY()));
                            path.cubicTo(P1.getX(), P1.getY(), P2.getX(), P2.getY(), P3.getX(), P3.getY());
                        }
                    } else {
                        boolean first = true;
                        int count = 1;

                        for (ValueLinePoint point : series.getSeries()) {
                            if (first) {
                                first = false;
                                continue;
                            }
                            currentOffset += widthOffset;
                            if (count == seriesPointCount - 1) {
                                // if the last offset is smaller than the width, then the offset should be as long as the graph
                                // to prevent a graph drop
                                if (currentOffset < mGraphWidth) {
                                    currentOffset = mGraphWidth;
                                }
                            }
                            point.setCoordinates(new Point2D(currentOffset, mGraphHeight - ((point.getValue() - mMinValue) * heightMultiplier)));
                            path.lineTo(point.getCoordinates().getX(), point.getCoordinates().getY());
                            count++;
                        }
                    }

                    series.setPath(path);
                }
            }

            if(calculateLegendBounds())
                Utils.calculateLegendInformation(mSeries.get(0).getSeries(), 0, mGraphWidth, mLegendPaint);

        }

        super.onDataChanged();
    }

    private boolean calculateLegendBounds() {
        int index = 0;
        int size = mSeries.get(0).getSeries().size();

        // Only calculate if more than one point is available
        if (size > 1) {

            for (ValueLinePoint valueLinePoint : mSeries.get(0).getSeries()) {
                if (!(index == 0 || index == size - 1)) {
                    valueLinePoint.setLegendBounds(new RectF(
                            valueLinePoint.getCoordinates().getX() - mSeries.get(0).getWidthOffset() / 2,
                            0,
                            valueLinePoint.getCoordinates().getX() + mSeries.get(0).getWidthOffset() / 2,
                            mLegendHeight));
                } else {
                    valueLinePoint.setIgnore(true);
                }

                index++;
            }

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Calculates the legend bounds for a custom list of legends.
     */
    protected void onLegendDataChanged() {

        int   legendCount = mLegendList.size();
        float margin = (mGraphWidth / legendCount);
        float currentOffset = 0;

        for (LegendModel model : mLegendList) {
            model.setLegendBounds(new RectF(currentOffset, 0, currentOffset + margin, mLegendHeight));
            currentOffset += margin;
        }

        Utils.calculateLegendInformation(mLegendList, 0, mGraphWidth, mLegendPaint);

        invalidateGlobal();
    }

    /**
     * Returns the first series.
     * @return The first series.
     */
    @Override
    public List<ValueLinePoint> getData() { return mSeries.get(0).getSeries(); }

    /**
     * Returns all series which are currently inserted.
     * @return Inserted series.
     */
    public List<ValueLineSeries> getDataSeries() { return mSeries; }

    // ---------------------------------------------------------------------------------------------
    //                          Override methods from view layers
    // ---------------------------------------------------------------------------------------------


    @Override
    protected void onGraphUnderlayDraw(Canvas _Canvas) {
        super.onGraphUnderlayDraw(_Canvas);
        mLegendPaint.setStrokeWidth(mXAxisStroke);
        _Canvas.drawText(Utils.getFloatString(mAxisValues[2], mShowDecimal), mAxisTextPadding, (int) (mGraphHeight * 0.25) - mAxisTextPadding, mAxisTextPaint);
        _Canvas.drawLine(0, (int) (mGraphHeight * 0.25), mWidth, (int) (mGraphHeight * 0.25), mAxisTextPaint);

        _Canvas.drawText(Utils.getFloatString(mAxisValues[1], mShowDecimal), mAxisTextPadding, (int) (mGraphHeight * 0.50) - mAxisTextPadding, mAxisTextPaint);
        _Canvas.drawLine(0, (int) (mGraphHeight * 0.50), mWidth, (int) (mGraphHeight * 0.50), mAxisTextPaint);

        _Canvas.drawText(Utils.getFloatString(mAxisValues[0], mShowDecimal), mAxisTextPadding, (int) (mGraphHeight * 0.75) - mAxisTextPadding, mAxisTextPaint);
        _Canvas.drawLine(0, (int) (mGraphHeight * 0.75), mWidth, (int) (mGraphHeight * 0.75), mAxisTextPaint);

        _Canvas.drawText(Utils.getFloatString(mMinValue, mShowDecimal), mAxisTextPadding, mGraphHeight - mAxisTextPadding, mAxisTextPaint);
        _Canvas.drawLine(0, mGraphHeight - mMinimumPadding, mWidth, mGraphHeight - mMinimumPadding, mAxisTextPaint);
    }

    @Override
    protected void onGraphDraw(Canvas _Canvas) {
        super.onGraphDraw(_Canvas);

        _Canvas.concat(mDrawMatrix);
        if(mHasNegativeValues) {
            _Canvas.translate(0, -mNegativeOffset);
        }

        // drawing of lines
        for (ValueLineSeries series : mSeries) {
            mLinePaint.setColor(series.getColor());
            _Canvas.drawPath(series.getPath(), mLinePaint);

        }
    }

    @Override
    protected void onGraphOverlayDraw(Canvas _Canvas) {
        super.onGraphOverlayDraw(_Canvas);

//        // draw x-axis
//        mLegendPaint.setStrokeWidth(mXAxisStroke);
//        _Canvas.drawLine(
//                0,
//                (mGraphHeight - mNegativeOffset) * Utils.getScaleY(mDrawMatrixValues) + Utils.getTranslationY(mDrawMatrixValues),
//                mGraphWidth,
//                (mGraphHeight - mNegativeOffset) * Utils.getScaleY(mDrawMatrixValues) + Utils.getTranslationY(mDrawMatrixValues),
//                mLegendPaint
//        );
    }

    @Override
    protected void onLegendDraw(Canvas _Canvas) {
        super.onLegendDraw(_Canvas);
        mLegendPaint.setColor(mLegendColor);
        mLegendPaint.setStrokeWidth(DEF_LEGEND_STROKE);

        if(!mSeries.isEmpty()) {

            if (mUseCustomLegend) {
                for (LegendModel model : mLegendList) {
                    RectF bounds = model.getLegendBounds();
                    _Canvas.drawText(model.getLegendLabel(), model.getLegendLabelPosition(), bounds.bottom - mMaxFontHeight, mLegendPaint);
                }
            } else {
                List<? extends BaseModel> list = mSeries.get(0).getSeries();
                for (BaseModel model : list) {
                    if (model.canShowLabel()) {
                        RectF bounds = model.getLegendBounds();
                        _Canvas.drawText(model.getLegendLabel(), model.getLegendLabelPosition(), bounds.bottom - mMaxFontHeight, mLegendPaint);
                    }
                }
            }
        }
    }

    public boolean containsPoints() {
        boolean result = false;
        for (ValueLineSeries sery : mSeries) {
            if(!sery.getSeries().isEmpty()) {
                result = true;
            }
        }
        return result;
    }


    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = ValueLineChart.class.getSimpleName();

    public static final boolean DEF_USE_CUBIC                       = false;
    public static final float   DEF_LINE_STROKE                     = 2f;
    public static final float   DEF_FIRST_MULTIPLIER                = 0.33f;
    public static final float   DEF_X_AXIS_STROKE                   = 0.5f;
    public static final float   DEF_LEGEND_STROKE                   = 2f;
    public static final boolean DEF_USE_DYNAMIC_SCALING             = false;
    public static final float   DEF_SCALING_FACTOR                  = 0.96f;
    public static final int     DEF_AXIS_TEXT_COLOR                 = 0xFF898989;
    public static final float   DEF_AXIS_TEXT_SIZE                  = 12f;

    private Paint                   mLinePaint;
    private Paint                   mLegendPaint;
    private Paint                   mAxisTextPaint;

    /**
     * This is used to have a little extra space on the top, so if a standard value is added
     * and it is the biggest value, preventing the standard value line being cropped out a bit.
     */
    private int                     mUsableGraphHeight;
    private float                   mGraphHeightPadding = Utils.dpToPx(2.f);

    private List<ValueLineSeries>   mSeries;
    private List<LegendModel>       mLegendList;

    private boolean                 mHasNegativeValues  = false;
    private float                   mNegativeValue      = 0.f;
    private float                   mNegativeOffset     = 0.f;
    private float                   mMinValue           = Float.MAX_VALUE;
    private float                   mMaxValue           = 0.f;

    private float                   mFirstMultiplier;
    private float                   mSecondMultiplier;

    private boolean                 mUseCustomLegend = false;

    /**
     * Indicates to fill the bottom area of a series with its given color.
     */
    private boolean                 mUseCubic;
    private float                   mLineStroke;
    private float                   mXAxisStroke;
    private float                   mScalingFactor;
    private float                   mAxisTextPadding = Utils.dpToPx(3f);
    private int                     mAxisTextColor;
    private float                   mAxisTextSize;

    /**
     * Enabling this when only positive and big values are present and only have little fluctuations,
     * a y-axis scaling takes place to see a better difference between the values.
     */
    private boolean                 mUseDynamicScaling;

    protected Matrix                mDrawMatrix = new Matrix();
    private   float[]               mDrawMatrixValues = new float[] {1f, 0f, 0f,
                                                                     0f, 1f, 0f,
                                                                     0f, 0f, 1f};
    /**
     * The values which will be drawn over the axis lines (25%, 50%, 75%)
     *
     * 25% = [0]
     * 50% = [1]
     * 75% = [2]
     */
    private float[]                 mAxisValues = new float[] {0f, 0f, 0f};
}

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
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import org.eazegraph.lib.R;
import org.eazegraph.lib.communication.IOnPointFocusedListener;
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
        initializeGraph();

        mUseCubic                     = DEF_USE_CUBIC;
        mUseOverlapFill               = DEF_USE_OVERLAP_FILL;
        mLineStroke                   = Utils.dpToPx(DEF_LINE_STROKE);
        mFirstMultiplier              = DEF_FIRST_MULTIPLIER;
        mSecondMultiplier             = 1.0f - mFirstMultiplier;
        mShowIndicator                = DEF_SHOW_INDICATOR;
        mIndicatorWidth               = Utils.dpToPx(DEF_INDICATOR_WIDTH);
        mIndicatorColor               = DEF_INDICATOR_COLOR;
        mIndicatorTextSize            = Utils.dpToPx(DEF_INDICATOR_TEXT_SIZE);
        mIndicatorLeftPadding         = Utils.dpToPx(DEF_INDICATOR_LEFT_PADDING);
        mIndicatorTopPadding          = Utils.dpToPx(DEF_INDICATOR_TOP_PADDING);
        mShowStandardValue            = DEF_SHOW_STANDARD_VALUE;
        mStandardValueIndicatorStroke = Utils.dpToPx(DEF_STANDARD_VALUE_INDICATOR_STROKE);
        mStandardValueColor           = DEF_STANDARD_VALUE_COLOR;
        mXAxisStroke                  = Utils.dpToPx(DEF_X_AXIS_STROKE);
        mShowDecimal                  = DEF_SHOW_DECIMAL;
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

            mUseCubic                     = a.getBoolean(R.styleable.ValueLineChart_egUseCubic,                         DEF_USE_CUBIC);
            mUseOverlapFill               = a.getBoolean(R.styleable.ValueLineChart_egUseOverlapFill,                   DEF_USE_OVERLAP_FILL);
            mLineStroke                   = a.getDimension(R.styleable.ValueLineChart_egLineStroke,                     Utils.dpToPx(DEF_LINE_STROKE));
            mFirstMultiplier              = a.getFloat(R.styleable.ValueLineChart_egCurveSmoothness,                    DEF_FIRST_MULTIPLIER);
            mSecondMultiplier             = 1.0f - mFirstMultiplier;
            mShowIndicator                = a.getBoolean(R.styleable.ValueLineChart_egShowValueIndicator,               DEF_SHOW_INDICATOR);
            mIndicatorWidth               = a.getDimension(R.styleable.ValueLineChart_egIndicatorWidth,                 Utils.dpToPx(DEF_INDICATOR_WIDTH));
            mIndicatorColor               = a.getColor(R.styleable.ValueLineChart_egIndicatorColor,                     DEF_INDICATOR_COLOR);
            mIndicatorTextSize            = a.getDimension(R.styleable.ValueLineChart_egIndicatorWidth,                 Utils.dpToPx(DEF_INDICATOR_TEXT_SIZE));
            mIndicatorLeftPadding         = a.getDimension(R.styleable.ValueLineChart_egIndicatorLeftPadding,           Utils.dpToPx(DEF_INDICATOR_LEFT_PADDING));
            mIndicatorTopPadding          = a.getDimension(R.styleable.ValueLineChart_egIndicatorTopPadding,            Utils.dpToPx(DEF_INDICATOR_TOP_PADDING));
            mShowStandardValue            = a.getBoolean(R.styleable.ValueLineChart_egShowStandardValue,                DEF_SHOW_STANDARD_VALUE);
            mStandardValueIndicatorStroke = a.getDimension(R.styleable.ValueLineChart_egStandardValueIndicatorStroke,   Utils.dpToPx(DEF_STANDARD_VALUE_INDICATOR_STROKE));
            mStandardValueColor           = a.getColor(R.styleable.ValueLineChart_egStandardValueColor,                 DEF_STANDARD_VALUE_COLOR);
            mXAxisStroke                  = a.getDimension(R.styleable.ValueLineChart_egXAxisStroke,                    Utils.dpToPx(DEF_X_AXIS_STROKE));
            mShowDecimal                  = a.getBoolean(R.styleable.ValueLineChart_egShowDecimal,                      DEF_SHOW_DECIMAL);

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
        mUseCustomLegend = true;
        onLegendDataChanged();
    }

    /**
     * Adds a standard value to the graph. The standard value is a horizontal line as an overlay
     * dependent on the loaded data set.
     * @param _standardValue The value which will be interpreted as a y-coordinate dependent
     *                       on the maximum value of the data set.
     */
    public void addStandardValue(float _standardValue) {
        mStandardValue = _standardValue;
        onDataChanged();
    }

    /**
     * Sets the onPointFocusedListener.
     * @param _listener An instance of the IOnPointFocusedListener interface.
     */
    public void setOnPointFocusedListener(IOnPointFocusedListener _listener) {
        mListener = _listener;
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
     * Checks if the graph uses an overlap fill. An overlap fill occurs whether the user set it explicitly
     * through the attributes or if only one data set is present.
     * @return True if overlap fill is activated.
     */
    public boolean isUseOverlapFill() {
        return mUseOverlapFill;
    }

    /**
     * Sets the overlap fill attribute.
     * @param _useOverlapFill True if an overlap fill should be used.
     */
    public void setUseOverlapFill(boolean _useOverlapFill) {
        mUseOverlapFill = _useOverlapFill;
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
        invalidate();
    }

    /**
     * Checks if the indicator should be shown or not.
     * @return True if the indicator is shown.
     */
    public boolean isShowIndicator() {
        return mShowIndicator;
    }

    /**
     * Sets if the indicator should be shown or not.
     * @param _showIndicator True if the indicator should be shown.
     */
    public void setShowIndicator(boolean _showIndicator) {
        mShowIndicator = _showIndicator;
        invalidate();
    }

    /**
     * Returns the indicator line width (stroke).
     * @return Indicator line width in px.
     */
    public float getIndicatorWidth() {
        return mIndicatorWidth;
    }

    /**
     * Sets the indicator line width (stroke)
     * @param _indicatorWidth Indicator width as a dp value.
     */
    public void setIndicatorWidth(float _indicatorWidth) {
        mIndicatorWidth = Utils.dpToPx(_indicatorWidth);
        invalidate();
    }

    /**
     * Returns the color of the indicator line.
     * @return Color value.
     */
    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    /**
     * Sets the indicator line color.
     * @param _indicatorColor Indicator line color value
     */
    public void setIndicatorColor(int _indicatorColor) {
        mIndicatorColor = _indicatorColor;
        invalidate();
    }

    /**
     * Returns the indicators value text size.
     * @return Indicator text size.
     */
    public float getIndicatorTextSize() {
        return mIndicatorTextSize;
    }

    /**
     * Sets the indicators value text size.
     * @param _indicatorTextSize Indicator text size in sp.
     */
    public void setIndicatorTextSize(float _indicatorTextSize) {
        mIndicatorTextSize = Utils.dpToPx(_indicatorTextSize);
        invalidate();
    }

    /**
     * Returns the left padding for the indicator text.
     * @return Indicator text left padding in px
     */
    public float getIndicatorLeftPadding() {
        return mIndicatorLeftPadding;
    }

    /**
     * Sets the left padding for the indicator text.
     * @param _indicatorLeftPadding Indicator text left padding in dp
     */
    public void setIndicatorLeftPadding(float _indicatorLeftPadding) {
        mIndicatorLeftPadding = Utils.dpToPx(_indicatorLeftPadding);
        invalidate();
    }

    /**
     * Returns the top padding for the indicator text.
     * @return Indicator text top padding in px
     */
    public float getIndicatorTopPadding() {
        return mIndicatorTopPadding;
    }

    /**
     * Sets the top padding for the indicator text.
     * @param _indicatorTopPadding Indicator text top padding in dp
     */
    public void setIndicatorTopPadding(float _indicatorTopPadding) {
        mIndicatorTopPadding = Utils.dpToPx(_indicatorTopPadding);
        invalidate();
    }

    /**
     * Checks if the standard value line should be shown or not.
     * @return True if the standard value line should be shown.
     */
    public boolean isShowStandardValue() {
        return mShowStandardValue;
    }

    /**
     * Sets if the standard value should be shown or not.
     * @param _showStandardValue True if the standard value line should be shown.
     */
    public void setShowStandardValue(boolean _showStandardValue) {
        mShowStandardValue = _showStandardValue;
        onDataChanged();
    }

    /**
     * Returns the stroke size of the standard value line.
     * @return Stroke size of standard value line.
     */
    public float getStandardValueIndicatorStroke() {
        return mStandardValueIndicatorStroke;
    }

    /**
     * Sets the standard value line stroke.
     * @param _standardValueIndicatorStroke Stroke size of standard value line in dp.
     */
    public void setStandardValueIndicatorStroke(float _standardValueIndicatorStroke) {
        mStandardValueIndicatorStroke = Utils.dpToPx(_standardValueIndicatorStroke);
        invalidate();
    }

    /**
     * Returns the color of the standard value line.
     * @return Color of the standard value line.
     */
    public int getStandardValueColor() {
        return mStandardValueColor;
    }

    /**
     * Sets the color for the standard value line.
     * @param _standardValueColor Color value for the standard value line.
     */
    public void setStandardValueColor(int _standardValueColor) {
        mStandardValueColor = _standardValueColor;
        invalidate();
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
        invalidate();
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
        mWidth = w;
        mHeight = h;

        mGraph.layout(0, 0, w, (int) (h - mLegendHeight));
        mGraphOverlay.layout(0, 0, w, (int) (h - mLegendHeight));
        mLegend.layout(0, (int) (h - mLegendHeight), w, h);
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
        mSeries     = new ArrayList<ValueLineSeries>();
        mLegendList = new ArrayList<LegendModel>();

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(mLineStroke);

        mLegendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendPaint.setColor(DEF_LEGEND_COLOR);
        mLegendPaint.setTextSize(mLegendTextSize);
        mLegendPaint.setStrokeWidth(2);
        mLegendPaint.setStyle(Paint.Style.FILL);

        mMaxFontHeight = Utils.calculateMaxTextHeight(mLegendPaint);

        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setColor(mIndicatorColor);
        mIndicatorPaint.setTextSize(mIndicatorTextSize);
        mIndicatorPaint.setStrokeWidth(mIndicatorWidth);
        mIndicatorPaint.setStyle(Paint.Style.FILL);

        mGraph = new Graph(getContext());
        addView(mGraph);

        mGraphOverlay = new GraphOverlay(getContext());
        addView(mGraphOverlay);

        mLegend = new Legend(getContext());
        addView(mLegend);

        mRevealAnimator = ValueAnimator.ofFloat(0, 1);
        mRevealAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRevealValue = animation.getAnimatedFraction();

                mScale.reset();
                mScale.setScale(1, 1.f * mRevealValue, 0, mUseableGraphHeight + mTopPadding - mNegativeOffset);

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
     * has changed.
     *
     * Calculates various offsets and positions for different overlay features based on the graph settings.
     * After the calculation the Path is generated as a normal path or cubic path (Based on 'egUseCubic' attribute).
     */
    @Override
    protected void onDataChanged() {

        if(!mSeries.isEmpty()) {
            int   seriesCount  = mSeries.size();
            float maxValue     = 0.f;
            mNegativeValue     = 0.f;
            mNegativeOffset    = 0.f;
            mHasNegativeValues = false;

            // calculate the maximum value present in data
            for (ValueLineSeries series : mSeries) {
                for (ValueLinePoint point : series.getSeries()) {
                    if (point.getValue() > maxValue)
                        maxValue = point.getValue();
                    if (point.getValue() < mNegativeValue)
                        mNegativeValue = point.getValue();
                }
            }

            // check if the standardvalue is greater than all other values
            if(mShowStandardValue) {
                if(mStandardValue > maxValue) {
                    maxValue = mStandardValue;
                }
            }

            // check if values below zero were found
            if(mNegativeValue < 0) {
                mHasNegativeValues = true;
                maxValue += (mNegativeValue * -1);
            }

            float heightMultiplier  = mUseableGraphHeight / maxValue;

            // calculate the offset
            if(mHasNegativeValues) {
                mNegativeOffset = (mNegativeValue * -1) * heightMultiplier;
            }

            // calculate the y position for standardValue
            if(mShowStandardValue) {
                mStandardValueY = (mUseableGraphHeight + mTopPadding) - (mStandardValue * heightMultiplier);
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
                    float firstY = (mUseableGraphHeight + mTopPadding) - (series.getSeries().get(0).getValue() * heightMultiplier);

                    Path path = new Path();
                    path.moveTo(firstX, firstY);
                    series.getSeries().get(0).setCoordinates(new Point2D(firstX, firstY));

                    // If a cubic curve should be drawn then calculate cubic path
                    // If not then just draw basic lines
                    if (mUseCubic) {
                        Point2D P1 = new Point2D();
                        Point2D P2 = new Point2D();
                        Point2D P3 = new Point2D();

                        for (int i = 0; i < seriesPointCount; i++) {

                            // Check if the end of the array has been reached and do the last calculation to prevent ArrayOutOfBounds
                            if ((seriesPointCount - i) < 3) {
                                P1.setX(currentOffset);
                                P1.setY((mUseableGraphHeight + mTopPadding) - (series.getSeries().get(i).getValue() * heightMultiplier));

                                P2.setX(mGraphWidth);
                                P2.setY((mUseableGraphHeight + mTopPadding) - (series.getSeries().get(i + 1).getValue() * heightMultiplier));
                                calculatePointDiff(P1, P2, P1, mSecondMultiplier);

                                P3.setX(mGraphWidth);
                                P3.setY((mUseableGraphHeight + mTopPadding) - (series.getSeries().get(i + 1).getValue() * heightMultiplier));
                                calculatePointDiff(P2, P3, P3, mFirstMultiplier);

                                path.cubicTo(P1.getX(), P1.getY(), P2.getX(), P2.getY(), P3.getX(), P3.getY());
                                series.getSeries().get(i + 1).setCoordinates(new Point2D(P2.getX(), P2.getY()));
                                break;
                            } else {
                                P1.setX(currentOffset);
                                P1.setY((mUseableGraphHeight + mTopPadding) - (series.getSeries().get(i).getValue() * heightMultiplier));

                                P2.setX(currentOffset + widthOffset);
                                P2.setY((mUseableGraphHeight + mTopPadding) - (series.getSeries().get(i + 1).getValue() * heightMultiplier));
                                calculatePointDiff(P1, P2, P1, mSecondMultiplier);

                                P3.setX(currentOffset + (2 * widthOffset));
                                P3.setY((mUseableGraphHeight + mTopPadding) - (series.getSeries().get(i + 2).getValue() * heightMultiplier));
                                calculatePointDiff(P2, P3, P3, mFirstMultiplier);

                                series.getSeries().get(i + 1).setCoordinates(new Point2D(P2.getX(), P2.getY()));
                            }

                            currentOffset += widthOffset;

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
                            point.setCoordinates(new Point2D(currentOffset, (mUseableGraphHeight + mTopPadding) - (point.getValue() * heightMultiplier)));
                            path.lineTo(point.getCoordinates().getX(), point.getCoordinates().getY());
                            count++;
                        }
                    }

                    if (mUseOverlapFill || seriesCount == 1) {
                        path.lineTo(mGraphWidth, mGraphHeight);
                        path.lineTo(0, mGraphHeight);
                        path.lineTo(firstX, firstY);
                    }

                    series.setPath(path);
                }
            }

            if(!mUseCustomLegend) {
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
                    Utils.calculateLegendInformation(mSeries.get(0).getSeries(), mLeftPadding, mGraphWidth + mLeftPadding, mLegendPaint);
                }
            }

            // set the first point for the indicator
            if(mShowIndicator && mSeries.size() == 1) {
                int size = mSeries.get(0).getSeries().size();
                int index;

                // Only calculate if more than one point is available
                if (size > 1) {
                    // position the indicator in the middle at the nearest value
                    if (size == 3) {
                        index = size / 2;
                    } else {
                        index = (size / 2) - 1;
                    }

                    mFocusedPoint = mSeries.get(0).getSeries().get(index);
                    mTouchedArea.setX(mFocusedPoint.getCoordinates().getX());
                    mTouchedArea.setY(mFocusedPoint.getCoordinates().getY());

                    calculateValueTextHeight();
                }
            }
        }

        super.onDataChanged();
        mLegend.invalidate();
        mGraphOverlay.invalidate();
    }

    /**
     * Calculates the legend bounds for a custom list of legends.
     */
    protected void onLegendDataChanged() {

        int   legendCount = mLegendList.size();
        float margin = (mLegendWidth / legendCount);
        float currentOffset = 0;

        for (LegendModel model : mLegendList) {
            model.setLegendBounds(new RectF(currentOffset, 0, currentOffset + margin, mLegendHeight));
            Rect textBounds = new Rect();
            mLegendPaint.getTextBounds(model.getLegendLabel(), 0, model.getLegendLabel().length(), textBounds);
            model.setTextBounds(textBounds);
            currentOffset += margin;
        }

        invalidate();
    }

    /**
     * Calculates the middle point between two points and multiplies its coordinates with the given
     * smoothness _Mulitplier.
     * @param _P1           First point
     * @param _P2           Second point
     * @param _Result       Resulting point
     * @param _Multiplier   Smoothness multiplier
     */
    private void calculatePointDiff(Point2D _P1, Point2D _P2, Point2D _Result, float _Multiplier) {
        float diffX = _P2.getX() - _P1.getX();
        float diffY = _P2.getY() - _P1.getY();
        _Result.setX(_P1.getX() + (diffX * _Multiplier));
        _Result.setY(_P1.getY() + (diffY * _Multiplier));
    }

    /**
     * Calculates the text height for the indicator value and sets its x-coordinate.
     */
    private void calculateValueTextHeight() {
        Rect rect = new Rect();
        String str = Utils.getFloatString(mFocusedPoint.getValue(), mShowDecimal);
        mIndicatorPaint.getTextBounds(str, 0, str.length(), rect);
        mValueTextHeight = rect.height();

        if(mFocusedPoint.getCoordinates().getX() + rect.width() + mIndicatorLeftPadding > mGraphWidth + mLeftPadding) {
            mGraphOverlay.mValueLabelX = (int) (mFocusedPoint.getCoordinates().getX() - (rect.width() + mIndicatorLeftPadding));
        }
        else {
            mGraphOverlay.mValueLabelX = (int) (mFocusedPoint.getCoordinates().getX() + mIndicatorLeftPadding);
        }
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

    //##############################################################################################
    // Graph
    //##############################################################################################
    private class Graph extends View {
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
         * Implement this to do your drawing.
         *
         * @param canvas the canvas on which the background will be drawn
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if(mUseOverlapFill) {
                mLinePaint.setStyle(Paint.Style.FILL);
            }
            else {
                if(mSeries.size() == 1) {
                    mLinePaint.setStyle(Paint.Style.FILL);
                }
                else {
                    mLinePaint.setStrokeWidth(mLineStroke);
                    mLinePaint.setStyle(Paint.Style.STROKE);
                }
            }

            canvas.concat(mScale);
            if(mHasNegativeValues) {
                canvas.translate(0, -mNegativeOffset);
            }
            // drawing of lines
            for (ValueLineSeries series : mSeries) {
                mLinePaint.setColor(series.getColor());
                canvas.drawPath(series.getPath(), mLinePaint);
            }

            canvas.restore();

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
            mUseableGraphHeight = mGraphHeight - mTopPadding;
        }

        @Override
        public boolean performClick() {
            return super.performClick();
        }

    }

    //##############################################################################################
    // GraphOverlay
    //##############################################################################################
    private class GraphOverlay extends View {
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
         * Implement this to do your drawing.
         *
         * @param canvas the canvas on which the background will be drawn
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // draw x-axis
            mLegendPaint.setStrokeWidth(mXAxisStroke);
            canvas.drawLine(
                    mLeftPadding,
                    mTopPadding + mGraphHeight - mNegativeOffset,
                    mLeftPadding + mGraphWidth,
                    mTopPadding + mGraphHeight - mNegativeOffset,
                    mLegendPaint
            );

            // draw touch indicator
            if(mShowIndicator && mSeries.size() == 1) {
                mIndicatorPaint.setColor(mIndicatorColor);
                mIndicatorPaint.setStrokeWidth(mIndicatorWidth);
                canvas.drawLine(mTouchedArea.getX(), 0, mTouchedArea.getX(), mGraphHeight, mIndicatorPaint);

                if(mFocusedPoint != null) {
                    canvas.drawText(Utils.getFloatString(mFocusedPoint.getValue(), mShowDecimal),
                            mValueLabelX,
                            mValueTextHeight + mIndicatorTopPadding,
                            mIndicatorPaint);
                }
            }

            // draw standard value line
            if(mShowStandardValue) {
                mIndicatorPaint.setColor(mStandardValueColor);
                mIndicatorPaint.setStrokeWidth(mStandardValueIndicatorStroke);
                canvas.drawLine(
                        mLeftPadding,
                        mStandardValueY - mNegativeOffset,
                        mLeftPadding + mGraphWidth,
                        mStandardValueY - mNegativeOffset,
                        mIndicatorPaint
                );
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
            mGraphWidth = w;
            mGraphHeight = h;
            mUseableGraphHeight = mGraphHeight - mTopPadding;
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

            performClick();

            float newX = event.getX();
            float newY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    return true;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:


                    break;
            }

            if(mShowIndicator && mSeries.size() == 1) {
                int size       = mSeries.get(0).getSeries().size();

                for (int i = 0; i < size; i++) {

                    // check if touchedX equals one the points
                    if (mSeries.get(0).getSeries().get(i).getCoordinates().getX() == newX) {
                        mFocusedPoint = mSeries.get(0).getSeries().get(i);
                        break;
                    } else {
                        // check if we reached the last when --> (true) use last point
                        if (i == size - 1) {
                            mFocusedPoint = mSeries.get(0).getSeries().get(i);
                            break;
                        } else {
                            float x = mSeries.get(0).getSeries().get(i).getCoordinates().getX();
                            float nextX = mSeries.get(0).getSeries().get(i + 1).getCoordinates().getX();

                            // check if touchedX is between two points
                            if (newX > x && newX < nextX) {
                                // check which distance between touchedX and the two points is smaller
                                if (newX - x > nextX - newX) {
                                    mFocusedPoint = mSeries.get(0).getSeries().get(i + 1);
                                    break;
                                } else {
                                    mFocusedPoint = mSeries.get(0).getSeries().get(i);
                                    break;
                                }
                            }
                            //check if touchedX distance between the points is equal -> choose first Point
                            else if (newX > x && newX < nextX) {
                                mFocusedPoint = mSeries.get(0).getSeries().get(i);
                                break;
                            }
                        }
                    }
                }

                if (mFocusedPoint != null) {
                    mTouchedArea = mFocusedPoint.getCoordinates();

                } else {
                    mTouchedArea.setX(newX);
                    mTouchedArea.setY(newY);
                }

                if(mLastPoint != mFocusedPoint) {
                    mLastPoint = mFocusedPoint;

                    calculateValueTextHeight();

                    if (mListener != null) {
                        mListener.onPointFocused(mSeries.get(0).getSeries().indexOf(mFocusedPoint));
                    }
                }

                invalidate();
                mLegend.invalidate();
            }
            return true;
        }

        @Override
        public boolean performClick() {
            return super.performClick();
        }

        private ValueLinePoint mLastPoint = null;
        private int            mValueLabelX = 0;
    }

    //##############################################################################################
    // Legend
    //##############################################################################################
    private class Legend extends View {
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

            mLegendPaint.setStrokeWidth(DEF_LEGEND_STROKE);

            if(!mSeries.isEmpty()) {
                if (mUseCustomLegend) {
                    for (LegendModel model : mLegendList) {
                        Rect textBounds = model.getTextBounds();
                        RectF bounds = model.getLegendBounds();
                        canvas.drawText(model.getLegendLabel(), bounds.centerX() - (textBounds.width() / 2), bounds.centerY(), mLegendPaint);
                        canvas.drawLine(bounds.centerX(), bounds.centerY() - textBounds.height() - mLegendTopPadding, bounds.centerX(), mLegendTopPadding, mLegendPaint);
                    }
                } else {
                    List<? extends BaseModel> list = mSeries.get(0).getSeries();
                    for (BaseModel model : list) {
                        if (model.canShowLabel()) {
                            RectF bounds = model.getLegendBounds();
                            canvas.drawText(model.getLegendLabel(), model.getLegendLabelPosition(), bounds.bottom - mMaxFontHeight, mLegendPaint);
                            canvas.drawLine(
                                    bounds.centerX(),
                                    bounds.bottom - mMaxFontHeight * 2 - mLegendTopPadding,
                                    bounds.centerX(),
                                    mLegendTopPadding, mLegendPaint
                            );
                        }
                    }
                }
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
            mLegendWidth = w;
            mLegendHeight = h;
        }
    }

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = ValueLineChart.class.getSimpleName();

    public static final boolean DEF_USE_CUBIC                       = false;
    public static final boolean DEF_USE_OVERLAP_FILL                = false;
    public static final float   DEF_LINE_STROKE                     = 5f;
    public static final float   DEF_FIRST_MULTIPLIER                = 0.33f;
    public static final boolean DEF_SHOW_INDICATOR                  = true;
    public static final float   DEF_INDICATOR_WIDTH                 = 2f;
    public static final int     DEF_INDICATOR_COLOR                 = 0xFF0000FF;
    // will be interpreted as sp value
    public static final float   DEF_INDICATOR_TEXT_SIZE             = 15.f;
    public static final float   DEF_INDICATOR_LEFT_PADDING          = 4.f;
    public static final float   DEF_INDICATOR_TOP_PADDING           = 4.f;

    public static final boolean DEF_SHOW_STANDARD_VALUE             = false;
    public static final float   DEF_STANDARD_VALUE_INDICATOR_STROKE = 2f;
    public static final int     DEF_STANDARD_VALUE_COLOR            = 0xFF00FF00;
    public static final float   DEF_X_AXIS_STROKE                   = 2f;
    public static final float   DEF_LEGEND_STROKE                   = 2f;
    public static final boolean DEF_SHOW_DECIMAL                    = true;

    private int                     mUseableGraphHeight;

    private Graph                   mGraph;
    private GraphOverlay            mGraphOverlay;
    private Legend                  mLegend;

    private Paint                   mLinePaint;
    private Paint                   mLegendPaint;
    private Paint                   mIndicatorPaint;

    private List<ValueLineSeries>   mSeries;
    private float                   mStandardValue = 0;
    private float                   mStandardValueY;
    private List<LegendModel>       mLegendList;

    private boolean                 mHasNegativeValues  = false;
    private float                   mNegativeValue      = 0.f;
    private float                   mNegativeOffset     = 0.f;

    private IOnPointFocusedListener mListener = null;

    private float                   mFirstMultiplier;
    private float                   mSecondMultiplier;

    private boolean                 mUseCustomLegend = false;
    private Point2D                 mTouchedArea     = new Point2D(0, 0);
    private ValueLinePoint          mFocusedPoint    = null;
    private float                   mValueTextHeight;

    private boolean                 mUseCubic;

    /**
     * Indicates to fill the bottom area of a series with its given color.
     */
    private boolean                 mUseOverlapFill;
    private float                   mLineStroke;
    private boolean                 mShowIndicator;
    private float                   mIndicatorWidth;
    private int                     mIndicatorColor;
    private float                   mIndicatorTextSize;
    private float                   mIndicatorLeftPadding;
    private float                   mIndicatorTopPadding;
    private boolean                 mShowStandardValue;
    private float                   mStandardValueIndicatorStroke;
    private int                     mStandardValueColor;
    private float                   mXAxisStroke;
    private boolean                 mShowDecimal;

    protected Matrix                mScale = new Matrix();
}

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
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Scroller;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import org.eazegraph.lib.R;
import org.eazegraph.lib.communication.IOnPointFocusedListener;
import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.models.LegendModel;
import org.eazegraph.lib.models.Point2D;
import org.eazegraph.lib.models.StandardValue;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.eazegraph.lib.utils.ScaleGestureDetectorCompat;
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
        mUseOverlapFill               = DEF_USE_OVERLAP_FILL;
        mLineStroke                   = Utils.dpToPx(DEF_LINE_STROKE);
        mFirstMultiplier              = DEF_FIRST_MULTIPLIER;
        mSecondMultiplier             = 1.0f - mFirstMultiplier;
        mShowIndicator                = DEF_SHOW_INDICATOR;
        mIndicatorWidth               = Utils.dpToPx(DEF_INDICATOR_WIDTH);
        mIndicatorLineColor           = DEF_INDICATOR_COLOR;
        mIndicatorTextColor           = DEF_INDICATOR_COLOR;
        mIndicatorTextSize            = Utils.dpToPx(DEF_INDICATOR_TEXT_SIZE);
        mIndicatorLeftPadding         = Utils.dpToPx(DEF_INDICATOR_LEFT_PADDING);
        mIndicatorTopPadding          = Utils.dpToPx(DEF_INDICATOR_TOP_PADDING);
        mShowStandardValues           = DEF_SHOW_STANDARD_VALUE;
        mXAxisStroke                  = Utils.dpToPx(DEF_X_AXIS_STROKE);
        mActivateIndicatorShadow      = DEF_ACTIVATE_INDICATOR_SHADOW;
        mIndicatorShadowStrength      = Utils.dpToPx(DEF_INDICATOR_SHADOW_STRENGTH);
        mIndicatorShadowColor         = DEF_INDICATOR_SHADOW_COLOR;
        mIndicatorTextUnit            = DEF_INDICATOR_TEXT_UNIT;
        mShowLegendBeneathIndicator   = DEF_SHOW_LEGEND_BENEATH_INDICATOR;
        mUseDynamicScaling            = DEF_USE_DYNAMIC_SCALING;
        mScalingFactor                = DEF_SCALING_FACTOR;
        mMaxZoomX                     = DEF_MAX_ZOOM_X;
        mMaxZoomY                     = DEF_MAX_ZOOM_Y;

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

            mUseCubic                     = a.getBoolean(R.styleable.ValueLineChart_egUseCubic,                     DEF_USE_CUBIC);
            mUseOverlapFill               = a.getBoolean(R.styleable.ValueLineChart_egUseOverlapFill,               DEF_USE_OVERLAP_FILL);
            mLineStroke                   = a.getDimension(R.styleable.ValueLineChart_egLineStroke,                 Utils.dpToPx(DEF_LINE_STROKE));
            mFirstMultiplier              = a.getFloat(R.styleable.ValueLineChart_egCurveSmoothness,                DEF_FIRST_MULTIPLIER);
            mSecondMultiplier             = 1.0f - mFirstMultiplier;
            mShowIndicator                = a.getBoolean(R.styleable.ValueLineChart_egShowValueIndicator,           DEF_SHOW_INDICATOR);
            mIndicatorWidth               = a.getDimension(R.styleable.ValueLineChart_egIndicatorWidth,             Utils.dpToPx(DEF_INDICATOR_WIDTH));
            mIndicatorLineColor           = a.getColor(R.styleable.ValueLineChart_egIndicatorLineColor,             DEF_INDICATOR_COLOR);
            mIndicatorTextColor           = a.getColor(R.styleable.ValueLineChart_egIndicatorTextColor,             DEF_INDICATOR_COLOR);
            mIndicatorTextSize            = a.getDimension(R.styleable.ValueLineChart_egIndicatorWidth,             Utils.dpToPx(DEF_INDICATOR_TEXT_SIZE));
            mIndicatorLeftPadding         = a.getDimension(R.styleable.ValueLineChart_egIndicatorLeftPadding,       Utils.dpToPx(DEF_INDICATOR_LEFT_PADDING));
            mIndicatorTopPadding          = a.getDimension(R.styleable.ValueLineChart_egIndicatorTopPadding,        Utils.dpToPx(DEF_INDICATOR_TOP_PADDING));
            mShowStandardValues           = a.getBoolean(R.styleable.ValueLineChart_egShowStandardValue,            DEF_SHOW_STANDARD_VALUE);
            mXAxisStroke                  = a.getDimension(R.styleable.ValueLineChart_egXAxisStroke,                Utils.dpToPx(DEF_X_AXIS_STROKE));
            mActivateIndicatorShadow      = a.getBoolean(R.styleable.ValueLineChart_egActivateIndicatorShadow,      DEF_ACTIVATE_INDICATOR_SHADOW);
            mIndicatorShadowStrength      = a.getDimension(R.styleable.ValueLineChart_egIndicatorShadowStrength,    Utils.dpToPx(DEF_INDICATOR_SHADOW_STRENGTH));
            mIndicatorShadowColor         = a.getColor(R.styleable.ValueLineChart_egIndicatorShadowColor,           DEF_INDICATOR_SHADOW_COLOR);
            mIndicatorTextUnit            = a.getString(R.styleable.ValueLineChart_egIndicatorTextUnit);
            mShowLegendBeneathIndicator   = a.getBoolean(R.styleable.ValueLineChart_egShowLegendBeneathIndicator,   DEF_SHOW_LEGEND_BENEATH_INDICATOR);
            mUseDynamicScaling            = a.getBoolean(R.styleable.ValueLineChart_egUseDynamicScaling,            DEF_USE_DYNAMIC_SCALING);
            mScalingFactor                = a.getFloat(R.styleable.ValueLineChart_egScalingFactor,                  DEF_SCALING_FACTOR);
            mMaxZoomX                     = a.getFloat(R.styleable.ValueLineChart_egMaxXZoom,                       DEF_MAX_ZOOM_X);
            mMaxZoomY                     = a.getFloat(R.styleable.ValueLineChart_egMaxYZoom,                       DEF_MAX_ZOOM_Y);

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        if(mIndicatorTextUnit == null) {
            mIndicatorTextUnit = "";
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
        mStandardValues.clear();
        mFocusedPoint = null;
        mLastPoint = null;
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
     * Adds a standard value to the graph. The standard value is a horizontal line as an overlay
     * dependent on the loaded data set.
     * @param _standardValue The value which will be interpreted as a y-coordinate dependent
     *                       on the maximum value of the data set.
     */
    public void addStandardValue(StandardValue _standardValue) {
        mStandardValues.add(_standardValue);
        onDataChanged();
    }

    /**
     * Adds a standard value to the graph. The standard value is a horizontal line as an overlay
     * dependent on the loaded data set.
     * @param _standardValue The value which will be interpreted as a y-coordinate dependent
     *                       on the maximum value of the data set.
     */
    public void addStandardValue(float _standardValue) {
        mStandardValues.add(new StandardValue(_standardValue));
        onDataChanged();
    }

    /**
     * Adds a list of standard values to the graph.
     * @param _standardValues The list with standard values.
     */
    public void addStandardValues(List<StandardValue> _standardValues) {
        mStandardValues.addAll(_standardValues);
        onDataChanged();
    }

    /**
     * Clears the list which contains all standard values.
     */
    public void clearStandardValues() {
        mStandardValues.clear();
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
        invalidateGlobal();
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
        invalidateGlobal();
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
        invalidateGlobal();
    }

    /**
     * Returns the color of the indicator line.
     * @return Color value.
     */
    public int getIndicatorLineColor() {
        return mIndicatorLineColor;
    }

    /**
     * Sets the indicator line color.
     * @param _indicatorLineColor Indicator line color value
     */
    public void setIndicatorLineColor(int _indicatorLineColor) {
        mIndicatorLineColor = _indicatorLineColor;
        invalidateGraphOverlay();
    }

    /**
     * Returns the color of the indicator text.
     *
     * @return Color value
     */
    public int getIndicatorTextColor() {
        return mIndicatorTextColor;
    }

    /**
     * Sets the indicator text color.
     *
     * @param _indicatorTextColor Indicator text color value
     */
    public void setIndicatorTextColor(int _indicatorTextColor) {
        mIndicatorTextColor = _indicatorTextColor;
        invalidateGraphOverlay();
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
        invalidateGraphOverlay();
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
        invalidateGraphOverlay();
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
        invalidateGraphOverlay();
    }

    /**
     * Checks if the standard value line should be shown or not.
     * @return True if the standard value line should be shown.
     */
    public boolean isShowStandardValues() {
        return mShowStandardValues;
    }

    /**
     * Sets if the standard value should be shown or not.
     * @param _showStandardValues True if the standard value line should be shown.
     */
    public void setShowStandardValues(boolean _showStandardValues) {
        mShowStandardValues = _showStandardValues;
        onDataChanged();
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

    /**
     *
     * @return Checks if the shadow layer for the indicator text is activated.
     */
    public boolean hasActivateIndicatorShadow() {
        return mActivateIndicatorShadow;
    }

    /**
     * Toggles the shadow layer for the indicator text.
     * @param _activateIndicatorShadow Indication if the shadow layer should be enabled or not.
     */
    public void setActivateIndicatorShadow(boolean _activateIndicatorShadow) {
        mActivateIndicatorShadow = _activateIndicatorShadow;
        invalidateGraphOverlay();
    }

    /**
     *
     * @return The shadow layers strength/radius.
     */
    public float getIndicatorShadowStrength() {
        return mIndicatorShadowStrength;
    }

    /**
     * Sets the shadow layers strength/radius.
     *
     * @param _indicatorShadowStrength The shadow strength/radius (in dp)
     */
    public void setIndicatorShadowStrength(float _indicatorShadowStrength) {
        mIndicatorShadowStrength = Utils.dpToPx(_indicatorShadowStrength);
        invalidateGraphOverlay();
    }

    /**
     *
     * @return The shadow color
     */
    public int getIndicatorShadowColor() {
        return mIndicatorShadowColor;
    }

    /**
     * Sets the color in which the shadow layer will be drawn.
     *
     * @param _indicatorShadowColor Color for the shadow.
     */
    public void setIndicatorShadowColor(int _indicatorShadowColor) {
        mIndicatorShadowColor = _indicatorShadowColor;
        invalidateGraphOverlay();
    }

    /**
     *
     * @return The currently set unit which is placed after the indicator text.
     */
    public String getIndicatorTextUnit() {
        return mIndicatorTextUnit;
    }

    /**
     * Sets the unit which is placed after the indicator text.
     * If it is an empty String, nothing will be displayed and disables the drawing of the unit.
     *
     * @param _indicatorTextUnit The unit which should be drawn.
     */
    public void setIndicatorTextUnit(String _indicatorTextUnit) {
        mIndicatorTextUnit = _indicatorTextUnit;
        invalidateGraphOverlay();
    }

    public boolean isUseDynamicScaling() {
        return mUseDynamicScaling;
    }

    public void setUseDynamicScaling(boolean _useDynamicScaling) {
        mUseDynamicScaling = _useDynamicScaling;
        onDataChanged();
    }

    public float getScalingFactor() {
        return mScalingFactor;
    }

    public void setScalingFactor(float _scalingFactor) {
        mScalingFactor = _scalingFactor;
        onDataChanged();
    }

    public float getMaxZoomX() {
        return mMaxZoomX;
    }

    public void setMaxZoomX(float _maxZoomX) {
        mMaxZoomX = _maxZoomX;
        // TODO: Animate
        resetZoom(true);
    }

    public float getMaxZoomY() {
        return mMaxZoomY;
    }

    public void setMaxZoomY(float _maxZoomY) {
        mMaxZoomY = _maxZoomY;
        // TODO: Animate
        resetZoom(true);
    }

    public void resetZoom(boolean _recalculate) {

        mDrawMatrixValues = new float[] {
                1f, 0f, 0f,
                0f, 1f, 0f,
                0f, 0f, 1f};
        mDrawMatrix.setValues(mDrawMatrixValues);

        if(containsPoints() && _recalculate) {
            recalculateXCoordinates(mGraphWidth * mDrawMatrixValues[0]);
            if (calculateLegendBounds())
                Utils.calculateLegendInformation(mSeries.get(0).getSeries(), 0, mGraphWidth * mDrawMatrixValues[0], mLegendPaint);

            calculateValueTextHeight();
        }

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
        mGraphOverlay.decelerate();

        mSeries     = new ArrayList<ValueLineSeries>();
        mLegendList = new ArrayList<LegendModel>();

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(mLineStroke);

        mLegendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendPaint.setColor(mLegendColor);
        mLegendPaint.setTextSize(mLegendTextSize);
        mLegendPaint.setStrokeWidth(2);
        mLegendPaint.setStyle(Paint.Style.FILL);

        mMaxFontHeight = Utils.calculateMaxTextHeight(mLegendPaint, null);

        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setColor(mIndicatorLineColor);
        mIndicatorPaint.setTextSize(mIndicatorTextSize);
        mIndicatorPaint.setStrokeWidth(mIndicatorWidth);
        mIndicatorPaint.setStyle(Paint.Style.FILL);

        mScaleGestureDetector = new ScaleGestureDetector(getContext(), mScaleGestureListener);
        mGestureDetector = new GestureDetector(getContext(), mGestureListener);
        mScroller = new Scroller(getContext());

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
            int   seriesCount  = mSeries.size();
            float maxValue     = 0.f;
            float minValue     = Float.MAX_VALUE;
            mNegativeValue     = 0.f;
            mNegativeOffset    = 0.f;
            mHasNegativeValues = false;

            // calculate the maximum and minimum value present in data
            for (ValueLineSeries series : mSeries) {
                for (ValueLinePoint point : series.getSeries()) {

                    if (point.getValue() > maxValue)
                        maxValue = point.getValue();

                    if (point.getValue() < mNegativeValue)
                        mNegativeValue = point.getValue();

                    if (point.getValue() < minValue)
                        minValue = point.getValue();
                }
            }

            // check if the standardvalue is greater than all other values
            if(mShowStandardValues) {
                for (StandardValue value : mStandardValues) {

                    if(value.getValue() > maxValue)
                        maxValue = value.getValue();

                    if (value.getValue() < mNegativeValue)
                        mNegativeValue = value.getValue();

                    if (value.getValue() < minValue)
                        minValue = value.getValue();
                }
            }


            if(!mUseDynamicScaling) {
                minValue = 0;
            }
            else {
                minValue *= mScalingFactor;
            }

            // check if values below zero were found
            if(mNegativeValue < 0) {
                mHasNegativeValues = true;
                maxValue += (mNegativeValue * -1);
                minValue = 0;
            }


            float heightMultiplier  = mUsableGraphHeight / (maxValue - minValue);

            // calculate the offset
            if(mHasNegativeValues) {
                mNegativeOffset = (mNegativeValue * -1) * heightMultiplier;
            }

            // calculate the y position for standardValue
            if(mShowStandardValues) {
                for (StandardValue value : mStandardValues) {
                    value.setY((int) (mGraphHeight - mNegativeOffset - ((value.getValue() - minValue) * heightMultiplier)));
                }
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
                    float firstY = mGraphHeight - ((series.getSeries().get(0).getValue() - minValue) * heightMultiplier);

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
                            P1.setY(mGraphHeight - ((series.getSeries().get(i).getValue() - minValue) * heightMultiplier));

                            P2.setX(offset2);
                            P2.setY(mGraphHeight - ((series.getSeries().get(i + 1).getValue() - minValue) * heightMultiplier));
                            Utils.calculatePointDiff(P1, P2, P1, mSecondMultiplier);

                            P3.setX(offset3);
                            P3.setY(mGraphHeight - ((series.getSeries().get(i3).getValue() - minValue) * heightMultiplier));
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
                            point.setCoordinates(new Point2D(currentOffset, mGraphHeight - ((point.getValue() - minValue) * heightMultiplier)));
                            path.lineTo(point.getCoordinates().getX(), point.getCoordinates().getY());
                            count++;
                        }
                    }

                    if (mUseOverlapFill) {
                        path.lineTo(mGraphWidth, mGraphHeight);
                        path.lineTo(0, mGraphHeight);
                        path.lineTo(firstX, firstY);
                    }

                    series.setPath(path);
                }
            }

            if(calculateLegendBounds())
                Utils.calculateLegendInformation(mSeries.get(0).getSeries(), 0, mGraphWidth, mLegendPaint);

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
                    mTouchedArea = mFocusedPoint.getCoordinates();

                    calculateValueTextHeight();
                }
            }

            resetZoom(false);
        }

        super.onDataChanged();
    }

    private void recalculateXCoordinates(float _GraphWidth) {
        int seriesPointCount = mSeries.get(0).getSeries().size();
        float widthOffset = _GraphWidth / (float) seriesPointCount;
        widthOffset += widthOffset / seriesPointCount;
        float currentOffset = 0;

        for (ValueLinePoint point : mSeries.get(0).getSeries()) {
            point.getCoordinates().setX(currentOffset);
            currentOffset += widthOffset;
        }
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
     * Calculates the text height for the indicator value and sets its x-coordinate.
     */
    private void calculateValueTextHeight() {
        Rect valueRect = new Rect();
        Rect legendRect = new Rect();
        String str = Utils.getFloatString(mFocusedPoint.getValue(), mShowDecimal) + (!mIndicatorTextUnit.isEmpty() ? " " + mIndicatorTextUnit : "");

        // calculate the boundaries for both texts
        mIndicatorPaint.getTextBounds(str, 0, str.length(), valueRect);
        mLegendPaint.getTextBounds(mFocusedPoint.getLegendLabel(), 0, mFocusedPoint.getLegendLabel().length(), legendRect);

        // calculate string positions in overlay
        mValueTextHeight = valueRect.height();
        mValueLabelY = (int) (mValueTextHeight + mIndicatorTopPadding);
        mLegendLabelY = (int) (mValueTextHeight + mIndicatorTopPadding + legendRect.height() + Utils.dpToPx(7.f));

        int chosenWidth = valueRect.width() > legendRect.width() ? valueRect.width() : legendRect.width();

        // check if text reaches over screen
        if (mFocusedPoint.getCoordinates().getX() + chosenWidth + mIndicatorLeftPadding > -Utils.getTranslationX(mDrawMatrixValues) + mGraphWidth) {
            mValueLabelX = (int) (mFocusedPoint.getCoordinates().getX() - (valueRect.width() + mIndicatorLeftPadding));
            mLegendLabelX = (int) (mFocusedPoint.getCoordinates().getX() - (legendRect.width() + mIndicatorLeftPadding));
        } else {
            mValueLabelX = mLegendLabelX = (int) (mFocusedPoint.getCoordinates().getX() + mIndicatorLeftPadding);
        }
    }

    /**
     * The scale listener, used for handling multi-finger scale gestures.
     */
    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        float mLastFocusX;
        float mLastFocusY;

        boolean mZoomIn;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mLastFocusX = detector.getFocusX();
            mLastFocusY = detector.getFocusY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

            mIsInteracting = true;

            Matrix transformationMatrix = new Matrix();
            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();

            float scaleX = ScaleGestureDetectorCompat.getCurrentSpanX(scaleGestureDetector) / ScaleGestureDetectorCompat.getPreviousSpanX(scaleGestureDetector);
            float scaleY = ScaleGestureDetectorCompat.getCurrentSpanY(scaleGestureDetector) / ScaleGestureDetectorCompat.getPreviousSpanY(scaleGestureDetector);

            //Zoom focus is where the fingers are centered,
            transformationMatrix.postTranslate(-focusX, -focusY);
            transformationMatrix.postScale(scaleX, scaleY);

            float focusShiftX = focusX - mLastFocusX;
            float focusShiftY = focusY - mLastFocusY;
            transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY);
            mDrawMatrix.postConcat(transformationMatrix);

            constrainView();

            recalculateXCoordinates(mGraphWidth * mDrawMatrixValues[0]);
            if(calculateLegendBounds())
                Utils.calculateLegendInformation(mSeries.get(0).getSeries(), 0, mGraphWidth * mDrawMatrixValues[0], mLegendPaint);

            mLastFocusX = focusX;
            mLastFocusY = focusY;

            if(mFocusedPoint != null) {
                calculateValueTextHeight();
            }
            invalidateGlobal();

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    };

    /**
     * The gesture listener, used for handling simple gestures such as double touches, scrolls,
     * and flings.
     */
    private final GestureDetector.SimpleOnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            mIsInteracting = true;

            mDrawMatrix.postTranslate(-distanceX, -distanceY);

            constrainView();

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
                (int) -Utils.getTranslationX(mDrawMatrixValues),
                (int) -Utils.getTranslationY(mDrawMatrixValues),
                velocityX,
                velocityY,
                0, (int) calculateMaxTranslation(Utils.getScaleX(mDrawMatrixValues), mGraphWidth),
                0, (int) calculateMaxTranslation(Utils.getScaleY(mDrawMatrixValues), mGraphHeight));

        // Start the animator and tell it to animate for the expected duration of the fling.
        mScrollAnimator.setDuration(mScroller.getDuration());
        mScrollAnimator.start();
    }

    private void tickScrollAnimation() {

        if (!mScroller.isFinished()) {
            mScroller.computeScrollOffset();
            int currX = -mScroller.getCurrX();
            int currY = -mScroller.getCurrY();

            mDrawMatrixValues[2] = currX;
            mDrawMatrixValues[5] = currY;

            mDrawMatrix.setValues(mDrawMatrixValues);
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

    public void constrainView() {
        mDrawMatrix.getValues(mDrawMatrixValues);

        // Check for minimum scale
        mDrawMatrixValues[0] = Math.max(1f, mDrawMatrixValues[0]);
        mDrawMatrixValues[4] = Math.max(1f, mDrawMatrixValues[4]);

        // Check for maximum scale
        mDrawMatrixValues[0] = Math.min(mMaxZoomX, mDrawMatrixValues[0]);
        mDrawMatrixValues[4] = Math.min(mMaxZoomY, mDrawMatrixValues[4]);

        // check for translation values so the graph wont be translated further as possible
        mDrawMatrixValues[2] = Math.min(0f, mDrawMatrixValues[2]);
        mDrawMatrixValues[5] = Math.min(0f, mDrawMatrixValues[5]);

        float scaleX = Utils.getScaleX(mDrawMatrixValues);
        float scaleY = Utils.getScaleY(mDrawMatrixValues);

        // check if maximum x-translation doesn't get too big
        float remainingTranslationX = (scaleX * mGraphWidth) + mDrawMatrixValues[2];
        if(remainingTranslationX < mGraphWidth) {
            mDrawMatrixValues[2] = -calculateMaxTranslation(scaleX, mGraphWidth);
        }

        // check if maximum y-translation doesn't get too big
        float remainingTranslationY = (scaleY * mGraphHeight) + mDrawMatrixValues[5];
        if(remainingTranslationY < mGraphHeight) {
            mDrawMatrixValues[5] = -calculateMaxTranslation(scaleY, mGraphHeight);
        }

        mDrawMatrix.setValues(mDrawMatrixValues);
    }

    private float calculateMaxTranslation(float _Scale, float _Dimension) {
        return _Scale * _Dimension - _Dimension;
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
    protected void onGraphDraw(Canvas _Canvas) {
        super.onGraphDraw(_Canvas);
        if(mUseOverlapFill) {
            mLinePaint.setStyle(Paint.Style.FILL);
        }
        else {
            mLinePaint.setStrokeWidth(mLineStroke);
            mLinePaint.setStyle(Paint.Style.STROKE);
        }

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

        // draw x-axis
        mLegendPaint.setStrokeWidth(mXAxisStroke);
        _Canvas.drawLine(
                0,
                (mGraphHeight - mNegativeOffset) * Utils.getScaleY(mDrawMatrixValues) + Utils.getTranslationY(mDrawMatrixValues),
                mGraphWidth,
                (mGraphHeight - mNegativeOffset) * Utils.getScaleY(mDrawMatrixValues) + Utils.getTranslationY(mDrawMatrixValues),
                mLegendPaint
        );

        if(containsPoints()) {
            // draw standard value line
            if (mShowStandardValues) {

                mIndicatorPaint.setPathEffect(mDashPathEffect);
                for (StandardValue value : mStandardValues) {
                    mIndicatorPaint.setColor(value.getColor());
                    mIndicatorPaint.setStrokeWidth(value.getStroke());
                    _Canvas.drawLine(
                            0,
                            value.getY() * Utils.getScaleY(mDrawMatrixValues) + Utils.getTranslationY(mDrawMatrixValues),
                            mGraphWidth,
                            value.getY() * Utils.getScaleY(mDrawMatrixValues) + Utils.getTranslationY(mDrawMatrixValues),
                            mIndicatorPaint
                    );
                }
            }

            // draw touch indicator
            // TODO: if mShowIndicator is true, then check all series not only if one series is inserted
            if (mShowIndicator && mSeries.size() == 1) {
                mIndicatorPaint.setPathEffect(null);
                mIndicatorPaint.setColor(mIndicatorLineColor);
                mIndicatorPaint.setStrokeWidth(mIndicatorWidth);

                _Canvas.translate(Utils.getTranslationX(mDrawMatrixValues), 0);
                _Canvas.drawLine(mTouchedArea.getX(), 0, mTouchedArea.getX(), mGraphHeight, mIndicatorPaint);

                if (mFocusedPoint != null) {

                    // set shadow
                    if (mActivateIndicatorShadow) {
                        mIndicatorPaint.setShadowLayer(mIndicatorShadowStrength, 0, 0, mIndicatorShadowColor);
                    }

                    mIndicatorPaint.setColor(mIndicatorTextColor);
                    _Canvas.drawText(Utils.getFloatString(mFocusedPoint.getValue(), mShowDecimal) + (!mIndicatorTextUnit.isEmpty() ? " " + mIndicatorTextUnit : ""),
                            mValueLabelX,
                            mValueLabelY,
                            mIndicatorPaint);

                    if (mShowLegendBeneathIndicator) {
                        mLegendPaint.setColor(mIndicatorTextColor);
                        _Canvas.drawText(mFocusedPoint.getLegendLabel(),
                                mLegendLabelX,
                                mLegendLabelY,
                                mLegendPaint);
                    }

                    // reset shadow
                    if (mActivateIndicatorShadow) {
                        mIndicatorPaint.setShadowLayer(0, 0, 0, 0x00000000);
                    }
                }
            }
        }

    }

    @Override
    protected void onLegendDraw(Canvas _Canvas) {
        super.onLegendDraw(_Canvas);
        mLegendPaint.setColor(mLegendColor);
        mLegendPaint.setStrokeWidth(DEF_LEGEND_STROKE);

        if(!mSeries.isEmpty()) {

            _Canvas.translate(Utils.getTranslationX(mDrawMatrixValues), 0);

            if (mUseCustomLegend) {
                for (LegendModel model : mLegendList) {
                    RectF bounds = model.getLegendBounds();
                    _Canvas.drawText(model.getLegendLabel(), model.getLegendLabelPosition(), bounds.bottom - mMaxFontHeight, mLegendPaint);
                    _Canvas.drawLine(
                            bounds.centerX(),
                            bounds.bottom - mMaxFontHeight * 2 - mLegendTopPadding,
                            bounds.centerX(),
                            mLegendTopPadding, mLegendPaint);
                }
            } else {
                List<? extends BaseModel> list = mSeries.get(0).getSeries();
                for (BaseModel model : list) {
                    if (model.canShowLabel()) {
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
        }
    }

    @Override
    protected boolean onGraphOverlayTouchEvent(MotionEvent _Event) {

        super.onGraphOverlayTouchEvent(_Event);

        if(!mStartedAnimation && containsPoints()) {
            mScaleGestureDetector.onTouchEvent(_Event);
            mGestureDetector.onTouchEvent(_Event);

            float newX = getScaledXCoordinate(_Event.getX());
            float newY = _Event.getY();

            switch (_Event.getAction()) {

                case MotionEvent.ACTION_UP:
                    if (!mIsInteracting) {
                        findNearestPoint(newX, newY);
                    } else {
                        mIsInteracting = false;
                    }
                    return true;
            }
        }

        return true;
    }

    private void findNearestPoint(float _X, float _Y) {
        if(mShowIndicator && mSeries.size() == 1) {
            int size       = mSeries.get(0).getSeries().size();

            for (int i = 0; i < size; i++) {

                float pointX = mSeries.get(0).getSeries().get(i).getCoordinates().getX();

                
                // check if touchedX equals one the points
                if (pointX == _X) {
                    mFocusedPoint = mSeries.get(0).getSeries().get(i);
                    break;
                } else {
                    // if first point bigger than touched x select first
                    if (i == 0 && pointX > _X) {
                        mFocusedPoint = series.get(i);
                        focusedPointOrder = i;
                        break;
                    } 
                    // check if we reached the last. if --> (true) use last point
                    else if (i == size - 1) {
                        mFocusedPoint = mSeries.get(0).getSeries().get(i);
                        break;
                    } else {

                        float nextX = mSeries.get(0).getSeries().get(i + 1).getCoordinates().getX();

                        // check if touchedX is between two points
                        if (_X > pointX && _X < nextX) {
                            // check which distance between touchedX and the two points is smaller
                            if (_X - pointX > nextX - _X) {
                                mFocusedPoint = mSeries.get(0).getSeries().get(i + 1);
                                break;
                            } else {
                                mFocusedPoint = mSeries.get(0).getSeries().get(i);
                                break;
                            }
                        }
                        //check if touchedX distance between the points is equal -> choose first Point
                        else if (_X > pointX && _X < nextX) {
                            mFocusedPoint = mSeries.get(0).getSeries().get(i);
                            break;
                        }
                    }
                }
            }

            if (mFocusedPoint != null) {
                mTouchedArea = mFocusedPoint.getCoordinates();

            } else {
                mTouchedArea.setX(_X);
                mTouchedArea.setY(_Y);
            }

            if(mLastPoint != mFocusedPoint) {
                mLastPoint = mFocusedPoint;

                calculateValueTextHeight();

                if (mListener != null) {
                    mListener.onPointFocused(mSeries.get(0).getSeries().indexOf(mFocusedPoint));
                }
            }

            invalidateGlobal();
        }
    }

    private float getScaledXCoordinate(float _X) {
        return _X - Utils.getTranslationX(mDrawMatrixValues);
    }

    private boolean containsPoints() {
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

    public static final boolean DEF_SHOW_STANDARD_VALUE             = true;
    public static final float   DEF_X_AXIS_STROKE                   = 2f;
    public static final float   DEF_LEGEND_STROKE                   = 2f;
    public static final boolean DEF_ACTIVATE_INDICATOR_SHADOW       = false;
    // dimension value
    public static final float   DEF_INDICATOR_SHADOW_STRENGTH       = 0.7f;
    public static final int     DEF_INDICATOR_SHADOW_COLOR          = 0xFF676767;
    public static final String  DEF_INDICATOR_TEXT_UNIT             = "";
    public static final boolean DEF_SHOW_LEGEND_BENEATH_INDICATOR   = false;
    public static final boolean DEF_USE_DYNAMIC_SCALING             = false;
    public static final float   DEF_SCALING_FACTOR                  = 0.96f;
    public static final float   DEF_MAX_ZOOM_X                      = 3.f;
    public static final float   DEF_MAX_ZOOM_Y                      = 3.f;

    private Paint                   mLinePaint;
    private Paint                   mLegendPaint;
    private Paint                   mIndicatorPaint;

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

    private IOnPointFocusedListener mListener = null;

    private float                   mFirstMultiplier;
    private float                   mSecondMultiplier;

    private boolean                 mUseCustomLegend = false;
    private Point2D                 mTouchedArea     = new Point2D(0, 0);
    private ValueLinePoint          mFocusedPoint    = null;
    private float                   mValueTextHeight;

    // GraphOverlay vars
    private ValueLinePoint          mLastPoint = null;
    private int                     mValueLabelX  = 0;
    private int                     mValueLabelY  = 0;
    private int                     mLegendLabelX = 0;
    private int                     mLegendLabelY = 0;

    private List<StandardValue>     mStandardValues = new ArrayList<StandardValue>();

    /**
     * Indicates to fill the bottom area of a series with its given color.
     */
    private boolean                 mUseOverlapFill;
    private boolean                 mUseCubic;
    private float                   mLineStroke;
    private boolean                 mShowIndicator;
    private float                   mIndicatorWidth;
    private int                     mIndicatorLineColor;
    private int                     mIndicatorTextColor;
    private float                   mIndicatorTextSize;
    private float                   mIndicatorLeftPadding;
    private float                   mIndicatorTopPadding;
    private boolean                 mShowStandardValues;
    private float                   mXAxisStroke;
    private boolean                 mActivateIndicatorShadow;
    private float                   mIndicatorShadowStrength;
    private int                     mIndicatorShadowColor;
    private String                  mIndicatorTextUnit;
    private boolean                 mShowLegendBeneathIndicator;
    /**
     * Enabling this when only positive and big values are present and only have little fluctuations,
     * a y-axis scaling takes place to see a better difference between the values.
     */
    private boolean                 mUseDynamicScaling;
    /**
     * The factor for the dynamic scaling, which determines how many percent of the minimum value
     * should be subtracted to achieve the scaling.
     */
    private float                   mScalingFactor;
    private float                   mMaxZoomX = DEF_MAX_ZOOM_X;
    private float                   mMaxZoomY = DEF_MAX_ZOOM_Y;

    protected Matrix                mDrawMatrix = new Matrix();
    private   float[]               mDrawMatrixValues = new float[] {1f, 0f, 0f,
                                                                     0f, 1f, 0f,
                                                                     0f, 0f, 1f};
    private boolean                 mIsInteracting = false;

    private DashPathEffect          mDashPathEffect = new DashPathEffect(new float[] {10,20}, 0);

    // State objects and values related to gesture tracking.
    private ScaleGestureDetector    mScaleGestureDetector;
    private GestureDetector         mGestureDetector;
    private Scroller                mScroller;
    private ValueAnimator           mScrollAnimator;


}

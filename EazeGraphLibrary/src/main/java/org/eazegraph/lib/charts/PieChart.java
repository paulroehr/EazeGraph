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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import org.eazegraph.lib.R;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A customizable PieChart which can be rotated to select a Pie Slice. It is possible to show a normal Pie Chart or
 * a Doughnut Pie Chart depending on the InnerPadding attributes.
 */
public class PieChart extends BaseChart {


    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public PieChart(Context context) {
        super(context);

        mLegendHeight        = 0f;
        mUseInnerPadding     = DEF_USE_INNER_PADDING;
        mInnerPadding        = DEF_INNER_PADDING;
        mInnerPaddingOutline = DEF_INNER_PADDING_OUTLINE;
        mHighlightStrength   = DEF_HIGHLIGHT_STRENGTH;
        mOpenClockwise       = DEF_OPEN_CLOCKWISE;
        mInnerPaddingColor   = DEF_INNER_PADDING_COLOR;
        mUseInnerValue       = DEF_USE_INNER_VALUE;
        mInnerValueSize      = Utils.dpToPx(DEF_INNER_VALUE_SIZE);
        mInnerValueColor     = DEF_INNER_VALUE_COLOR;
        mInnerValueString    = "";

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
    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLegendHeight        = 0f;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PieChart,
                0, 0
        );

        try {

            mUseInnerPadding     = a.getBoolean(R.styleable.PieChart_egUseInnerPadding,     DEF_USE_INNER_PADDING);
            mInnerPadding        = a.getFloat(R.styleable.PieChart_egInnerPadding,          DEF_INNER_PADDING);
            mInnerPaddingOutline = a.getFloat(R.styleable.PieChart_egInnerPaddingOutline,   DEF_INNER_PADDING_OUTLINE);
            mHighlightStrength   = a.getFloat(R.styleable.PieChart_egHighlightStrength,     DEF_HIGHLIGHT_STRENGTH);
            mOpenClockwise       = a.getBoolean(R.styleable.PieChart_egOpenClockwise,       DEF_OPEN_CLOCKWISE);
            mInnerPaddingColor   = a.getColor(R.styleable.PieChart_egInnerPaddingColor,     DEF_INNER_PADDING_COLOR);
            mUseInnerValue       = a.getBoolean(R.styleable.PieChart_egUseInnerValue,       DEF_USE_INNER_VALUE);
            mInnerValueSize      = a.getDimension(R.styleable.PieChart_egInnerValueSize,    Utils.dpToPx(DEF_INNER_VALUE_SIZE));
            mInnerValueColor     = a.getColor(R.styleable.PieChart_egInnerValueColor,       DEF_INNER_VALUE_COLOR);
            mInnerValueString    = a.getString(R.styleable.PieChart_egInnerValueString);

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        if (mInnerValueString == null) {
            mInnerValueString = "";
        }

        initializeGraph();
    }

    /**
     * Checks if the InnerPadding is set or not. If it's set, a Doughnut Pie Chart is displayed.
     *
     * @return True if InnerPadding should be used.
     */
    public boolean isUseInnerPadding() {
        return mUseInnerPadding;
    }

    /**
     * Sets the InnerPadding. If the InnerPadding should be used, a complete recalculation is initiated.
     *
     * @param _useInnerPadding Indicates whether to use InnerPadding or not.
     */
    public void setUseInnerPadding(boolean _useInnerPadding) {
        mUseInnerPadding = _useInnerPadding;
        onDataChanged();
    }

    /**
     * Returns the InnerPadding's value.
     *
     * @return The InnerPadding's value in percent (between 0 - 100)
     */
    public float getInnerPadding() {
        return mInnerPadding;
    }

    /**
     * Sets the InnerPadding's value. After setting a recalculation is initiated.
     *
     * @param _innerPadding The InnerPadding's value in percent (between 0 - 100)
     */
    public void setInnerPadding(float _innerPadding) {
        mInnerPadding = _innerPadding;
        onDataChanged();
    }

    /**
     * Sets the InnerPadding's color. After setting a recalculation is initiated.
     * @param color the new InnerPadding's color
     */
    public void setInnerPaddingColor(int color) {
        mInnerPaddingColor = color;
        invalidateGraph();
    }

    /**
     * Returns the color of the InnerPadding.
     *
     * @return the color of the InnerPadding
     */
    public int getInnerPaddingColor() { return mInnerPaddingColor; }

    /**
     * Returns the size of the InnerPaddingOutline (which is the highlighted part).
     *
     * @return The outline size in percent (between 0 - 100) dependent on the normal InnerPadding.
     */
    public float getInnerPaddingOutline() {
        return mInnerPaddingOutline;
    }

    /**
     * Sets the outline size of the InnerPadding.
     *
     * @param _innerPaddingOutline The outline size in percent (between 0 - 100) dependent on the normal InnerPadding.
     */
    public void setInnerPaddingOutline(float _innerPaddingOutline) {
        mInnerPaddingOutline = _innerPaddingOutline;
        onDataChanged();
    }

    /**
     * Returns the highlight strength for the InnerPaddingOutline.
     *
     * @return The highlighting value for the outline.
     */
    public float getHighlightStrength() {
        return mHighlightStrength;
    }

    /**
     * Sets the highlight strength for the InnerPaddingOutline.
     *
     * @param _highlightStrength The highlighting value for the outline.
     */
    public void setHighlightStrength(float _highlightStrength) {
        mHighlightStrength = _highlightStrength;
        for (PieModel model : mPieData) {
            model.setHighlightedColor(Utils.manipulateColor(model.getColor(), mHighlightStrength));
        }
        invalidateGlobal();
    }

    /**
     * Checks if the animation should open clockwise or counter-clockwise.
     *
     * @return True for clockwise.
     */
    public boolean isOpenClockwise() {
        return mOpenClockwise;
    }

    /**
     * Sets if the starting animation should be opened clockwise or counter-clockwise.
     *
     * @param _openClockwise True for a clockwise aniamtion.
     */
    public void setOpenClockwise(boolean _openClockwise) {
        mOpenClockwise = _openClockwise;
    }

    public boolean isUseInnerValue() {
        return mUseInnerValue;
    }

    public void setUseInnerValue(boolean _useInnerValue) {
        mUseInnerValue = _useInnerValue;
        invalidateGlobal();
    }

    public float getInnerValueSize() {
        return mInnerValueSize;
    }

    public void setInnerValueSize(float _innerValueSize) {
        mInnerValueSize = _innerValueSize;
        mValuePaint.setTextSize(_innerValueSize);
        calculateInnerValueHeight();
        invalidateGlobal();
    }

    public int getInnerValueColor() {
        return mInnerValueColor;
    }

    public void setInnerValueColor(int _innerValueColor) {
        mInnerValueColor = _innerValueColor;
        mValuePaint.setColor(_innerValueColor);
        invalidateGlobal();
    }

    public String getInnerValueString() {
        return mInnerValueString;
    }

    public void setInnerValueString(String _innerValueString) {
        mInnerValueString = _innerValueString;
        calculateInnerValueHeight();
        invalidateGlobal();
    }

    /**
     * Adds a new Pie Slice to the PieChart. After inserting and calculation of the highlighting color
     * a complete recalculation is initiated.
     *
     * @param _Slice The newly added PieSlice.
     */
    public void addPieSlice(PieModel _Slice) {
        _Slice.setHighlightedColor(Utils.manipulateColor(_Slice.getColor(), mHighlightStrength));
        mPieData.add(_Slice);
        mTotalValue += _Slice.getValue();
        onDataChanged();
    }

    /**
     * Resets and clears the data object.
     */
    @Override
    public void update() {
        mTotalValue = 0;
        for (PieModel slice : mPieData) {
            mTotalValue += slice.getValue();
        }
        onDataChanged();
    }

    /**
     * Resets and clears the data object.
     */
    @Override
    public void clearChart() {
        mPieData.clear();
        mTotalValue = 0;
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {

    }

    /**
     * This is the main entry point after the graph has been inflated. Used to initialize the graph
     * and its corresponding members.
     */
    @Override
    protected void initializeGraph() {
        super.initializeGraph();

        mPieData = new ArrayList<>();

        mTotalValue = 0;

        mGraphPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendPaint.setTextSize(mLegendTextSize);
        mLegendPaint.setColor(DEF_LEGEND_COLOR);
        mLegendPaint.setStyle(Paint.Style.FILL);

        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setColor(mInnerValueColor);
        mValuePaint.setTextSize(mInnerValueSize);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        mRevealAnimator = ValueAnimator.ofFloat(0, 1);
        mRevealAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRevealValue = animation.getAnimatedFraction();
                invalidateGlobal();
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

        calculateInnerValueHeight();

        if(this.isInEditMode()) {
            addPieSlice(new PieModel("Breakfast", 15, Color.parseColor("#FE6DA8")));
            addPieSlice(new PieModel("Lunch", 25, Color.parseColor("#56B7F1")));
            addPieSlice(new PieModel("Dinner", 35, Color.parseColor("#CDA67F")));
            addPieSlice(new PieModel("Snack", 25, Color.parseColor("#FED70E")));
        }
    }

    /**
     * Should be called after new data is inserted. Will be automatically called, when the view dimensions
     * has changed.
     *
     * Calculates the start- and end-angles for every PieSlice.
     */
    @Override
    protected void onDataChanged() {
        super.onDataChanged();

        int currentAngle = 0;
        int index = 0;
        int size = mPieData.size();

        for (PieModel model : mPieData) {
            int endAngle = (int) (currentAngle + model.getValue() * 360.f / mTotalValue);
            if(index == size-1) {
                endAngle = 360;
            }

            model.setStartAngle(currentAngle);
            model.setEndAngle(endAngle);
            currentAngle = model.getEndAngle();
            index++;
        }
    }

    private void calculateInnerValueHeight() {
        Rect bounds = new Rect();
        mValuePaint.getTextBounds(mInnerValueString, 0, mInnerValueString.length(), bounds);
        mInnerValueHeight = bounds.height();
    }

    /**
     * Returns the graph boundaries.
     * @return Graph bounds.
     */
    private RectF getGraphBounds() {
        return mGraphBounds;
    }

    /**
     * Returns the datasets which are currently inserted.
     * @return the datasets
     */
    @Override
    public List<PieModel> getData() { return mPieData; }


    // ---------------------------------------------------------------------------------------------
    //                          Override methods from view layers
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onGraphDraw(Canvas _Canvas) {
        super.onGraphDraw(_Canvas);

        if (!mPieData.isEmpty()) {

            float innerStartAngle = 0;
            float innerSweepAngle = 0;
            int   amountOfPieSlices = mPieData.size();

            for (int pieIndex = 0; pieIndex < amountOfPieSlices; pieIndex++) {
                PieModel model = mPieData.get(pieIndex);

                mGraphPaint.setColor(model.getColor());

                // TODO: put calculation in the animation onUpdate method and provide an animated value
                float startAngle;
                float sweepAngle = (model.getEndAngle() - model.getStartAngle()) * mRevealValue;

                if (mOpenClockwise) {
                    startAngle = model.getStartAngle() * mRevealValue;
                }
                else {
                    startAngle = 360 - model.getEndAngle() * mRevealValue;
                }

                if(pieIndex == 0) {
                    innerStartAngle = startAngle +  (mOpenClockwise ? 0 : (float) Math.ceil(sweepAngle));
                }

                if (mOpenClockwise)
                    innerSweepAngle += sweepAngle;
                else
                    innerSweepAngle -= (float) Math.ceil(sweepAngle);

                _Canvas.drawArc(mGraphBounds,
                        startAngle,
                        sweepAngle,
                        true, mGraphPaint);

                // Draw the highlighted inner edges if an InnerPadding is selected
                if (mUseInnerPadding) {
                    mGraphPaint.setColor(model.getHighlightedColor());

                    _Canvas.drawArc(mInnerBounds,
                            startAngle,
                            sweepAngle,
                            true, mGraphPaint);
                }
            }

            // Draw inner white circle
            if (mUseInnerPadding) {
                mGraphPaint.setColor(mInnerPaddingColor);

                _Canvas.drawArc(mInnerOutlineBounds,
                        innerStartAngle,
                        innerSweepAngle,
                        true,
                        mGraphPaint);
            }
        }
    }

    @Override
    protected void onGraphOverlayDraw(Canvas _Canvas) {
        super.onGraphOverlayDraw(_Canvas);

        if (mUseInnerValue) {
            _Canvas.drawText(
                    mInnerValueString,
                    mGraphBounds.centerX(),
                    mGraphBounds.centerY() + mInnerValueHeight / 2,
                    mValuePaint);
        }

    }

    @Override
    protected void onGraphSizeChanged(int w, int h, int oldw, int oldh) {
        super.onGraphSizeChanged(w, h, oldw, oldh);

        // Figure out how big we can make the pie.
        mPieDiameter = Math.min(w, h);
        mPieRadius = mPieDiameter / 2.f;
        // calculate the left and right space to be center aligned
        float centeredValueWidth  = (w - mPieDiameter) / 2f;
        float centeredValueHeight = (h - mPieDiameter) / 2f;
        mGraphBounds = new RectF(
                0.0f,
                0.0f,
                mPieDiameter,
                mPieDiameter);
        mGraphBounds.offsetTo(centeredValueWidth, centeredValueHeight);

        mCalculatedInnerPadding         = (mPieRadius / 100) * mInnerPadding;
        mCalculatedInnerPaddingOutline  = (mPieRadius / 100) * mInnerPaddingOutline;

        mInnerBounds = new RectF(
                mGraphBounds.centerX() - mCalculatedInnerPadding - mCalculatedInnerPaddingOutline,
                mGraphBounds.centerY() - mCalculatedInnerPadding - mCalculatedInnerPaddingOutline,
                mGraphBounds.centerX() + mCalculatedInnerPadding + mCalculatedInnerPaddingOutline,
                mGraphBounds.centerY() + mCalculatedInnerPadding + mCalculatedInnerPaddingOutline);

        mInnerOutlineBounds = new RectF(
                mGraphBounds.centerX() - mCalculatedInnerPadding,
                mGraphBounds.centerY() - mCalculatedInnerPadding,
                mGraphBounds.centerX() + mCalculatedInnerPadding,
                mGraphBounds.centerY() + mCalculatedInnerPadding);

        mGraph.setPivot(mGraphBounds.centerX(), mGraphBounds.centerY());
        mGraph.rotateTo((-90 % 360 + 360) % 360);
        onDataChanged();

    }

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = PieChart.class.getSimpleName();

    public static final float   DEF_INNER_PADDING           = 65.f;
    public static final float   DEF_INNER_PADDING_OUTLINE   = 5.f;
    public static final boolean DEF_USE_INNER_PADDING       = true;
    public static final float   DEF_HIGHLIGHT_STRENGTH      = 1.15f;
    public static final boolean DEF_OPEN_CLOCKWISE          = true;
    public static final int     DEF_INNER_PADDING_COLOR     = 0xFFF3F3F3; // Holo light background
    public static final boolean DEF_USE_INNER_VALUE         = false;
    public static final float   DEF_INNER_VALUE_SIZE        = 12f;
    public static final int     DEF_INNER_VALUE_COLOR       = 0xFF898989;

    private List<PieModel>      mPieData;

    private Paint               mGraphPaint;
    private Paint               mLegendPaint;
    private Paint               mValuePaint;

    private RectF               mGraphBounds;
    private RectF               mInnerBounds;
    private RectF               mInnerOutlineBounds;

    private float               mPieDiameter;
    private float               mPieRadius;
    private float               mTotalValue;

    // Attributes -----------------------------------------------------
    private boolean             mUseInnerPadding;
    private float               mInnerPadding;
    private float               mInnerPaddingOutline;
    private int                 mInnerPaddingColor;
    private float               mHighlightStrength;
    private boolean             mOpenClockwise;
    private boolean             mUseInnerValue;
    private float               mInnerValueSize;
    private int                 mInnerValueColor;
    private String              mInnerValueString;
    // END - Attributes -----------------------------------------------

    private int                 mInnerValueHeight;
    private float               mCalculatedInnerPadding;
    private float               mCalculatedInnerPaddingOutline;

}

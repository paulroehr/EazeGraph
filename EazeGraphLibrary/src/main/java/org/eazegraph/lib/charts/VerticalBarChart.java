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
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.eazegraph.lib.R;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple Bar Chart where the bar heights are dependent on each other.
 */
public class VerticalBarChart extends BaseBarChart {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public VerticalBarChart(Context context) {
        super(context);

        mUseMaximumValue = DEF_USE_MAXIMUM_VALUE;
        mMaximumValue    = DEF_MAXIMUM_VALUE;
        mValueUnit       = DEF_VALUE_UNIT;
        mShowBarLabel    = DEF_SHOW_BAR_LABEL;
        mBarLabelColor   = DEF_BAR_LABEL_COLOR;

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
    public VerticalBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.VerticalBarChart,
                0, 0
        );

        try {

            mUseMaximumValue = a.getBoolean(R.styleable.VerticalBarChart_egUseMaximumValue, DEF_USE_MAXIMUM_VALUE);
            mMaximumValue    = a.getFloat(R.styleable.VerticalBarChart_egMaximumValue,      DEF_MAXIMUM_VALUE);
            mValueUnit       = a.getString(R.styleable.VerticalBarChart_egValueUnit);
            mShowBarLabel    = a.getBoolean(R.styleable.VerticalBarChart_egShowBarLabel,    DEF_SHOW_BAR_LABEL);
            mBarLabelColor   = a.getColor(R.styleable.VerticalBarChart_egBarLabelColor,     DEF_BAR_LABEL_COLOR);

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        if (mValueUnit == null) {
            mValueUnit = DEF_VALUE_UNIT;
        }

        initializeGraph();
    }

    /**
     * Adds a new {@link org.eazegraph.lib.models.BarModel} to the BarChart.
     * @param _Bar The BarModel which will be added to the chart.
     */
    public void addBar(BarModel _Bar) {
        mData.add(_Bar);
        onDataChanged();
    }

    /**
     * Adds a new list of {@link org.eazegraph.lib.models.BarModel} to the BarChart.
     * @param _List The BarModel list which will be added to the chart.
     */
    public void addBarList(List<BarModel> _List) {
        mData = _List;
        onDataChanged();
    }

    /**
     * Returns the data which is currently present in the chart.
     * @return The currently used data.
     */
    @Override
    public List<BarModel> getData() {
        return mData;
    }

    /**
     *
     * @return
     */
    public float getMaximumValue() {
        return mMaximumValue;
    }

    /**
     *
     * @param _maximumValue
     */
    public void setMaximumValue(float _maximumValue) {
        mMaximumValue = _maximumValue;
        onDataChanged();
    }

    /**
     *
     * @return
     */
    public boolean isUseMaximumValue() {
        return mUseMaximumValue;
    }

    /**
     *
     * @param _useMaximumValue
     */
    public void setUseMaximumValue(boolean _useMaximumValue) {
        mUseMaximumValue = _useMaximumValue;
        onDataChanged();
    }

    /**
     *
     * @return
     */
    public String getValueUnit() {
        return mValueUnit;
    }

    /**
     *
     * @param _valueUnit
     */
    public void setValueUnit(String _valueUnit) {
        mValueUnit = _valueUnit == null ? DEF_VALUE_UNIT : _valueUnit;
    }

    /**
     *
     * @return
     */
    public boolean getBarLabel() {
        return mShowBarLabel;
    }

    /**
     *
     * @param _showBarLabel
     */
    public void setBarLabel(boolean _showBarLabel) {
        mShowBarLabel = _showBarLabel;
        onDataChanged();
    }

    /**
     *
     * @return
     */
    public int getBarLabelColor() {
        return mBarLabelColor;
    }

    /**
     *
     * @param _barLabelColor
     */
    public void setBarLabelColor(int _barLabelColor) {
        mBarLabelColor = _barLabelColor;
        invalidateGraph();
    }

    /**
     * Resets and clears the data object.
     */
    @Override
    public void clearChart() {
        mData.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This is the main entry point after the graph has been inflated. Used to initialize the graph
     * and its corresponding members.
     */
    @Override
    protected void initializeGraph() {
        super.initializeGraph();
        mData = new ArrayList<>();

        mValuePaint = new Paint(mLegendPaint);

        if(this.isInEditMode()) {
            addBar(new BarModel(2.3f));
            addBar(new BarModel(2.f));
            addBar(new BarModel(3.3f));
            addBar(new BarModel(1.1f));
            addBar(new BarModel(2.7f));
            addBar(new BarModel(2.3f));
            addBar(new BarModel(2.f));
            addBar(new BarModel(3.3f));
            addBar(new BarModel(1.1f));
            addBar(new BarModel(2.7f));
        }
    }

    /**
     * Should be called after new data is inserted. Will be automatically called, when the view dimensions
     * has changed.
     */
    @Override
    protected void onDataChanged() {
        calculateBarPositions(mData.size());
        super.onDataChanged();
    }

    /**
     * Calculates the bar boundaries based on the bar width and bar margin.
     * @param _Width    Calculated bar width
     * @param _Margin   Calculated bar margin
     */
    protected void calculateBounds(float _Width, float _Margin) {
        float maxValue       = 0;
        int   last           = 0;
        float maxLegendWidth = 0;

        if (mUseMaximumValue) {
            maxValue = mMaximumValue;
        }
        else {
            for (BarModel model : mData) {
                if(model.getValue() > maxValue) {
                    maxValue = model.getValue();
                }
            }
        }


        if (mShowBarLabel) {
            float measuredText;
            for (BarModel model : mData) {
                measuredText = mValuePaint.measureText(model.getLegendLabel());
                if (maxLegendWidth < measuredText) {
                    maxLegendWidth = measuredText;
                }
            }
        }

        float legendWidthDistance = mShowBarLabel ? maxLegendWidth + mValueDistance : 0;
        float widthMultiplier = (mGraphWidth - legendWidthDistance) / maxValue;

        for (BarModel model : mData) {
            float width = model.getValue() * widthMultiplier;
            last += _Margin / 2;
            model.setBarBounds(new RectF(0, last, width, last + _Width));
            model.setLegendBounds(new RectF(last, 0, last + _Width, mLegendHeight));
            last += _Width + (_Margin / 2);
        }

        Utils.calculateLegendInformation(mData, 0, mContentRect.width(), mLegendPaint);
        mMaxFontHeight = Utils.calculateMaxTextHeight(mValuePaint, "190");
    }



    /**
     * Callback method for drawing the bars in the child classes.
     * @param _Canvas The canvas object of the graph view.
     */
    protected void drawBars(Canvas _Canvas) {

        RectF  bounds;
        String valueString;
        float  animatedRightOffset;

        for (BarModel model : mData) {
            bounds              = model.getBarBounds();
            valueString         = Utils.getFloatString(model.getValue(), mShowDecimal) + mValueUnit;
            animatedRightOffset = bounds.right * mRevealValue;

            mGraphPaint.setColor(model.getColor());

            _Canvas.drawRect(
                    bounds.left,
                    bounds.top,
                    bounds.right * mRevealValue,
                    bounds.bottom, mGraphPaint);

            if (mShowValues && animatedRightOffset > mValuePaint.measureText(valueString)) {
                mValuePaint.setColor(mLegendColor);
                _Canvas.drawText(
                        valueString,
                        bounds.left + mValueDistance,
                        bounds.centerY() + (mMaxFontHeight / 2),
                        mValuePaint
                );
            }

            if (mShowBarLabel) {
                mValuePaint.setColor(mBarLabelColor);
                _Canvas.drawText(
                        model.getLegendLabel(),
                        animatedRightOffset + mValueDistance,
                        bounds.centerY() + (mMaxFontHeight / 2),
                        mValuePaint
                );
            }
        }
    }

    /**
     * Returns the list of data sets which hold the information about the legend boundaries and text.
     * @return List of BaseModel data sets.
     */
    @Override
    protected List<? extends BaseModel> getLegendData() {
        return mData;
    }

    @Override
    protected List<RectF> getBarBounds() {
        ArrayList<RectF> bounds = new ArrayList<RectF>();
        for (BarModel model : mData) {
            bounds.add(model.getBarBounds());
        }
        return bounds;
    }

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = VerticalBarChart.class.getSimpleName();

    public static final boolean DEF_USE_MAXIMUM_VALUE   = false;
    public static final float   DEF_MAXIMUM_VALUE       = 150.0f;
    public static final String  DEF_VALUE_UNIT          = "";
    public static final boolean DEF_SHOW_BAR_LABEL      = false;
    public static final int     DEF_BAR_LABEL_COLOR     = 0xFF898989;

    private List<BarModel>  mData;

    private Paint           mValuePaint;

    private float           mMaximumValue;
    private boolean         mUseMaximumValue;
    private String          mValueUnit;
    private boolean         mShowBarLabel;
    private int             mBarLabelColor;

    private int             mValueDistance = (int) Utils.dpToPx(4);
}

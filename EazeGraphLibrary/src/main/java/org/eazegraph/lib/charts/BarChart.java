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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple Bar Chart where the bar heights are dependent on each other.
 */
public class BarChart extends BaseBarChart {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public BarChart(Context context) {
        super(context);
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
    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);

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
    public List<BarModel> getBarList() {
        return mData;
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
        boolean result = false;

        return result;
    }

    /**
     * This is the main entry point after the graph has been inflated. Used to initialize the graph
     * and its corresponding members.
     */
    @Override
    protected void initializeGraph() {
        super.initializeGraph();
        mData = new ArrayList<BarModel>();

        if(this.isInEditMode()) {
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
        float maxValue = 0;
        int   last = mLeftPadding;

        for (BarModel model : mData) {
            if(model.getValue() > maxValue) {
                maxValue = model.getValue();
            }
        }

        float heightMultiplier = mGraphHeight / maxValue;

        for (BarModel model : mData) {
            float height = model.getValue() * heightMultiplier;
            last += _Margin / 2;
            model.setBarBounds(new RectF(last, mGraphHeight - height + mTopPadding, last + _Width, mGraphHeight + mTopPadding));
            model.setLegendBounds(new RectF(last, 0, last + _Width, mLegendHeight));
            last += _Width + (_Margin / 2);

        }

        Utils.calculateLegendInformation(mData, mLeftPadding, mGraphWidth + mLeftPadding, mLegendPaint);
    }

    /**
     * Callback method for drawing the bars in the child classes.
     * @param _Canvas The canvas object of the graph view.
     */
    protected void drawBars(Canvas _Canvas) {

        Paint mValuePaint = new Paint(mLegendPaint);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        for (BarModel model : mData) {
            RectF bounds = model.getBarBounds();
            mGraphPaint.setColor(model.getColor());

            _Canvas.drawRect(
                    bounds.left,
                    bounds.bottom - (bounds.height() * mRevealValue),
                    bounds.right,
                    bounds.bottom, mGraphPaint);

            if (mShowValues) {
                canvas.drawText(mFormatter.format(model.getValue()), model.getLegendBounds().centerX(),
                        Math.max(mValuePaint.getTextSize(), bounds.top - 10), mValuePaint);
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

    /**
     * Returns the amount of datasets which are currently inserted.
     * @return Amount of datasets.
     */
    @Override
    protected int getDataSize() {
        return mData.size();
    }

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = BarChart.class.getSimpleName();

    private List<BarModel>  mData;

}

package org.eazegraph.lib.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat Borrás Paronella on 15/07/2014.
 */
public class VerticalBarChart extends BaseBarChart<BarModel> {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public VerticalBarChart(Context context) {
        super(context);
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
    public VerticalBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void calculateBarPositions(int dataSize) {

        float barHeight = mBarHeight;
        float margin = mBarMargin;

        if (!mFixedBarHeight) {
            // calculate the bar width if the bars should be dynamically displayed
            barHeight = (mGraphHeight / dataSize) - margin;
        } else {
            // calculate margin between bars if the bars have a fixed width
            float accumulatedBarHeights = barHeight * dataSize;
            float remainingHeight = mGraphHeight - accumulatedBarHeights;
            margin = remainingHeight / dataSize;
        }

        calculateBounds(barHeight, margin);
    }

    @Override
    protected void setUpEditMode() {
        addBar(new BarModel(2.3f, 0xFF123456));
        addBar(new BarModel(2.f, 0xFF343456));
        addBar(new BarModel(3.3f, 0xFF563456));
    }

    protected void calculateBounds(float height, float margin) {
        float maxValue = 0;
        int last = mLeftPadding;

        for (BarModel model : mData) {
            maxValue = Math.max(model.getValue(), maxValue);
        }

        float widthMultiplier = mGraphWidth / maxValue;

        for (BarModel model : mData) {
            if (!model.isIgnore()) {
                float width = model.getValue() * widthMultiplier;
                last += margin / 2;
                RectF barBounds = new RectF(0,
                        last + 10,
                        width,
                        last + (height - 10));

                RectF legendBound = new RectF(last,
                        +last,
                        last + height,
                        mLegendHeight);

                model.setBarBounds(barBounds);
                model.setLegendBounds(legendBound);
                last += height + (margin / 2);
            }
        }

        Utils.calculateLegendInformation(mData, mLeftPadding, mGraphWidth + mLeftPadding, mLegendPaint);
    }

    protected void drawBars(Canvas canvas) {

        for (BarModel model : mData) {
            RectF bounds = model.getBarBounds();
            mGraphPaint.setColor(model.getColor());

            if (bounds != null) {
                canvas.drawRect(
                        bounds.left,
                        bounds.top,
                        bounds.left + (bounds.width() * mRevealValue),
                        bounds.bottom, mGraphPaint);
            }
        }
    }

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = VerticalBarChart.class.getSimpleName();

}

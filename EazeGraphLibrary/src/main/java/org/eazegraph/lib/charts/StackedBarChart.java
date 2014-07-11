package org.eazegraph.lib.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.models.StackedBarModel;
import org.eazegraph.lib.utils.Utils;


/**
 * Created by Paul Cech on 27/05/14.
 */
public class StackedBarChart extends BaseBarChart {
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public StackedBarChart(Context context) {
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
    public StackedBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeGraph();
    }

    public void addBar(StackedBarModel _Bar) {
        mData.add(_Bar);
        onDataChanged();
    }

    public void setData(List<StackedBarModel> _List) {
        mData = _List;
        onDataChanged();
    }

    public List<StackedBarModel> getData() {
        return mData;
    }

    public void clearChart() {
        mData.clear();
        onDataChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;

        return result;
    }

    @Override
    protected void initializeGraph() {
        super.initializeGraph();
        mData = new ArrayList<StackedBarModel>();

        if(this.isInEditMode()) {
            StackedBarModel s1 = new StackedBarModel();

            s1.addBar(new BarModel(2.3f, 0xFF123456));
            s1.addBar(new BarModel(2.f,  0xFF1EF556));
            s1.addBar(new BarModel(3.3f, 0xFF1BA4E6));

            StackedBarModel s2 = new StackedBarModel();
            s2.addBar(new BarModel(1.1f, 0xFF123456));
            s2.addBar(new BarModel(2.7f, 0xFF1EF556));
            s2.addBar(new BarModel(0.7f, 0xFF1BA4E6));

            addBar(s1);
            addBar(s2);
        }
    }

    @Override
    protected void onDataChanged() {
        calculateBarPositions(mData.size());
        super.onDataChanged();
    }

    protected void calculateBounds(float _Width, float _Margin) {

        int   last = mLeftPadding;

        for (StackedBarModel model : mData) {
            float lastHeight = mTopPadding;
            float cumulatedValues = 0;

            for (BarModel barModel : model.getBars()) {
                cumulatedValues += barModel.getValue();
            }

            last += _Margin / 2;

            for (BarModel barModel : model.getBars()) {
                // calculate height for the StackedBarModel part
                float height = ((barModel.getValue() * mGraphHeight) / cumulatedValues) + lastHeight;
                barModel.setBarBounds(new RectF(last, lastHeight, last + _Width, height));
                lastHeight = height;
            }
            model.setLegendBounds(new RectF(last, 0, last + _Width, mLegendHeight));

            last += _Width + (_Margin / 2);
        }

        Utils.calculateLegendInformation(mData, mLeftPadding, mGraphWidth + mLeftPadding, mLegendPaint);
    }

    protected void drawBars(Canvas canvas) {
        for (StackedBarModel model : mData) {
            float lastTop;
            float lastBottom = mGraphHeight + mTopPadding;

            for (BarModel barModel : model.getBars()) {
                RectF bounds = barModel.getBarBounds();
                mGraphPaint.setColor(barModel.getColor());

                float height = (bounds.height() * mRevealValue);
                lastTop = lastBottom - height;

                canvas.drawRect(
                        bounds.left,
                        lastTop,
                        bounds.right,
                        lastBottom,
                        mGraphPaint);
                lastBottom = lastTop;
            }
        }
    }

    @Override
    protected List<? extends BaseModel> getLegendData() {
        return mData;
    }

    @Override
    protected int getDataSize() {
        return mData.size();
    }

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = BarChart.class.getSimpleName();

    private List<StackedBarModel>  mData;

}

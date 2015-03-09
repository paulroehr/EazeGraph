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
import org.eazegraph.lib.models.StackedBarModel;
import org.eazegraph.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * A rather simple type of a bar chart, where all the bars have the same height and their inner bars
 * heights are dependent on each other.
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

        mTextSize       = Utils.dpToPx(DEF_TEXT_SIZE);
        mShowSeparators = DEF_SHOW_SEPARATORS;
        mSeparatorWidth = Utils.dpToPx(DEF_SEPARATOR_WIDTH);

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
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StackedBarChart,
                0, 0
        );

        try {

            mTextSize       = a.getDimension(R.styleable.StackedBarChart_egBarTextSize,     Utils.dpToPx(DEF_TEXT_SIZE));
            mShowSeparators = a.getBoolean(R.styleable.StackedBarChart_egShowSeparators,    DEF_SHOW_SEPARATORS);
            mSeparatorWidth = a.getDimension(R.styleable.StackedBarChart_egSeparatorWidth,  Utils.dpToPx(DEF_SEPARATOR_WIDTH));

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        initializeGraph();
    }

    // TODO: Make method which returns the sp value
    /**
     * Returns the text size for the values which are shown in the bars.
     * @return The text size in px
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * Sets the text size for the values which are shown in the bars.
     * @param _textSize Size in sp
     */
    public void setTextSize(float _textSize) {
        mTextSize = Utils.dpToPx(_textSize);
        onDataChanged();
    }

    /**
     * Returns the current state if the separator between the bars are shown or not
     * @return True if the separators are shown
     */
    public boolean isShowSeparators() {
        return mShowSeparators;
    }

    /**
     * Sets the parameter if the separators between the bars should be shown or not
     * @param _showSeparators True if the separators should be shown
     */
    public void setShowSeparators(boolean _showSeparators) {
        mShowSeparators = _showSeparators;
        invalidateGlobal();
    }

    // TODO: Make method which returns the sp value
    /**
     * Returns the separator width.
     * @return The separator width in px.
     */
    public float getSeparatorWidth() {
        return mSeparatorWidth;
    }

    /**
     * Sets the separator width.
     * @param _separatorWidth The width in sp.
     */
    public void setSeparatorWidth(float _separatorWidth) {
        mSeparatorWidth = _separatorWidth;
        onDataChanged();
    }

    /**
     * Adds a new {@link org.eazegraph.lib.models.StackedBarModel} to the BarChart.
     * @param _Bar The StackedBarModel which will be added to the chart.
     */
    public void addBar(StackedBarModel _Bar) {
        mData.add(_Bar);
        onDataChanged();
    }

    /**
     * Adds a new list of {@link org.eazegraph.lib.models.StackedBarModel} to the BarChart.
     * @param _List The StackedBarModel list which will be added to the chart.
     */
    public void addBarList(List<StackedBarModel> _List) {
        mData = _List;
        onDataChanged();
    }

    /**
     * Returns the data which is currently present in the chart.
     * @return The currently used data.
     */
    @Override
    public List<StackedBarModel> getData() {
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

        mSeperatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSeperatorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mSeperatorPaint.setStrokeWidth(mSeparatorWidth);
        mSeperatorPaint.setColor(0xFFFFFFFF);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(0xFFFFFFFF);

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

        int last = 0;

        for (StackedBarModel model : mData) {
            float lastY = 0;
            float cumulatedValues = 0;
            // used if seperators are enabled, to prevent information loss
            int usableGraphHeight = mGraphHeight - (int) (mSeparatorWidth * (model.getBars().size() - 1));

            for (BarModel barModel : model.getBars()) {
                cumulatedValues += barModel.getValue();
            }

            last += _Margin / 2;

            for (BarModel barModel : model.getBars()) {
                // calculate topX for the StackedBarModel part
                float newY = ((barModel.getValue() * usableGraphHeight) / cumulatedValues) + lastY;
                float height = newY - lastY;
                Rect textBounds = new Rect();
                String value = String.valueOf(barModel.getValue());

                mTextPaint.getTextBounds(value, 0, value.length(), textBounds);

                if (textBounds.height() * 1.5f < height && textBounds.width() * 1.1f < _Width) {
                    barModel.setShowValue(true);
                    barModel.setValueBounds(textBounds);
                }

                barModel.setBarBounds(new RectF(last, lastY, last + _Width, newY));
                lastY = newY;
            }
            model.setLegendBounds(new RectF(last, 0, last + _Width, mLegendHeight));

            last += _Width + (_Margin / 2);
        }

        Utils.calculateLegendInformation(mData, 0, mContentRect.width(), mLegendPaint);
    }

    /**
     * Callback method for drawing the bars in the child classes.
     * @param _Canvas The canvas object of the graph view.
     */
    protected void drawBars(Canvas _Canvas) {
        for (StackedBarModel model : mData) {
            float lastTop;
            float lastBottom = mGraphHeight;

            for (int index = 0; index < model.getBars().size(); index++) {
                BarModel barModel = model.getBars().get(index);

                RectF bounds = barModel.getBarBounds();
                mGraphPaint.setColor(barModel.getColor());

                float height = (bounds.height() * mRevealValue);
                lastTop = lastBottom - height;

                _Canvas.drawRect(
                        bounds.left,
                        lastTop,
                        bounds.right,
                        lastBottom,
                        mGraphPaint
                );

                if (mShowValues && barModel.isShowValue()) {
                    _Canvas.drawText(
                            String.valueOf(barModel.getValue()),
                            bounds.centerX(),
                            (lastTop + height / 2) + barModel.getValueBounds().height()/2,
                            mTextPaint
                    );
                }

                lastBottom = lastTop;

                if (mShowSeparators && index < model.getBars().size() - 1) {
                    lastBottom -= mSeparatorWidth;
                }
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
        for (StackedBarModel model : mData) {
            bounds.add(model.getBounds());
        }
        return bounds;
    }

    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = BarChart.class.getSimpleName();

    public static final float   DEF_TEXT_SIZE       = 12f;
    public static final boolean DEF_SHOW_SEPARATORS = false;
    public static final float   DEF_SEPARATOR_WIDTH = 2f;

    private Paint                  mSeperatorPaint;
    private Paint                  mTextPaint;

    private List<StackedBarModel>  mData;

    private float                  mTextSize;
    private boolean                mShowSeparators;
    private float                  mSeparatorWidth;
}

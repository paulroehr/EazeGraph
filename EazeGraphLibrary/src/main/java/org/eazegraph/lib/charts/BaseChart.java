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
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.nineoldandroids.animation.ValueAnimator;

import org.eazegraph.lib.R;
import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.utils.Utils;

import java.util.List;

/**
 * This is the main chart class and should be inherited by every graph. This class provides some general
 * methods and variables, which are needed and used by every type of chart.
 */
public abstract class BaseChart extends ViewGroup {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    protected BaseChart(Context context) {
        super(context);

        mLegendHeight   = Utils.dpToPx(DEF_LEGEND_HEIGHT);
        mLegendTextSize = Utils.dpToPx(DEF_LEGEND_TEXT_SIZE);
        mAnimationTime  = DEF_ANIMATION_TIME;
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
    public BaseChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BaseChart,
                0, 0
        );

        try {

            mLegendHeight       = a.getDimension(R.styleable.BaseChart_egLegendHeight,     Utils.dpToPx(DEF_LEGEND_HEIGHT));
            mLegendTextSize     = a.getDimension(R.styleable.BaseChart_egLegendTextSize,   Utils.dpToPx(DEF_LEGEND_TEXT_SIZE));
            mAnimationTime      = a.getInt(R.styleable.BaseChart_egAnimationTime,          DEF_ANIMATION_TIME);

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

    }

    /**
     * Returns the current height of the legend view
     * @return Legend view height
     */
    public float getLegendHeight() {
        return mLegendHeight;
    }

    /**
     * Sets and updates the height of the legend view.
     * @param _legendHeight The new legend view height.
     */
    public void setLegendHeight(float _legendHeight) {
        mLegendHeight = Utils.dpToPx(_legendHeight);

        if(getData.size() > 0)
            onDataChanged();
    }

    /**
     * Returns the text size which is used by the legend.
     * @return Size of the legend text.
     */
    public float getLegendTextSize() {
        return mLegendTextSize;
    }

    /**
     * Sets the size of the text which is displayed in the legend. (Interpreted as sp value)
     * @param _legendTextSize Size of the legend text.
     */
    public void setLegendTextSize(float _legendTextSize) {
        mLegendTextSize = Utils.dpToPx(_legendTextSize);
    }

    /**
     * Returns the animation time in milliseconds.
     * @return Animation time.
     */
    public int getAnimationTime() {
        return mAnimationTime;
    }

    /**
     * Sets the animation time in milliseconds.
     * @param _animationTime Animation time in milliseconds.
     */
    public void setAnimationTime(int _animationTime) {
        mAnimationTime = _animationTime;
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
        mLeftPadding    = getPaddingLeft();
        mTopPadding     = getPaddingTop();
        mRightPadding   = getPaddingRight();
        mBottomPadding  = getPaddingBottom();
    }

    /**
     * Starts the chart animation.
     */
    public void startAnimation() {
        if(mRevealAnimator != null) {
            mStartedAnimation = true;
            mRevealAnimator.setDuration(mAnimationTime).start();
        }
    }

    /**
     * This is the main entry point after the graph has been inflated. Used to initialize the graph
     * and its corresponding members.
     */
    protected abstract void initializeGraph();

    /**
     * Should be called after new data is inserted. Will be automatically called, when the view dimensions
     * has changed.
     */
    protected void onDataChanged() {
        invalidate();
    }

    /**
     * Returns the datasets which are currently inserted.
     * @return the datasets
     */
    public abstract List<? extends BaseModel> getData();

    /**
     * Resets and clears the data object.
     */
    public abstract void clearChart();

    //##############################################################################################
    // Variables
    //##############################################################################################


    public static final float   DEF_LEGEND_HEIGHT       = 58.f;
    public static final int     DEF_LEGEND_COLOR        = 0xFF898989;
    // will be interpreted as sp value
    public static final float   DEF_LEGEND_TEXT_SIZE    = 12.f;
    public static final int     DEF_ANIMATION_TIME      = 2000;

    protected int               mHeight;
    protected int               mWidth;

    protected int               mGraphWidth;
    protected int               mGraphHeight;

    protected float             mLegendWidth;
    protected float             mLegendHeight;
    protected float             mLegendTextSize;

    protected int               mLeftPadding;
    protected int               mTopPadding;
    protected int               mRightPadding;
    protected int               mBottomPadding;

    protected float             mMaxFontHeight;
    protected float             mLegendTopPadding = Utils.dpToPx(4.f);

    protected ValueAnimator     mRevealAnimator     = null;
    protected float             mRevealValue        = 1.0f;
    protected int               mAnimationTime      = 1000;
    protected boolean           mStartedAnimation   = false;
}

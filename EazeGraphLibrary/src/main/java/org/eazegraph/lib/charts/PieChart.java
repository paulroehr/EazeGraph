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

/**
 * Important:
 *
 * This PieChart partially uses google provided code from their developer website (code sample):
 *
 *      http://developer.android.com/training/custom-views/create-view.html
 *
 * Mainly it's the code which handles the touch and rotation/animation handling. I did not logically modified
 * the code, I only copied and used the bits I needed and renamed the variables.
 * Another function which I extracted from the code sample is the "vectorToScalarScroll(...)" - function.
 * This can be found in the "Utils" - class.
 *
 * That's why I include the Apache License part from the sample:
 *
 * *************************************************************************************************
 *
 *  Copyright (C) 2012 The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.eazegraph.lib.charts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import org.eazegraph.lib.R;
import org.eazegraph.lib.communication.IOnItemFocusChangedListener;
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

        mUseInnerPadding     = DEF_USE_INNER_PADDING;
        mInnerPadding        = DEF_INNER_PADDING;
        mInnerPaddingOutline = DEF_INNER_PADDING_OUTLINE;
        mHighlightStrength   = DEF_HIGHLIGHT_STRENGTH;
        mUsePieRotation      = DEF_USE_PIE_ROTATION;
        mAutoCenterInSlice   = DEF_AUTO_CENTER;
        mDrawValueInPie      = DEF_DRAW_VALUE_IN_PIE;
        mValueTextSize       = Utils.dpToPx(DEF_VALUE_TEXT_SIZE);
        mValueTextColor      = DEF_VALUE_TEXT_COLOR;
        mUseCustomInnerValue = DEF_USE_CUSTOM_INNER_VALUE;
        mOpenClockwise       = DEF_OPEN_CLOCKWISE;
        mInnerPaddingColor   = DEF_INNER_PADDING_COLOR;
        mInnerValueUnit      = DEF_INNER_VALUE_UNIT;

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
            mUsePieRotation      = a.getBoolean(R.styleable.PieChart_egUsePieRotation,      DEF_USE_PIE_ROTATION);
            mAutoCenterInSlice   = a.getBoolean(R.styleable.PieChart_egAutoCenter,          DEF_AUTO_CENTER);
            mDrawValueInPie      = a.getBoolean(R.styleable.PieChart_egDrawValueInPie,      DEF_DRAW_VALUE_IN_PIE);
            mValueTextSize       = a.getDimension(R.styleable.PieChart_egValueTextSize,     Utils.dpToPx(DEF_VALUE_TEXT_SIZE));
            mValueTextColor      = a.getColor(R.styleable.PieChart_egValueTextColor,        DEF_VALUE_TEXT_COLOR);
            mUseCustomInnerValue = a.getBoolean(R.styleable.PieChart_egUseCustomInnerValue, DEF_USE_CUSTOM_INNER_VALUE);
            mOpenClockwise       = a.getBoolean(R.styleable.PieChart_egOpenClockwise,       DEF_OPEN_CLOCKWISE);
            mInnerPaddingColor   = a.getColor(R.styleable.PieChart_egInnerPaddingColor,     DEF_INNER_PADDING_COLOR);
            mInnerValueUnit      = a.getString(R.styleable.PieChart_egInnerValueUnit);

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        initializeGraph();
    }

    /**
     * Sets the onItemFocusChangedListener.
     *
     * @param _listener The instance of the IOnItemFocusChangedListener interface.
     */
    public void setOnItemFocusChangedListener(IOnItemFocusChangedListener _listener) {
        mListener = _listener;
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
            highlightSlice(model);
        }
        invalidateGlobal();
    }

    /**
     * Checks if the AutoCenter is activated or not.
     * AutoCenter is only available for API Level 11 and higher.
     *
     * @return True if AutoCenter is activated.
     */
    public boolean isAutoCenterInSlice() {
        return mAutoCenterInSlice;
    }

    /**
     * Sets the AutoCenter property.
     * AutoCenter is only available for API Level 11 and higher.
     *
     * @param _autoCenterInSlice True when AutoCenter should be used.
     */
    public void setAutoCenterInSlice(boolean _autoCenterInSlice) {
        mAutoCenterInSlice = _autoCenterInSlice;
    }

    /**
     * Checks if the PieRotation is enabled or not.
     *
     * @return True if rotation is enabled.
     */
    public boolean isUsePieRotation() {
        return mUsePieRotation;
    }

    /**
     * Sets the PieRotation property to activate or deactivate the rotation.
     *
     * @param _usePieRotation True if rotation should be enabled.
     */
    public void setUsePieRotation(boolean _usePieRotation) {
        mUsePieRotation = _usePieRotation;
    }

    /**
     * Checks if the currently selected PieSlice's value should be drawn in the center.
     *
     * @return True if the value is drawn in the center.
     */
    public boolean isDrawValueInPie() {
        return mDrawValueInPie;
    }

    /**
     * Sets the property which indicates whether the currently selected PieSlice's value should be
     * drawn in the center or not.
     *
     * @param _drawValueInPie True if the value should be drawn in the center.
     */
    public void setDrawValueInPie(boolean _drawValueInPie) {
        mDrawValueInPie = _drawValueInPie;
        invalidateGlobal();
    }

    /**
     * Returns the text size of the value which is drawn in the center of the PieChart.
     *
     * @return The value's text size.
     */
    public float getValueTextSize() {
        return mValueTextSize;
    }

    /**
     * Sets the text size of the value which is drawn in the center of the PieChart.
     *
     * @param _valueTextSize The value's text size in sp.
     */
    public void setValueTextSize(float _valueTextSize) {
        mValueTextSize = Utils.dpToPx(_valueTextSize);
        invalidateGlobal();
    }

    /**
     * Returns the color of the text which is drawn in the center of the PieChart.
     *
     * @return Color value of the text.
     */
    public int getValueTextColor() {
        return mValueTextColor;
    }

    /**
     * Sets the color of the text which is drawn in the center of the PieChart.
     *
     * @param _valueTextColor Color value of the text.
     */
    public void setValueTextColor(int _valueTextColor) {
        mValueTextColor = _valueTextColor;
    }

    /**
     * Returns the custom String which is displayed in the center of the PieChart.
     *
     * @return Value of String.
     */
    public String getInnerValueString() {
        return mInnerValueString;
    }

    /**
     * Sets the custom String which is displayed in the center of the PieChart.
     *
     * @param _innerValueString Custom String.
     */
    public void setInnerValueString(String _innerValueString) {
        mInnerValueString = _innerValueString;
        invalidateGraphOverlay();
    }

    /**
     * Checks if a custom inner value should be displayed or not.
     *
     * @return True if a custom inner value is shown.
     */
    public boolean isUseCustomInnerValue() {
        return mUseCustomInnerValue;
    }

    /**
     * Sets the indication whether a custom inner value should be drawn or not. If it's true, no
     * value of the currently selected PieSlcie is shown in the center of the PieChart.
     *
     * @param _useCustomInnerValue True if a custom inner value should be used.
     */
    public void setUseCustomInnerValue(boolean _useCustomInnerValue) {
        mUseCustomInnerValue = _useCustomInnerValue;
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

    /**
     *
     * @return The unit which is displayed after the value in the center of the PieChart.
     */
    public String getInnerValueUnit() {
        return mInnerValueUnit;
    }

    /**
     * Sets the unit which will be displayed after the value in the center of the PieChart.
     *
     * @param _innerValueUnit The unit appendix
     */
    public void setInnerValueUnit(String _innerValueUnit) {
        mInnerValueUnit = _innerValueUnit;
    }

    /**
     * Returns the index of the currently selected data item.
     *
     * @return The zero-based index of the currently selected data item.
     */
    public int getCurrentItem() {
        return mCurrentItem;
    }

    /**
     * Set the currently selected item. Calling this function will set the current selection
     * and rotate the pie to bring it into view.
     *
     * @param currentItem The zero-based index of the item to select.
     */
    public void setCurrentItem(int currentItem) {
        setCurrentItem(currentItem, true);
    }

    /**
     * Set the current item by index. Optionally, scroll the current item into view. This version
     * is for internal use--the scrollIntoView option is always true for external callers.
     *
     * @param currentItem    The index of the current item.
     * @param scrollIntoView True if the pie should rotate until the current item is centered.
     *                       False otherwise. If this parameter is false, the pie rotation
     *                       will not change.
     */
    private void setCurrentItem(int currentItem, boolean scrollIntoView) {
        mCurrentItem = currentItem;
        if (mListener != null) {
            mListener.onItemFocusChanged(currentItem);
        }
        if (scrollIntoView) {
            centerOnCurrentItem();
        }
        invalidateGlobal();
    }

    /**
     * Returns the current rotation of the pie graphic.
     *
     * @return The current pie rotation, in degrees.
     */
    public int getPieRotation() {
        return mPieRotation;
    }

    /**
     * Set the current rotation of the pie graphic. Setting this value may change
     * the current item.
     *
     * @param rotation The current pie rotation, in degrees.
     */
    public void setPieRotation(int rotation) {
        mPieRotation = (rotation % 360 + 360) % 360;
        mGraph.rotateTo(mPieRotation);

        calcCurrentItem();
    }

    /**
     * Adds a new Pie Slice to the PieChart. After inserting and calculation of the highlighting color
     * a complete recalculation is initiated.
     *
     * @param _Slice The newly added PieSlice.
     */
    public void addPieSlice(PieModel _Slice) {
        highlightSlice(_Slice);
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
    public boolean onTouchEvent(MotionEvent event) {
        // Let the GestureDetector interpret this event
        boolean result = false;

        if(mUsePieRotation) {
            result = mDetector.onTouchEvent(event);

            // If the GestureDetector doesn't want this event, do some custom processing.
            // This code just tries to detect when the user is done scrolling by looking
            // for ACTION_UP events.
            if (!result) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // User is done scrolling, it's now safe to do things like autocenter
                    stopScrolling();
                    result = true;
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
            result = true;
        }

        return result;
    }


    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // If the API level is less than 11, we can't rely on the view animation system to
        // do the scrolling animation. Need to tick it here and call postInvalidate() until the scrolling is done.
        if (Build.VERSION.SDK_INT < 11) {
            tickScrollAnimation();
            if (!mScroller.isFinished()) {
                mGraph.postInvalidate();
            }
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {

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

        mGraph.setPivot(mGraphBounds.centerX(), mGraphBounds.centerY());

        onDataChanged();
    }

    /**
     * This is the main entry point after the graph has been inflated. Used to initialize the graph
     * and its corresponding members.
     */
    @Override
    protected void initializeGraph() {
        super.initializeGraph();

        Utils.setLayerToSW(this);

        mPieData = new ArrayList<PieModel>();

        mTotalValue = 0;

        mGraphPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendPaint.setTextSize(mLegendTextSize);
        mLegendPaint.setColor(mLegendColor);
        mLegendPaint.setStyle(Paint.Style.FILL);

        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setTextSize(mValueTextSize);
        mValuePaint.setColor(mValueTextColor);
        mValuePaint.setStyle(Paint.Style.FILL);

        mGraph.rotateTo(mPieRotation);
        mGraph.decelerate();

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

        if(mUsePieRotation) {
            // Set up an animator to animate the PieRotation property. This is used to
            // correct the pie's orientation after the user lets go of it.
            mAutoCenterAnimator = ObjectAnimator.ofInt(PieChart.this, "PieRotation", 0);
            // Add a listener to hook the onAnimationEnd event so that we can do
            // some cleanup when the pie stops moving.
            mAutoCenterAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    mGraph.decelerate();
                }

                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationRepeat(Animator animator) {
                }
            });

            // Create a Scroller to handle the fling gesture.
            if (Build.VERSION.SDK_INT < 11) {
                mScroller = new Scroller(getContext());
            } else {
                mScroller = new Scroller(getContext(), null, true);
            }

            // The scroller doesn't have any built-in animation functions--it just supplies
            // values when we ask it to. So we have to have a way to call it every frame
            // until the fling ends. This code (ab)uses a ValueAnimator object to generate
            // a callback on every animation frame. We don't use the animated value at all.
            mScrollAnimator = ValueAnimator.ofFloat(0, 1);
            mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    tickScrollAnimation();
                }
            });

            // Create a gesture detector to handle onTouch messages
            mDetector = new GestureDetector(PieChart.this.getContext(), new GestureListener());

            // Turn off long press--this control doesn't use it, and if long press is enabled,
            // you can't scroll for a bit, pause, then scroll some more (the pause is interpreted
            // as a long press, apparently)
            mDetector.setIsLongpressEnabled(false);
        }

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
        calcCurrentItem();
        onScrollFinished();
    }

    /**
     * Calculate the highlight color. Saturate at 0xff to make sure that high values
     * don't result in aliasing.
     *
     * @param _Slice The Slice which will be highlighted.
     */
    private void highlightSlice(PieModel _Slice) {

        int color = _Slice.getColor();
        _Slice.setHighlightedColor(Color.argb(
                0xff,
                Math.min((int) (mHighlightStrength * (float) Color.red(color)), 0xff),
                Math.min((int) (mHighlightStrength * (float) Color.green(color)), 0xff),
                Math.min((int) (mHighlightStrength * (float) Color.blue(color)), 0xff)
        ));
    }

    /**
     * Calculate which pie slice is under the pointer, and set the current item
     * field accordingly.
     */
    private void calcCurrentItem() {
        int pointerAngle;

        // calculate the correct pointer angle, depending on clockwise drawing or not
        if(mOpenClockwise) {
            pointerAngle = (mIndicatorAngle + 360 - mPieRotation) % 360;
        }
        else {
            pointerAngle = (mIndicatorAngle + 180 + mPieRotation) % 360;
        }

        for (int i = 0; i < mPieData.size(); ++i) {
            PieModel model = mPieData.get(i);
            if (model.getStartAngle() <= pointerAngle && pointerAngle <= model.getEndAngle()) {
                if (i != mCurrentItem) {
                    setCurrentItem(i, false);
                }
                break;
            }
        }
    }

    private void tickScrollAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.computeScrollOffset();
            setPieRotation(mScroller.getCurrY());
        } else {
            mScrollAnimator.cancel();
            onScrollFinished();
        }
    }


    /**
     * Force a stop to all pie motion. Called when the user taps during a fling.
     */
    private void stopScrolling() {
        mScroller.forceFinished(true);
        mAutoCenterAnimator.cancel();

        onScrollFinished();
    }

    /**
     * Called when the user finishes a scroll action.
     */
    private void onScrollFinished() {
        if (mAutoCenterInSlice) {
            centerOnCurrentItem();
        } else {
            mGraph.decelerate();
        }
    }

    /**
     * Kicks off an animation that will result in the pointer being centered in the
     * pie slice of the currently selected item.
     */
    private void centerOnCurrentItem() {
        if(!mPieData.isEmpty()) {
            PieModel current = mPieData.get(getCurrentItem());
            int targetAngle;

            if(mOpenClockwise) {
                targetAngle = (mIndicatorAngle - current.getStartAngle()) - ((current.getEndAngle() - current.getStartAngle()) / 2);
                if (targetAngle < 0 && mPieRotation > 0) targetAngle += 360;
            }
            else {
                targetAngle = current.getStartAngle() + (current.getEndAngle() - current.getStartAngle()) / 2;
                targetAngle += mIndicatorAngle;
                if (targetAngle > 270 && mPieRotation < 90) targetAngle -= 360;
            }

            mAutoCenterAnimator.setIntValues(targetAngle);
            mAutoCenterAnimator.setDuration(AUTOCENTER_ANIM_DURATION).start();

        }
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
        else {
            // No Data available
            mGraphPaint.setColor(0xFFB6B6B6);
            _Canvas.drawArc(mGraphBounds,
                    0,
                    360,
                    true, mGraphPaint);

            // Draw inner white circle
            if (mUseInnerPadding) {
                mGraphPaint.setColor(0xFFC6C6C6);

                _Canvas.drawArc(mInnerBounds,
                        0,
                        360,
                        true, mGraphPaint);

                mGraphPaint.setColor(mInnerPaddingColor);

                _Canvas.drawArc(mInnerOutlineBounds,
                        0,
                        360,
                        true,
                        mGraphPaint);
            }
        }
    }

    @Override
    protected void onGraphOverlayDraw(Canvas _Canvas) {
        super.onGraphOverlayDraw(_Canvas);

        if(!mPieData.isEmpty() && mDrawValueInPie) {
            PieModel model = mPieData.get(mCurrentItem);

            if(!mUseCustomInnerValue) {
                mInnerValueString = Utils.getFloatString(model.getValue(), mShowDecimal);
                if (mInnerValueUnit != null && mInnerValueUnit.length() > 0) {
                    mInnerValueString += " " + mInnerValueUnit;
                }
            }

            mValuePaint.getTextBounds(mInnerValueString, 0, mInnerValueString.length(), mValueTextBounds);
            _Canvas.drawText(
                    mInnerValueString,
                    mInnerBounds.centerX() - (mValueTextBounds.width() / 2),
                    mInnerBounds.centerY() + (mValueTextBounds.height() / 2),
                    mValuePaint
            );
        }
    }

    @Override
    protected void onLegendDraw(Canvas _Canvas) {
        super.onLegendDraw(_Canvas);

        _Canvas.drawPath(mTriangle, mLegendPaint);

        float height = mMaxFontHeight = Utils.calculateMaxTextHeight(mLegendPaint, null);

        if(!mPieData.isEmpty()) {
            PieModel model = mPieData.get(mCurrentItem);

            // center text in view
            // TODO: move the boundary calculation out of onDraw
            mLegendPaint.getTextBounds(model.getLegendLabel(), 0, model.getLegendLabel().length(), mTextBounds);
            _Canvas.drawText(
                    model.getLegendLabel(),
                    (mLegendWidth / 2) - (mTextBounds.width() / 2),
                    mIndicatorSize * 2 + mIndicatorBottomMargin + mIndicatorTopMargin + height,
                    mLegendPaint
            );
        }
        else {

            mLegendPaint.getTextBounds(mEmptyDataText, 0, mEmptyDataText.length(), mTextBounds);
            _Canvas.drawText(
                    mEmptyDataText,
                    (mLegendWidth / 2) - (mTextBounds.width() / 2),
                    mIndicatorSize * 2 + mIndicatorBottomMargin + mIndicatorTopMargin + height,
                    mLegendPaint
            );
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
    }

    @Override
    protected void onLegendSizeChanged(int w, int h, int oldw, int oldh) {
        super.onLegendSizeChanged(w, h, oldw, oldh);

        mTriangle = new Path();
        mTriangle.moveTo((w / 2) - mIndicatorSize, mIndicatorSize*2 + mIndicatorTopMargin);
        mTriangle.lineTo((w / 2) + mIndicatorSize, mIndicatorSize*2 + mIndicatorTopMargin);
        mTriangle.lineTo(w / 2, mIndicatorTopMargin);
        mTriangle.lineTo((w / 2) - mIndicatorSize, mIndicatorSize*2 + mIndicatorTopMargin);

    }

    /**
     * Extends {@link android.view.GestureDetector.SimpleOnGestureListener} to provide custom gesture
     * processing.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Set the pie rotation directly.
            float scrollTheta = Utils.vectorToScalarScroll(
                    distanceX,
                    distanceY,
                    e2.getX() - getGraphBounds().centerX(),
                    e2.getY() - getGraphBounds().centerY());
            setPieRotation(mPieRotation - (int) scrollTheta / FLING_VELOCITY_DOWNSCALE);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Set up the Scroller for a fling
            float scrollTheta = Utils.vectorToScalarScroll(
                    velocityX,
                    velocityY,
                    e2.getX() - getGraphBounds().centerX(),
                    e2.getY() - getGraphBounds().centerY());
            mScroller.fling(
                    0,
                    mPieRotation,
                    0,
                    (int) scrollTheta / FLING_VELOCITY_DOWNSCALE,
                    0,
                    0,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE);

            // Start the animator and tell it to animate for the expected duration of the fling.
            mScrollAnimator.setDuration(mScroller.getDuration());
            mScrollAnimator.start();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            // The user is interacting with the pie, so we want to turn on acceleration
            // so that the interaction is smooth.
            if (isAnimationRunning()) {
                stopScrolling();
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            performClick();
            return true;
        }
    }

    /**
     * Checks if the animation is currently running.
     * @return True if animation is running.
     */
    private boolean isAnimationRunning() {
        return !mScroller.isFinished() || mAutoCenterAnimator.isRunning();
    }


    //##############################################################################################
    // Variables
    //##############################################################################################

    private static final String LOG_TAG = PieChart.class.getSimpleName();

    public static final float   DEF_INNER_PADDING           = 65.f;
    public static final float   DEF_INNER_PADDING_OUTLINE   = 5.f;
    public static final boolean DEF_USE_INNER_PADDING       = true;
    public static final float   DEF_HIGHLIGHT_STRENGTH      = 1.15f;
    public static final boolean DEF_USE_PIE_ROTATION        = true;
    public static final boolean DEF_AUTO_CENTER             = true;
    public static final boolean DEF_DRAW_VALUE_IN_PIE       = true;
    public static final float   DEF_VALUE_TEXT_SIZE         = 14.f;
    public static final int     DEF_VALUE_TEXT_COLOR        = 0xFF898989;
    public static final boolean DEF_USE_CUSTOM_INNER_VALUE  = false;
    public static final boolean DEF_OPEN_CLOCKWISE          = true;
    public static final int     DEF_INNER_PADDING_COLOR     = 0xFFF3F3F3; // Holo light background
    public static final String  DEF_INNER_VALUE_UNIT        = "";

    /**
     * The initial fling velocity is divided by this amount.
     */
    public static final int     FLING_VELOCITY_DOWNSCALE = 4;

    public static final int     AUTOCENTER_ANIM_DURATION = 250;

    private List<PieModel>      mPieData;

    private Paint               mGraphPaint;
    private Paint               mLegendPaint;
    private Paint               mValuePaint;

    private RectF               mGraphBounds;
    private RectF               mInnerBounds;
    private RectF               mInnerOutlineBounds;

    // Inner Value stuff
    private Rect                mValueTextBounds = new Rect();

    // Legend stuff
    private float               mIndicatorSize = Utils.dpToPx(8);
    private float               mIndicatorTopMargin = Utils.dpToPx(6);
    private float               mIndicatorBottomMargin = Utils.dpToPx(4);
    private Path                mTriangle;
    private Rect                mTextBounds = new Rect();


    private float               mPieDiameter;
    private float               mPieRadius;
    private float               mTotalValue;
    private String              mInnerValueString = "";

    // Attributes -----------------------------------------------------
    private boolean             mUseInnerPadding;
    private float               mInnerPadding;
    private float               mInnerPaddingOutline;
    private int                 mInnerPaddingColor;
    private float               mHighlightStrength;
    private boolean             mAutoCenterInSlice;
    private boolean             mUsePieRotation;
    private boolean             mDrawValueInPie;
    private float               mValueTextSize;
    private int                 mValueTextColor;
    private boolean             mUseCustomInnerValue;
    private boolean             mOpenClockwise;
    private String              mInnerValueUnit;
    // END - Attributes -----------------------------------------------

    private float               mCalculatedInnerPadding;
    private float               mCalculatedInnerPaddingOutline;

    private int                 mPieRotation;
    // Indicator is located at the bottom
    private int                 mIndicatorAngle = 90;
    private int                 mCurrentItem = 0;

    private ObjectAnimator      mAutoCenterAnimator;
    private Scroller            mScroller;
    private ValueAnimator       mScrollAnimator;
    private GestureDetector     mDetector;

    private IOnItemFocusChangedListener mListener;

}

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

package org.eazegraph.lib.models;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * The BaseModel is the parent model of every chart model. It basically only holds the information
 * about the legend labels of the childs value.
 */
public abstract class BaseModel {

    protected BaseModel(String _legendLabel) {
        mLegendLabel = _legendLabel;
    }

    protected BaseModel() {
    }

    public String getLegendLabel() {
        return mLegendLabel;
    }

    public void setLegendLabel(String _LegendLabel) {
        mLegendLabel = _LegendLabel;
    }

    public boolean canShowLabel() {
        return mShowLabel;
    }

    public void setShowLabel(boolean _showLabel) {
        mShowLabel = _showLabel;
    }

    public int getLegendLabelPosition() {
        return mLegendLabelPosition;
    }

    public void setLegendLabelPosition(int _legendLabelPosition) {
        mLegendLabelPosition = _legendLabelPosition;
    }

    public RectF getLegendBounds() {
        return mLegendBounds;
    }

    public void setLegendBounds(RectF _legendBounds) {
        mLegendBounds = _legendBounds;
    }

    public Rect getTextBounds() {
        return mTextBounds;
    }

    public void setTextBounds(Rect _textBounds) {
        mTextBounds = _textBounds;
    }

    public boolean isIgnore() {
        return mIgnore;
    }

    public void setIgnore(boolean _ignore) {
        mIgnore = _ignore;
    }

    /**
     * Label value
     */
    protected String    mLegendLabel;

    /**
     * Indicates whether the label should be shown or not.
     */
    protected boolean   mShowLabel;

    /**
     * X-coordinate of the label.
     */
    private int     mLegendLabelPosition;

    /**
     * Boundaries of the label
     */
    private RectF   mLegendBounds;

    /**
     * Boundaries of the legend labels value
     */
    private Rect    mTextBounds;

    /**
     * Indicates if the label should be ignored, when the boundaries are calculated.
     */
    private boolean mIgnore = false;
}

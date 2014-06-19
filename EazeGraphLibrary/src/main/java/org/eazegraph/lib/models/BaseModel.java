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

    protected String    mLegendLabel;
    protected boolean   mShowLabel;

    private int     mLegendLabelPosition;
    private RectF   mLegendBounds;
    private Rect    mTextBounds;
    private boolean mIgnore = false;
}

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
 * Model for the {@link org.eazegraph.lib.charts.BarChart}
 */
public class BarModel extends BaseModel implements Comparable {

    public BarModel(String _legendLabel, float _value, int _color) {
        super(_legendLabel);
        mValue = _value;
        mColor = _color;
    }

    public BarModel(float _value, int _color) {
        super("" + _value);
        mValue = _value;
        mColor = _color;
    }

    public BarModel(float _value) {
        super("" + _value);
        mValue = _value;
        mColor = 0xFFFF0000;
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float _value) {
        mValue = _value;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int _color) {
        mColor = _color;
    }

    public RectF getBarBounds() {
        return mBarBounds;
    }

    public void setBarBounds(RectF _bounds) {
        mBarBounds = _bounds;
    }

    public boolean isShowValue() {
        return mShowValue;
    }

    public void setShowValue(boolean _showValue) {
        mShowValue = _showValue;
    }

    public Rect getValueBounds() {
        return mValueBounds;
    }

    public void setValueBounds(Rect _valueBounds) {
        mValueBounds = _valueBounds;
    }

    @Override
    public int compareTo(Object o) {
        BarModel bar = (BarModel) o;
        if (this.mValue > bar.getValue()) {
            return 1;
        }
        else if (this.mValue == bar.getValue()) {
            return 0;
        }
        else {
            return -1;
        }
    }

    /**
     * Value of the bar.
     */
    private float mValue;

    /**
     * Color in which the bar will be drawn.
     */
    private int mColor;

    /**
     * Bar boundaries.
     */
    private RectF mBarBounds;

    private boolean mShowValue = false;

    private Rect mValueBounds = new Rect();
}
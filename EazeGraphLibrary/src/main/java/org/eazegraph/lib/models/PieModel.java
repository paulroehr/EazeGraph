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

/**
 * Model for the {@link org.eazegraph.lib.charts.PieChart}
 */
public class PieModel extends BaseModel implements Comparable {

    public PieModel(String _legendLabel, float _value, int _color) {
        super(_legendLabel);
        mValue = _value;
        mColor = _color;
    }

    public PieModel(float _value, int _color) {
        super("");
        mValue = _value;
        mColor = _color;
    }

    public PieModel() {
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float _Value) {
        mValue = _Value;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int _Color) {
        mColor = _Color;
    }

    public int getHighlightedColor() {
        return mHighlightedColor;
    }

    public void setHighlightedColor(int _HighlightedColor) {
        mHighlightedColor = _HighlightedColor;
    }

    public int getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(int _StartAngle) {
        mStartAngle = _StartAngle;
    }

    public int getEndAngle() {
        return mEndAngle;
    }

    public void setEndAngle(int _EndAngle) {
        mEndAngle = _EndAngle;
    }

    @Override
    public int compareTo(Object o) {
        PieModel pie = (PieModel) o;
        if (this.mValue > pie.getValue()) {
            return 1;
        }
        else if (this.mValue == pie.getValue()) {
            return 0;
        }
        else {
            return -1;
        }
    }

    /**
     * Value of the Pie Slice
     */
    private float mValue;

    /**
     * The color in which the pie slice will be drawn.
     */
    private int   mColor;

    /**
     * The highlighted mColor value.
     */
    private int   mHighlightedColor;

    /**
     * Start angle in the PieChart
     */
    private int   mStartAngle;

    /**
     * End angle in the PieChart
     */
    private int   mEndAngle;

}

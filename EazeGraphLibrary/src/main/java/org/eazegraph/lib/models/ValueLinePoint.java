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
 * Model for the {@link org.eazegraph.lib.models.ValueLineSeries}.
 */
public class ValueLinePoint extends BaseModel implements Comparable {

    public ValueLinePoint(float _value) {
        super("" + _value);
        mValue = _value;
    }

    public ValueLinePoint(String _legendLabel, float _value) {
        super(_legendLabel);
        mValue = _value;
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float _value) {
        mValue = _value;
    }

    public Point2D getCoordinates() {
        return mCoordinates;
    }

    public void setCoordinates(Point2D _coordinates) {
        mCoordinates = _coordinates;
    }

    @Override
    public int compareTo(Object o) {
        ValueLinePoint point = (ValueLinePoint) o;
        if (this.mValue > point.getValue()) {
            return 1;
        }
        else if (this.mValue == point.getValue()) {
            return 0;
        }
        else {
            return -1;
        }
    }

    /**
     * Value of the Point.
     */
    private float   mValue;

    /**
     * The coordinates for the chart. These are calculated dynamically.
     */
    private Point2D mCoordinates;
}

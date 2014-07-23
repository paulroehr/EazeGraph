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

import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for the {@link org.eazegraph.lib.charts.ValueLineChart}
 */
public class ValueLineSeries {

    public ValueLineSeries(List<ValueLinePoint> _series) {
        mSeries = _series;
        mPath = new Path();
    }

    public ValueLineSeries() {
        mSeries = new ArrayList<ValueLinePoint>();
        mPath = new Path();
    }

    public void addPoint(ValueLinePoint _valueLinePoint) {
        mSeries.add(_valueLinePoint);
    }

    public List<ValueLinePoint> getSeries() {
        return mSeries;
    }

    public void setSeries(List<ValueLinePoint> _series) {
        mSeries = _series;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int _color) {
        mColor = _color;
    }

    public Path getPath() {
        return mPath;
    }

    public void setPath(Path _path) {
        mPath = _path;
    }

    public float getWidthOffset() {
        return mWidthOffset;
    }

    public void setWidthOffset(float _widthOffset) {
        mWidthOffset = _widthOffset;
    }

    /**
     * The list of points, which will be concatenated as a Path.
     */
    private List<ValueLinePoint>    mSeries;

    /**
     * The generated Path based on the mSeries points.
     */
    private Path                    mPath;

    /**
     * The color of the path.
     */
    private int                     mColor;

    /**
     * Indicates the offset between each point in the series. This is calculated dynamically.
     */
    private float                   mWidthOffset;
}

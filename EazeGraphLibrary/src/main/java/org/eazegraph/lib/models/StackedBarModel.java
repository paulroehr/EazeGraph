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

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for the {@link org.eazegraph.lib.charts.StackedBarChart}.
 * This is a simple Wrapper class for various {@link org.eazegraph.lib.models.BarModel}
 */
public class StackedBarModel extends BaseModel {

    public StackedBarModel() {
        super("Unset");
        mBars = new ArrayList<BarModel>();
    }

    public StackedBarModel(String _legendLabel) {
        super(_legendLabel);
        mBars = new ArrayList<BarModel>();
    }

    public StackedBarModel(List<BarModel> _bars) {
        super("Unset");
        mBars = _bars;
    }

    public StackedBarModel(String _legendLabel, List<BarModel> _bars) {
        super(_legendLabel);
        mBars = _bars;
    }

    public List<BarModel> getBars() {
        return mBars;
    }

    public void setBars(List<BarModel> _bars) {
        mBars = _bars;
    }

    public void addBar(BarModel _bar) {
        mBars.add(_bar);
    }

    public RectF getBounds() {
        RectF bounds = new RectF();
        if(!mBars.isEmpty()) {
            // get bounds from complete StackedBar
            bounds.set(
                    mBars.get(0).getBarBounds().left,
                    mBars.get(0).getBarBounds().top,
                    mBars.get(mBars.size() - 1).getBarBounds().right,
                    mBars.get(mBars.size() - 1).getBarBounds().bottom
            );
        }
        return bounds;
    }

    /**
     * Bars which are in the StackedBar.
     */
    List<BarModel> mBars;
}

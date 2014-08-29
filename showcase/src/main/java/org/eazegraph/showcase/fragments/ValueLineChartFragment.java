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

package org.eazegraph.showcase.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.communication.IOnPointFocusedListener;
import org.eazegraph.lib.models.StandardValue;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.eazegraph.showcase.R;

public class ValueLineChartFragment extends ChartFragment {


    public ValueLineChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_value_line_chart, container, false);
        mValueLineChart = (ValueLineChart) view.findViewById(R.id.linechart);
        loadData();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mValueLineChart.startAnimation();
    }

    @Override
    public void restartAnimation() {
        mValueLineChart.startAnimation();
    }

    @Override
    public void onReset() {
        mValueLineChart.resetZoom(true);
    }

    private void loadData() {


        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF63CBB0);

//        series.addPoint(new ValueLinePoint(163.4f));
//        series.addPoint(new ValueLinePoint(162.f));
//        series.addPoint(new ValueLinePoint(161.4f));
//        series.addPoint(new ValueLinePoint(160.4f));
//        series.addPoint(new ValueLinePoint(159.4f));
//        series.addPoint(new ValueLinePoint(160.4f));
//        series.addPoint(new ValueLinePoint(158.4f));
//        series.addPoint(new ValueLinePoint(158.f));
//        series.addPoint(new ValueLinePoint(144.f));
//        series.addPoint(new ValueLinePoint(134.f));
//        series.addPoint(new ValueLinePoint(120.f));
//        series.addPoint(new ValueLinePoint(180.f));

        series.addPoint(new ValueLinePoint(4.4f));
        series.addPoint(new ValueLinePoint(2.4f));
        series.addPoint(new ValueLinePoint(3.2f));
        series.addPoint(new ValueLinePoint(2.6f));
        series.addPoint(new ValueLinePoint(5.0f));
        series.addPoint(new ValueLinePoint(3.5f));
        series.addPoint(new ValueLinePoint(2.4f));
        series.addPoint(new ValueLinePoint(0.4f));
        series.addPoint(new ValueLinePoint(3.4f));
        series.addPoint(new ValueLinePoint(2.5f));
        series.addPoint(new ValueLinePoint(1.4f));
        series.addPoint(new ValueLinePoint(4.4f));
        series.addPoint(new ValueLinePoint(2.4f));
        series.addPoint(new ValueLinePoint(3.2f));
        series.addPoint(new ValueLinePoint(2.6f));
        series.addPoint(new ValueLinePoint(5.0f));
        series.addPoint(new ValueLinePoint(3.5f));
        series.addPoint(new ValueLinePoint(2.4f));
        series.addPoint(new ValueLinePoint(0.4f));
        series.addPoint(new ValueLinePoint(3.4f));
        series.addPoint(new ValueLinePoint(2.5f));
        series.addPoint(new ValueLinePoint(1.0f));
        series.addPoint(new ValueLinePoint(4.4f));
        series.addPoint(new ValueLinePoint(2.4f));
        series.addPoint(new ValueLinePoint(3.2f));
        series.addPoint(new ValueLinePoint(2.6f));
        series.addPoint(new ValueLinePoint(5.0f));
        series.addPoint(new ValueLinePoint(3.5f));
        series.addPoint(new ValueLinePoint(2.4f));
        series.addPoint(new ValueLinePoint(0.4f));
        series.addPoint(new ValueLinePoint(3.4f));
        series.addPoint(new ValueLinePoint(2.5f));
        series.addPoint(new ValueLinePoint(1.0f));
        series.addPoint(new ValueLinePoint(4.2f));
        series.addPoint(new ValueLinePoint(2.4f));
        series.addPoint(new ValueLinePoint(3.6f));
        series.addPoint(new ValueLinePoint(1.0f));
        series.addPoint(new ValueLinePoint(2.5f));
        series.addPoint(new ValueLinePoint(2.0f));
        series.addPoint(new ValueLinePoint(1.4f));

//        mValueLineChart.addStandardValue(new StandardValue(140f));
//        mValueLineChart.addStandardValue(new StandardValue(163.4f));
        mValueLineChart.addSeries(series);
        mValueLineChart.setOnPointFocusedListener(new IOnPointFocusedListener() {
            @Override
            public void onPointFocused(int _PointPos) {
                Log.d("Test", "Pos: " + _PointPos);
            }
        });

    }

    private ValueLineChart mValueLineChart;
}

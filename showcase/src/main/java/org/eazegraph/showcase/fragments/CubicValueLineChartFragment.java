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
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.eazegraph.showcase.R;

public class CubicValueLineChartFragment extends ChartFragment {


    public CubicValueLineChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cubic_value_line_chart, container, false);
        mCubicValueLineChart = (ValueLineChart) view.findViewById(R.id.cubiclinechart);
        loadData();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mCubicValueLineChart.startAnimation();
    }

    @Override
    public void restartAnimation() {
        mCubicValueLineChart.startAnimation();
    }

    private void loadData() {
        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        series.addPoint(new ValueLinePoint("Jun 6", 165f));
        series.addPoint(new ValueLinePoint("Jun 8", 164.5f));
        series.addPoint(new ValueLinePoint("Jun 10", 164.1f));
        series.addPoint(new ValueLinePoint("Jun 11", 163.5f));
        series.addPoint(new ValueLinePoint("Jun 13", 162.9f));


//        series.addPoint(new ValueLinePoint("Jan", 2.4f));
//        series.addPoint(new ValueLinePoint("Feb", 3.4f));
//        series.addPoint(new ValueLinePoint("Mar", 1.4f));
//        series.addPoint(new ValueLinePoint("Apr", 1.2f));
//        series.addPoint(new ValueLinePoint("Mai", 2.6f));
//        series.addPoint(new ValueLinePoint("Jun", 2.0f));
//        series.addPoint(new ValueLinePoint("Jul", 3.5f));
//        series.addPoint(new ValueLinePoint("Aug", 2.4f));
//        series.addPoint(new ValueLinePoint("Sep", 2.4f));
//        series.addPoint(new ValueLinePoint("Oct", 3.4f));
//        series.addPoint(new ValueLinePoint("Nov", 4.4f));
//        series.addPoint(new ValueLinePoint("Dec", 1.8f));
//        series.addPoint(new ValueLinePoint("Jan", 2.2f));
//        series.addPoint(new ValueLinePoint("Feb", 3.4f));
//        series.addPoint(new ValueLinePoint("Mar", 2.0f));
//        series.addPoint(new ValueLinePoint("Apr", 2.8f));
//        series.addPoint(new ValueLinePoint("Mai", 3.5f));
//        series.addPoint(new ValueLinePoint("Jun", 2.4f));

        ValueLineSeries series1 = new ValueLineSeries();
        series1.setColor(0xFFF33741);
//
//        series1.addPoint(new ValueLinePoint("Jan", 1.4f));
//        series1.addPoint(new ValueLinePoint("Feb", 2.4f));
//        series1.addPoint(new ValueLinePoint("Mar", 0.4f));
//        series1.addPoint(new ValueLinePoint("Apr", 0.2f));
//        series1.addPoint(new ValueLinePoint("Mai", 1.6f));
//        series1.addPoint(new ValueLinePoint("Jun", 1.0f));
//        series1.addPoint(new ValueLinePoint("Jul", 2.5f));
//        series1.addPoint(new ValueLinePoint("Aug", 1.4f));
//        series1.addPoint(new ValueLinePoint("Sep", .4f));
//        series1.addPoint(new ValueLinePoint("Oct", 2.4f));
//        series1.addPoint(new ValueLinePoint("Nov", 1.4f));
//        series1.addPoint(new ValueLinePoint("Dec", .5f));
//        series1.addPoint(new ValueLinePoint("Jan", 1.2f));
//        series1.addPoint(new ValueLinePoint("Feb", 2.4f));
//        series1.addPoint(new ValueLinePoint("Mar", 1.6f));
//        series1.addPoint(new ValueLinePoint("Apr", 2.0f));
//        series1.addPoint(new ValueLinePoint("Mai", 1.5f));
//        series1.addPoint(new ValueLinePoint("Jun", 1.4f));

        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.addSeries(series1);
        mCubicValueLineChart.addStandardValue(165f);
        mCubicValueLineChart.addStandardValue(153f);
//        mCubicValueLineChart.addStandardValue(4.3f);
        mCubicValueLineChart.setOnPointFocusedListener(new IOnPointFocusedListener() {
            @Override
            public void onPointFocused(int _PointPos) {
                Log.d("Test", "Pos: " + _PointPos);
            }
        });

    }

    private ValueLineChart mCubicValueLineChart;
}

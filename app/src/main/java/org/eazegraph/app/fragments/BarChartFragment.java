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

package org.eazegraph.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.eazegraph.app.R;
import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.BaseBarChart;
import org.eazegraph.lib.models.BarModel;

public class BarChartFragment extends ChartFragment implements BaseBarChart.BarChartListener<BarModel> {

    public BarChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        mBarChart = (BarChart) view.findViewById(R.id.barchart);
        mBarChart.setUseLegend(true);
        mBarChart.setBarChartListener(this);
        mBarChart2 = (BarChart) view.findViewById(R.id.barchart2);
        mBarChart2.setUseLegend(false);
        mBarChart2.setBarChartListener(this);
        loadData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBarChart.startAnimation();
        mBarChart2.startAnimation();
    }

    @Override
    public void restartAnimation() {
        mBarChart.startAnimation();
        mBarChart2.startAnimation();
    }

    private void loadData() {
        mBarChart.addBar(new BarModel(2.3f, 0xFF123456));
        mBarChart.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart.addBar(new BarModel(3.3f, 0xFF563456));
        mBarChart.addBar(new BarModel(1.1f, 0xFF873F56));
        mBarChart.addBar(new BarModel(2.7f, 0xFF56B7F1));
        mBarChart.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart.addBar(new BarModel(0.4f, 0xFF1FF4AC));
        mBarChart.addBar(new BarModel(4.f,  0xFF1BA4E6));
        mBarChart.addBar(new BarModel(2.7f, 0xFF56B7F1));
        mBarChart.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart.addBar(new BarModel(0.4f, 0xFF1FF4AC));
        mBarChart.addBar(new BarModel(4.f,  0xFF1BA4E6));

        mBarChart2.addBar(new BarModel(2.3f, 0xFF123456));
        mBarChart2.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart2.addBar(new BarModel(3.3f, 0xFF563456));
        mBarChart2.addBar(new BarModel(1.1f, 0xFF873F56));
        mBarChart2.addBar(new BarModel(2.7f, 0xFF56B7F1));
        mBarChart2.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart2.addBar(new BarModel(0.4f, 0xFF1FF4AC));
        mBarChart2.addBar(new BarModel(4.f,  0xFF1BA4E6));
        mBarChart2.addBar(new BarModel(2.7f, 0xFF56B7F1));
        mBarChart2.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart2.addBar(new BarModel(0.4f, 0xFF1FF4AC));
        mBarChart2.addBar(new BarModel(4.f,  0xFF1BA4E6));
    }

    private BarChart mBarChart;
    private BarChart mBarChart2;

    @Override
    public void onBarClick(BaseBarChart view, int i, BarModel model) {
        Toast.makeText(getActivity(), "Bar chart: " + i, Toast.LENGTH_SHORT).show();
    }
}

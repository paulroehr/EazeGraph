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

package org.eazegraph.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import org.eazegraph.app.fragments.BarChartFragment;
import org.eazegraph.app.fragments.ChartFragment;
import org.eazegraph.app.fragments.CubicValueLineChartFragment;
import org.eazegraph.app.fragments.PieChartFragment;
import org.eazegraph.app.fragments.StackedBarChartFragment;
import org.eazegraph.app.fragments.ValueLineChartFragment;

public class ChartActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        onSectionAttached(position);
        switch (position) {
            case 0:
                mCurrentFragment = new BarChartFragment();
                break;
            case 1:
                mCurrentFragment = new StackedBarChartFragment();
                break;
            case 2:
                mCurrentFragment = new PieChartFragment();
                break;
            case 3:
                mCurrentFragment = new ValueLineChartFragment();
                break;
            case 4:
                mCurrentFragment = new CubicValueLineChartFragment();
                break;
            default:
                mCurrentFragment = new BarChartFragment();
                break;
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mCurrentFragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.nav_bar_chart);
                break;
            case 1:
                mTitle = getString(R.string.nav_stacked_bar_chart);
                break;
            case 2:
                mTitle = getString(R.string.nav_pie_chart);
                break;
            case 3:
                mTitle = getString(R.string.nav_value_line_chart);
                break;
            case 4:
                mTitle = getString(R.string.nav_cubic_value_line_chart);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.chart, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_restart) {
            mCurrentFragment.restartAnimation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ChartFragment mCurrentFragment;
}

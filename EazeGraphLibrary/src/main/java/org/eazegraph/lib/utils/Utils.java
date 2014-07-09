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

package org.eazegraph.lib.utils;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.List;

import org.eazegraph.lib.models.BaseModel;

public class Utils {

    //dpToPx----------------------------------------------------------------------------------------
    /**
     * Converts density-independent pixel (dp) to pixel (px)
     *
     * @param _Dp the dp value to convert in pixel
     *
     * @return the converted value in pixels
     */
    public static float dpToPx(float _Dp) {
        return _Dp * Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * Helper method for translating (_X,_Y) scroll vectors into scalar rotation of a circle.
     *
     * @param _Dx The _X component of the current scroll vector.
     * @param _Dy The _Y component of the current scroll vector.
     * @param _X  The _X position of the current touch, relative to the circle center.
     * @param _Y  The _Y position of the current touch, relative to the circle center.
     * @return The scalar representing the change in angular position for this scroll.
     */
    public static float vectorToScalarScroll(float _Dx, float _Dy, float _X, float _Y) {
        // get the length of the vector
        float l = (float) Math.sqrt(_Dx * _Dx + _Dy * _Dy);

        // decide if the scalar should be negative or positive by finding
        // the dot product of the vector perpendicular to (_X,_Y).
        float crossX = -_Y;
        float crossY = _X;

        float dot = (crossX * _Dx + crossY * _Dy);
        float sign = Math.signum(dot);

        return l * sign;
    }

    /**
     * Calculates the legend positions and which legend title should be displayed or not.
     *
     * Important: the LegendBounds in the _Models should be set and correctly calculated before this
     * function is called!
     * @param _Models The graph data which should have the BaseModel class as parent class.
     * @param _StartX Left starting point on the screen. Should be the absolute pixel value!
     * @param _Paint  The correctly set Paint which will be used for the text painting in the later process
     */
    public static void calculateLegendInformation(List<? extends BaseModel> _Models, float _StartX, float _EndX, Paint _Paint) {
        float lastX = _StartX;
        float textMargin = Utils.dpToPx(10.f);

        // calculate the legend label positions and check if there is enough space to display the label,
        // if not the label will not be shown
        for (BaseModel model : _Models) {
            if (!model.isIgnore()) {
                Rect textBounds = new Rect();
                RectF legendBounds = model.getLegendBounds();

                _Paint.getTextBounds(model.getLegendLabel(), 0, model.getLegendLabel().length(), textBounds);
                model.setTextBounds(textBounds);

                float centerX           = legendBounds.centerX();
                float centeredTextPos   = centerX - (textBounds.width() / 2);
                float textStartPos      = centeredTextPos - textMargin;

                if (lastX == _StartX) {
                    model.setShowLabel(true);
                    // check if the text position is beyond the screen
                    if (textStartPos + textMargin < _StartX) {
                        // set text position to screen start
                        model.setLegendLabelPosition((int) _StartX);
                        lastX = textBounds.width();
                    } else {
                        // set text centered beneath the line
                        model.setLegendLabelPosition((int) centeredTextPos);
                        lastX = centerX + (textBounds.width() / 2);
                    }
                }
                // check if the text is too big to fit on the screen
                else if(centeredTextPos + textBounds.width() > _EndX) {
                    model.setShowLabel(false);
                }
                else {
                    // check if the current legend label overrides the label before
                    // if the label overrides the label before, the current label will not be shown.
                    // If not the label will be shown and the label position is calculated
                    if (textStartPos < lastX) {
                        if (lastX + textMargin < legendBounds.left) {
                            model.setLegendLabelPosition((int) (lastX + textMargin));
                            model.setShowLabel(true);
                            lastX = lastX + textMargin + textBounds.width();
                        } else {
                            model.setShowLabel(false);
                        }
                    }
                    else {
                        model.setShowLabel(true);
                        model.setLegendLabelPosition((int) centeredTextPos);
                        lastX = centerX + (textBounds.width() / 2);
                    }
                }
            }
        }

    }

    /**
     * Returns an string with or without the decimal places.
     * @param _value
     * @param _showDecimal
     * @return
     */
    public static String getFloatString(float _value, boolean _showDecimal) {
        if (_showDecimal) {
            return _value+"";
        }
        else {
            return ((int) _value) + "";
        }
    }

    private static final String LOG_TAG = Utils.class.getSimpleName();
}

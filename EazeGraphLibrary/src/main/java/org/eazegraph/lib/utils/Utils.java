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

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.View;

import org.eazegraph.lib.models.BaseModel;
import org.eazegraph.lib.models.Point2D;

import java.util.List;

/**
 * A helper class which consists of static helper methods.
 */
public class Utils {

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
     * Calculates the middle point between two points and multiplies its coordinates with the given
     * smoothness _Mulitplier.
     * @param _P1           First point
     * @param _P2           Second point
     * @param _Result       Resulting point
     * @param _Multiplier   Smoothness multiplier
     */
    public static void calculatePointDiff(Point2D _P1, Point2D _P2, Point2D _Result, float _Multiplier) {
        float diffX = _P2.getX() - _P1.getX();
        float diffY = _P2.getY() - _P1.getY();
        _Result.setX(_P1.getX() + (diffX * _Multiplier));
        _Result.setY(_P1.getY() + (diffY * _Multiplier));
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
        float textMargin = Utils.dpToPx(10.f);
        float lastX = _StartX;

        // calculate the legend label positions and check if there is enough space to display the label,
        // if not the label will not be shown
        for (BaseModel model : _Models) {
            if (!model.isIgnore()) {
                Rect textBounds = new Rect();
                RectF legendBounds = model.getLegendBounds();

                _Paint.getTextBounds(model.getLegendLabel(), 0, model.getLegendLabel().length(), textBounds);
                model.setTextBounds(textBounds);

                float centerX = legendBounds.centerX();
                float centeredTextPos = centerX - (textBounds.width() / 2);
                float textStartPos = centeredTextPos - textMargin;

                // check if the text is too big to fit on the screen
                if (centeredTextPos + textBounds.width() > _EndX - textMargin) {
                    model.setShowLabel(false);
                } else {
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
                    } else {
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
     * @param _value        The value which should be converted
     * @param _showDecimal  Indicates whether the decimal numbers should be shown or not
     * @return              A generated string of the value.
     */
    public static String getFloatString(float _value, boolean _showDecimal) {
        if (_showDecimal) {
            return _value+"";
        }
        else {
            return ((int) _value) + "";
        }
    }

    /**
     * Calculates the maximum text height which is possible based on the used Paint and its settings.
     *
     * @param _Paint Paint object which will be used to display a text.
     * @param _Text  The text which should be measured. If null, a default text is chosen, which
     *               has a maximum possible height
     * @return Maximum text height in px.
     */
    public static float calculateMaxTextHeight(Paint _Paint, String _Text) {
        Rect height = new Rect();
        String text = _Text == null ? "MgHITasger" : _Text;
        _Paint.getTextBounds(text, 0, text.length(), height);
        return height.height();
    }

    /**
     * Checks if a point is in the given rectangle.
     *
     * @param _Rect rectangle which is checked
     * @param _X    x-coordinate of the point
     * @param _Y    y-coordinate of the point
     * @return True if the points intersects with the rectangle.
     */
    public static boolean intersectsPointWithRectF(RectF _Rect, float _X, float _Y) {
        return _X > _Rect.left && _X < _Rect.right && _Y > _Rect.top && _Y < _Rect.bottom;
    }


    @SuppressLint("NewApi")
    public static void setLayerToSW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @SuppressLint("NewApi")
    public static void setLayerToHW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    /**
     * Returns the x-scale component of the _Matrix
     * @param _Matrix A float 3x3 matrix
     * @return X-Scale
     */
    public static float getScaleX(float[] _Matrix) {
        return _Matrix[0];
    }

    /**
     * Returns the y-scale component of the _Matrix
     * @param _Matrix A float 3x3 matrix
     * @return Y-Scale
     */
    public static float getScaleY(float[] _Matrix) {
        return _Matrix[4];
    }

    /**
     * Returns the x-translation component of the _Matrix
     * @param _Matrix A float 3x3 matrix
     * @return X-Translation
     */
    public static float getTranslationX(float[] _Matrix) {
        return _Matrix[2];
    }

    /**
     * Returns the y-translation component of the _Matrix
     * @param _Matrix A float 3x3 matrix
     * @return Y-Translation
     */
    public static float getTranslationY(float[] _Matrix) {
        return _Matrix[5];
    }

    /**
     * Sets the x-scale component in a 3x3 matrix
     * @param _Value        The value which will be set
     * @param _DestMatrix   The matrix where the value will be saved
     */
    public static void setScaleX(float _Value, float[] _DestMatrix) {
        _DestMatrix[0] = _Value;
    }

    /**
     * Sets the y-scale component in a 3x3 matrix
     * @param _Value        The value which will be set
     * @param _DestMatrix   The matrix where the value will be saved
     */
    public static void setScaleY(float _Value, float[] _DestMatrix) {
        _DestMatrix[4] = _Value;
    }

    /**
     * Sets the x-translation component in a 3x3 matrix
     * @param _Value        The value which will be set
     * @param _DestMatrix   The matrix where the value will be saved
     */
    public static void setTranslationX(float _Value, float[] _DestMatrix) {
        _DestMatrix[2] = _Value;
    }

    /**
     * Sets the y-translation component in a 3x3 matrix
     * @param _Value        The value which will be set
     * @param _DestMatrix   The matrix where the value will be saved
     */
    public static void setTranslationY(float _Value, float[] _DestMatrix) {
        _DestMatrix[5] = _Value;
    }

    private static final String LOG_TAG = Utils.class.getSimpleName();
}

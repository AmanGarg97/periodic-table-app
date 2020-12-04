/*
 * The MIT License (MIT)
 * Copyright © 2012 Steve Guidetti
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ultramegatech.ey.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ultramegatech.ey.R;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.ey.util.PreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Renders a color legend on a PeriodicTableView.
 *
 * @author Steve Guidetti
 */
class PeriodicTableLegend {
    /**
     * Map of key values to labels
     */
    @NonNull
    private final HashMap<String, String> mMap = new LinkedHashMap<>();

    /**
     * Paint used to draw backgrounds
     */
    @NonNull
    private final Paint mPaint = new Paint();

    /**
     * Paint used to draw text
     */
    @NonNull
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * Rectangle used to draw backgrounds
     */
    @NonNull
    private final Rect mRect = new Rect();
    Context context;
    /**
     * @param context The Context
     */
    PeriodicTableLegend(@NonNull Context context) {
        invalidate(context);
        this.context = context;
    }

    /**
     * Load the legend data from resources.
     *
     * @param context The Context
     */
    void invalidate(@NonNull Context context) {
        final Resources res = context.getResources();
        final String[] keys;
        final String[] nameValues;
        if(PreferenceUtils.COLOR_BLOCK.equals(PreferenceUtils.getPrefElementColors())) {
            keys = res.getStringArray(R.array.ptBlocks);
            nameValues = res.getStringArray(R.array.ptBlocks);
        } else {
            keys = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
            nameValues = res.getStringArray(R.array.ptCategories);
        }

        mMap.clear();
        for(int i = 0; i < keys.length; i++) {
            mMap.put(keys[i], nameValues[i]);
        }
    }

    /**
     * Render the legend within the specified rectangle on the specified Canvas. The legend appears
     * as a grid of colored rectangles in 4 rows and a variable number of columns. Each rectangle
     * contains text declaring the value represented by the rectangle's color.
     *
     * @param canvas Canvas on which to draw
     * @param rect   Boundaries within which to draw
     */
    void drawLegend(@NonNull Canvas canvas, @NonNull Rect rect) {
        final int count = mMap.size();
        final int rows = 4;
        final int cols = (int)Math.ceil(count / (double)rows);
        final int boxHeight = (rect.bottom - rect.top) / rows;

        mTextPaint.setTextSize(boxHeight / 2f);

        int boxWidth = 0;
        if(cols < 2) {
            boxWidth = rect.width();
        } else {
            for(String value : mMap.values()) {
                boxWidth = (int)Math.ceil(Math.max(boxWidth, mTextPaint.measureText(value)));
            }
            boxWidth += boxWidth / 10;
        }

        final float totalWidth = boxWidth * cols;
        if(totalWidth > rect.width()) {
            boxWidth *= rect.width() / totalWidth;
            mTextPaint.setTextSize(mTextPaint.getTextSize() * rect.width() / totalWidth);
        } else {
            rect.left += (rect.width() - totalWidth) / 2;
        }

        int n = 0;
        for(Entry<String, String> entry : mMap.entrySet()) {
            mRect.top = rect.top + n % rows * boxHeight + 1;
            mRect.left = rect.left + n / rows * boxWidth + 1;
            mRect.bottom = mRect.top + boxHeight - 1;
            mRect.right = mRect.left + boxWidth - 1;
            this.rects.put(entry.getValue(), new Rect(mRect));
            mPaint.setColor(ElementUtils.getKeyColor(entry.getKey()));
            canvas.drawRect(mRect, mPaint);

            canvas.drawText(entry.getValue(), mRect.left + boxWidth / 20f,
                    mRect.bottom - boxHeight / 2f + mTextPaint.getTextSize() / 2, mTextPaint);

            n++;
        }
    }

    HashMap<String, Rect> rects = new HashMap<String, Rect>();
    public boolean wasDown = false;
    String valClicked = "";
    void onClick(int x, int y) {
        if(this.wasDown) return;
        this.wasDown = true;
        Iterator it = rects.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Rect> pair = (Map.Entry)it.next();
            if(pair.getValue().contains(x, y)) {
                Log.d("Clicking", "onClick: selected: " + pair.getKey());
                this.valClicked = pair.getKey();
                break;
            }

        }
    }
    void clickComplete() {
        this.wasDown = false;
        Log.d("Click Complete", "clickComplete: " + this.valClicked);
        if(this.valClicked == "") return ;
        AlertDialog alertDialog = new AlertDialog.Builder(this.context).create();
        alertDialog.setTitle(this.valClicked);
        alertDialog.setMessage("Malleable \n" +
                "Ductile \n" +
                "Lose 1 electron to form cations \n" +
                "Solids at rtp \n" +
                "Low density\n" +
                "Good conductors of heat and electricity \n" +
                "\n" +
                "React with water to produce H2 gas \n" +
                "eg: M + H20 —> M(OH) + H2\n" +
                "Metal oxides are generally basic in nature \n" +
                "\n" +
                "Bonding : Positive kernels immersed in a sea of delocalised electrons resulting in metallic bonding.\n" +
                "\n" +
                "\n" +
                "Periodic Trends\n" +
                "Down the group,\n" +
                "no. of shells increases, shielding increases. Therefore, effective nuclear charge decreases and atomic radius increases.\n" +
                "Ionisation energy decreases \n" +
                "Metallic property (ability to lose electrons) increases \n" +
                "Reactivity increases \n");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            } });
        alertDialog.show();
        this.valClicked = "";
    }
}

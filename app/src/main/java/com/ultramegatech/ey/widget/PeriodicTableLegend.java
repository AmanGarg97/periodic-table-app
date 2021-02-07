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
//import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
//import android.nfc.Tag;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ultramegatech.ey.AboutFragment;
import com.ultramegatech.ey.R;
import com.ultramegatech.ey.SettingsActivity;
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

    String getText(@Nullable String titleName) {
        String text = "• " + titleName;
        switch(titleName) {
            case "Alkali metal":
                text = "• Malleable \n" +
                        "• Ductile \n" +
                        "• Lose 1 electron to form cations \n" +
                        "• Solids at rtp \n" +
                        "• Low density\n" +
                        "• Good conductors of heat and electricity \n" +
                        "\n" +
                        "• React with water to produce H2 gas \n" +
                        "eg: M + H20 —> M(OH) + H2\n" +
                        "• Metal oxides are generally basic in nature \n" +
                        "\n" +
                        "• Bonding : Positive kernels immersed in a sea of delocalised electrons resulting in metallic bonding.\n" +
                        "\n" +
                        "\n" +
                        "• Periodic Trends\n" +
                        "Down the group,\n" +
                        "1. no. of shells increases, shielding increases. Therefore, effective nuclear charge decreases and atomic radius increases.\n" +
                        "2. Ionisation energy decreases \n" +
                        "3. Metallic property (ability to lose electrons) increases \n" +
                        "4. Reactivity increases \n";
                return text;
            case "Alkaline earth metal":
                text = "• Malleable \n" +
                        "• Ductile \n" +
                        "• Lose 2 electrons to form cations \n" +
                        "• Solids at rtp \n" +
                        "• Low density\n" +
                        "• Good conductors of heat and electricity \n" +
                        "• Less reactive than alkali metals \n" +
                        "\n" +
                        "• React with water to produce H2 gas \n" +
                        "eg: M + 2H20 —> M(OH)2 + H2\n" +
                        "• Metal oxides are generally basic in nature \n" +
                        "\n" +
                        "Bonding : Positive kernels immersed in a sea of delocalised electrons resulting in metallic bonding.\n" +
                        "\n" +
                        "\n" +
                        "• Periodic Trends\n" +
                        "Down the group,\n" +
                        "1. no. of shells increases, shielding increases. Therefore, effective nuclear charge decreases and atomic radius increases.\n" +
                        "2. Ionisation energy decreases \n" +
                        "3. Metallic property (ability to lose electrons) increases \n" +
                        "4. Reactivity increases \n";
                return text;
            case "Transition metal":
                text = "• Form stable ions having partially filled d orbitals \n" +
                        "• Exhibit variable oxidation states \n" +
                        "• Are used as catalysts\n" +
                        "• Form coloured compounds \n" +
                        "• Higher MP and BP than representative metals \n" +
                        "• Harder and more tensile than representative metals \n" +
                        "• High density \n" +
                        "\n" +
                        "\n" +
                        "• Periodic Properties\n" +
                        "• Down the group,\n" +
                        "1. No. of shells increases, shielding increases. Therefore, effective nuclear charge decreases and atomic radius increases.\n" +
                        "2. Ionisation energy decreases \n" +
                        "3. Metallic property (ability to lose electrons) increases \n" +
                        "4. Reactivity increases \n";
                return text;
            case "Metalliod":
                text = "• Exhibit properties of both metals and nonmetals";
                return text;
            case "Non-metal":
                text = "• Low BP and MP\n" +
                        "• Generally liquids and gases at rtp or brittle solids at rtp.\n" +
                        "\n" +
                        "• Form acidic oxides\n" +
                        "• Acidic oxides dissolved in water form acids \n" +
                        "HCl (g) + H20 ——> HCl (aq)\n" +
                        "\n" +
                        "• Bonding : can share electrons to form covalent bonds.\n" +
                        "\n" +
                        "• Periodic Properties\n" +
                        "\n" +
                        "Down the group,\n" +
                        "1. no. of shells increases, shielding increases. Therefore, effective nuclear charge decreases and atomic radius increases.\n" +
                        "2. Electronegativity decreases ( F is most electronegative element)\n" +
                        "3. Non metallic property (ability to gain electrons) decreases \n" +
                        "4. Reactivity decreases \n";
                return text;
            case "Noble gas":
                text = "• Are inert because they have completely filled valence shells ";
                return text;
            case "Actinide":
                text = "• All actinides are radioactive ";
                return text;
        }

        return text;
    }


    void clickComplete() {
        this.wasDown = false;
        Log.d("Click Complete", "clickComplete: " + this.valClicked);
        if(this.valClicked == "") return ;
        AlertDialog alertDialog = new AlertDialog.Builder(this.context).create();
        alertDialog.setTitle(this.valClicked);
//        alertDialog.setTitle(Html.fromHtml("<font color='#008000''>"this.valClicked;"</font>"));
        String message = getText(this.valClicked);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            } });
        alertDialog.show();
        this.valClicked = "";
    }
}

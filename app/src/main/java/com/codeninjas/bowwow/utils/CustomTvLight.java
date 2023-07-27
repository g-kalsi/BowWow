package com.codeninjas.bowwow.utils;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Reaston3 on 4/4/2017.
 */

public class CustomTvLight extends androidx.appcompat.widget.AppCompatTextView {

    public CustomTvLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTvLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTvLight(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "muli_light.ttf");
            setTypeface(tf);
        }
    }
}
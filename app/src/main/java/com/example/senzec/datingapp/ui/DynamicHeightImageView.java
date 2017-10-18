package com.example.senzec.datingapp.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.senzec.datingapp.R;

/**
 * Created by power hashing on 4/24/2017.
 */


public class DynamicHeightImageView extends ImageView {

    // region Member Variables
    private double heightRatio;
    // endregion

    // region Constructors
    public DynamicHeightImageView(Context context) {
        super(context);
        init(context, null);
    }

    public DynamicHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DynamicHeightImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    // endregion

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (heightRatio > 0.0) {
            // set the image views size
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width * heightRatio);
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    // region Helper Methods
    private void init(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            TypedArray attributeArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.DynamicHeightImageView);

            try {
                float heightRatio = attributeArray.getFloat(R.styleable.DynamicHeightImageView_heightRatio, 1.0F);
                this.heightRatio = heightRatio;
            } finally {
                attributeArray.recycle();
            }
        }
    }

    public void setHeightRatio(double ratio) {
        if (ratio != heightRatio) {
            heightRatio = ratio;
            requestLayout();
        }
    }

    public double getHeightRatio() {
        return heightRatio;
    }
    // endregion
}

package com.ap.bharosaadvisor.pinlockview;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ap.bharosaadvisor.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class IndicatorDots extends LinearLayout
{
    @IntDef({IndicatorType.FIXED, IndicatorType.FILL, IndicatorType.FILL_WITH_ANIMATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IndicatorType
    {
        int FIXED = 0;
        int FILL = 1;
        int FILL_WITH_ANIMATION = 2;
    }

    private static final int DEFAULT_PIN_LENGTH = 4;
    private static final int DEFAULT_ANIMATION_DURATION = 200;

    private int dotDiameter;
    private int dotSpacing;
    private int fillDrawable;
    private int emptyDrawable;
    private int pinLength;
    private int indicatorType;
    private int previousLength;

    public IndicatorDots(Context context)
    {
        this(context, null);
    }

    public IndicatorDots(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public IndicatorDots(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        @SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PinLockView);

        try
        {
            dotDiameter = (int) typedArray.getDimension(R.styleable.PinLockView_dotDiameter, ResourceUtils.getDimensionInPx(getContext(), R.dimen.dot_diameter));
            dotSpacing = (int) typedArray.getDimension(R.styleable.PinLockView_dotSpacing, ResourceUtils.getDimensionInPx(getContext(), R.dimen.dot_spacing));
            fillDrawable = typedArray.getResourceId(R.styleable.PinLockView_dotFilledBackground,
                    R.drawable.dot_filled);
            emptyDrawable = typedArray.getResourceId(R.styleable.PinLockView_dotEmptyBackground,
                    R.drawable.dot_empty);
            pinLength = typedArray.getInt(R.styleable.PinLockView_pinLength, DEFAULT_PIN_LENGTH);
            indicatorType = typedArray.getInt(R.styleable.PinLockView_indicatorType,
                    IndicatorType.FIXED);
        } finally
        {
            typedArray.recycle();
        }

        initView(context);
    }

    private void initView(Context context)
    {
        if (indicatorType == 0)
        {
            for (int i = 0; i < pinLength; i++)
            {
                View dot = new View(context);
                emptyDot(dot);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotDiameter,
                        dotDiameter);
                params.setMargins(dotSpacing, 0, dotSpacing, 0);
                dot.setLayoutParams(params);

                addView(dot);
            }
        } else if (indicatorType == 2)
        {
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.setDuration(DEFAULT_ANIMATION_DURATION);
            layoutTransition.setStartDelay(LayoutTransition.APPEARING, 0);
            setLayoutTransition(layoutTransition);
        }
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (indicatorType != 0)
        {
            ViewGroup.LayoutParams params = this.getLayoutParams();
            params.height = dotDiameter;
            requestLayout();
        }
    }

    void updateDot(int length)
    {
        if (indicatorType == 0)
        {
            if (length > 0)
            {
                if (length > previousLength)
                    fillDot(getChildAt(length - 1));
                else
                    emptyDot(getChildAt(length));
                previousLength = length;
            } else
            {
                // When {@code pinLength} is 0, we need to reset all the views back to empty
                for (int i = 0; i < getChildCount(); i++)
                {
                    View v = getChildAt(i);
                    emptyDot(v);
                }
                previousLength = 0;
            }
        } else
        {
            if (length > 0)
            {
                if (length > previousLength)
                {
                    View dot = new View(getContext());
                    fillDot(dot);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotDiameter,
                            dotDiameter);
                    params.setMargins(dotSpacing, 0, dotSpacing, 0);
                    dot.setLayoutParams(params);

                    addView(dot, length - 1);
                } else
                    removeViewAt(length);
                previousLength = length;
            } else
            {
                removeAllViews();
                previousLength = 0;
            }
        }
    }

    private void emptyDot(View dot)
    {
        dot.setBackgroundResource(emptyDrawable);
    }

    private void fillDot(View dot)
    {
        dot.setBackgroundResource(fillDrawable);
    }

    public int getPinLength()
    {
        return pinLength;
    }

    public void setPinLength(int pinLength)
    {
        this.pinLength = pinLength;
        removeAllViews();
        initView(getContext());
    }

    public
    @IndicatorType
    int getIndicatorType()
    {
        return indicatorType;
    }

    public void setIndicatorType(@IndicatorType int type)
    {
        this.indicatorType = type;
        removeAllViews();
        initView(getContext());
    }
}
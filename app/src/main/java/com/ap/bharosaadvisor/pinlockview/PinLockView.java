package com.ap.bharosaadvisor.pinlockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.adapters.PinLockAdapter;

/**
 * Represents a numeric lock view which can used to taken numbers as input.
 * The length of the input can be customized using {@link PinLockView#setPinLength(int)}, the default value being 4
 * <p/>
 * It can also be used as dial pad for taking number inputs.
 * Optionally, {@link IndicatorDots} can be attached to this view to indicate the length of the input taken
 */
public class PinLockView extends RecyclerView
{
    private static final int DEFAULT_PIN_LENGTH = 4;
    private static final int[] DEFAULT_KEY_SET = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

    private String pin = "";
    private int pinLength;
    private int horizontalSpacing, verticalSpacing;
    private int textColor, deleteButtonPressedColor;
    private int textSize, buttonSize, deleteButtonWidthSize, deleteButtonHeightSize;
    private Drawable buttonBackgroundDrawable;
    private Drawable deleteButtonDrawable;
    private boolean showDeleteButton;
    private IndicatorDots indicatorDots;
    private PinLockAdapter adapter;
    private PinLockListener pinLockListener;
    private CustomizationOptionsBundle customizationOptionsBundle;
    private int[] customKeySet;
    private PinLockAdapter.OnNumberClickListener onNumberClickListener
            = new PinLockAdapter.OnNumberClickListener()
    {
        @Override
        public void onNumberClicked(int keyValue)
        {
            if (pin.length() < getPinLength())
            {
                pin = pin.concat(String.valueOf(keyValue));
                if (isIndicatorDotsAttached())
                    indicatorDots.updateDot(pin.length());

                if (pin.length() == 1)
                {
                    adapter.setPinLength(pin.length());
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                }

                if (pinLockListener != null)
                {
                    if (pin.length() == pinLength)
                        pinLockListener.onComplete(pin);
                    else
                        pinLockListener.onPinChange(pin.length(), pin);
                }
            } else
            {
                if (!isShowDeleteButton())
                {
                    resetPinLockView();
                    pin = pin.concat(String.valueOf(keyValue));

                    if (isIndicatorDotsAttached())
                        indicatorDots.updateDot(pin.length());
                    if (pinLockListener != null)
                        pinLockListener.onPinChange(pin.length(), pin);

                } else
                {
                    if (pinLockListener != null)
                        pinLockListener.onComplete(pin);
                }
            }
        }
    };

    private PinLockAdapter.OnDeleteClickListener mOnDeleteClickListener
            = new PinLockAdapter.OnDeleteClickListener()
    {
        @Override
        public void onDeleteClicked()
        {
            if (pin.length() > 0)
            {
                pin = pin.substring(0, pin.length() - 1);
                if (isIndicatorDotsAttached())
                    indicatorDots.updateDot(pin.length());

                if (pin.length() == 0)
                {
                    adapter.setPinLength(pin.length());
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                }

                if (pinLockListener != null)
                {
                    if (pin.length() == 0)
                    {
                        pinLockListener.onEmpty();
                        clearInternalPin();
                    } else
                        pinLockListener.onPinChange(pin.length(), pin);
                }
            } else
            {
                if (pinLockListener != null)
                    pinLockListener.onEmpty();
            }
        }

        @Override
        public void onDeleteLongClicked()
        {
            resetPinLockView();
            if (pinLockListener != null)
                pinLockListener.onEmpty();
        }
    };

    public PinLockView(Context context)
    {
        super(context);
        init(null);
    }

    public PinLockView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public PinLockView(Context context, @Nullable AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attributeSet)
    {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.PinLockView);

        try
        {
            pinLength = typedArray.getInt(R.styleable.PinLockView_pinLength, DEFAULT_PIN_LENGTH);
            horizontalSpacing = (int) typedArray.getDimension(R.styleable.PinLockView_keypadHorizontalSpacing, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_horizontal_spacing));
            verticalSpacing = (int) typedArray.getDimension(R.styleable.PinLockView_keypadVerticalSpacing, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_vertical_spacing));
            textColor = typedArray.getColor(R.styleable.PinLockView_keypadTextColor, ResourceUtils.getColor(getContext(), R.color.text_number_pressed));
            textSize = (int) typedArray.getDimension(R.styleable.PinLockView_keypadTextSize, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_text_size));
            buttonSize = (int) typedArray.getDimension(R.styleable.PinLockView_keypadButtonSize, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_button_size));
            deleteButtonWidthSize = (int) typedArray.getDimension(R.styleable.PinLockView_keypadDeleteButtonSize, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_delete_button_size_width));
            deleteButtonHeightSize = (int) typedArray.getDimension(R.styleable.PinLockView_keypadDeleteButtonSize, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_delete_button_size_height));
            buttonBackgroundDrawable = typedArray.getDrawable(R.styleable.PinLockView_keypadButtonBackgroundDrawable);
            deleteButtonDrawable = typedArray.getDrawable(R.styleable.PinLockView_keypadDeleteButtonDrawable);
            showDeleteButton = typedArray.getBoolean(R.styleable.PinLockView_keypadShowDeleteButton, true);
            deleteButtonPressedColor = typedArray.getColor(R.styleable.PinLockView_keypadDeleteButtonPressedColor, ResourceUtils.getColor(getContext(), R.color.text_number_pressed));
        } finally
        {
            typedArray.recycle();
        }

        customizationOptionsBundle = new CustomizationOptionsBundle();
        customizationOptionsBundle.setTextColor(textColor);
        customizationOptionsBundle.setTextSize(textSize);
        customizationOptionsBundle.setButtonSize(buttonSize);
        customizationOptionsBundle.setButtonBackgroundDrawable(buttonBackgroundDrawable);
        customizationOptionsBundle.setDeleteButtonDrawable(deleteButtonDrawable);
        customizationOptionsBundle.setDeleteButtonWidthSize(deleteButtonWidthSize);
        customizationOptionsBundle.setDeleteButtonHeightSize(deleteButtonHeightSize);
        customizationOptionsBundle.setShowDeleteButton(showDeleteButton);
        customizationOptionsBundle.setDeleteButtonPressesColor(deleteButtonPressedColor);

        initView();
    }

    private void initView()
    {
        setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter = new PinLockAdapter();
        adapter.setOnItemClickListener(onNumberClickListener);
        adapter.setOnDeleteClickListener(mOnDeleteClickListener);
        adapter.setCustomizationOptions(customizationOptionsBundle);
        setAdapter(adapter);

        addItemDecoration(new ItemSpaceDecoration(horizontalSpacing, verticalSpacing, 3, false));
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void setTypeFace(Typeface typeFace)
    {
        adapter.setTypeFace(typeFace);
    }

    /**
     * Sets a {@link PinLockListener} to the to listen to pin update events
     *
     * @param pinLockListener the listener
     */
    public void setPinLockListener(PinLockListener pinLockListener)
    {
        this.pinLockListener = pinLockListener;
    }

    /**
     * Get the length of the current pin length
     *
     * @return the length of the pin
     */
    public int getPinLength()
    {
        return pinLength;
    }

    /**
     * Sets the pin length dynamically
     *
     * @param pinLength the pin length
     */
    public void setPinLength(int pinLength)
    {
        this.pinLength = pinLength;

        if (isIndicatorDotsAttached())
        {
            indicatorDots.setPinLength(pinLength);
        }
    }

    /**
     * Get the text color in the buttons
     *
     * @return the text color
     */
    public int getTextColor()
    {
        return textColor;
    }

    /**
     * Set the text color of the buttons dynamically
     *
     * @param textColor the text color
     */
    public void setTextColor(int textColor)
    {
        this.textColor = textColor;
        customizationOptionsBundle.setTextColor(textColor);
        adapter.notifyDataSetChanged();
    }

    /**
     * Get the size of the text in the buttons
     *
     * @return the size of the text in pixels
     */
    public int getTextSize()
    {
        return textSize;
    }

    /**
     * Set the size of text in pixels
     *
     * @param textSize the text size in pixels
     */
    public void setTextSize(int textSize)
    {
        this.textSize = textSize;
        customizationOptionsBundle.setTextSize(textSize);
        adapter.notifyDataSetChanged();
    }

    /**
     * Get the size of the pin buttons
     *
     * @return the size of the button in pixels
     */
    public int getButtonSize()
    {
        return buttonSize;
    }

    /**
     * Set the size of the pin buttons dynamically
     *
     * @param buttonSize the button size
     */
    public void setButtonSize(int buttonSize)
    {
        this.buttonSize = buttonSize;
        customizationOptionsBundle.setButtonSize(buttonSize);
        adapter.notifyDataSetChanged();
    }

    /**
     * Get the current background drawable of the buttons, can be null
     *
     * @return the background drawable
     */
    public Drawable getButtonBackgroundDrawable()
    {
        return buttonBackgroundDrawable;
    }

    /**
     * Set the background drawable of the buttons dynamically
     *
     * @param buttonBackgroundDrawable the background drawable
     */
    public void setButtonBackgroundDrawable(Drawable buttonBackgroundDrawable)
    {
        this.buttonBackgroundDrawable = buttonBackgroundDrawable;
        customizationOptionsBundle.setButtonBackgroundDrawable(buttonBackgroundDrawable);
        adapter.notifyDataSetChanged();
    }

    /**
     * Get the drawable of the delete button
     *
     * @return the delete button drawable
     */
    public Drawable getDeleteButtonDrawable()
    {
        return deleteButtonDrawable;
    }

    /**
     * Set the drawable of the delete button dynamically
     *
     * @param deleteBackgroundDrawable the delete button drawable
     */
    public void setDeleteButtonDrawable(Drawable deleteBackgroundDrawable)
    {
        this.deleteButtonDrawable = deleteBackgroundDrawable;
        customizationOptionsBundle.setDeleteButtonDrawable(deleteBackgroundDrawable);
        adapter.notifyDataSetChanged();
    }

    /**
     * Get the delete button width size in pixels
     *
     * @return size in pixels
     */
    public int getDeleteButtonWidthSize()
    {
        return deleteButtonWidthSize;
    }

    /**
     * Get the delete button size height in pixels
     *
     * @return size in pixels
     */
    public int getDeleteButtonHeightSize()
    {
        return deleteButtonHeightSize;
    }

    /**
     * Set the size of the delete button width in pixels
     *
     * @param deleteButtonWidthSize size in pixels
     */
    public void setDeleteButtonWidthSize(int deleteButtonWidthSize)
    {
        this.deleteButtonWidthSize = deleteButtonWidthSize;
        customizationOptionsBundle.setDeleteButtonWidthSize(deleteButtonWidthSize);
        adapter.notifyDataSetChanged();
    }

    /**
     * Set the size of the delete button height in pixels
     *
     * @param deleteButtonHeightSize size in pixels
     */
    public void setDeleteButtonHeightSize(int deleteButtonHeightSize)
    {
        this.deleteButtonHeightSize = deleteButtonHeightSize;
        customizationOptionsBundle.setDeleteButtonWidthSize(deleteButtonHeightSize);
        adapter.notifyDataSetChanged();
    }

    /**
     * Is the delete button shown
     *
     * @return returns true if shown, false otherwise
     */
    public boolean isShowDeleteButton()
    {
        return showDeleteButton;
    }

    /**
     * Dynamically set if the delete button should be shown
     *
     * @param showDeleteButton true if the delete button should be shown, false otherwise
     */
    public void setShowDeleteButton(boolean showDeleteButton)
    {
        this.showDeleteButton = showDeleteButton;
        customizationOptionsBundle.setShowDeleteButton(showDeleteButton);
        adapter.notifyDataSetChanged();
    }

    /**
     * Get the delete button pressed/focused state color
     *
     * @return color of the button
     */
    public int getDeleteButtonPressedColor()
    {
        return deleteButtonPressedColor;
    }

    /**
     * Set the pressed/focused state color of the delete button
     *
     * @param deleteButtonPressedColor the color of the delete button
     */
    public void setDeleteButtonPressedColor(int deleteButtonPressedColor)
    {
        this.deleteButtonPressedColor = deleteButtonPressedColor;
        customizationOptionsBundle.setDeleteButtonPressesColor(deleteButtonPressedColor);
        adapter.notifyDataSetChanged();
    }

    public int[] getCustomKeySet()
    {
        return customKeySet;
    }

    public void setCustomKeySet(int[] customKeySet)
    {
        this.customKeySet = customKeySet;

        if (adapter != null)
            adapter.setKeyValues(customKeySet);
    }

    public void enableLayoutShuffling()
    {
        this.customKeySet = ShuffleArrayUtils.shuffle(DEFAULT_KEY_SET);

        if (adapter != null)
            adapter.setKeyValues(customKeySet);
    }

    private void clearInternalPin()
    {
        pin = "";
    }

    /**
     * Resets the {@link PinLockView}, clearing the entered pin
     * and resetting the {@link IndicatorDots} if attached
     */
    public void resetPinLockView()
    {
        clearInternalPin();

        adapter.setPinLength(pin.length());
        adapter.notifyItemChanged(adapter.getItemCount() - 1);

        if (indicatorDots != null)
            indicatorDots.updateDot(pin.length());
    }

    /**
     * Returns true if {@link IndicatorDots} are attached to {@link PinLockView}
     *
     * @return true if attached, false otherwise
     */
    public boolean isIndicatorDotsAttached()
    {
        return indicatorDots != null;
    }

    /**
     * Attaches {@link IndicatorDots} to {@link PinLockView}
     *
     * @param mIndicatorDots the view to attach
     */
    public void attachIndicatorDots(IndicatorDots mIndicatorDots)
    {
        this.indicatorDots = mIndicatorDots;
    }
}

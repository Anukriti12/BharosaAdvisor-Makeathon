package com.ap.bharosaadvisor.adapters;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ap.bharosaadvisor.EnterPinActivity;
import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.pinlockview.CustomizationOptionsBundle;

import java.lang.ref.WeakReference;

public class PinLockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private static final int VIEW_TYPE_NUMBER = 0;
    private static final int VIEW_TYPE_DELETE = 1;
    private static final int VIEW_TYPE_DONE= 2;

    private CustomizationOptionsBundle customizationOptionsBundle;
    private OnNumberClickListener onNumberClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private int pinLength;
    private int[] keyValues;
    private Typeface typeface = null;

    public PinLockAdapter()
    {
        this.keyValues = getAdjustKeyValues(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
    }

    public void setTypeFace(Typeface typeFace)
    {
        typeface = typeFace;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder ;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_NUMBER)
        {
            View view = inflater.inflate(R.layout.layout_number_item, parent, false);
            viewHolder = new NumberViewHolder(view, typeface);
        } else if((viewType == VIEW_TYPE_DELETE))
        {
            View view = inflater.inflate(R.layout.layout_delete_item, parent, false);
            viewHolder = new DeleteViewHolder(view);
        }else
        {
            View view = inflater.inflate(R.layout.layout_done_item, parent, false);
            viewHolder = new DoneViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if (holder.getItemViewType() == VIEW_TYPE_NUMBER)
        {
            NumberViewHolder vh1 = (NumberViewHolder) holder;
            configureNumberButtonHolder(vh1, position);
        } else if (holder.getItemViewType() == VIEW_TYPE_DELETE) {
            DeleteViewHolder vh2 = (DeleteViewHolder) holder;
            configureDeleteButtonHolder(vh2);
        }

//        else
//        {
//            DoneViewHolder vh3 = (DoneViewHolder) holder;
//            configureDoneButtonHolder(vh3);
//        }

    }

    private void configureNumberButtonHolder(NumberViewHolder holder, int position)
    {
        if (holder != null)
        {
            if (position == 9)
                holder.mNumberButton.setVisibility(View.GONE);
            else
            {
                holder.mNumberButton.setText(String.valueOf(keyValues[position]));
                holder.mNumberButton.setVisibility(View.VISIBLE);
                holder.mNumberButton.setTag(keyValues[position]);
            }

            if (customizationOptionsBundle != null)
            {
                holder.mNumberButton.setTextColor(customizationOptionsBundle.getTextColor());
                if (customizationOptionsBundle.getButtonBackgroundDrawable() != null)
                {
                    holder.mNumberButton.setBackground(
                            customizationOptionsBundle.getButtonBackgroundDrawable());
                }
                holder.mNumberButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        customizationOptionsBundle.getTextSize());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        customizationOptionsBundle.getButtonSize(),
                        customizationOptionsBundle.getButtonSize());
                holder.mNumberButton.setLayoutParams(params);
            }
        }
    }

    private void configureDeleteButtonHolder(DeleteViewHolder holder)
    {
        if (holder != null)
        {
            if (customizationOptionsBundle.isShowDeleteButton() && pinLength > 0)
            {
                holder.mButtonImage.setVisibility(View.VISIBLE);
                if (customizationOptionsBundle.getDeleteButtonDrawable() != null)
                    holder.mButtonImage.setImageDrawable(customizationOptionsBundle.getDeleteButtonDrawable());
                holder.mButtonImage.setColorFilter(customizationOptionsBundle.getTextColor(),
                        PorterDuff.Mode.SRC_ATOP);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        customizationOptionsBundle.getDeleteButtonWidthSize(),
                        customizationOptionsBundle.getDeleteButtonHeightSize());
                holder.mButtonImage.setLayoutParams(params);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return 12;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == getItemCount() - 1)
            return VIEW_TYPE_DELETE;
        if (position == getItemCount() - 3)
            return VIEW_TYPE_DONE;

        return VIEW_TYPE_NUMBER;
    }

    public int getPinLength()
    {
        return pinLength;
    }

    public void setPinLength(int pinLength)
    {
        this.pinLength = pinLength;
    }

    public int[] getKeyValues()
    {
        return keyValues;
    }

    public void setKeyValues(int[] keyValues)
    {
        this.keyValues = getAdjustKeyValues(keyValues);
        notifyDataSetChanged();
    }

    private int[] getAdjustKeyValues(int[] keyValues)
    {
        int[] adjustedKeyValues = new int[keyValues.length + 1];
        for (int i = 0; i < keyValues.length; i++)
        {
            if (i < 9)
                adjustedKeyValues[i] = keyValues[i];
            else
            {
                adjustedKeyValues[i] = -1;
                adjustedKeyValues[i + 1] = keyValues[i];
            }
        }
        return adjustedKeyValues;
    }

    public OnNumberClickListener getOnItemClickListener()
    {
        return onNumberClickListener;
    }

    public void setOnItemClickListener(OnNumberClickListener onNumberClickListener)
    {
        this.onNumberClickListener = onNumberClickListener;
    }

    public OnDeleteClickListener getOnDeleteClickListener()
    {
        return onDeleteClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener)
    {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public CustomizationOptionsBundle getCustomizationOptions()
    {
        return customizationOptionsBundle;
    }

    public void setCustomizationOptions(CustomizationOptionsBundle customizationOptionsBundle)
    {
        this.customizationOptionsBundle = customizationOptionsBundle;
    }

    public interface OnNumberClickListener
    {
        void onNumberClicked(int keyValue);
    }

    public interface OnDeleteClickListener
    {
        void onDeleteClicked();

        void onDeleteLongClicked();
    }

    public class NumberViewHolder extends RecyclerView.ViewHolder
    {
        Button mNumberButton;

        @SuppressLint("ClickableViewAccessibility")
        NumberViewHolder(final View itemView, Typeface font)
        {
            super(itemView);
            mNumberButton = itemView.findViewById(R.id.button);

            if (font != null)
                mNumberButton.setTypeface(font);

            mNumberButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (onNumberClickListener != null)
                        onNumberClickListener.onNumberClicked((Integer) v.getTag());
                }
            });

            mNumberButton.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        mNumberButton.startAnimation(scale());
                    return false;
                }
            });
        }
    }

    public class DeleteViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout mDeleteButton;
        ImageView mButtonImage;

        @SuppressLint("ClickableViewAccessibility")
        DeleteViewHolder(final View itemView)
        {
            super(itemView);
            mDeleteButton = itemView.findViewById(R.id.button);
            mButtonImage = itemView.findViewById(R.id.buttonImage);

            if (customizationOptionsBundle.isShowDeleteButton() && pinLength > 0)
            {
                mDeleteButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (onDeleteClickListener != null)
                            onDeleteClickListener.onDeleteClicked();
                    }
                });

                mDeleteButton.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        if (onDeleteClickListener != null)
                            onDeleteClickListener.onDeleteLongClicked();
                        return true;
                    }
                });

                mDeleteButton.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if (event.getAction() == MotionEvent.ACTION_DOWN)
                            mDeleteButton.startAnimation(scale());
                        return false;
                    }
                });
            }
        }
    }

    public class DoneViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout mDoneButton;
        ImageView mButtonImage;

        @SuppressLint("ClickableViewAccessibility")
        DoneViewHolder(final View itemView)
        {
            super(itemView);
            mDoneButton = itemView.findViewById(R.id.button1);
            mButtonImage = itemView.findViewById(R.id.buttonImage1);

                mDoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EnterPinActivity.instance.get().performPinSet();
                    }
                });
        }
    }

    private Animation scale()
    {
        ScaleAnimation scaleAnimation = new ScaleAnimation(.75F, 1f, .75F, 1f,
                Animation.RELATIVE_TO_SELF, .5F, Animation.RELATIVE_TO_SELF, .5F);
        int BUTTON_ANIMATION_DURATION = 150;
        scaleAnimation.setDuration(BUTTON_ANIMATION_DURATION);
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }
}
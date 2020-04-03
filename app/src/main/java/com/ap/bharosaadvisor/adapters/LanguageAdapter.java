package com.ap.bharosaadvisor.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.data.Language;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder>
{
    private Language[] data;
    private Context ctx;
    private TextView lastSelectedLabel;

    public int selectedLanguage;

    public LanguageAdapter(Context _ctx, Language[] _data)
    {
        data = _data;
        ctx = _ctx;
    }

    @NonNull
    @Override
    public LanguageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_language, parent, false);
        return new LanguageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LanguageAdapter.ViewHolder holder, int position)
    {
        holder.label.setText(data[position].languageName);
        if (position == selectedLanguage)
            selectLanguage(holder);

        holder.label.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectLanguage(holder);
            }
        });
    }

    private void selectLanguage(@NonNull final LanguageAdapter.ViewHolder holder)
    {
        selectedLanguage = holder.getAdapterPosition();

        if (lastSelectedLabel != null)
            lastSelectedLabel.setBackgroundColor(ctx.getResources().getColor(R.color.colorTransparent));
        holder.label.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
        lastSelectedLabel = holder.label;
    }

    @Override
    public int getItemCount()
    {
        return data.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView label;

        ViewHolder(View itemView)
        {
            super(itemView);
            label = itemView.findViewById(R.id.language_label);
        }
    }
}

package com.ap.bharosaadvisor.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ap.bharosaadvisor.R;

import java.util.ArrayList;

public class RecentTransactionsAdapter extends RecyclerView.Adapter<RecentTransactionsAdapter.ViewHolder>
{
    private Context ctx;
    private ArrayList<String> data;

    public RecentTransactionsAdapter(Context _ctx, ArrayList<String> _data)
    {
        data = _data;
        ctx = _ctx;
    }

    @NonNull
    @Override
    public RecentTransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_recent_transaction, parent, false);
        return new RecentTransactionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentTransactionsAdapter.ViewHolder holder, int position)
    {
        String value = data.get(position);
        holder.label.setText(value);
        if (!value.contains(ctx.getString(R.string.no_recent_transactions)))
        {
            if (value.contains("Purchase"))
                holder.label.setTextColor(ctx.getResources().getColor(R.color.colorGreen));
            else
                holder.label.setTextColor(ctx.getResources().getColor(R.color.colorRed));
        }
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView label;

        ViewHolder(View itemView)
        {
            super(itemView);
            label = itemView.findViewById(R.id.transaction_label);
        }
    }
}

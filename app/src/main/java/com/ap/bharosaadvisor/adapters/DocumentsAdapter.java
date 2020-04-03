package com.ap.bharosaadvisor.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ap.bharosaadvisor.DocumentActivity;
import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.data.LearnDocument;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.ViewHolder>
{
    private Context ctx;
    private Activity activity;
    private ArrayList<LearnDocument> data;

    public DocumentsAdapter(Context _ctx, Activity _activity, ArrayList<LearnDocument> _data)
    {
        ctx = _ctx;
        activity = _activity;
        data = _data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {
        holder.title.setText(data.get(position).title);
        Picasso.with(ctx)
                .load(data.get(position).thumbnail)
                .into(holder.thumbnail, new Callback()
                {
                    @Override
                    public void onSuccess()
                    {
                        holder.loader.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError()
                    {

                    }
                });
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(activity, DocumentActivity.class);
                intent.putExtra("url", data.get(holder.getAdapterPosition()).link);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        View loader;
        ImageView thumbnail;

        ViewHolder(View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.document_title);
            loader = itemView.findViewById(R.id.document_loader);
            thumbnail = itemView.findViewById(R.id.document_thumbnail);
        }
    }
}

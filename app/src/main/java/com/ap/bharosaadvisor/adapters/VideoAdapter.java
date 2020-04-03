package com.ap.bharosaadvisor.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ap.bharosaadvisor.VideoPlayerActivity;
import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.data.LearnVideo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>
{
    private ArrayList<LearnVideo> data;
    private Context ctx;

    public VideoAdapter(Context _ctx, ArrayList<LearnVideo> _videoIds)
    {
        data = _videoIds;
        ctx = _ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_card,
                parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {
        viewHolder.title.setText(data.get(position).title);
        Picasso.with(ctx)
                .load(data.get(position).thumbnail)
                .into(viewHolder.thumbnail, new Callback()
                {
                    @Override
                    public void onSuccess()
                    {
                        viewHolder.loader.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError()
                    {

                    }
                });
        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ctx, VideoPlayerActivity.class);
                intent.putExtra("url", data.get(viewHolder.getAdapterPosition()).link);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView thumbnail;
        View loader;
        TextView title;

        ViewHolder(View itemView)
        {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.video_thumbnail);
            loader = itemView.findViewById(R.id.video_loader);
            title = itemView.findViewById(R.id.video_title);
        }
    }
}
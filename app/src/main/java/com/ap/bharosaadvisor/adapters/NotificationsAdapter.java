package com.ap.bharosaadvisor.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.data.Notification;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.RecyclerViewHolder>
{
    private ArrayList<Notification> notifications;

    public NotificationsAdapter(ArrayList<Notification> _notifications)
    {
        notifications = _notifications;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position)
    {
        holder.notificationContent.setText(notifications.get(position).content);
        holder.notificationIcon.setImageResource(notifications.get(position).contentIcon);
    }

    @Override
    public int getItemCount()
    {
        return notifications.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView notificationContent;
        ImageView notificationIcon;

        RecyclerViewHolder(View itemView)
        {
            super(itemView);
            notificationContent = itemView.findViewById(R.id.tx_item);
            notificationIcon = itemView.findViewById(R.id.iv);

        }
    }


}

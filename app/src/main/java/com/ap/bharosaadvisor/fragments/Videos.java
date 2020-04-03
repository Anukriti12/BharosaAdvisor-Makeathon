package com.ap.bharosaadvisor.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.adapters.VideoAdapter;

import static com.ap.bharosaadvisor.helper.Utils.LEARN_VIDEOS;

public class Videos extends Fragment
{

    Context ctx;
    FragmentManager fm;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.learn_videos, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();

        RecyclerView recyclerView = rootView.findViewById(R.id.videos_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter recyclerViewAdapter = new VideoAdapter(ctx, LEARN_VIDEOS);
        recyclerView.setAdapter(recyclerViewAdapter);

        return rootView;
    }
}
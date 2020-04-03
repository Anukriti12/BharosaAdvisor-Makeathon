package com.ap.bharosaadvisor.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.adapters.DocumentsAdapter;
import com.ap.bharosaadvisor.helper.Utils;

public class Documents extends Fragment
{
    Context ctx;
    FragmentManager fm;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.learn_documents, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();

        RecyclerView recyclerView = rootView.findViewById(R.id.document_view);
        recyclerView.setAdapter(new DocumentsAdapter(ctx, getActivity(), Utils.LEARN_DOCUMENTS));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return rootView;
    }
}

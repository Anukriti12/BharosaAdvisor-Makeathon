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
import android.widget.CheckBox;

import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.adapters.CheckListAdapter;

import static com.ap.bharosaadvisor.helper.Utils.CHECKLIST_FEATURES;
import static com.ap.bharosaadvisor.helper.Utils.CHECKLIST_STATUS;

public class Checklist extends Fragment
{
    Context ctx;
    FragmentManager fm;

    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState)
    {

        final View rootView = inflater.inflate(R.layout.fragment_checklist, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();

        RecyclerView checklistView = rootView.findViewById(R.id.main_recycler);
        CheckListAdapter checkListAdapter = new CheckListAdapter(getActivity(), ctx, CHECKLIST_FEATURES);
        checklistView.setAdapter(checkListAdapter);
        checklistView.setHasFixedSize(true);
        checklistView.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false));

        return rootView;
    }
}

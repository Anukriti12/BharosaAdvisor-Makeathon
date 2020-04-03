package com.ap.bharosaadvisor.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ap.bharosaadvisor.R;

public class editValues extends Fragment
{
    Context ctx;
    FragmentManager fm;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_editvalues, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();

        return rootView;
    }
}

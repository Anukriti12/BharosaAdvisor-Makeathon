package com.ap.bharosaadvisor.fragments;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.TextView;

import com.ap.bharosaadvisor.MainActivity;
import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.adapters.RecentTransactionsAdapter;
import com.baoyz.widget.PullRefreshLayout;
import com.baoyz.widget.SmartisanDrawable;

import java.util.ArrayList;
import java.util.Collections;


import static com.ap.bharosaadvisor.helper.Utils.DATA_FOUND;
import static com.ap.bharosaadvisor.helper.Utils.DATA_LOADED;
import static com.ap.bharosaadvisor.helper.Utils.FIFTY_YEAR_LABEL;
import static com.ap.bharosaadvisor.helper.Utils.FIFTY_YEAR_VALUE;
import static com.ap.bharosaadvisor.helper.Utils.FetchMonitorData;
import static com.ap.bharosaadvisor.helper.Utils.GAIN_LOSS;
import static com.ap.bharosaadvisor.helper.Utils.MARKET_VALUE;
import static com.ap.bharosaadvisor.helper.Utils.MULTIPLE;
import static com.ap.bharosaadvisor.helper.Utils.NET_INVESTMENT;
import static com.ap.bharosaadvisor.helper.Utils.RECENT_TRANSACTIONS;
import static com.ap.bharosaadvisor.helper.Utils.TEN_YEAR_LABEL;
import static com.ap.bharosaadvisor.helper.Utils.TEN_YEAR_VALUE;
import static com.ap.bharosaadvisor.helper.Utils.TOTAL_INVESTMENT;
import static com.ap.bharosaadvisor.helper.Utils.TWENTY_YEAR_LABEL;
import static com.ap.bharosaadvisor.helper.Utils.TWENTY_YEAR_VALUE;
import static com.ap.bharosaadvisor.helper.Utils.WITHDRAWAL;

public class Monitor extends Fragment
{
    Context ctx;
    FragmentManager fm;
    View rootView;
    TextView monitorWealth10YearLabel;
    TextView monitorWealth20YearLabel;
    TextView monitorWealth50YearLabel;
    TextView actionInvested;
    TextView actionWithdrawn;
    TextView actionNetInvestment;
    TextView wealthTodayMarketValue;
    TextView wealthTodayWithdrawn;
    TextView wealthTodayMultiple;
    TextView monitorWealth10Year;
    TextView monitorWealth20Year;
    TextView monitorWealth50Year;

    RecyclerView recentTransactions;
    View dataNotFound;
    PullRefreshLayout dataFound;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_monitor, container, false);

        ctx = rootView.getContext();
        fm = getFragmentManager();

        dataFound = rootView.findViewById(R.id.monitor_data_found);
        dataNotFound = rootView.findViewById(R.id.monitor_data_not_found);
        dataFound.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                FetchMonitorData(getActivity(), Monitor.this, dataFound);
            }
        });
        dataFound.setColorSchemeColors(Color.GRAY);
        dataFound.setRefreshDrawable(new SmartisanDrawable(ctx, dataFound));

        actionInvested = rootView.findViewById(R.id.monitor_action_invested);
        actionWithdrawn = rootView.findViewById(R.id.monitor_action_withdrawn);
        actionNetInvestment = rootView.findViewById(R.id.monitor_action_new_investment);
        wealthTodayMarketValue = rootView.findViewById(R.id.monitor_wealth_today_value);
        wealthTodayWithdrawn = rootView.findViewById(R.id.monitor_wealth_today_gain_loss);
        wealthTodayMultiple = rootView.findViewById(R.id.monitor_wealth_today_multiple);
        monitorWealth10YearLabel = rootView.findViewById(R.id.monitor_wealth_counter_10_label);
        monitorWealth20YearLabel = rootView.findViewById(R.id.monitor_wealth_counter_20_label);
        monitorWealth50YearLabel = rootView.findViewById(R.id.monitor_wealth_counter_50_label);
        monitorWealth10Year = rootView.findViewById(R.id.monitor_wealth_counter_10);
         monitorWealth20Year = rootView.findViewById(R.id.monitor_wealth_counter_20);
         monitorWealth50Year = rootView.findViewById(R.id.monitor_wealth_counter_50);
        recentTransactions = rootView.findViewById(R.id.monitor_recent_transactions);
        recentTransactions.setLayoutManager(new LinearLayoutManager(ctx));

        if (DATA_LOADED)
            UpdateData();

        return rootView;
    }

    public void UpdateData()
    {
        if (DATA_FOUND)
        {
            dataFound.setVisibility(View.VISIBLE);
            actionInvested.setText(TOTAL_INVESTMENT);
            actionWithdrawn.setText(WITHDRAWAL);
            actionNetInvestment.setText(NET_INVESTMENT);
            wealthTodayMarketValue.setText(MARKET_VALUE);
            wealthTodayWithdrawn.setText(GAIN_LOSS);
            wealthTodayMultiple.setText(MULTIPLE);

            if (RECENT_TRANSACTIONS.size() > 0)
                recentTransactions.setAdapter(new RecentTransactionsAdapter(ctx, RECENT_TRANSACTIONS));
            else
                recentTransactions.setAdapter(new RecentTransactionsAdapter(ctx,
                        new ArrayList<>(Collections.singletonList(getString(R.string.no_recent_transactions)))));

            if (MainActivity.TOUR_DONE)
                AnimateWealth();
        } else
            dataNotFound.setVisibility(View.VISIBLE);
    }

    public void AnimateWealth()
    {
        monitorWealth10YearLabel.setText(localizedLabel(TEN_YEAR_LABEL));
        monitorWealth20YearLabel.setText(localizedLabel(TWENTY_YEAR_LABEL));
        monitorWealth50YearLabel.setText(localizedLabel(FIFTY_YEAR_LABEL));

        monitorWealth10Year.setText(String.valueOf(TEN_YEAR_VALUE));
        monitorWealth20Year.setText(String.valueOf(TWENTY_YEAR_VALUE));
        monitorWealth50Year.setText(String.valueOf(FIFTY_YEAR_VALUE));
    }

    private String localizedLabel(String val)
    {
        switch (val)
        {
            case "lakhs":
                return ctx.getResources().getString(R.string.lacs);
            case "crores":
                return ctx.getResources().getString(R.string.crores);
        }

        return "";
    }
}

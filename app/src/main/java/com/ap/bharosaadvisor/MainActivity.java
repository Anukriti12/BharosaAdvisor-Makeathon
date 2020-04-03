package com.ap.bharosaadvisor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.bharosaadvisor.adapters.AddTransactionsAdapter;
import com.ap.bharosaadvisor.adapters.WithdrawTransactionsAdapter;
import com.ap.bharosaadvisor.data.Transaction;
import com.ap.bharosaadvisor.data.LearnDocument;
import com.ap.bharosaadvisor.data.LearnVideo;
import com.ap.bharosaadvisor.fragments.Checklist;
import com.ap.bharosaadvisor.fragments.Learn;
import com.ap.bharosaadvisor.fragments.Monitor;
import com.ap.bharosaadvisor.helper.LocaleHelper;
import com.ap.bharosaadvisor.helper.RequestHelper;
import com.ap.bharosaadvisor.helper.Utils;
import com.takusemba.spotlight.OnSpotlightStateChangedListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.shape.Circle;
import com.takusemba.spotlight.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, TextWatcher {
    public static boolean TOUR_DONE = false;

    final int SPONSOR_AD_TIME = 10 * 1000;

    Dialog error;
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    MaterialDialog dialogCredit;
    MaterialDialog dialogAdd;
    MaterialDialog dialogWithdraw;
    MaterialDialog dialogEstimate;
    MaterialDialog dialogPrivacy;
    InputMethodManager inputMethodManager;
    ActionBarDrawerToggle actionBarDrawerToggle;
    SharedPreferences sharedPreferences;
    EditText estimateLumpsum;
    EditText estimateMonthlyInvestment;
    EditText estimateMonthlyWithdrawn;
    View estimatedWealth;
    Button estimateButton;
    Button estimateCancel;
    ScrollView estimateSheet;
    ScrollView privacyScroll;
    TextView estimatedWealth10YearLabel;
    TextView estimatedWealth20YearLabel;
    TextView estimatedWealth50YearLabel;
    TextView estimatedWealth10YearInfo;
    TextView estimatedWealth20YearInfo;
    TextView estimatedWealth50YearInfo;
    EditText addLumpsum;
    TextView addLumpsumLabel;
    EditText addMonthlyInvestment;
    TextView addMonthlyInvestmentLabel;
    Button addButton;
    Button addCancel;
    TextView withdrawLumpsumInvestmentLabel;
    EditText withdrawLumpsumInvestment;
    TextView withdrawMonthlyInvestLabel;
    EditText withdrawMonthlyInvest;
    View withdrawPanel;
    Button withdrawButton;
    Button withdrawCancel;
    RadioGroup withdrawRadioGroup;
    RadioButton withdrawSpecific;
    RadioButton withdrawAll;
    Monitor monitorFragment;
    RecyclerView addTransactions;
    RecyclerView withdrawTransactions;
    boolean doubleBackToExitPressedOnce = false;
    boolean addLumpsumVerify = false;
    boolean addMonthlyVerify = false;
    boolean withdrawLumpsumVerify = false;
    boolean withdrawMonthlyVerify = false;
    private ProgressDialog transactionAPIDialog;
    CheckBox privacyCheck;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, Utils.language));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView navUsername = navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        TextView navUserSrc = navigationView.getHeaderView(0).findViewById(R.id.nav_user_src);
        navUsername.setText(Utils.USERNAME);
        navUserSrc.setText(Utils.USER_SRC);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        sharedPreferences = getSharedPreferences(Utils.PREFERENCE_LABEL, Context.MODE_PRIVATE);

        dialogCredit = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_credit, false)
                .build();
        dialogAdd = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_add, false)
                .build();
        dialogWithdraw = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_withdraw, false)
                .build();
        dialogEstimate = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_estimate, false)
                .build();
        error= new MaterialDialog.Builder(this)
                .title("Estimation Not Possible")
                .content("Your withdrawal amount is too high")
                .positiveText(R.string.okay)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        error.dismiss();
                    }
                })
                .canceledOnTouchOutside(false)
                .build();
//        dialogPrivacy = new MaterialDialog.Builder(this)
//                .customView(R.layout.dialog_privacy, true)
//                .build();
//         dialogPrivacy.setCanceledOnTouchOutside(false);

        assert dialogAdd.getCustomView() != null;
        addLumpsum = dialogAdd.getCustomView().findViewById(R.id.add_lumpsum_investment);
        addLumpsumLabel = dialogAdd.getCustomView().findViewById(R.id.add_lumpsum_investment_label);
        addMonthlyInvestment = dialogAdd.getCustomView().findViewById(R.id.add_monthly_investment);
        addMonthlyInvestmentLabel = dialogAdd.getCustomView().findViewById(R.id.add_monthly_investment_label);
        addButton = dialogAdd.getCustomView().findViewById(R.id.add_button);
        addCancel = dialogAdd.getCustomView().findViewById(R.id.add_cancel);
        addTransactions = dialogAdd.getCustomView().findViewById(R.id.add_transactions);

        assert dialogWithdraw.getCustomView() != null;
        withdrawLumpsumInvestmentLabel = dialogWithdraw.getCustomView().findViewById(R.id.withdraw_lumpsum_investment_label);
        withdrawLumpsumInvestment = dialogWithdraw.getCustomView().findViewById(R.id.withdraw_lumpsum_investment);
        withdrawMonthlyInvestLabel = dialogWithdraw.getCustomView().findViewById(R.id.withdraw_monthly_investment_label);
        withdrawMonthlyInvest = dialogWithdraw.getCustomView().findViewById(R.id.withdraw_monthly_investment);
        withdrawRadioGroup = dialogWithdraw.getCustomView().findViewById(R.id.withdraw_radio_group);
        withdrawSpecific = dialogWithdraw.getCustomView().findViewById(R.id.withdraw_specific);
        withdrawAll = dialogWithdraw.getCustomView().findViewById(R.id.withdraw_all_units);
        withdrawTransactions = dialogWithdraw.getCustomView().findViewById(R.id.withdraw_transactions);
        withdrawPanel = dialogWithdraw.getCustomView().findViewById(R.id.withdraw_panel);
        withdrawButton = dialogWithdraw.getCustomView().findViewById(R.id.withdraw_button);
        withdrawCancel = dialogWithdraw.getCustomView().findViewById(R.id.withdraw_cancel);
        assert dialogEstimate.getCustomView() != null;
        estimateLumpsum = dialogEstimate.getCustomView().findViewById(R.id.estimate_lumpsum_investment);
        estimateMonthlyInvestment = dialogEstimate.getCustomView().findViewById(R.id.estimate_monthly_investment);
        estimateMonthlyWithdrawn = dialogEstimate.getCustomView().findViewById(R.id.estimate_monthly_withdrawal);
        estimatedWealth = dialogEstimate.getCustomView().findViewById(R.id.estimated_wealth);
        estimatedWealth10YearLabel = dialogEstimate.getCustomView().findViewById(R.id.estimate_wealth_label_10);
        estimatedWealth20YearLabel = dialogEstimate.getCustomView().findViewById(R.id.estimate_wealth_label_20);
        estimatedWealth50YearLabel = dialogEstimate.getCustomView().findViewById(R.id.estimate_wealth_label_50);
        estimatedWealth10YearInfo = dialogEstimate.getCustomView().findViewById(R.id.estimate_wealth_info_10);
        estimatedWealth20YearInfo = dialogEstimate.getCustomView().findViewById(R.id.estimate_wealth_info_20);
        estimatedWealth50YearInfo = dialogEstimate.getCustomView().findViewById(R.id.estimate_wealth_info_50);
        estimateButton = dialogEstimate.getCustomView().findViewById(R.id.estimate_button);
        estimateCancel = dialogEstimate.getCustomView().findViewById(R.id.estimate_cancel);
        estimateSheet = dialogEstimate.getCustomView().findViewById(R.id.estimate_sheet);
//        assert dialogPrivacy.getCustomView() != null;
//        privacyScroll = dialogPrivacy.getCustomView().findViewById(R.id.privacy_scroll);
//        privacyCheck = dialogPrivacy.getCustomView().findViewById(R.id.privacy_check);

        if (savedInstanceState == null) {
            monitorFragment = new Monitor();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, monitorFragment)
                    .commit();
            navigationView.setCheckedItem(R.id.nav_monitor);
        }

        findViewById(R.id.main_estimate).setOnClickListener(this);
        addLumpsum.addTextChangedListener(this);
        addMonthlyInvestment.addTextChangedListener(this);
        addButton.setOnClickListener(this);
        addCancel.setOnClickListener(this);
        withdrawSpecific.setOnClickListener(this);
        withdrawAll.setOnClickListener(this);
        withdrawLumpsumInvestment.addTextChangedListener(this);
        withdrawMonthlyInvest.addTextChangedListener(this);

        withdrawButton.setOnClickListener(this);
        withdrawCancel.setOnClickListener(this);
        estimateLumpsum.addTextChangedListener(this);
        estimateMonthlyInvestment.addTextChangedListener(this);
        estimateMonthlyWithdrawn.addTextChangedListener(this);
        estimateButton.setOnClickListener(this);
        estimateCancel.setOnClickListener(this);

        transactionAPIDialog = new ProgressDialog(this);
        transactionAPIDialog.setMessage("Connecting...");
        transactionAPIDialog.setCanceledOnTouchOutside(false);

        //TODO - FETCH NOTIFICATIONS
//        notificationsRecyclerView = findViewById(R.id.notification_recycler_view);
//        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        notificationsRecyclerView.setHasFixedSize(true);
//        NOTIFICATIONS.clear();
//        NOTIFICATIONS.add(new Notification("Your monthly due date approaching", R.drawable.ic_notification));
//        NOTIFICATIONS.add(new Notification("XYZ sent you a request to join your group", R.drawable.ic_notification));
//        NOTIFICATIONS.add(new Notification("Monthly amount deducted from your account", R.drawable.ic_notification));
//        NOTIFICATIONS.add(new Notification("You changed your password", R.drawable.ic_notification));
//        NOTIFICATIONS.add(new Notification("Your wallet balance is low", R.drawable.ic_notification));
//        NOTIFICATIONS.add(new Notification("Your payEezz date is about to end", R.drawable.ic_notification));
//        NOTIFICATIONS.add(new Notification("Your sponsor uploaded a new advertisement, have a look ", R.drawable.ic_notification));
//        NOTIFICATIONS.add(new Notification("You can watch a new video uploaded in the learn section", R.drawable.ic_notification));
//        NOTIFICATIONS.add(new Notification("You can read a new book uploaded in the learn section", R.drawable.ic_notification));
//        NOTIFICATIONS.add(new Notification("Your last transaction request failed due to your less PayEezz limit", R.drawable.ic_notification));
//        NOTIFICATIONS.add(new Notification("Your request for redemption has been approved", R.drawable.ic_notification));
//        NOTIFICATIONS.add(new Notification("Your request for redemption has been denied", R.drawable.ic_notification));
//        adapter = new NotificationsAdapter(NOTIFICATIONS);
//        notificationsRecyclerView.setAdapter(adapter);

        Utils.FetchMonitorData(this, monitorFragment);
        FetchChecklistInfo();
        FetchLoginTable();
        TOUR_DONE = sharedPreferences.getBoolean("tutorialShown", false);

        if (!TOUR_DONE)
            showFirstTimeLoginTutorial();
        else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //TODO - IMPLEMENT SPONSOR ADS
//                    Intent intent = new Intent(MainActivity.this, SponsorActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
                }
            };
            new Handler().postDelayed(runnable, SPONSOR_AD_TIME);
        }
    }


       void FetchLoginTable() {
        try {
            RequestHelper request = new RequestHelper(this,
                    Utils.SERVICE_URL + "apiwealthsimple/checkwealthuser",
                    false,
                    false);

            request.execute(new JSONObject().put("userId", Utils.USER_ID));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void FetchChecklistInfo() {
        try {
            RequestHelper request = new RequestHelper(this,
                    Utils.SERVICE_URL + "apiwealthsimple/wealthchecklist",
                    false,
                    false);

            request.setSharedPreferenceCaching("api_wealthchecklist");
            request.execute(new JSONObject());

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                @Override
                public void onFetched(JSONObject result) {
                    try {
                        String statusMessage = result.getString("status");

                        if (!statusMessage.equals("error")) {
                            JSONArray items = result.getJSONArray("data");
                            Utils.CHECKLIST_FEATURES.clear();
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject jsonObject = items.getJSONObject(i);
                                Utils.CHECKLIST_FEATURES.add(jsonObject.getString("description"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            RequestHelper request = new RequestHelper(this,
                    Utils.SERVICE_URL + "apiwealthsimple/activecheckliststatus",
                    false,
                    false);

            request.execute(new JSONObject().put("userId", Utils.USER_ID));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                @Override
                public void onFetched(JSONObject result) {
                    try {
                        if (result.getString("status").equals("Y")) {
                            sharedPreferences.edit()
                                    .putBoolean("checklistStatus", true)
                                    .apply();
                            Utils.CHECKLIST_STATUS = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.main_estimate): {
                showEstimateAmountDialog();
                break;
            }
            case (R.id.add_button): {
                int addLumpsumAmount = 0;
                int addMonthlyAmount = 0;

                try {
                    addLumpsumAmount = Integer.parseInt(addLumpsum.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                try {
                    addMonthlyAmount = Integer.parseInt(addMonthlyInvestment.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (addLumpsumAmount % 100 != 0 || addMonthlyAmount % 100 != 0) {
                    Toast.makeText(MainActivity.this,
                            R.string.message_amount_multiple,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (addLumpsumAmount == 0 && addMonthlyAmount == 0) {
                    Toast.makeText(MainActivity.this,
                            R.string.message_amount_invalid,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                final int finalAddLumpsumAmount = addLumpsumAmount;
                final int finalAddMonthlyAmount = addMonthlyAmount;

                if (Utils.NEW_TRANS) {
                    if ((finalAddLumpsumAmount < 500 && finalAddLumpsumAmount != 0) ||
                            (finalAddMonthlyAmount < 500 && finalAddMonthlyAmount != 0)) {
                        Toast.makeText(MainActivity.this,
                                R.string.message_amount_more,
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                new MaterialDialog.Builder(this)
                        .title(R.string.confirm)
                        .content(R.string.sure_add)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (addLumpsumVerify || addMonthlyVerify) {
                                    try {
                                        RequestHelper request = new RequestHelper(MainActivity.this,
                                                Utils.SERVICE_URL + "apiwealthsimple/addmoney",
                                                true,
                                                true);

                                        request.execute(new JSONObject()
                                                .put("userId", Utils.USER_ID)
                                                .put("lumpsum_aomunt", (finalAddLumpsumAmount == 0) ? "" : finalAddLumpsumAmount)
                                                .put("monthly_amount", (finalAddMonthlyAmount == 0) ? "" : finalAddMonthlyAmount));

                                        request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                                            @Override
                                            public void onFetched(JSONObject result) throws JSONException {
                                                if (result.getString("status").equals("success")) {
                                                    dialogAdd.dismiss();
                                                    Utils.FetchMonitorData(MainActivity.this, monitorFragment);
                                                    new MaterialDialog.Builder(MainActivity.this)
                                                            .title(R.string.message)
                                                            .content(result.getString("message"))
                                                            .positiveText(R.string.mdtp_done_label)
                                                            .show();
                                                }
                                            }
                                        });
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                        .show();

                break;
            }
            case (R.id.add_cancel): {
                dialogAdd.hide();
                break;
            }
            case (R.id.withdraw_button): {
                if (withdrawRadioGroup.getCheckedRadioButtonId() == R.id.withdraw_all_units) {
                    String msg = getString(R.string.allunits_delete);

                    new MaterialDialog.Builder(MainActivity.this)
                            .title(R.string.confirm)
                            .content(Html.fromHtml(msg + "<br>" + "<br>" + "Penalty Free Date: " + "</br>" + "</br>" + "<b>" + Utils.PENALTY_FREE_DATE + "</b>" +
                                    "<br>" + "STCG Free Date: " + "</br>" + "<b>" + Utils.STCG_FREE_DATE + "</b>"))
                            .positiveText(R.string.yes)
                            .negativeText(R.string.no)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    for (Transaction transaction : Utils.WITHDRAW_TRANSACTIONS) {
                                        try {
                                            RequestHelper request = new RequestHelper(MainActivity.this,
                                                    Utils.SERVICE_URL + "apiwealthsimple/deletetransactiondata",
                                                    false,
                                                    true);

                                            request.execute(new JSONObject().put("Id", transaction.id));
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    sendWithdrawalRequest(Utils.ALL_UNITS_AMOUNT, 0, true);
                                }
                            })
                            .show();
                } else {
                    int withdrawLumpsumAmount = 0;
                    int withdrawMonthlyAmount = 0;

                    try {
                        withdrawLumpsumAmount = Integer.parseInt(withdrawLumpsumInvestment.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    try {
                        withdrawMonthlyAmount = Integer.parseInt(withdrawMonthlyInvest.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    if (withdrawLumpsumAmount % 100 != 0 || withdrawMonthlyAmount % 100 != 0) {
                        Toast.makeText(MainActivity.this,
                                R.string.message_amount_multiple,
                                Toast.LENGTH_LONG).show();
                        return;
                    }


                    if (withdrawLumpsumAmount == 0 && withdrawMonthlyAmount == 0) {
                        Toast.makeText(MainActivity.this,
                                R.string.message_amount_invalid,
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    int totalHoldings = withdrawLumpsumAmount + withdrawMonthlyAmount;
                    int balance = Utils.ALL_UNITS_AMOUNT;
                    for (Transaction transaction : Utils.WITHDRAW_TRANSACTIONS)
                        balance -= Integer.parseInt(transaction.data);

                    String msg = getString(R.string.allunits_delete);
                    String msgDesc;
                    boolean reduced = false;

                    if (balance <= 0) {
                        Toast.makeText(MainActivity.this,
                                R.string.insufficient_withdraw,
                                Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        if (totalHoldings < balance) {
                            msgDesc = msg
                                    + "<br>" + "<br>" + "Penalty Free Date: " + "</br>" + "</br>" + "<b>" + Utils.PENALTY_FREE_DATE + "</b>" +
                                    "<br>" + "STCG Free Date: " + "</br>" + "<b>" + Utils.STCG_FREE_DATE + "</b>";
                        } else {
                            String change;
                            reduced = true;

                            if (withdrawLumpsumAmount != 0 && withdrawMonthlyAmount != 0) {
                                withdrawLumpsumAmount = Utils.ALL_UNITS_AMOUNT;
                                withdrawMonthlyAmount = 0;
                                change = "Lumpsum amount: " + withdrawLumpsumAmount +
                                        " Monthly amount: " + withdrawMonthlyAmount;
                            } else {
                                if (withdrawLumpsumAmount > withdrawMonthlyAmount) {
                                    withdrawLumpsumAmount = balance;
                                    change = "Lumpsum amount reduced to " + withdrawLumpsumAmount;
                                } else {
                                    withdrawMonthlyAmount = balance;
                                    change = "Monthly amount reduced to " + withdrawMonthlyAmount;
                                }
                            }

                            msgDesc = "Since withdrawal amount exceeds the holding, all the units will be sold - " + change +
                                    "<br>" + "<br>" + "Penalty Free Date: " + "</br>" + "</br>" + "<b>" + Utils.PENALTY_FREE_DATE + "</b>" +
                                    "<br>" + "STCG Free Date: " + "</br>" + "<b>" + Utils.STCG_FREE_DATE + "</b>";
                        }
                    }

                    final int finalWithdrawLumpsumAmount = withdrawLumpsumAmount;
                    final int finalWithdrawMonthlyAmount = withdrawMonthlyAmount;
                    final boolean finalReduced = reduced;

                    new MaterialDialog.Builder(this)
                            .title(R.string.confirm)
                            .content(Html.fromHtml(msgDesc))
                            .positiveText(R.string.yes)
                            .negativeText(R.string.no)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    sendWithdrawalRequest(finalWithdrawLumpsumAmount, finalWithdrawMonthlyAmount, finalReduced);
                                }
                            })
                            .show();
                }
                break;
            }
            case (R.id.withdraw_all_units): {
                int balance = Utils.ALL_UNITS_AMOUNT;
                for (Transaction transaction : Utils.WITHDRAW_TRANSACTIONS)
                    balance -= Integer.parseInt(transaction.data);
                if (balance <= 0) {
                    Toast.makeText(MainActivity.this,
                            R.string.insufficient_withdraw,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                withdrawPanel.setVisibility(View.GONE);
                withdrawButton.setVisibility(View.VISIBLE);
                withdrawButton.setEnabled(true);
                withdrawButton.setAlpha(1.0f);
                break;
            }
            case (R.id.withdraw_specific): {
                int balance = Utils.ALL_UNITS_AMOUNT;
                for (Transaction transaction : Utils.WITHDRAW_TRANSACTIONS)
                    balance -= Integer.parseInt(transaction.data);
                if (balance <= 0) {
                    Toast.makeText(MainActivity.this,
                            R.string.insufficient_withdraw,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                withdrawPanel.setVisibility(View.VISIBLE);

                if (!withdrawLumpsumVerify && !withdrawMonthlyVerify)
                    withdrawButton.setVisibility(View.GONE);
                else
                    withdrawButton.setVisibility(View.VISIBLE);

                if (withdrawLumpsumInvestment.getText().length() > 0 || withdrawMonthlyInvest.getText().length() > 0) {
                    withdrawButton.setEnabled(true);
                    withdrawButton.setAlpha(1.0f);
                } else {
                    withdrawButton.setEnabled(false);
                    withdrawButton.setAlpha(0.5f);
                }
                break;
            }
            case (R.id.withdraw_cancel): {
                dialogWithdraw.hide();
                break;
            }
            case (R.id.estimate_button): {
                assert dialogEstimate.getCustomView() != null;
                inputMethodManager.hideSoftInputFromWindow(
                        dialogEstimate.getCustomView().getWindowToken(), 0);

                BigInteger estimateLumpsumAmount = BigInteger.ZERO;
                try {
                    estimateLumpsumAmount = new BigInteger(estimateLumpsum.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                BigInteger estimateMonthlyAmount = BigInteger.ZERO;
                try {
                    estimateMonthlyAmount = new BigInteger(estimateMonthlyInvestment.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                BigInteger estimateWithdrawnAmount = BigInteger.ZERO;
                try {
                    estimateWithdrawnAmount = new BigInteger(estimateMonthlyWithdrawn.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                int compare= estimateWithdrawnAmount.compareTo(estimateMonthlyAmount);
                if(compare==1) {

                    error.show();

                }
                Utils.FetchEstimateData(this, estimateLumpsumAmount, estimateMonthlyAmount,
                        estimateWithdrawnAmount, estimatedWealth, estimatedWealth10YearLabel,
                        estimatedWealth20YearLabel, estimatedWealth50YearLabel, estimatedWealth10YearInfo,
                        estimatedWealth20YearInfo, estimatedWealth50YearInfo, estimateSheet);

                break;
            }
            case (R.id.estimate_cancel): {
                dialogEstimate.dismiss();
                break;
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_add: {
                if (!(Utils.CHECKLIST_STATUS)) {
                    navigationView.setCheckedItem(R.id.nav_checklist);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, new Checklist())
                            .commit();
                    Toast.makeText(MainActivity.this,
                            R.string.message_accept_checklist,
                            Toast.LENGTH_LONG)
                            .show();
                    break;
                }

                callTransactionAPIs(true);
                break;
            }
            case R.id.main_withdraw: {
                if (Utils.NEW_TRANS) {
                    Toast.makeText(this,
                            R.string.withdraw_new_user,
                            Toast.LENGTH_LONG)
                            .show();
                    break;
                }

                callTransactionAPIs(false);
                break;
            }
//            case R.id.main_notification:
//            {
//                if (drawerLayout.isDrawerOpen(GravityCompat.END))
//                    drawerLayout.closeDrawer(GravityCompat.END);
//                else
//                    drawerLayout.openDrawer(GravityCompat.END);
//                break;
//            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            //TODO - INCLUDE IMPLEMENTED FUNCTIONALITY
            case R.id.nav_monitor: {
                navigationView.setCheckedItem(R.id.nav_monitor);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new Monitor())
                        .commit();
                break;
            }
            case R.id.nav_checklist: {
                navigationView.setCheckedItem(R.id.nav_checklist);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new Checklist())
                        .commit();
                break;
            }
//            case R.id.nav_edit_values:
//            {
//                navigationView.setCheckedItem(R.id.nav_edit_values);
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.main_container, new editValues())
//                        .commit();
//                break;
//            }
            case R.id.nav_learn: {
                navigationView.setCheckedItem(R.id.nav_learn);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new Learn())
                        .commit();
                break;
            }
            case R.id.nav_add: {
                if (!(Utils.CHECKLIST_STATUS)) {
                    navigationView.setCheckedItem(R.id.nav_checklist);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, new Checklist())
                            .commit();
                    Toast.makeText(MainActivity.this,
                            R.string.message_accept_checklist,
                            Toast.LENGTH_LONG)
                            .show();
                    break;
                }

                callTransactionAPIs(true);
                break;
            }
            case R.id.nav_withdraw: {
                if (Utils.NEW_TRANS) {
                    Toast.makeText(this,
                            R.string.withdraw_new_user,
                            Toast.LENGTH_LONG)
                            .show();
                    break;
                }

                callTransactionAPIs(false);
                break;
            }
            case R.id.nav_estimate: {
                showEstimateAmountDialog();
                break;
            }
//            case R.id.nav_language:
//            {
//                Intent intent = new Intent(MainActivity.this, LanguageActivity.class);
//                intent.putExtra("calledFromMainNavigation", true);
//                startActivity(intent);
//                break;
//            }
            case R.id.nav_credits: {
                drawerLayout.closeDrawer(GravityCompat.START);
                dialogCredit.show();
                break;
            }
            case R.id.nav_sign_out: {
                Utils.ClearUtilData();
                String prevLang = sharedPreferences.getString("language", null);
                sharedPreferences.edit()
                        .clear()
                        .putString("language", prevLang)
                        .apply();

                Intent intent = new Intent(MainActivity.this,
                        OnBoardingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            }
        }
        return false;
    }

    void showAddAmountDialog() {
        dialogAdd.show();

        addButton.setEnabled(false);
        addButton.setAlpha(0.5f);

        if (addLumpsumVerify)
            addLumpsum.requestFocus();
        else if (addMonthlyVerify)
            addMonthlyInvestment.requestFocus();

        addLumpsum.setText("");
        addMonthlyInvestment.setText("");
    }

    void getAddStatus() {
        try {
            RequestHelper request = new RequestHelper(this,
                    Utils.SERVICE_URL + "apiwealthsimple/addstatus",
                    false,
                    true);

            request.execute(new JSONObject().put("userId", Utils.USER_ID));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                @Override
                public void onFetched(JSONObject result) throws JSONException {
                    if (result.getString("status").equals("success")) {
                        JSONObject data = result.getJSONObject("data");

                        JSONObject lumpsum = data.getJSONObject("lumpsum");
                        if (lumpsum.getString("statuscode").equals("0")) {
                            addLumpsumLabel.setVisibility(View.VISIBLE);
                            addLumpsum.setVisibility(View.VISIBLE);
                            addLumpsumVerify = true;
                        } else {
                            addLumpsumLabel.setVisibility(View.GONE);
                            addLumpsum.setVisibility(View.GONE);
                            addLumpsumVerify = false;
                        }

                        JSONObject monthly = data.getJSONObject("monthly");
                        if (monthly.getString("statuscode").equals("0")) {
                            addMonthlyInvestmentLabel.setVisibility(View.VISIBLE);
                            addMonthlyInvestment.setVisibility(View.VISIBLE);
                            addMonthlyVerify = true;
                        } else {
                            addMonthlyInvestmentLabel.setVisibility(View.GONE);
                            addMonthlyInvestment.setVisibility(View.GONE);
                            addMonthlyVerify = false;
                        }

                        if (!addLumpsumVerify && !addMonthlyVerify)
                            addButton.setVisibility(View.GONE);
                        else
                            addButton.setVisibility(View.VISIBLE);

                        getAddTransactionData();
                    }
                }
            });

            request.setOnFailedListener(new RequestHelper.OnFailedListener() {
                @Override
                public void onFailed() {
                    transactionAPIDialog.dismiss();
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getAddTransactionData() {
        try {
            RequestHelper request = new RequestHelper(this,
                    Utils.SERVICE_URL + "apiwealthsimple/addtransactiondata",
                    false,
                    true);

            request.execute(new JSONObject().put("userId", Utils.USER_ID));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                @Override
                public void onFetched(JSONObject result) {
                    try {
                        String statusMessage = result.getString("status");
                        Utils.ADD_TRANSACTIONS.clear();
                        addTransactions.setVisibility(View.GONE);

                        if (!statusMessage.equals("error")) {
                            JSONArray addTransactionsArray = result.getJSONArray("data");
                            for (int i = 0; i < addTransactionsArray.length(); i++) {
                                JSONObject jsonObject = addTransactionsArray.getJSONObject(i);
                                String ID = jsonObject.getString("id");
                                String type = jsonObject.getString("investment_type");
                                String formattedAmount = jsonObject.getString("amount_money");
                                String amount = formattedAmount.replaceAll(",", "");

                                Utils.ADD_TRANSACTIONS.add(new Transaction(ID, type,
                                        i + 1 + ".  " + type + ": ", amount, formattedAmount));
                            }

                            if (Utils.ADD_TRANSACTIONS.size() > 0) {
                                addTransactions.setVisibility(View.VISIBLE);
                                addTransactions.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                addTransactions.setAdapter(new AddTransactionsAdapter(MainActivity.this, dialogAdd,
                                        MainActivity.this, Utils.ADD_TRANSACTIONS));
                            }
                        }

                        transactionAPIDialog.dismiss();
                        showAddAmountDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            request.setOnFailedListener(new RequestHelper.OnFailedListener() {
                @Override
                public void onFailed() {
                    transactionAPIDialog.dismiss();
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void callTransactionAPIs(final boolean callingAddAPI) {
        transactionAPIDialog.show();

        try {
            RequestHelper request = new RequestHelper(this,
                    Utils.SERVICE_URL + "apiwealthsimple/firsttransaction",
                    false,
                    true);

            request.execute(new JSONObject().put("userId", Utils.USER_ID));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                @Override
                public void onFetched(JSONObject result) throws JSONException {
                    Utils.NEW_TRANS = result.getString("Status").equals("success") &&
                            result.getString("first_transaction_flag").equals("0");

                    if (callingAddAPI)
                        getAddStatus();
                    else
                        getWithdrawTransactionStatus();
                }

            });

            request.setOnFailedListener(new RequestHelper.OnFailedListener() {
                @Override
                public void onFailed() {
                    transactionAPIDialog.dismiss();
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void showWithdrawAmountDialog() {
        dialogWithdraw.show();
        Utils.UpdateAllUnits(this, withdrawMonthlyInvestLabel, withdrawMonthlyInvest);

        withdrawButton.setEnabled(false);
        withdrawButton.setAlpha(0.5f);
        withdrawPanel.setVisibility(View.VISIBLE);
        withdrawRadioGroup.check(R.id.withdraw_specific);
        withdrawLumpsumInvestment.requestFocus();
        withdrawLumpsumInvestment.setText("");
        withdrawMonthlyInvest.setText("");
    }

    void getWithdrawTransactionStatus() {
        try {
            RequestHelper request = new RequestHelper(this,
                    Utils.SERVICE_URL + "apiwealthsimple/withdrawstatus",
                    false,
                    true);

            request.execute(new JSONObject().put("userId", Utils.USER_ID));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                @Override
                public void onFetched(JSONObject result) throws JSONException {
                    if (result.getString("status").equals("success")) {
                        JSONObject data = result.getJSONObject("data");

                        JSONObject lumpsum = data.getJSONObject("lumpsum");
                        if (lumpsum.getString("statuscode").equals("0")) {
                            withdrawLumpsumInvestmentLabel.setVisibility(View.VISIBLE);
                            withdrawLumpsumInvestment.setVisibility(View.VISIBLE);
                            withdrawLumpsumVerify = true;
                        } else {
                            withdrawLumpsumInvestmentLabel.setVisibility(View.GONE);
                            withdrawLumpsumInvestment.setVisibility(View.GONE);
                            withdrawLumpsumVerify = false;
                        }

                        JSONObject monthly = data.getJSONObject("monthly");
                        if (monthly.getString("statuscode").equals("0")) {
                            withdrawMonthlyInvestLabel.setVisibility(View.VISIBLE);
                            withdrawMonthlyInvest.setVisibility(View.VISIBLE);
                            withdrawMonthlyVerify = true;
                        } else {
                            withdrawMonthlyInvestLabel.setVisibility(View.GONE);
                            withdrawMonthlyInvest.setVisibility(View.GONE);
                            withdrawMonthlyVerify = false;
                        }

                        if (!withdrawLumpsumVerify && !withdrawMonthlyVerify)
                            withdrawButton.setVisibility(View.GONE);
                        else
                            withdrawButton.setVisibility(View.VISIBLE);

                        getWithdrawTransactionData();
                    }
                }
            });
            request.setOnFailedListener(new RequestHelper.OnFailedListener() {
                @Override
                public void onFailed() {
                    transactionAPIDialog.dismiss();
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void sendWithdrawalRequest(int withdrawLumpsumAmount, int withdrawMonthlyAmount, boolean setBalanced) {
        try {
            RequestHelper request = new RequestHelper(MainActivity.this,
                    Utils.SERVICE_URL + "apiwealthsimple/withdrawalmoney",
                    true,
                    true);

            request.execute(new JSONObject()
                    .put("userId", Utils.USER_ID)
                    .put("lumpsum_aomunt", (withdrawLumpsumAmount == 0) ? "" : withdrawLumpsumAmount)
                    .put("monthly_amount", (withdrawMonthlyAmount == 0) ? "" : withdrawMonthlyAmount)
                    .put("allunits", (setBalanced) ? Utils.BALANCE_UNITS : ""));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                @Override
                public void onFetched(JSONObject result) throws JSONException {
                    if (result.getString("status").equals("success")) {
                        dialogWithdraw.dismiss();
                        Utils.FetchMonitorData(MainActivity.this, monitorFragment);
                        new MaterialDialog.Builder(MainActivity.this)
                                .title(R.string.message)
                                .content(result.getString("message"))
                                .positiveText(R.string.mdtp_done_label)
                                .show();
                    }
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getWithdrawTransactionData() {
        try {
            RequestHelper request = new RequestHelper(this,
                    Utils.SERVICE_URL + "apiwealthsimple/withdrawtransactiondata",
                    false,
                    true);

            request.execute(new JSONObject().put("userId", Utils.USER_ID));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                @Override
                public void onFetched(JSONObject result) {
                    try {
                        String statusMessage = result.getString("status");
                        Utils.WITHDRAW_TRANSACTIONS.clear();
                        withdrawTransactions.setVisibility(View.GONE);

                        if (!statusMessage.equals("error")) {

                            JSONArray withdrawTransactionsArray = result.getJSONArray("data");
                            for (int i = 0; i < withdrawTransactionsArray.length(); i++) {
                                JSONObject jsonObject = withdrawTransactionsArray.getJSONObject(i);
                                String ID = jsonObject.getString("id");
                                String type = jsonObject.getString("investment_type");
                                String formattedAmount = jsonObject.getString("amount_money");
                                String amount = formattedAmount.replaceAll(",", "");

                                Utils.WITHDRAW_TRANSACTIONS.add(new Transaction(ID, type,
                                        i + 1 + ".  " + type + ": ", amount, formattedAmount));
                            }

                            if (Utils.WITHDRAW_TRANSACTIONS.size() > 0) {
                                withdrawTransactions.setVisibility(View.VISIBLE);
                                withdrawTransactions.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                withdrawTransactions.setAdapter(new WithdrawTransactionsAdapter(MainActivity.this, dialogWithdraw,
                                        MainActivity.this, Utils.WITHDRAW_TRANSACTIONS));
                            }
                        }

                        transactionAPIDialog.dismiss();
                        showWithdrawAmountDialog();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            request.setOnFailedListener(new RequestHelper.OnFailedListener() {
                @Override
                public void onFailed() {
                    transactionAPIDialog.dismiss();
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void showEstimateAmountDialog() {
        dialogEstimate.show();

        estimateButton.setEnabled(false);
        estimateButton.setAlpha(0.5f);
        estimateLumpsum.requestFocus();
        estimateLumpsum.setText("");
        estimateMonthlyInvestment.setText("");
        estimateMonthlyWithdrawn.setText("");
        estimatedWealth.setVisibility(View.GONE);
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (estimateLumpsum.getText().length() > 0 ||
                estimateMonthlyInvestment.getText().length() > 0 ||
                estimateMonthlyWithdrawn.getText().length() > 0) {
            estimateButton.setEnabled(true);
            estimateButton.setAlpha(1.0f);
        } else {
            estimateButton.setEnabled(false);
            estimateButton.setAlpha(0.5f);
        }

        if (addLumpsum.getText().length() > 0 || addMonthlyInvestment.getText().length() > 0) {
            addButton.setEnabled(true);
            addButton.setAlpha(1.0f);
        } else {
            addButton.setEnabled(false);
            addButton.setAlpha(0.5f);
        }

        if (withdrawLumpsumInvestment.getText().length() > 0 || withdrawMonthlyInvest.getText().length() > 0) {
            withdrawButton.setEnabled(true);
            withdrawButton.setAlpha(1.0f);
        } else {
            withdrawButton.setEnabled(false);
            withdrawButton.setAlpha(0.5f);
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else if (drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.closeDrawer(GravityCompat.END);
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.message_back, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
//}
void showFirstTimeLoginTutorial() {
//    dialogPrivacy.show();
//
//    privacyCheck.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            dialogPrivacy.hide();
//            showLoginTutorial();
//        }
//    });
    showLoginTutorial();
//    if(!privacyCheck.isChecked())
//    {
//        new MaterialDialog.Builder(this)
//                .title(R.string.confirm)
//                .content("Kindly agree to our Privacy Policy first")
//                .positiveText(R.string.okay)
//                .negativeText(R.string.cancel)
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        dialogPrivacy.show();
//                    }
//                })
//                .show();
//    }

 }

    public void showLoginTutorial() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        SimpleTarget targetAddWithdraw = new SimpleTarget.Builder(this)
                .setPoint(displayMetrics.widthPixels - 150f, 100f)
                .setShape(new Circle(300f))
                .setTitle(getString(R.string.spotlight_title1))
                .setDescription(getString(R.string.spotlight_description1))
                .build();

        SimpleTarget targetEstimate = new SimpleTarget.Builder(this)
                .setPoint(100f, displayMetrics.heightPixels - 100f)
                .setShape(new Circle(100f))
                .setTitle(getString(R.string.spotlight_title2))
                .setDescription(getString(R.string.spotlight_description2))
                .build();

        Spotlight.with(this)
                .setOverlayColor(R.color.background)
                .setDuration(1000L)
                .setAnimation(new DecelerateInterpolator(2f))
                .setTargets(targetAddWithdraw, targetEstimate)
                .setClosedOnTouchedOutside(true)
                .setOnSpotlightStateListener(new OnSpotlightStateChangedListener() {
                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onEnded() {
                        TOUR_DONE = true;
                        sharedPreferences.edit().putBoolean("tutorialShown", true).apply();
                        if (monitorFragment != null && Utils.DATA_LOADED)
                            monitorFragment.UpdateData();
                    }
                })
                .start();
    }

}
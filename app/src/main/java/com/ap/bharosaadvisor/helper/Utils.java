package com.ap.bharosaadvisor.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ap.bharosaadvisor.MainActivity;
import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.data.Transaction;
import com.ap.bharosaadvisor.data.Notification;
import com.ap.bharosaadvisor.data.LearnDocument;
import com.ap.bharosaadvisor.data.LearnVideo;
import com.ap.bharosaadvisor.fragments.Monitor;
import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static android.view.View.*;

public class Utils
{
    public static final String PREFERENCE_LABEL = "BharosaPrefs";
    public static final String SERVICE_URL = "https://www.bharosaclub.com/member/";
    public static int USER_ID = -1;
    public static String USERNAME;
    public static String USER_SRC;
    public static Boolean DATA_LOADED = false;
    public static Boolean DATA_FOUND = false;
    public static String WITHDRAWAL;
    public static String TOTAL_INVESTMENT;
    public static String NET_INVESTMENT;
    public static String MARKET_VALUE;
    public static String GAIN_LOSS;
    public static String MULTIPLE;
    public static float TEN_YEAR_VALUE;
    public static String TEN_YEAR_LABEL;
    public static float TWENTY_YEAR_VALUE;
    public static String TWENTY_YEAR_LABEL;
    public static float FIFTY_YEAR_VALUE;
    public static String FIFTY_YEAR_LABEL;
    public static ArrayList<String> RECENT_TRANSACTIONS = new ArrayList<>();
    public static ArrayList<Notification> NOTIFICATIONS = new ArrayList<>();
    public static ArrayList<String> CHECKLIST_FEATURES = new ArrayList<>();
    public static ArrayList<LearnDocument> LEARN_DOCUMENTS = new ArrayList<>();
    public static ArrayList<LearnVideo> LEARN_VIDEOS = new ArrayList<>();
    public static ArrayList<Transaction> ADD_TRANSACTIONS = new ArrayList<>();
    public static ArrayList<Transaction> WITHDRAW_TRANSACTIONS = new ArrayList<>();
    public static int ALL_UNITS_AMOUNT = 0;
    public static int BALANCE_UNITS = 0;
    public static String PENALTY_FREE_DATE = "";
    public static String STCG_FREE_DATE = "";
    public static Boolean CHECKLIST_STATUS = false;
    public static Boolean NEW_TRANS = false;
    public static String language;
    public static String DEVICE_ID = "web";
    public static String DEVICE_TYPE = "AND " + Build.MANUFACTURER + " " + Build.MODEL + " " +
            Build.VERSION.RELEASE + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();

    public static void ClearUtilData()
    {
        USER_ID = -1;
        DATA_LOADED = false;
        DATA_FOUND = false;
        RECENT_TRANSACTIONS.clear();
        NOTIFICATIONS.clear();
    }

    private static String bytesToHexString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes)
        {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1)
                sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String sha256(String s)
    {
        MessageDigest digest;
        String hash;

        try
        {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(s.getBytes());

            hash = bytesToHexString(digest.digest());

            return hash;
        } catch (NoSuchAlgorithmException e1)
        {
            return s;
        }
    }

    public static String getBase64(String val)
    {
        if (val == null)
            return "";

        String result = new String(Base64.encode(val.getBytes(), Base64.DEFAULT));
        result = result.substring(0, result.length() - 1);
        return result;
    }

    public static void FetchMonitorData(Activity activity, final Monitor monitorFragment)
    {
        FetchMonitorData(activity, monitorFragment, null);
    }

    public static void FetchMonitorData(Activity activity, final Monitor monitorFragment, final PullRefreshLayout refresher)
    {
        try
        {
            RequestHelper request = new RequestHelper(activity,
                    SERVICE_URL + "apiwealthsimple/monitordata",
                    refresher == null,
                    refresher == null);

            request.setSharedPreferenceCaching("api_monitordata");
            request.execute(new JSONObject().put("userId", USER_ID));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener()
            {
                @Override
                public void onFetched(JSONObject result)
                {
                    try
                    {
                        String statusMessage = result.getString("status");

                        if (!statusMessage.equals("error"))
                        {
                            DATA_FOUND = true;
                            WITHDRAWAL = result.getString("withdrawal");
                            TOTAL_INVESTMENT = result.getString("totalinvestamount");
                            NET_INVESTMENT = result.getString("investamount");
                            MARKET_VALUE = result.getString("CurrentValue");
                            GAIN_LOSS = result.getString("gainloss");
                            MULTIPLE = result.getString("multiple");

                            WITHDRAWAL = (WITHDRAWAL.equals("")) ? "" : WITHDRAWAL + " ₹";
                            TOTAL_INVESTMENT = (TOTAL_INVESTMENT.equals("")) ? "" : TOTAL_INVESTMENT + " ₹";
                            NET_INVESTMENT = (NET_INVESTMENT.equals("")) ? "" : NET_INVESTMENT + " ₹";
                            MARKET_VALUE = (MARKET_VALUE.equals("")) ? "" : MARKET_VALUE + " ₹";
                            GAIN_LOSS = (GAIN_LOSS.equals("")) ? "" : GAIN_LOSS + " ₹";
                            MULTIPLE = (MULTIPLE.equals("")) ? "" : MULTIPLE;

                            String[] tenYearValues = result.getString("tenyearvalue").split(" ");
                            String[] twentyYearValues = result.getString("twentyyearvalue").split(" ");
                            String[] fiftyYearValues = result.getString("fiftyyearvalue").split(" ");

                            TEN_YEAR_VALUE = Float.parseFloat(tenYearValues[0]);
                            if (tenYearValues.length >= 2)
                                TEN_YEAR_LABEL = tenYearValues[1];
                            else
                                TEN_YEAR_LABEL = "Lacs";

                            TWENTY_YEAR_VALUE = Float.parseFloat(twentyYearValues[0]);
                            if (twentyYearValues.length >= 2)
                                TWENTY_YEAR_LABEL = twentyYearValues[1];
                            else
                                TWENTY_YEAR_LABEL = "Lacs";
                            FIFTY_YEAR_VALUE = Float.parseFloat(fiftyYearValues[0]);
                            if (fiftyYearValues.length >= 2)
                                FIFTY_YEAR_LABEL = fiftyYearValues[1];
                            else
                                FIFTY_YEAR_LABEL = "Lacs";

                            RECENT_TRANSACTIONS.clear();
                            JSONArray recentTransactions = result.getJSONArray("data");
                            for (int i = 0; i < recentTransactions.length(); i++)
                            {
                                JSONObject jsonObject = recentTransactions.getJSONObject(i);
                                String infoDate = jsonObject.getString("transaction_date");
                                String infoType = jsonObject.getString("transaction_type");
                                String infoAmount = jsonObject.getString("total_amount");
                                String infoStatus = jsonObject.getString("transaction_status");
                                RECENT_TRANSACTIONS.add(infoDate + " - " + infoType + ": " + infoAmount + " [" + infoStatus + "]");
                            }
                        } else
                            DATA_FOUND = false;

                        DATA_LOADED = true;
                        if (monitorFragment != null)
                            monitorFragment.UpdateData();

                        if (refresher != null)
                            refresher.setRefreshing(false);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static void  FetchLearnContent(boolean doCaching, Activity activity)
    {
        try
        {
            RequestHelper request = new RequestHelper(activity,
                    Utils.SERVICE_URL + "apiwealthsimple/learn",
                    false,
                    false);

            if (doCaching)
                request.setSharedPreferenceCaching("api_learn");
            request.execute(new JSONObject());

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener()
            {
                @Override
                public void onFetched(JSONObject result) throws JSONException
                {
                    JSONObject pdf = result.getJSONObject("pdf");
                    if (pdf.getString("status").equals("success"))
                    {
                        JSONArray pdfDataArray = pdf.getJSONArray("data");
                        Utils.LEARN_DOCUMENTS.clear();
                        for (int i = 0; i < pdfDataArray.length(); i++)
                        {
                            JSONObject pdfData = pdfDataArray.getJSONObject(i);
                            Utils.LEARN_DOCUMENTS.add(new LearnDocument(pdfData.getString("url_link"),
                                    pdfData.getString("title"),
                                    pdfData.getString("thumbnail_image_path")));
                        }
                    }

                    JSONObject video = result.getJSONObject("vedio");
                    if (video.getString("status").equals("success"))
                    {
                        JSONArray videoDataArray = video.getJSONArray("data");
                        Utils.LEARN_VIDEOS.clear();
                        for (int i = 0; i < videoDataArray.length(); i++)
                        {
                            JSONObject videoData = videoDataArray.getJSONObject(i);
                            Utils.LEARN_VIDEOS.add(new LearnVideo(videoData.getString("url_link"),
                                    videoData.getString("title")));
                        }
                    }
                }
            });
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
    public static void UpdateAllUnits(Activity activity, final TextView withdrawMonthlyInvestLabel, final EditText withdrawMonthlyInvest)
    {
        try
        {
            RequestHelper request = new RequestHelper(activity,
                    Utils.SERVICE_URL + "apiwealthsimple/allunits",
                    false,
                    false);

            request.setSharedPreferenceCaching("api_allunits");
            request.execute(new JSONObject().put("userId", Utils.USER_ID)
                    .put("lumpsum_amount", "")
                    .put("monthly_amount", ""));
            request.setOnFetchedListener(new RequestHelper.OnFetchedListener()
            {
                @Override
                public void onFetched(JSONObject result) throws JSONException
                {
                    ALL_UNITS_AMOUNT = result.getJSONArray("total_marketvalue").getInt(0);
                    JSONObject data = result.getJSONObject("data");
                    PENALTY_FREE_DATE = data.getString("Penalty_free_date");
                    STCG_FREE_DATE = data.getString("STCG_Free_date");
                    BALANCE_UNITS = result.getJSONArray("BalanceUnits").getInt(0);
                }
            });

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static void FetchEstimateData(final Activity activity,
                                         BigInteger estimateLumpsumAmount, BigInteger estimateMonthlyAmount,
                                         BigInteger estimateWithdrawnAmount, final View estimatedWealth,
                                         final TextView estimatedWealth10YearLabel,
                                         final TextView estimatedWealth20YearLabel, final TextView estimatedWealth50YearLabel,
                                         final TextView estimatedWealth10YearInfo,
                                         final TextView estimatedWealth20YearInfo, final TextView estimatedWealth50YearInfo,
                                         final ScrollView scrollView)
    {
        if (estimateLumpsumAmount.equals(BigInteger.ZERO) &&
                estimateMonthlyAmount.equals(BigInteger.ZERO) &&
                estimateWithdrawnAmount.equals(BigInteger.ZERO))
        {
            Toast.makeText(activity,
                    R.string.message_amount_invalid,
                    Toast.LENGTH_LONG).show();
            return;
        }

        try
        {
            RequestHelper request = new RequestHelper(activity,
                    SERVICE_URL + "apiwealthsimple/estimate",
                    true,
                    true);

            request.execute(new JSONObject()
                    .put("lumpsum_invest_amt", estimateLumpsumAmount.toString())
                    .put("mothly_invest_amt", estimateMonthlyAmount.toString())
                    .put("mothly_withdrawal_amt", estimateWithdrawnAmount.toString()));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener()
            {
                @SuppressLint("SetTextI18n")
                @Override
                public void onFetched(JSONObject result) throws JSONException
                {
                    if (result.getString("status").equals("success"))
                    {
                        scrollView.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });

                        estimatedWealth.setVisibility(VISIBLE);

                        String[] data = {
                                result.getString("ten_yr_text"),
                                result.getString("ten_yr_total_invest_amount"),
                                result.getString("ten_yr_x_growth"),
                                result.getString("ten_yr_fd_two_x_growth"),
                                result.getString("twenty_yr_text"),
                                result.getString("twenty_yr_total_invest_amount"),
                                result.getString("twenty_yr_x_growth"),
                                result.getString("twenty_yr_fd_two_x_growth"),
                                result.getString("fifty_yr_text"),
                                result.getString("fifty_yr_total_invest_amount"),
                                result.getString("fifty_yr_x_growth"),
                                result.getString("fifty_yr_fd_two_x_growth")
                        };

                        estimatedWealth10YearLabel.setText("In 10 years");
                        estimatedWealth10YearInfo.setText(Html.fromHtml("Net amount invested: " +"<b>"+ data[1] + "</b>"+
                                "<br>" +
                                "WealthSimple-TM (3X growth): " +"</br>"+ "<b>"+data[2] + "</b>" + "<br>"+
                                "FDs (2X growth): " + "</br>"+"<b>"+data[3] + "</b>"+" <br>"+"</br>"));
                        estimatedWealth20YearLabel.setText("In 20 years");
                        estimatedWealth20YearInfo.setText(Html.fromHtml("Net amount invested: " +"<b>"+ data[5] + "</b>"+
                                "<br>" +
                                "WealthSimple-TM (3X growth): " +"</br>"+ "<b>"+data[6] + "</b>" + "<br>"+
                                "FDs (2X growth): " + "</br>"+"<b>"+data[7] + "</b>"+" <br>"+"</br>"));
                        estimatedWealth50YearLabel.setText("In 50 years");
                        estimatedWealth50YearInfo.setText(Html.fromHtml("Net amount invested: " +"<b>"+ data[9] + "</b>"+
                                "<br>" +
                                "WealthSimple-TM (3X growth): " +"</br>"+ "<b>"+data[10] + "</b>" + "<br>"+
                                "FDs (2X growth): " + "</br>"+"<b>"+data[11] + "</b>"+" <br>"+"</br>"));
                    }
                }
            });

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
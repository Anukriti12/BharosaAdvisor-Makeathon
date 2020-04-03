package com.ap.bharosaadvisor.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.bharosaadvisor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static com.ap.bharosaadvisor.helper.Utils.PREFERENCE_LABEL;

public class RequestHelper extends AsyncTask<JSONObject, Void, JSONObject>
{
    private URL url;
    private OnFetchedListener listener;
    private OnFailedListener listener2;
    private boolean showProgressDialog;
    private boolean showBadNetworkConnectionDialog;
    private ProgressDialog dialog;
    private MaterialDialog.Builder errorDialog;
    private String authToken;
    private boolean doSharedPrefCaching;
    private String sharedPrefsKey;
    private SharedPreferences sharedPreferences;

    public interface OnFetchedListener
    {
        void onFetched(JSONObject result) throws JSONException;
    }

    public interface OnFailedListener
    {
        void onFailed();
    }

    public RequestHelper(Activity _activity, String _url,
                         boolean _showProgressDialog, boolean _showBadNetworkConnectionDialog)
            throws MalformedURLException
    {
        url = new URL(_url);
        showProgressDialog = _showProgressDialog;
        showBadNetworkConnectionDialog = _showBadNetworkConnectionDialog;
        sharedPreferences = _activity.getSharedPreferences(PREFERENCE_LABEL, Context.MODE_PRIVATE);

        if (showBadNetworkConnectionDialog)
        {
            errorDialog = new MaterialDialog.Builder(_activity)
                    .title(R.string.unable_to_connect)
                    .content(R.string.unable_to_connect_message)
                    .positiveText(R.string.okay);
        }

        if (showProgressDialog)
        {
            dialog = new ProgressDialog(_activity);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public RequestHelper(Activity _activity, String _url,
                         boolean _showProgressDialog, boolean _showBadNetworkConnectionDialog,
                         String _authToken)
            throws MalformedURLException
    {
        authToken = _authToken;
        url = new URL(_url);
        showProgressDialog = _showProgressDialog;
        showBadNetworkConnectionDialog = _showBadNetworkConnectionDialog;
        sharedPreferences = _activity.getSharedPreferences(PREFERENCE_LABEL, Context.MODE_PRIVATE);

        if (showBadNetworkConnectionDialog)
        {
            errorDialog = new MaterialDialog.Builder(_activity)
                    .title(R.string.unable_to_connect)
                    .content(R.string.unable_to_connect_message)
                    .positiveText(R.string.okay);
        }

        if (showProgressDialog)
            dialog = new ProgressDialog(_activity);
    }

    public void setSharedPreferenceCaching(String _key)
    {
        doSharedPrefCaching = true;
        sharedPrefsKey = _key;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        if (showProgressDialog)
        {
            dialog.setMessage("Connecting...");
            dialog.show();
        }
    }

    @Override
    protected JSONObject doInBackground(JSONObject... args)
    {
        try
        {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("auth-token", authToken);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//            System.out.println("<<< (" + url + ") " + args[0].toString());
            os.writeBytes(args[0].toString());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder("");
                String line;

                if ((line = in.readLine()) != null)
                    sb.append(line);

                in.close();
                return new JSONObject(sb.toString());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result)
    {
        if (showProgressDialog && dialog.isShowing())
            dialog.dismiss();

        if (result == null && showBadNetworkConnectionDialog)
            errorDialog.show();

        if (doSharedPrefCaching)
        {
            if (result != null)
            {
                String resString = result.toString();
                resString = resString.replaceAll("NA", "0");
                try
                {
                    result = new JSONObject(resString);
                } catch (JSONException e)
                {
                    errorDialog.show();
                }

                sharedPreferences.edit()
                        .putString(sharedPrefsKey, resString)
                        .apply();
            } else
            {
                try
                {
                    String resultString = sharedPreferences.getString(sharedPrefsKey, null);
                    if (resultString == null)
                        result = null;
                    else
                        result = new JSONObject(resultString);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            if (result != null)
            {
                String resString = result.toString();
                resString = resString.replaceAll("NA", "0");
                try
                {
                    result = new JSONObject(resString);
                } catch (JSONException e)
                {
                    errorDialog.show();
                }
            }
        }

        if (listener != null && result != null)
        {
            try
            {
//                System.out.println(">>> (" + url + ") " + result.toString());
                listener.onFetched(result);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        if (listener2 != null && result == null)
            listener2.onFailed();
    }

    public void setOnFetchedListener(OnFetchedListener _listener)
    {
        listener = _listener;
    }

    public void setOnFailedListener(OnFailedListener _listener)
    {
        listener2 = _listener;
    }
}

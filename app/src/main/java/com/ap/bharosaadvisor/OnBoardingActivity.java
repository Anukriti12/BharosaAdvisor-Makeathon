package com.ap.bharosaadvisor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.bharosaadvisor.helper.LocaleHelper;

import com.ap.bharosaadvisor.helper.RequestHelper;
import com.ap.bharosaadvisor.helper.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.Calendar;

public class OnBoardingActivity extends AppCompatActivity
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TextWatcher {
    final int AUTHENTICATION_REQUEST = 11;
    final int AUTOMATIC_AUTHENTICATION_REQUEST = 15;
    final int ACTIVITY_TIMEOUT = 4 * 60 * 1000;

    enum LoginStage {
        LOGIN_MOBILE, LOGIN_DATE_OF_BIRTH, LOGIN_PASSWORD
    }

    String dateOfBirth;
    LoginStage loginStage = LoginStage.LOGIN_MOBILE;
    TextView loginLabel;
    TextView mobileLabel;
    TextView passwordLabel;
    Button loginButton;
    EditText mobileInput;
    EditText passwordInput;
    //TextView loginEstimate;
    Button signupLink;
    Button forgotPasswordLink;
    Animation animFadeIn;
    Animation animFadeOut;
    Animation animSlideLeft;
    Animation animRotate;
    ImageView bharosalogo;
    SharedPreferences sharedPreferences;
    InputMethodManager inputMethodManager;
    MaterialDialog dialogEstimate;
    EditText estimateLumpsumInvestment;
    EditText estimateMonthlyInvestment;
    EditText estimateMonthlyWithdraw;
    View estimatedWealth;
    TextView estimatedWealth10YearLabel;
    TextView estimatedWealth20YearLabel;
    TextView estimatedWealth50YearLabel;
    TextView estimatedWealth10YearInfo;
    TextView estimatedWealth20YearInfo;
    TextView estimatedWealth50YearInfo;
    ScrollView estimateSheet;
    Button estimateButton;
    Button estimateCancel;
    Resources resources;
    String fieldUsername;
    String fieldDOB;
    String fieldPassword;
    String fieldDeviceID;
    String fieldDeviceType;
    Boolean automaticLoggedIn = false;
    Boolean automaticAuthenticated = false;
    MaterialDialog dialogForgotPassword;
    EditText forgotPassMobile;
    Button forgotPassButton;
    Button forgotPassCancel;
    View loginLayout;
    View bottomLayout;
    Dialog error;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        loginLayout = findViewById(R.id.on_boarding_layout_login);
        bottomLayout = findViewById(R.id.on_boarding_bottom_layout);
        sharedPreferences = getSharedPreferences(Utils.PREFERENCE_LABEL, Context.MODE_PRIVATE);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        new Handler().postDelayed(runnable, ACTIVITY_TIMEOUT);

        loginLabel = findViewById(R.id.on_boarding_login_label);
        mobileLabel = findViewById(R.id.on_boarding_login_mobile_label);
        passwordLabel = findViewById(R.id.on_boarding_login_password_label);
        loginButton = findViewById(R.id.on_boarding_login_button);
        mobileInput = findViewById(R.id.on_boarding_login_mobile_input);
        passwordInput = findViewById(R.id.on_boarding_login_password_input);
        // loginEstimate = findViewById(R.id.on_boarding_login_estimate);
        signupLink = findViewById(R.id.on_boarding_signup_link);
        forgotPasswordLink = findViewById(R.id.on_boarding_forgot_password_link);
        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        animSlideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_right_center);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        bharosalogo = findViewById(R.id.bharosalogo);
        forgotPasswordLink.setOnClickListener(this);

        rotateAnimation();

        dialogEstimate = new MaterialDialog
                .Builder(OnBoardingActivity.this)
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

        assert dialogEstimate.getCustomView() != null;
        estimateLumpsumInvestment = dialogEstimate.getCustomView().findViewById(R.id.estimate_lumpsum_investment);
        estimateMonthlyInvestment = dialogEstimate.getCustomView().findViewById(R.id.estimate_monthly_investment);
        estimateMonthlyWithdraw = dialogEstimate.getCustomView().findViewById(R.id.estimate_monthly_withdrawal);
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

        loginButton.setOnClickListener(this);
        estimateButton.setOnClickListener(this);
        estimateCancel.setOnClickListener(this);
        findViewById(R.id.on_boarding_login_estimate).setOnClickListener(this);
        findViewById(R.id.learn).setOnClickListener(this);
        estimateLumpsumInvestment.addTextChangedListener(this);
        estimateMonthlyInvestment.addTextChangedListener(this);
        estimateMonthlyWithdraw.addTextChangedListener(this);
        mobileInput.addTextChangedListener(this);
        passwordInput.addTextChangedListener(this);

        dialogForgotPassword = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_forgot_pass, false)
                .build();
        assert dialogForgotPassword.getCustomView() != null;
        forgotPassMobile = dialogForgotPassword.getCustomView().findViewById(R.id.forgot_mobile);
        forgotPassButton = dialogForgotPassword.getCustomView().findViewById(R.id.forgot_button);
        forgotPassCancel = dialogForgotPassword.getCustomView().findViewById(R.id.forgot_cancel);
        forgotPassMobile.addTextChangedListener(this);
        forgotPassButton.setOnClickListener(this);
        forgotPassCancel.setOnClickListener(this);

        Utils.FetchLearnContent(true, this);

        if (sharedPreferences.getString("lastUserName", null) != null) {
            loginLayout.setVisibility(View.INVISIBLE);
            bottomLayout.setVisibility(View.INVISIBLE);

            fieldUsername = sharedPreferences.getString("lastUserName", "");
            fieldDOB = sharedPreferences.getString("lastUserDateOfBirth", "");
            fieldPassword = sharedPreferences.getString("lastPassword", "");
            fieldDeviceID = sharedPreferences.getString("lastDeviceID", "");
            fieldDeviceType = sharedPreferences.getString("lastDeviceType", "");

            SendCredentialsVerificationRequest(false);
        } else {
            final View introPanel = findViewById(R.id.on_boarding_intro_panel);
            final View introThumb = findViewById(R.id.on_boarding_intro_money);
            final View introText = findViewById(R.id.on_boarding_intro_text);
            final View introButton = findViewById(R.id.on_boarding_intro_button);

            introPanel.setVisibility(View.VISIBLE);
            introPanel.startAnimation(animFadeIn);
            introThumb.startAnimation(animFadeIn);
            introText.startAnimation(animFadeIn);
            introButton.startAnimation(animFadeIn);
            introButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    introPanel.setVisibility(View.GONE);
                    introPanel.startAnimation(animFadeOut);
                    introThumb.startAnimation(animFadeOut);
                    introText.startAnimation(animFadeOut);
                    introButton.startAnimation(animFadeOut);
                }
            });
        }

        Utils.language = sharedPreferences.getString("language", null);

        if (Utils.language == null) {
            //TODO - Uncomment for multiple language support
            sharedPreferences.edit()
                    .putString("language", "en")
                    .apply();
            updateView(sharedPreferences.getString("language", "en"));
            Intent intent = new Intent(OnBoardingActivity.this, LanguageActivity.class);
            intent.putExtra("calledFromMainNavigation", false);
            startActivity(intent);
        } else updateView(sharedPreferences.getString("language", "en"));
    }

    private void rotateAnimation() {
        animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        bharosalogo.startAnimation(animRotate);
    }

    @Override
    protected void onResume() {
        updateView(sharedPreferences.getString("language", "en"));
        super.onResume();
    }

    private void updateView(String lang) {
        Context ctx = LocaleHelper.setLocale(this, lang);
        resources = ctx.getResources();

        loginLabel.setText(resources.getString(R.string.login));
        mobileLabel.setText(resources.getString(R.string.mobile_no));
        passwordLabel.setText(resources.getString(R.string.password));
        loginButton.setText(resources.getString(R.string.next));
        estimateButton.setText(resources.getString(R.string.next));
        // findViewById(R.id.on_boarding_login_estimate).setText(resources.getString(R.string.estimate));
        signupLink.setText(resources.getString(R.string.signup_now));
        forgotPasswordLink.setText(resources.getString(R.string.forgot_password));
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case (R.id.on_boarding_login_estimate): {
                dialogEstimate.show();

                estimateButton.setEnabled(false);
                estimateButton.setAlpha(0.5f);
                estimateLumpsumInvestment.setText("");
                estimateMonthlyInvestment.setText("");
                estimateMonthlyWithdraw.setText("");
                estimatedWealth.setVisibility(View.GONE);
                break;
            }
            case (R.id.learn): {
                if (Utils.LEARN_DOCUMENTS.size() == 0)
                {
                    Toast.makeText(this,
                            R.string.could_not_load_learn, Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                startActivity(new Intent(this, LearnActivity.class));
                break;
            }
            case (R.id.estimate_cancel): {
                dialogEstimate.dismiss();
                break;
            }
            case (R.id.estimate_button): {
                assert dialogEstimate.getCustomView() != null;
                inputMethodManager.hideSoftInputFromWindow(
                        dialogEstimate.getCustomView().getWindowToken(), 0);

                BigInteger estimateLumpsumAmount = BigInteger.ZERO;
                try {
                    estimateLumpsumAmount = new BigInteger(estimateLumpsumInvestment.getText().toString());
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
                    estimateWithdrawnAmount = new BigInteger(estimateMonthlyWithdraw.getText().toString());
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

            case (R.id.on_boarding_login_button): {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (loginStage == LoginStage.LOGIN_MOBILE) {
                    fieldUsername = Utils.getBase64(mobileInput.getText().toString());
                    SendMobileVerificationRequest(false);
                } else if (loginStage == LoginStage.LOGIN_PASSWORD) {
                    fieldUsername = Utils.getBase64(mobileInput.getText().toString());
                    fieldDOB = Utils.getBase64(dateOfBirth);
                    fieldPassword = Utils.getBase64(passwordInput.getText().toString());
                    fieldDeviceID = Utils.getBase64(Utils.DEVICE_ID);
                    fieldDeviceType = Utils.getBase64(Utils.DEVICE_TYPE);
                    SendCredentialsVerificationRequest(true);
                }
                break;
            }
            case (R.id.on_boarding_forgot_password_link): {
                forgotPassMobile.setText("");
                forgotPassButton.setEnabled(false);
                forgotPassButton.setAlpha(0.5f);
                dialogForgotPassword.show();
                break;
            }
            case (R.id.forgot_button): {
                SendMobileVerificationRequest(true);

                break;
            }
            case (R.id.forgot_cancel): {
                dialogForgotPassword.dismiss();
                break;
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (loginStage == LoginStage.LOGIN_DATE_OF_BIRTH) {

            if(monthOfYear<9 && dayOfMonth<10)
                dateOfBirth = "0"+dayOfMonth + "-0" + (monthOfYear+1) + "-" + year;
            else if(monthOfYear<9 && dayOfMonth>10)
                dateOfBirth = dayOfMonth + "-0" + (monthOfYear+1) + "-" + year ;
            else if(monthOfYear>9 && dayOfMonth<10)
                dateOfBirth = "0"+ dayOfMonth + "-" + monthOfYear + "-" + year ;
            else if(monthOfYear>9 && dayOfMonth>10)
                dateOfBirth = dayOfMonth + "-" + monthOfYear + "-" + year ;

            ShowPasswordField();
        } else
            SendForgotPasswordRequest(true);
    }

    void SendMobileVerificationRequest(final boolean isForgotPassword) {
        try {
            RequestHelper request = new RequestHelper(this,
                    Utils.SERVICE_URL + "apiwealthapp/checkuser",
                    true,
                    true);

            request.execute(new JSONObject()
                    .put("userName", (isForgotPassword) ? Utils.getBase64(forgotPassMobile.getText().toString()) : fieldUsername));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                @Override
                public void onFetched(JSONObject result) {
                    try {
                        int status = result.getInt("statusCode");

                        if (status == 0) {
                            int userCount = result.getInt("userCount");

                            if (userCount > 1) {
                                if (!isForgotPassword)
                                    loginStage = LoginStage.LOGIN_DATE_OF_BIRTH;

                                Calendar now = Calendar.getInstance();
                                DatePickerDialog dateOfBirthPicker = DatePickerDialog.newInstance(
                                        OnBoardingActivity.this,
                                        now.get(Calendar.YEAR),
                                        now.get(Calendar.MONTH),
                                        now.get(Calendar.DAY_OF_MONTH)
                                );

                                dateOfBirthPicker.showYearPickerFirst(true);
                                dateOfBirthPicker.setTitle(resources.getString(R.string.confirm_dob));
                                dateOfBirthPicker.show(getFragmentManager(), "DATE OF BIRTH PICKER");
                            } else {
                                if (isForgotPassword)
                                    SendForgotPasswordRequest(false);
                                else
                                    ShowPasswordField();
                            }
                        } else {
                            new MaterialDialog.Builder(OnBoardingActivity.this)
                                    .title(R.string.login_failed)
                                    .content(R.string.login_failed_message)
                                    .positiveText("Ok")
                                    .show();
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

    void SendForgotPasswordRequest(boolean dateOfBirthRequired) {
        try {
            String forgotUsername = Utils.getBase64(forgotPassMobile.getText().toString());
            String forgotDateOfBirth = (dateOfBirthRequired) ? "" : Utils.getBase64(dateOfBirth);
            String forgotDeviceId = Utils.getBase64(Utils.DEVICE_ID);
            String forgotDeviceType = Utils.getBase64(Utils.DEVICE_TYPE);

            RequestHelper request = new RequestHelper(this,
                    Utils.SERVICE_URL + "api/forgotapi",
                    true, true,
                    forgotUsername + forgotDeviceId);

            request.execute(new JSONObject()
                    .put("userName", forgotUsername)
                    .put("userDateOfBirth", forgotDateOfBirth)
                    .put("deviceId", forgotDeviceId)
                    .put("deviceType", forgotDeviceType));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                @Override
                public void onFetched(JSONObject result) throws JSONException {
                    if (result.getString("statusCode").equals("0")) {
                        dialogForgotPassword.dismiss();
                        Toast.makeText(OnBoardingActivity.this,
                                R.string.forgot_pass_message,
                                Toast.LENGTH_LONG)
                                .show();
                    } else {
                        dialogForgotPassword.dismiss();
                        Toast.makeText(OnBoardingActivity.this,
                                R.string.unable_to_connect_message,
                                Toast.LENGTH_LONG)
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

    void SendCredentialsVerificationRequest(final boolean isManual) {
        try {
            final RequestHelper request = new RequestHelper(this,
                    Utils.SERVICE_URL + "apiwealthapp/login",
                    true, true,
                    fieldUsername + fieldDeviceID);

            request.setSharedPreferenceCaching("api_login");
            request.execute(new JSONObject()
                    .put("userName", fieldUsername)
                    .put("userDateOfBirth", fieldDOB)
                    .put("password", fieldPassword)
                    .put("deviceId", fieldDeviceID)
                    .put("deviceType", fieldDeviceType));

            request.setOnFetchedListener(new RequestHelper.OnFetchedListener() {
                @Override
                public void onFetched(JSONObject result) {
                    try {
                        int status = result.getInt("statusCode");

                        if (status == 0) {
                            Utils.USER_ID = Integer.parseInt(result.getString("userid"));
                            Utils.USERNAME = result.getString("username");
                            Utils.USER_SRC = result.getString("mobilenumber");

                            if (isManual) {
                                Intent intent = EnterPinActivity.getIntent(OnBoardingActivity.this, true);
                                startActivityForResult(intent, AUTHENTICATION_REQUEST);
                            } else {
                                automaticLoggedIn = true;
                                if (automaticAuthenticated)
                                    LoggedInSecurely();
                            }
                        } else {
                            loginStage = LoginStage.LOGIN_MOBILE;
                            loginButton.setText(resources.getString(R.string.next));
                            mobileLabel.setVisibility(View.VISIBLE);
                            mobileInput.setVisibility(View.VISIBLE);
                            passwordLabel.setVisibility(View.GONE);
                            passwordInput.setVisibility(View.GONE);

                            if (!isManual) {
                                loginLayout.setVisibility(View.VISIBLE);
                                bottomLayout.setVisibility(View.VISIBLE);
                            }

                            new MaterialDialog.Builder(OnBoardingActivity.this)
                                    .title(R.string.login_failed)
                                    .content(R.string.login_failed_credential_message)
                                    .positiveText("Ok")
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            if (!isManual) {
                Intent intent = EnterPinActivity.getIntent(OnBoardingActivity.this, false);
                startActivityForResult(intent, AUTOMATIC_AUTHENTICATION_REQUEST);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void ShowPasswordField() {
        loginStage = LoginStage.LOGIN_PASSWORD;

        loginButton.setText(resources.getString(R.string.login));
        loginButton.setEnabled(false);
        loginButton.setAlpha(0.5f);

        mobileLabel.startAnimation(animFadeOut);
        mobileInput.startAnimation(animFadeOut);
        mobileLabel.setVisibility(View.GONE);
        mobileInput.setVisibility(View.GONE);

        passwordLabel.setVisibility(View.VISIBLE);
        passwordInput.setText("");
        passwordInput.setVisibility(View.VISIBLE);
        passwordInput.startAnimation(animSlideLeft);
        passwordLabel.startAnimation(animSlideLeft);
    }

    void LoggedInSecurely() {
        Intent intent = new Intent(OnBoardingActivity.this,
                MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AUTHENTICATION_REQUEST: {
                if (resultCode == EnterPinActivity.RESULT_OK) {
                    sharedPreferences.edit()
                            .putString("lastUserName", fieldUsername)
                            .putString("lastUserDateOfBirth", fieldDOB)
                            .putString("lastPassword", fieldPassword)
                            .putString("lastDeviceID", fieldDeviceID)
                            .putString("lastDeviceType", fieldDeviceType)
                            .apply();

                    LoggedInSecurely();
                } else
                    finish();

                break;
            }
            case AUTOMATIC_AUTHENTICATION_REQUEST: {
                if (resultCode == EnterPinActivity.RESULT_OK) {
                    automaticAuthenticated = true;
                    if (automaticLoggedIn)
                        LoggedInSecurely();
                }
                break;
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if ((loginStage == LoginStage.LOGIN_MOBILE && mobileInput.getText().length() == 10) ||
                (loginStage == LoginStage.LOGIN_PASSWORD && passwordInput.getText().length() > 0)) {
            loginButton.setEnabled(true);
            loginButton.setAlpha(1.0f);
        } else {
            loginButton.setEnabled(false);
            loginButton.setAlpha(0.5f);
        }

        if (estimateLumpsumInvestment.getText().length() > 0 ||
                estimateMonthlyInvestment.getText().length() > 0 ||
                estimateMonthlyWithdraw.getText().length() > 0) {
            estimateButton.setEnabled(true);
            estimateButton.setAlpha(1.0f);
        } else {
            estimateButton.setEnabled(false);
            estimateButton.setAlpha(0.5f);
        }

        if (forgotPassMobile.getText().length() == 10) {
            forgotPassButton.setEnabled(true);
            forgotPassButton.setAlpha(1.0f);
        } else {
            forgotPassButton.setEnabled(false);
            forgotPassButton.setAlpha(0.5f);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}

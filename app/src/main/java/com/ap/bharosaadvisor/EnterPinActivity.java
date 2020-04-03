package com.ap.bharosaadvisor;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ap.bharosaadvisor.helper.Animate;
import com.ap.bharosaadvisor.helper.FingerPrintListener;
import com.ap.bharosaadvisor.helper.FingerprintHandler;
import com.ap.bharosaadvisor.helper.Utils;
import com.ap.bharosaadvisor.pinlockview.IndicatorDots;
import com.ap.bharosaadvisor.pinlockview.PinLockListener;
import com.ap.bharosaadvisor.pinlockview.PinLockView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class EnterPinActivity extends AppCompatActivity
{
    public static final int RESULT_BACK_PRESSED = RESULT_FIRST_USER;
    public static final String EXTRA_SET_PIN = "set_pin";
    public static final String EXTRA_FONT_TEXT = "textFont";
    public static final String EXTRA_FONT_NUM = "numFont";

    private static final int PIN_LENGTH = 4;
    private static final String FINGER_PRINT_KEY = "FingerPrintKey";
    private static final String PREFERENCES = "AuthenticationPrefs";
    private static final String KEY_PIN = "pin";

    private PinLockView pinLockView;
    private TextView textTitle;
    private TextView textAttempts;
    private TextView textFingerText;
    private AppCompatImageView imageViewFingerView;
    private Cipher cipher;
    private KeyStore keyStore;
    private boolean setPin = false;
    private String firstPin = "";
    public static WeakReference<EnterPinActivity> instance;
    private AnimatedVectorDrawable showFingerprint;
    private AnimatedVectorDrawable fingerprintToTick;
    private AnimatedVectorDrawable fingerprintToCross;

    public static Intent getIntent(Context context, boolean setPin)
    {
        Intent intent = new Intent(context, EnterPinActivity.class);
        intent.putExtra(EXTRA_SET_PIN, setPin);
        return intent;
    }

    public static void updateActivity(EnterPinActivity activity)
    {
        instance = new WeakReference<>(activity);
    }

    public static Intent getIntent(Context context, String fontText, String fontNum)
    {
        Intent intent = new Intent(context, EnterPinActivity.class);
        intent.putExtra(EXTRA_FONT_TEXT, fontText);
        intent.putExtra(EXTRA_FONT_NUM, fontNum);
        return intent;
    }

    public static Intent getIntent(Context context, boolean setPin, String fontText, String fontNum)
    {
        Intent intent = getIntent(context, fontText, fontNum);
        intent.putExtra(EXTRA_SET_PIN, setPin);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_enterpin);
        updateActivity(this);

        textAttempts = findViewById(R.id.attempts);
        textTitle = findViewById(R.id.title);
        IndicatorDots mIndicatorDots;
        imageViewFingerView = findViewById(R.id.fingerView);
        textFingerText = findViewById(R.id.fingerText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            showFingerprint = (AnimatedVectorDrawable) getDrawable(R.drawable.show_fingerprint);
            fingerprintToTick = (AnimatedVectorDrawable) getDrawable(R.drawable.fingerprint_to_tick);
            fingerprintToCross = (AnimatedVectorDrawable) getDrawable(R.drawable.fingerprint_to_cross);
        }

        setPin = getIntent().getBooleanExtra(EXTRA_SET_PIN, false);

        if (setPin)
            changeLayoutForSetPin();
        else
        {
            String pin = getPinFromSharedPreferences();
            if (pin.equals(""))
            {
                changeLayoutForSetPin();
                setPin = true;
            } else
                checkForFingerPrint();
        }

        final PinLockListener pinLockListener = new PinLockListener()
        {
            @Override
            public void onComplete(String pin)
            {
                if (setPin)
                    setPin(pin);
                else
                    checkPin(pin);
            }

            @Override
            public void onEmpty()
            {
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin)
            {
            }
        };

        pinLockView = findViewById(R.id.pinlockView);
        mIndicatorDots = findViewById(R.id.indicator_dots);
        pinLockView.attachIndicatorDots(mIndicatorDots);
        pinLockView.setPinLockListener(pinLockListener);
        pinLockView.setPinLength(PIN_LENGTH);
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);
        checkForFont();
    }

    private void checkForFont()
    {
        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_FONT_TEXT))
        {
            String font = intent.getStringExtra(EXTRA_FONT_TEXT);
            setTextFont(font);
        }
        if (intent.hasExtra(EXTRA_FONT_NUM))
        {
            String font = intent.getStringExtra(EXTRA_FONT_NUM);
            setNumFont(font);
        }
    }

    private void setTextFont(String font)
    {
        try
        {
            Typeface typeface = Typeface.createFromAsset(getAssets(), font);

            textTitle.setTypeface(typeface);
            textAttempts.setTypeface(typeface);
            textFingerText.setTypeface(typeface);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setNumFont(String font)
    {
        try
        {
            Typeface typeface = Typeface.createFromAsset(getAssets(), font);
            pinLockView.setTypeFace(typeface);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException
    {
        try
        {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                mKeyGenerator.init(new
                        KeyGenParameterSpec.Builder(FINGER_PRINT_KEY,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            }

            mKeyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc)
        {
            throw new FingerprintException(exc);
        }
    }

    public void performPinSet()
    {
        finish();
    }

    //Create a new method that we’ll use to initialize our cipher//
    public boolean initCipher()
    {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                cipher = Cipher.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES + "/"
                                + KeyProperties.BLOCK_MODE_CBC + "/"
                                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e)
        {
            return false;
        }

        try
        {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(FINGER_PRINT_KEY,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    private void writePinToSharedPreferences(String pin)
    {
        SharedPreferences prefs = this.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PIN, Utils.sha256(pin)).apply();
    }

    private String getPinFromSharedPreferences()
    {
        SharedPreferences prefs = this.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PIN, "");
    }

    private void setPin(String pin)
    {
        if (firstPin.equals(""))
        {
            firstPin = pin;
            textTitle.setText(getString(R.string.pinlock_secondPin));
            pinLockView.resetPinLockView();
        } else
        {
            if (pin.equals(firstPin))
            {
                writePinToSharedPreferences(pin);
                setResult(RESULT_OK);
                finish();
            } else
            {
                shake();
                textTitle.setText(getString(R.string.pinlock_tryagain));
                pinLockView.resetPinLockView();
                firstPin = "";
            }
        }
    }

    private void checkPin(String pin)
    {
        if (Utils.sha256(pin).equals(getPinFromSharedPreferences()))
        {
            setResult(RESULT_OK);
            finish();
        } else
        {
            shake();
            textAttempts.setText(getString(R.string.pinlock_wrongpin));
            pinLockView.resetPinLockView();
        }
    }

    private void shake()
    {
        ObjectAnimator.ofFloat(pinLockView, "translationX",
                0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                .setDuration(1000)
                .start();
    }

    private void changeLayoutForSetPin()
    {
        imageViewFingerView.setVisibility(View.GONE);
        textFingerText.setVisibility(View.GONE);
        textAttempts.setVisibility(View.GONE);
        textTitle.setText(getString(R.string.pinlock_settitle));
    }

    private void checkForFingerPrint()
    {
        final FingerPrintListener fingerPrintListener = new FingerPrintListener()
        {
            @Override
            public void onSuccess()
            {
                setResult(RESULT_OK);
                Animate.animate(imageViewFingerView, fingerprintToTick);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        finish();
                    }
                }, 750);
            }

            @Override
            public void onFailed()
            {
                Animate.animate(imageViewFingerView, fingerprintToCross);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Animate.animate(imageViewFingerView, showFingerprint);
                    }
                }, 750);
            }

            @Override
            public void onError(CharSequence errorString)
            {
                Toast.makeText(EnterPinActivity.this, errorString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHelp(CharSequence helpString)
            {
                Toast.makeText(EnterPinActivity.this, helpString, Toast.LENGTH_SHORT).show();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            if (Objects.requireNonNull(fingerprintManager).isHardwareDetected())
            {
                KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                FingerprintManager mFingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    imageViewFingerView.setVisibility(View.GONE);
                    return;
                }

                assert mFingerprintManager != null;
                if (!mFingerprintManager.hasEnrolledFingerprints())
                {
                    imageViewFingerView.setVisibility(View.GONE);
                    return;
                }

                assert mKeyguardManager != null;
                if (!mKeyguardManager.isKeyguardSecure())
                {
                    imageViewFingerView.setVisibility(View.GONE);
                } else
                {
                    try
                    {
                        generateKey();
                        if (initCipher())
                        {
                            FingerprintManager.CryptoObject mCryptoObject = new FingerprintManager.CryptoObject(cipher);

                            FingerprintHandler helper = new FingerprintHandler(this);
                            helper.startAuth(mFingerprintManager, mCryptoObject);
                            helper.setFingerPrintListener(fingerPrintListener);
                        }
                    } catch (FingerprintException e)
                    {
                        e.printStackTrace();
                    }
                }
            } else
                imageViewFingerView.setVisibility(View.GONE);
        } else
            imageViewFingerView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_BACK_PRESSED);
        super.onBackPressed();
    }

    private class FingerprintException extends Exception
    {
        FingerprintException(Exception e)
        {
            super(e);
        }
    }
}
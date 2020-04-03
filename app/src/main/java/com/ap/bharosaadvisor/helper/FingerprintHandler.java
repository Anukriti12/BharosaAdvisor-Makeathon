package com.ap.bharosaadvisor.helper;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback
{
    // You should use the CancellationSignal method whenever your app can no longer process user input, for example when your app goes
    // into the background. If you don’t use this method, then other apps will be unable to access the touch sensor, including the lockscreen!//

    private Context context;
    private FingerPrintListener fingerPrintListener;

    public FingerprintHandler(Context mContext)
    {
        context = mContext;
    }

    //Implement the startAuth method, which is responsible for starting the fingerprint authentication process//
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject)
    {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)
            return;

        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    public void setFingerPrintListener(FingerPrintListener mfingerPrintListener)
    {
        this.fingerPrintListener = mfingerPrintListener;
    }

    @Override
    //onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//
    public void onAuthenticationError(int errMsgId, CharSequence errString)
    {
        fingerPrintListener.onError(errString);
    }

    @Override
    //onAuthenticationFailed is called when the fingerprint doesn’t match with any of the fingerprints registered on the device//
    public void onAuthenticationFailed()
    {
        fingerPrintListener.onFailed();
    }

    @Override
    //onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
    //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString)
    {
        fingerPrintListener.onHelp(helpString);
    }

    @Override
    //onAuthenticationSucceeded is called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
    {
        fingerPrintListener.onSuccess();
    }
}

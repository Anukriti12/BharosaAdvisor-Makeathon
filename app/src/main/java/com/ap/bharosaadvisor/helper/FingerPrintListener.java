package com.ap.bharosaadvisor.helper;

/**
 * Created by Arcane on 7/11/2017.
 */

public interface FingerPrintListener
{

    void onSuccess();

    void onFailed();

    void onError(CharSequence errorString);

    void onHelp(CharSequence helpString);

}


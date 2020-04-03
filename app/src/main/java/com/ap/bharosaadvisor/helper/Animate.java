package com.ap.bharosaadvisor.helper;

import android.annotation.TargetApi;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;

public class Animate
{
    @TargetApi(Build.VERSION_CODES.M)
    public static void animate(AppCompatImageView view, AnimatedVectorDrawable scanFingerprint)
    {
        view.setImageDrawable(scanFingerprint);
        scanFingerprint.start();
    }
}

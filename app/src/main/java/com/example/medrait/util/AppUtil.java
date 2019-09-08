package com.example.medrait.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;

import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.App;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.example.medrait.R;
import com.google.android.material.snackbar.Snackbar;


import java.util.List;


public final class AppUtil {

    private static final String MIME_TYPE_SHARE = "text/plain";
    private static final String MIME_TYPE_MAIL = "plain/text";

    private AppUtil() {

    }

    public static boolean isConnected() {
        final ConnectivityManager cm = (ConnectivityManager)App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();

                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            } else {
                final Network n = cm.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }

        return false;
    }






    public static void bindImage(String url, ImageView target, boolean centerCrop) {
        Drawable drawable = ContextCompat.getDrawable(target.getContext(), R.drawable.ic_image_24dp);
        DrawableRequestBuilder<String> builder = Glide.with(App.getContext())

                .load(url)
                .error(R.drawable.ic_broken_image_24dp)
                .placeholder(drawable)
                .crossFade();
        if (centerCrop) builder.centerCrop();
        builder.into(target);
    }

    public static Snackbar createSnackbar(Activity activity, @StringRes int resId) {
        View root = activity.findViewById(android.R.id.content);
        return Snackbar.make(root, resId, Snackbar.LENGTH_LONG);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void setVectorBg(View target, @DrawableRes int drRes, @ColorRes int normalRes,
                                   @ColorRes int pressedRes) {
        int[][] states = new int[][]{
                new int[]{
                        android.R.attr.state_pressed},
                new int[]{

                }
        };
        int[] colors = new int[]{
                ContextCompat.getColor(target.getContext(), pressedRes),
                ContextCompat.getColor(target.getContext(), normalRes)
        };
        ColorStateList cl = new ColorStateList(states, colors);

        // if you pass application as context is throw exception: Resources$NotFoundException:
        // File res/drawable/ic_close_24dp.xml from drawable resource ID #0x7f02005c
        Drawable drawable = ContextCompat.getDrawable(target.getContext(), drRes);
        Drawable wrapped = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrapped, cl);
        target.setBackground(wrapped);
    }

    public static Intent createShareIntent(String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND)
                .setType(MIME_TYPE_SHARE)
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, text);
        return Intent.createChooser(intent, App.getContext().getString(R.string.chooserTitle));
    }

    public static Intent createMailIntent(String mail, String subject) {
        Intent intent = new Intent(Intent.ACTION_SEND)
                .setType(MIME_TYPE_MAIL)
                .putExtra(Intent.EXTRA_EMAIL, new String[]{mail})
                .putExtra(Intent.EXTRA_SUBJECT, subject);
        return Intent.createChooser(intent, App.getContext().getString(R.string.chooserTitle));
    }

    public static boolean isNullOrEmpty(List list) {
        return list == null || list.isEmpty();
    }

}
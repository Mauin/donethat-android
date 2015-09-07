package com.mtramin.donethat.view;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.adapters.ListenerUtil;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.mtramin.donethat.R;
import com.mtramin.donethat.util.ViewUtil;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

/**
 * TODO: JAVADOC
 */
public class DataBindingDeclarations {

    @BindingAdapter("bind:imageUri")
    public static void loadImage(ImageView view, Uri uri) {
        Glide.with(view.getContext())
                .load(uri)
                .asBitmap()
                .placeholder(R.color.primary)
                .into(view);
    }

    @BindingAdapter("bind:imageUri")
    public static void loadImage(ImageView view, String uri) {
        Glide.with(view.getContext())
                .load(uri)
                .asBitmap()
                .placeholder(R.color.primary)
                .into(view);
    }

    @BindingAdapter("bind:date")
    public static void setDate(TextView view, DateTime date) {
        view.setText(DateUtils.formatDateTime(view.getContext(), date, DateUtils.FORMAT_SHOW_DATE));
    }

    @BindingAdapter("bind:locationText")
    public static void loactionText(TextView view, LatLng location) {
        // TODO geocoder
        view.setText(location.toString());
    }

    @BindingAdapter("bind:statusBarPaddingTop")
    public static void statusBarPaddingTop(View view, boolean shouldApplyPadding) {
        int padding = 0;

        if (shouldApplyPadding) {
            padding = ViewUtil.getStatusBarHeight(view.getContext());
        }

        view.setPadding(view.getPaddingLeft(), padding, view.getPaddingRight(), view.getPaddingBottom());
    }
}

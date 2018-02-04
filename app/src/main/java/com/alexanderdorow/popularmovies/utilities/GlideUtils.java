package com.alexanderdorow.popularmovies.utilities;

import android.content.Context;
import android.widget.ImageView;

import com.alexanderdorow.popularmovies.R;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class GlideUtils {

    public static void showFadedImage(Context context, String url, ImageView imageView) {
        RequestOptions into = new RequestOptions()
                .fitCenter();
        Glide.with(context)
                .load(url)
                .apply(into)
                .transition(GenericTransitionOptions.with(R.anim.fade_in_animation))
                .into(imageView);
    }

}

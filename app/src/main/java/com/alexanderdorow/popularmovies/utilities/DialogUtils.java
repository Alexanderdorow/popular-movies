package com.alexanderdorow.popularmovies.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.alexanderdorow.popularmovies.R;

public class DialogUtils {

    public static void showNetworkError(int message, int title, Context context,
                                        DialogInterface.OnClickListener onClickListener,
                                        DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.try_again, onClickListener);

        if (cancelListener != null) {
            builder.setOnCancelListener(cancelListener);
        }

        builder.show();
    }

}

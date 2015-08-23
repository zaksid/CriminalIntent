package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.ImageView;

/**
 * Created by alexander on 8/24/15.
 * <p/>
 * Provides some image handling
 */
public class PictureUtils {

    /**
     * Get a BitmapDrawable from a local file that is scaled down
     * to fit the current Window size.
     *
     * @param activity app's activity
     * @param path     Path to image file
     * @return Scaled to screen sizes picture
     */
    @SuppressWarnings("deprecation")
    public static BitmapDrawable getScaledDrawable(Activity activity, String path) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        float destinationWidth = display.getWidth();
        float destinationHeihgt = display.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcWidth > destinationWidth || srcHeight > destinationHeihgt) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destinationHeihgt);
            } else {
                inSampleSize = Math.round(srcWidth / destinationWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return new BitmapDrawable(activity.getResources(), bitmap);
    }


    /**
     * Unloads the image from view and cleans it up
     *
     * @param imageView ImageView with bitmap
     */
    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) {
            return;
        }

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        drawable.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}

package com.sample.chrono12.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.net.URL

object ImageLoader {
    fun loadImage(urlString: String): Bitmap?{

        val url = URL(urlString)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = false
        }

//        BitmapFactory.decodeStream(url.openStream(), null, options)
//
//        val sampleSize = calculateSampleSize(options, reqWidth, reqHeight)
//
//        val finalOptions = BitmapFactory.Options().apply {
//            inJustDecodeBounds = false
//            inSampleSize = sampleSize
//        }

        val bitmap = BitmapFactory.decodeStream(url.openStream(), null, options)
        Log.d("image", "Bitmap : $bitmap")
        return bitmap
    }

    fun calculateSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run {outHeight to outWidth}
        var inSampleSize = 1
        Log.d("image", "$reqHeight , $reqWidth")
        if (height > reqHeight || width > reqWidth) {

            val heightRatio: Int = height / reqHeight
            val widthRatio: Int = width / reqWidth

            inSampleSize = if(heightRatio<widthRatio) heightRatio else widthRatio
        }

        return inSampleSize
    }
}


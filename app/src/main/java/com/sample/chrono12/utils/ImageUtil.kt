package com.sample.chrono12.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import androidx.lifecycle.LifecycleCoroutineScope
import com.sample.chrono12.R
import kotlinx.coroutines.*
import java.net.URL

object ImageUtil {

    private val memoryCache: LruCache<String, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }

    fun loadImage(url: String, imageview: ImageView, lifecycleCoroutineScope: LifecycleCoroutineScope) {

        if (getBitmapFromMemoryCache(url) != null) {
            imageview.setImageBitmap(getBitmapFromMemoryCache(url))
            Log.d("TAG", "Image set from cache")
        } else {
            lifecycleCoroutineScope.launch {
                imageview.setImageResource(R.drawable.ic_image_loaading)
                val result = async(Dispatchers.IO) {
                    val imageUrl = URL(url)

                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }

                    BitmapFactory.decodeStream(imageUrl.openStream(), null, options)

                    val sampleSize = calculateSampleSize(options, imageview.width, imageview.height)

                    val finalOptions = BitmapFactory.Options().apply {
                        inJustDecodeBounds = false
                        inSampleSize = sampleSize
                    }

                    return@async BitmapFactory.decodeStream(imageUrl.openStream(), null, finalOptions)
                }


                try {
                    val bitmap = result.await()
                    memoryCache.put(url, bitmap)
                    Log.d("TAG", "Image set from loading url")
                    imageview.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Log.d("TAG", "Exception : $e")
                }
            }
        }

    }

    private fun calculateSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun getBitmapFromMemoryCache(key: String): Bitmap? = memoryCache.get(key)

}
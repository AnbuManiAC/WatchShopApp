package com.sample.chrono12.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import androidx.lifecycle.LifecycleCoroutineScope
import com.sample.chrono12.R
import com.sample.chrono12.data.models.ImageKey
import kotlinx.coroutines.*
import java.net.URL
import kotlin.coroutines.coroutineContext

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

    fun loadImage(url: String, imageview: ImageView, imageKey: ImageKey = ImageKey.SMALL) {

        if (getBitmapFromMemoryCache(url+imageKey) != null) {
            imageview.setImageBitmap(getBitmapFromMemoryCache(url+imageKey.toString()))
            Log.d("TAG", "Image set from cache")
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) { imageview.setImageResource(R.drawable.ic_image_loaading) }
                val result: Deferred<Bitmap?> = async(Dispatchers.IO) {
                    val imageUrl = URL(url)

                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }

                    try {
                        BitmapFactory.decodeStream(imageUrl.openStream(), null, options)

                        val sampleSize =
                            calculateSampleSize(options, imageview.width, imageview.height)

                        val finalOptions = BitmapFactory.Options().apply {
                            inJustDecodeBounds = false
                            inSampleSize = sampleSize
                        }

                        return@async BitmapFactory.decodeStream(
                            imageUrl.openStream(),
                            null,
                            finalOptions
                        )
                    } catch (e: Exception) {
                        return@async null
                    }
                }


                try {
                    withContext(Dispatchers.Main) {
                        val bitmap = result.await()
                        if (bitmap != null) {
                            memoryCache.put(url+imageKey.toString(), bitmap)
                            Log.d("TAG", "Image set from loading url")
                            imageview.setImageBitmap(bitmap)
                        }

                    }

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
            Log.d("SampleSize", "$reqHeight $reqWidth $halfHeight $halfWidth $inSampleSize")
            while (inSampleSize != 0 && halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
                Log.d("SampleSizeUpdated", "$halfHeight $halfWidth $inSampleSize")

            }
        }

        return inSampleSize
    }

    private fun getBitmapFromMemoryCache(key: String): Bitmap? = memoryCache.get(key)

}
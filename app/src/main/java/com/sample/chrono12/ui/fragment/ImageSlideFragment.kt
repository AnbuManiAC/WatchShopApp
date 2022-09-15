package com.sample.chrono12.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.sample.chrono12.R
import com.sample.chrono12.ui.adapter.ImageSliderAdapter
import com.sample.chrono12.ui.adapter.ThumbnailAdapter
import java.io.InputStream
import java.net.URL


class ImageSlideFragment : Fragment() {

    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var thumbnailAdapter: ThumbnailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_image_slide, container, false)
        return view
    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val imageList = listOf(
//            "https://i.picsum.photos/id/0/5616/3744.jpg?hmac=3GAAioiQziMGEtLbfrdbcoenXoWAW-zlyEAMkfEdBzQ",
//            "https://i.picsum.photos/id/10/2500/1667.jpg?hmac=J04WWC_ebchx3WwzbM-Z4_KC_LeLBWr5LZMaAkWkF68",
//            "https://i.picsum.photos/id/1001/5616/3744.jpg?hmac=38lkvX7tHXmlNbI0HzZbtkJ6_wpWyqvkX4Ty6vYElZE",
//            "https://i.picsum.photos/id/1004/5616/3744.jpg?hmac=Or7EJnz-ky5bsKa9_frdDcDCR9VhCP8kMnbZV6-WOrY",
//            "https://i.picsum.photos/id/101/2621/1747.jpg?hmac=cu15YGotS0gIYdBbR1he5NtBLZAAY6aIY5AbORRAngs",
//            "https://i.picsum.photos/id/1010/5184/3456.jpg?hmac=7SE0MNAloXpJXDxio2nvoshUx9roGIJ_5pZej6qdxXs",
//            "https://i.picsum.photos/id/1013/4256/2832.jpg?hmac=UmYgZfqY_sNtHdug0Gd73bHFyf1VvzFWzPXSr5VTnDA",
//            "https://i.picsum.photos/id/1016/3844/2563.jpg?hmac=WEryKFRvTdeae2aUrY-DHscSmZuyYI9jd_-p94stBvc"
//
//        )
//
//        val vpImage = view.findViewById<ViewPager2>(R.id.vp_image)
//        val rvThumbnail = view.findViewById<RecyclerView>(R.id.rv_thumbnail)
//
//        imageSliderAdapter = ImageSliderAdapter(imageList)
//        vpImage.adapter = imageSliderAdapter
//        thumbnailAdapter = ThumbnailAdapter(imageList){
//            vpImage.currentItem = imageList.indexOf(it)
//        }
//
//        rvThumbnail.adapter = thumbnailAdapter
//        rvThumbnail.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//
//        vpImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
//            override fun onPageSelected(position: Int) {
//                thumbnailAdapter.updateSelectedPosition(position)
//                rvThumbnail.smoothScrollToPosition(position)
//            }
//        })
//    }
//
//

}
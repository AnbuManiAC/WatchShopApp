package com.sample.chrono12.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sample.chrono12.data.dao.WatchDao
import com.sample.chrono12.data.entities.*

@Database(
    entities = [
        Address::class,
        Cart::class,
        Category::class,
        Favorite::class,
        Order::class,
        OrderedProductList::class,
        Product::class,
        ProductBrand::class,
        ProductDetail::class,
        ProductImage::class,
        ProductReview::class,
        ProductCategoryCrossRef::class,
        SearchSuggestion::class,
        SubCategory::class,
        User::class
    ],
    version = 1,
    exportSchema = false
    )
abstract class WatchShopDatabase : RoomDatabase() {
    abstract val watchDao: WatchDao
}
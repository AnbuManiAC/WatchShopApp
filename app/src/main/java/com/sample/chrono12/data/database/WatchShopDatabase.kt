package com.sample.chrono12.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sample.chrono12.data.dao.UserDao
import com.sample.chrono12.data.dao.WatchDao
import com.sample.chrono12.data.entities.*

@Database(
    entities = [
        Address::class,
        AddressGroup::class,
        BusinessDetail::class,
        Cart::class,
        Category::class,
        Favorite::class,
        Order::class,
        ProductOrdered::class,
        Product::class,
        ProductBrand::class,
        ProductDetail::class,
        ProductImage::class,
        ProductReview::class,
        ProductCategory::class,
        SearchSuggestion::class,
        SubCategory::class,
        User::class
    ],
    version = 1,
    exportSchema = true
    )
abstract class WatchShopDatabase : RoomDatabase() {

    abstract val watchDao: WatchDao

    abstract val userDao: UserDao
}
package com.sample.chrono12.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sample.chrono12.data.dao.UserDao
import com.sample.chrono12.data.dao.WatchDao
import com.sample.chrono12.data.entities.*

@Database(
    entities = [
        Address::class,
        AddressAndGroupCrossRef::class,
        AddressGroup::class,
        Cart::class,
        Category::class,
        WishList::class,
        Order::class,
        ProductOrdered::class,
        Product::class,
        ProductBrand::class,
        ProductDetail::class,
        ProductImage::class,
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

    abstract val userDao: UserDao
}
package com.sample.chrono12.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sample.chrono12.data.entities.User

@Dao
interface UserDao {

    //Signup, Signin
    @Insert
    suspend fun registerUser(user: User) : Long

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :emailId)")
    suspend fun isRegisteredUser(emailId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :emailId AND password = :password)")
    suspend fun validateUser(emailId: String, password: String): Int

    @Query("SELECT userId FROM User WHERE email = :emailId")
    suspend fun getUserId(emailId: Int): Int




}
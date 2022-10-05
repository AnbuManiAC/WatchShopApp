package com.sample.chrono12.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import com.sample.chrono12.R

object SharedPrefUtil {

    fun getBulkOrderId(requireActivity: FragmentActivity): Int {
        val sharedPreferences = getSharedPreference(requireActivity)
        val editor = getEditor(sharedPreferences)
        val bulkOrderIdKey = requireActivity.getString(R.string.bulk_order_id)
        var bulkOrderId = sharedPreferences.getInt(bulkOrderIdKey, 0)
        bulkOrderId++
        editor.putInt(bulkOrderIdKey, bulkOrderId).apply()
        return bulkOrderId
    }

    fun getSortType(requireActivity: FragmentActivity): String {
        val sharedPreferences = getSharedPreference(requireActivity)
        val sortTypeKey = requireActivity.getString(R.string.sort_type)
        return sharedPreferences.getString(sortTypeKey, "")!!
    }

    fun changeSortType(requireActivity: FragmentActivity, sortType: String){
        val sharedPreferences = getSharedPreference(requireActivity)
        val editor = getEditor(sharedPreferences)
        val sortTypeKey = requireActivity.getString(R.string.sort_type)
        editor.putString(sortTypeKey, sortType).apply()
    }

    fun getUserId(requireActivity: FragmentActivity): Long {
        val sharedPreferences = getSharedPreference(requireActivity)
        val userIdKey = requireActivity.getString(R.string.user_id)
        return sharedPreferences.getLong(userIdKey, 0)
    }

    fun setUserId(requireActivity: FragmentActivity, userId: Long){
        val sharedPreferences = getSharedPreference(requireActivity)
        val editor = getEditor(sharedPreferences)
        val userIdKey = requireActivity.getString(R.string.user_id)
        editor.putLong(userIdKey, userId).apply()
    }

    private fun getSharedPreference(requireActivity: FragmentActivity) =
        requireActivity.getSharedPreferences(
            requireActivity.getString(R.string.user_pref),
            Context.MODE_PRIVATE
        )


    private fun getEditor(sharedPreferences: SharedPreferences) =
        sharedPreferences.edit()
}
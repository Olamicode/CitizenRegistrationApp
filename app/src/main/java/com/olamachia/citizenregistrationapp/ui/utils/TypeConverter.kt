package com.olamachia.citizenregistrationapp.ui.utils


import com.google.gson.Gson

object TypeConverter {
    val gson = Gson()

    fun<T> convertPojoToString(pojoData: T): String {
        return gson.toJson(pojoData)
    }

    inline fun<reified T> convertStringToPojo(data: String): T {
        return gson.fromJson(data, T::class.java)
    }

}
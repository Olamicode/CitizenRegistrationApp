package com.olamachia.citizenregistrationapp.data

data class Countries(
    val data: List<Country>
)

data class Country(
    val name: String,
    val code: String
)
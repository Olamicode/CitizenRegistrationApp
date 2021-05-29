package com.olamachia.citizenregistrationapp.data


data class Citizen(
    var child: Child? = null,
    var mother: Parent? = null,
    var father: Parent? = null,
    var informant: Informant? = null,
    var dateAdded: String? = null,
    var regStatus: String? = null,
    var hospital: Hospital? = null
)

data class Child(
    var firstname: String? = null,
    var lastname: String? = null,
    var dateOfBirth: String? = null,
    var gender: String? = null,
    var placeOfOccurrence: String? = null,
    var town: String? = null,
    var typeOfBirth: String? = null,
    var birthOrder: String? = null,
)


data class Parent(
    var firstname: String? = null,
    var lastname: String? = null,
    var martialStatus: String? = null,
    var nationality: String? = null,
    var stateOfOrigin: String? = null,
    var ethicOrigin: String? = null,
    var educationLevel: String? = null,
    var occupation: String? = null,
    var phoneNumber: String? = null,
    var nationalIDNumber: String? = null,
)

data class Informant(
    var firstname: String? = null,
    var lastname: String? = null,
    var relationship: String? = null,
    var address: String? = null,
    var phoneNumber: String? = null,
    var nationalIDNumber: String? = null,
)

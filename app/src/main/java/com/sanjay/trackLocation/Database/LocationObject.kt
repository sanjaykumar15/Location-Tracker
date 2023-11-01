package com.sanjay.trackLocation.Database

import io.realm.kotlin.types.RealmObject

class LocationObject(): RealmObject {
    var userId: Int = 0
    var city: String = ""
    var state:String = ""
    var pinCode: String = ""
    var country: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var dateTime: String = ""
}
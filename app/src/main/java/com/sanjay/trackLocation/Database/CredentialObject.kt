package com.sanjay.trackLocation.Database

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CredentialObject(): RealmObject {
    @PrimaryKey
    var id: Int = 1
    var name: String = ""
    var email: String = ""
    var pass: String = ""
    var contact: String = ""
    var isLoggedIn: Boolean = false
}
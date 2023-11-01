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

/*import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
open class CredentialModel: RealmModel {

    @PrimaryKey
    var id: Int?=1

    @Required
    var name: String?=""

    @Required
    var email: String?=""

    @Required
    var pass: String?=""

    @Required
    var contact: String?=""

//    @Required
    var isLoggedIn: Boolean = false

}*/

package com.sanjay.trackLocation.Database

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort

class OperationModel : ViewModel() {

    val logInResultData by lazy { MutableLiveData<List<CredentialObject>>() }
    val locationResult by lazy { MutableLiveData<List<LocationObject>>() }
    val loggedUserResultData by lazy { MutableLiveData<List<CredentialObject>>() }
    val userId by lazy { MutableLiveData<Int>() }
    val config =
        RealmConfiguration.create(schema = setOf(CredentialObject::class, LocationObject::class))
    var realm = Realm.open(config)

    fun signUp(nameVal: String, emailVal: String, passVal: String, contactVal: String) {

        val resultItems: RealmResults<CredentialObject> =
            realm.query<CredentialObject>().sort("id", Sort.DESCENDING).limit(1).find()
        if (resultItems.size == 0) {
            userId.value = 1
        } else {
            userId.value = resultItems[0].id + 1
        }

        realm.writeBlocking {
            copyToRealm(CredentialObject().apply {
                id = userId.value!!
                name = nameVal
                email = emailVal
                pass = passVal
                contact = contactVal
                isLoggedIn = true
            })
        }

    }

    fun logIn(email: String, pass: String) {

        val res: RealmResults<CredentialObject> =
            realm.query<CredentialObject>("email == $0 AND pass == $1", email, pass).find()
        logInResultData.value = realm.copyFromRealm(res)

    }

    fun writeLocation(
        id: Int, cityVal: String, stateVal: String, pinVal: String,
        countryVal: String, latVal: Double, longVal: Double, dateTimeVal: String
    ) {

        realm.writeBlocking {
            copyToRealm(LocationObject().apply {
                userId = id
                city = cityVal
                state = stateVal
                pinCode = pinVal
                country = countryVal
                latitude = latVal
                longitude = longVal
                dateTime = dateTimeVal
            })
        }

    }

    fun getLocationFromDB(id: Int) {

        val res: RealmResults<LocationObject> = realm.query<LocationObject>("userId == $0", id)
            .sort("dateTime", sortOrder = Sort.DESCENDING).find()
        locationResult.value = realm.copyFromRealm(res)

    }

    fun getLoggedInUser() {
        val resultItems: RealmResults<CredentialObject> =
            realm.query<CredentialObject>("isLoggedIn == $0", true).find()
        loggedUserResultData.value = realm.copyFromRealm(resultItems)
    }

    fun updateIsLoggedIn(id: Int, isLoggedInVal: Boolean) {
        realm.writeBlocking {
            val res: RealmResults<CredentialObject> =
                this.query<CredentialObject>("id == $0", id).find()
            res[0].isLoggedIn = isLoggedInVal
        }
    }

}

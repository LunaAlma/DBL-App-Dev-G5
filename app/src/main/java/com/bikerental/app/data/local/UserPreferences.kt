package com.bikerental.app.data.local

//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.MutablePreferences
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.core.booleanPreferencesKey
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.core.stringPreferencesKey
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
//import javax.inject.Inject
//import javax.inject.Singleton
//import kotlin.let
//
//@Singleton
//class UserPreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {
//
//    companion object {
//        private val USER_ID = stringPreferencesKey("USER_ID")
//        private val USER_NAME =
//            stringPreferencesKey("USER_NAME")
//        private val USER_EMAIL =
//            stringPreferencesKey("USER_EMAIL")
//        private val USER_PROFILE_PIC_URL =
//            stringPreferencesKey("USER_PROFILE_PIC_URL")
//    }
//
//    suspend fun getUserId() = DataStore.data.map { it[USER_ID] }.first()
//
//    suspend fun setUserId(userId: String) {
//        dataStore.edit { it[USER_ID] set userId }
//    }
//
//    suspend fun removeUserId() {
//        dataStore.edit { MutablePreferences.remove(USER_ID) }
//    }
//
//    suspend fun getUserName() = DataStore.data.map { it[USER_NAME] }.first()
//
//    suspend fun setUserName(userName: String) {
//        dataStore.edit { it[USER_NAME] set userName }
//    }
//
//    suspend fun removeUserName() {
//        dataStore.edit { MutablePreferences.remove(USER_NAME) }
//    }
//
//    suspend fun getUserEmail() = DataStore.data.map { it[USER_EMAIL] }.first()
//
//    suspend fun setUserEmail(email: String) {
//        dataStore.edit { it[USER_EMAIL] set email }
//    }
//
//    suspend fun removeUserEmail() {
//        dataStore.edit { MutablePreferences.remove(USER_EMAIL) }
//    }
//
//    suspend fun getUserProfilePicUrlUrl() = androidx.datastore.core.DataStore.data.map { it[USER_PROFILE_PIC_URL] }.first()
//
//    suspend fun setUserProfileProfilePicUrl(url: String?) {
//        url?.let {
//            dataStore.edit { it[USER_PROFILE_PIC_URL] set url }
//        } ?: removeUserProfilePicUrl()
//    }
//
//    suspend fun removeUserProfilePicUrl() {
//        dataStore.edit {
//            androidx.datastore.preferences.core.MutablePreferences.remove(
//                USER_PROFILE_PIC_URL
//            )
//        }
//    }
//    suspend fun getOnBoardingComplete() =
//        androidx.datastore.core.DataStore.data.map { it[ON_BOARDING_COMPLETED] }.first() ?: false
//
//    suspend fun setOnBoardingComplete(complete: Boolean) {
//        dataStore.edit { it[ON_BOARDING_COMPLETED] set complete }
//    }
//
//    suspend fun removeOnBoardingComplete() {
//        dataStore.edit {
//            androidx.datastore.preferences.core.MutablePreferences.remove(
//                ON_BOARDING_COMPLETED
//            )
//        }
//    }
//
//
//}
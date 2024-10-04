package xyz.penpencil.competishun.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import xyz.penpencil.competishun.data.model.TopicContentModel
import com.student.competishun.gatekeeper.type.UpdateUserInput
import xyz.penpencil.competishun.ui.adapter.createGson

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "Competishun_Prefs", Context.MODE_PRIVATE
    )

    private val gson : Gson = createGson()

    companion object {
        private const val KEY_ACCESS_TOKEN = "accessToken"
        private const val KEY_Order_Id = "rzpOrderId"
        private const val KEY_REFRESH_TOKEN = "refreshToken"
        private const val KEY_UPDATE_USER_INPUT = "updateUserInput"
        private const val KEY_MOBILE_NO = "mobileNo"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_CITY = "city"
        private const val KEY_USER_ID = "userId"
        private const val KEY_PREPARATION_FOR = "preparingFor"
        private const val KEY_OTHER_PREP_FOR = "OtherFor"
        private const val KEY_TARGET_YEAR = "targetYear"
        private const val KEY_REFERENCE = "reference"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_FATHER_NAME = "father_name"
        private const val KEY_USER_MOBILE_NO = "user_mob_no"
        private const val KEY_Tshirt = "t_shirt_size"
        private const val IS_FIRST_INSTALL = "is_first_install"

        private const val KEY_DOWNLOADED_ITEM_PREFIX = "downloaded_item_"
        private const val KEY_DOWNLOADED_ITEM_BM_PREFIX = "downloaded_item_bm"



    }

    fun saveDownloadedItemBm(item: TopicContentModel) {
        val json = gson.toJson(item)
        sharedPreferences.edit().putString(KEY_DOWNLOADED_ITEM_BM_PREFIX + item.id, json).apply()
        Log.e("SharedPreferences", "Saved downloaded item: ${item.url}")
    }

    fun getDownloadedItemsBm(): List<TopicContentModel> {
        val items = mutableListOf<TopicContentModel>()
        sharedPreferences.all.forEach { entry ->
            if (entry.key.startsWith(KEY_DOWNLOADED_ITEM_BM_PREFIX)) {
                val json = entry.value as? String
                json?.let {
                    val item = gson.fromJson(it, TopicContentModel::class.java)
                    items.add(item)
                }
            }
        }
        return items
    }


    fun saveDownloadedItem(item: TopicContentModel) {
        val json = gson.toJson(item)
        sharedPreferences.edit().putString(KEY_DOWNLOADED_ITEM_PREFIX + item.id, json).apply()
        Log.e("SharedPreferences", "Saved downloaded item: ${item.url}")
    }

    fun getDownloadedItems(): List<TopicContentModel> {
        val items = mutableListOf<TopicContentModel>()
        sharedPreferences.all.forEach { entry ->
            if (entry.key.startsWith(KEY_DOWNLOADED_ITEM_PREFIX)) {
                val json = entry.value as? String
                json?.let {
                    val item = gson.fromJson(it, TopicContentModel::class.java)
                    items.add(item)
                }
            }
        }
        return items
    }
    fun getDownloadedVideos(): List<TopicContentModel> {
        return getDownloadedItems().filter { it.fileType == "VIDEO" }
    }

    fun getDownloadedPdfs(): List<TopicContentModel> {
        return getDownloadedItems().filter { it.fileType == "PDF" }
    }


    fun deleteDownloadedItem(item: TopicContentModel) {
        sharedPreferences.edit().remove(KEY_DOWNLOADED_ITEM_PREFIX + item.id).apply()
    }
    fun deleteDownloadedItemBm(item: TopicContentModel) {
        sharedPreferences.edit().remove(KEY_DOWNLOADED_ITEM_BM_PREFIX + item.id).apply()
    }



    var shirtSize: String?
        get() = sharedPreferences.getString(KEY_Tshirt, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_Tshirt, value).apply()

            Log.e("sharedPreferences token", shirtSize.toString())
        }


    var isBottomSheetShown: Boolean
        get() = sharedPreferences.getBoolean("isBottomSheetShown", false)
        set(value) = sharedPreferences.edit().putBoolean("isBottomSheetShown", value).apply()

    var isMyCourseAvailable: Boolean
        get() = sharedPreferences.getBoolean("isMyCourseAvailable", false)
        set(value) = sharedPreferences.edit().putBoolean("isMyCourseAvailable", value).apply()



    var fatherName: String?
        get() = sharedPreferences.getString(KEY_FATHER_NAME, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_FATHER_NAME, value).apply()

            Log.e("sharedPreferences token", fatherName.toString())
        }


    var whatsUpNo: String?
        get() = sharedPreferences.getString(KEY_USER_MOBILE_NO, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_USER_MOBILE_NO, value).apply()

            Log.e("sharedPreferences token", whatsUpNo.toString())
        }


    var fullName: String?
        get() = sharedPreferences.getString(KEY_FULL_NAME, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_FULL_NAME, value).apply()

            Log.e("sharedPreferences token", fullName.toString())
        }


    var accessToken: String?
        get() = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, value).apply()

            Log.e("sharedPreferences token", accessToken.toString())
        }

    var rzpOrderId: String?
        get() = sharedPreferences.getString(KEY_Order_Id, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_Order_Id, value).apply()

            Log.e("sharedPreferences rzpOrderId", rzpOrderId.toString())
        }

    var userId: String?
        get() = sharedPreferences.getString(KEY_USER_ID, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_USER_ID, value).apply()

            Log.e("sharedPreferences token", userId.toString())
        }
    var mobileNo: String?
        get() = sharedPreferences.getString(KEY_MOBILE_NO, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_MOBILE_NO, value).apply()

            Log.e("sharedPrefnumber sa", mobileNo.toString())
        }
    var name: String?
        get() = sharedPreferences.getString(KEY_NAME, null)
        set(value){
            sharedPreferences.edit().putString(KEY_NAME, value).apply()
            Log.e("sharedPrefname sa", name.toString())
        }
    var email: String?
        get() = sharedPreferences.getString(KEY_EMAIL, null)
        set(value){
            sharedPreferences.edit().putString(KEY_EMAIL, value).apply()
            Log.e("sharedPrefname sa", email.toString())
        }
    var city:String?
        get() = sharedPreferences.getString(KEY_CITY, null)
        set(value){
            sharedPreferences.edit().putString(KEY_CITY, value).apply()
            Log.e("sharedPrefcity sa", city.toString() )
        }
    var preparingFor:String?
        get() = sharedPreferences.getString(KEY_PREPARATION_FOR, null)
        set(value){
            sharedPreferences.edit().putString(KEY_PREPARATION_FOR, value).apply()
            Log.e("sharedPrefpreparing sa", preparingFor.toString() )
        }
    var descriptionText:String?
        get() = sharedPreferences.getString(KEY_OTHER_PREP_FOR, null)
        set(value){
            sharedPreferences.edit().putString(KEY_OTHER_PREP_FOR, value).apply()
            Log.e("sharedPrefpreparing sa", descriptionText.toString() )
        }
    var targetYear: Int?
        get() = sharedPreferences.getInt(KEY_TARGET_YEAR, 0)
        set(value){
            if (value != null) {
                sharedPreferences.edit().putInt(KEY_TARGET_YEAR, value).apply()
            }
            Log.e("sharedPreftarget sa", targetYear.toString() )
        }

    var reference: String?
        get() = sharedPreferences.getString(KEY_REFERENCE, null)
        set(value){
            if(!value.isNullOrEmpty()){
                sharedPreferences.edit().putString(KEY_REFERENCE, value).apply()
            }
            Log.e("sharedPrefreference sa", reference.toString() )
        }

    var refreshToken: String?
        get() = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, value).apply()
            Log.e("sharedPrefrefre tok", refreshToken.toString())
        }
    var updateUserInput: UpdateUserInput?
        get() {
            val json = sharedPreferences.getString(KEY_UPDATE_USER_INPUT, null)
            return if (json != null) gson.fromJson(json, UpdateUserInput::class.java) else null
        }
        set(value) {
            val json = gson.toJson(value)
            sharedPreferences.edit().putString(KEY_UPDATE_USER_INPUT, json).apply()
            Log.e("sharedPref UserInput", accessToken.toString())
        }

    var isReferenceSelectionInProgress: Boolean
        get() = sharedPreferences.getBoolean("isReferenceSelectionInProgress", false)
        set(value) = sharedPreferences.edit().putBoolean("isReferenceSelectionInProgress", value).apply()

    var isFormValid: Boolean
        get() = sharedPreferences.getBoolean("isFormValid", false)
        set(value) = sharedPreferences.edit().putBoolean("isFormValid", value).apply()

    var isFirstInstall: Boolean
        get() = sharedPreferences.getBoolean(IS_FIRST_INSTALL, true)
        set(value) = sharedPreferences.edit().putBoolean(IS_FIRST_INSTALL, value).apply()

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String,defaultValue: String): String? {
        return sharedPreferences.getString(key,defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun clearAccessToken() {
        sharedPreferences.edit().remove(KEY_ACCESS_TOKEN).apply()
    }
    fun clearRefreshToken() {
        sharedPreferences.edit().remove(KEY_REFRESH_TOKEN).apply()
    }

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}
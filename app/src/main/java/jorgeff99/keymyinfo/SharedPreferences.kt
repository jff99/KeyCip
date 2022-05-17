package jorgeff99.keymyinfo

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SharedPreferences(val context: Context) {
    val NAME_SHAREDPREFERENCES = "userPreferences"
    val SHAREDPREFERENCES_PIN  = "pin"
    val SHAREDPREFERENCES_NAME  = "name"
    val SHAREDPREFERENCES_FINGERPRINT  = "fingerprint"
    val SHAREDPREFERENCES_TUTORIAL_INTRODUCTION  = "introduction"
    val SHAREDPREFERENCES_GENKEY  = "genKey"
    val SHAREDPREFERENCES_PERMISSION  = "permission"
    val SHAREDPREFERENCES_KEYBOARDTHEME  = "keyboardTheme"
    val SHAREDPREFERENCES_KEYBOARDLANGUAGE = "keyboardLanguage"
    val SHAREDPREFERENCES_SOUNDSWITCH  = "soundSwitch"
    val SHAREDPREFERENCES_VIBRATIONSWITCH = "vibrationSwitch"
    val SHAREDPREFERENCES_TUTORIALVISUALIZATION = "tutorial"


    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        NAME_SHAREDPREFERENCES,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    fun saveString(specificPReference: String, value:String){
        sharedPreferences.edit().putString(specificPReference,value).apply()
    }

    fun getString(specificPReference: String): String{
        return sharedPreferences.getString(specificPReference,"")!!
    }

    fun saveInt(specificPReference: String, value:Int){
        sharedPreferences.edit().putInt(specificPReference,value).apply()
    }

    fun getInt(specificPReference: String): Int{
        return sharedPreferences.getInt(specificPReference,-1)!!
    }

    fun saveBoolean(specificPReference: String, value:Boolean){
        sharedPreferences.edit().putBoolean(specificPReference,value).apply()
    }

    fun getBoolean(specificPReference: String): Boolean{
        return sharedPreferences.getBoolean(specificPReference,false)!!
    }

    fun savePin(pin:String){
        sharedPreferences.edit().putString(SHAREDPREFERENCES_PIN,pin).apply()
    }

    fun getPin():String{
        return sharedPreferences.getString(SHAREDPREFERENCES_PIN,"")!!
    }

    fun saveName(name:String){
        sharedPreferences.edit().putString(SHAREDPREFERENCES_NAME,name).apply()
    }

    fun getName():String{
        return sharedPreferences.getString(SHAREDPREFERENCES_NAME,"")!!
    }

    fun saveFingerPrint(fingerprint:Int){
        sharedPreferences.edit().putInt(SHAREDPREFERENCES_FINGERPRINT,fingerprint).apply()
    }

    fun getFingerPrint():Int{
        return sharedPreferences.getInt(SHAREDPREFERENCES_FINGERPRINT,-1)!!
    }


}
/*
* This Kotlin class is part of KeyCip Android app.
* The whole open source code is available in https://github.com/jff99/KeyCip
* Copyright (C) 2022 Jorge Fernández Fonseca
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package jorgeff99.keymyinfo.common

import android.content.Context
import android.os.Bundle
import android.provider.Settings.Secure.getString
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application.Companion.sharedPreferences
import jorgeff99.keymyinfo.fragments.genkey.NameInsertFragment
import jorgeff99.keymyinfo.fragments.operation.BlankFragment
import jorgeff99.keymyinfo.fragments.operation.LoadingOperation
import java.util.concurrent.Executor

/*
* The main objective of this class is to define the logic in order to authenticate through
* the fingerprint (or another biometric method)
*/

class Fingerprint(val context: Context) {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    /*
    Return 0 if biometric method is available and configured. Return -1if the
    conditions are not satisfied
    */
    fun checkFingerprint(root: View): Int{
        val biometricManager = BiometricManager.from(root.context)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)){
            BiometricManager.BIOMETRIC_SUCCESS ->
                return 0
        }
        return -1
    }

    /*
      Display the biometric prompt in order to authenticate. When flag is null, it means that fingerprint
      is required in the tutorial (so that the next fragment displayed needs to be NameInsert)
    */
    fun showFingerprint(root: View, fragment: Fragment, flag: MaterialButton?, selectedOptions: ArrayList<String>?){
        val biometricManager = BiometricManager.from(root.context)
        executor = ContextCompat.getMainExecutor(root.context)
        biometricPrompt = BiometricPrompt(fragment, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(root.context,
                        "${context.resources.getString(R.string.authError)} $errString", Toast.LENGTH_SHORT)
                        .show()
                    sharedPreferences.savePin("")
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                        Toast.makeText(root.context,
                            context.resources.getString(R.string.authComplete), Toast.LENGTH_SHORT)
                            .show()
                        sharedPreferences.saveFingerPrint(0)

                        //When flag is null, fingerprint is required to make an operation
                        if(selectedOptions != null){
                            val bundle = Bundle()
                            bundle.putStringArrayList("selectedOptions", selectedOptions)
                            val transaction = fragment.activity?.supportFragmentManager?.beginTransaction()
                            val fragment = LoadingOperation()
                            fragment.arguments = bundle
                            transaction?.replace(R.id.operation, fragment)
                            transaction?.commit()
                        }

                        else if(flag == null){
                            val transaction = fragment.activity?.supportFragmentManager?.beginTransaction()
                            val fragment = NameInsertFragment()
                            transaction?.replace(R.id.fragment_container, fragment)
                            transaction?.commit()
                        }
                        else{
                            flag.text = flag.context.getString(R.string.biometricCancelButton)
                        }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(root.context, context.resources.getString(R.string.authFailed),
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })


        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(root.context.getString(R.string.fingerprintTitle))
            .setSubtitle(root.context.getString(R.string.fingerprintExplanatioon))
            .setNegativeButtonText(root.context.getString(R.string.cancel))
            .build()

        biometricPrompt.authenticate(promptInfo)

    }

}
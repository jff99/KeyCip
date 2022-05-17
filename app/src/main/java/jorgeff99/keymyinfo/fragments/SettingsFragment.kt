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

package jorgeff99.keymyinfo.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jorgeff99.keymyinfo.ConceptActivity
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application
import jorgeff99.keymyinfo.common.Application.Companion.sharedPreferences

class SettingsFragment : Fragment() {
    private lateinit var root:View
    private lateinit var accessButton: MaterialButton
    private lateinit var biometricButton: MaterialButton
    private lateinit var tutorialButton: MaterialButton
    private lateinit var githubButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_settings, container, false)

        // Button to redirect user to allow access to internal storage
        accessButton = root.findViewById(R.id.accessButton)
        accessButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
        }

        // Button to allow biometric auth. Text of the button is displayed depending on whether is already activated or not
        biometricButton = root.findViewById(R.id.biometricButton)
        biometricButton.text = checkText()
        biometricButton.setOnClickListener {
            fingerprintOperation()
            biometricButton.text = checkText()
        }

        // Button to access tutorial to revise concepts
        tutorialButton = root.findViewById(R.id.tutorialButton)
        tutorialButton.setOnClickListener {
            sharedPreferences.saveInt("tutorial", 0)
            val intent = Intent(requireContext(), ConceptActivity::class.java)
            requireContext().startActivity(intent)
        }

        // Access to github repository
        githubButton = root.findViewById(R.id.githubButton)
        githubButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jff99/KeyCip")))
        }

        return root
    }

    private fun checkText():String{
        return if(sharedPreferences.getFingerPrint() == 0){
            getString(R.string.biometricCancelButton)
        } else{
            sharedPreferences.saveFingerPrint(-1)
            getString(R.string.biometricButton)
        }
    }

    private fun fingerprintOperation(){
        if(sharedPreferences.getFingerPrint() == 0){
            sharedPreferences.saveFingerPrint(-1)
            Toast.makeText(
                context,
                resources.getString(R.string.notVinculate),
                Toast.LENGTH_LONG
            ).show()
        }
        else{
            if (Application.autenticator.checkFingerprint(root) == 0) {
                MaterialAlertDialogBuilder(root.context)
                    .setTitle(resources.getString(R.string.fingerprintTitle))
                    .setMessage(resources.getString(R.string.fingerprintExplanatioon))
                    .setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
                        // Respond to negative button press
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                        // Respond to positive button press
                        Application.autenticator.showFingerprint(root, this, biometricButton, null)
                    }
                    .show()

            } else {
                Toast.makeText(root.context,
                    resources.getString(R.string.configureBiometric), Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

}


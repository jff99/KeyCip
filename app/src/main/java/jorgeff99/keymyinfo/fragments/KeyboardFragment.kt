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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application.Companion.sharedPreferences

class KeyboardFragment : Fragment() {
    // Switch in order to activate the sound of the keyboard
    private lateinit var switchSound: SwitchMaterial
    // Switch in order to activate the vibration of the keyboard
    private lateinit var switchVibration: SwitchMaterial
    // Toggle in order to change the language of the keyboard
    private lateinit var toggleLanguageGroup: MaterialButtonToggleGroup
    // Button in order to access the users setting to add keyboard as to the available
    private lateinit var establishLKeyboard: Button
    private lateinit var youtubeButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_keyboard, container, false)

        // Check whether sound for the keyboard is activated
        switchSound = root.findViewById(R.id.switchSound)
        switchSound.isChecked = sharedPreferences.getBoolean("soundSwitch")
        switchSound.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.saveBoolean("soundSwitch", isChecked)
        }

        // Check whether vibration for the keyboard is activated
        switchVibration = root.findViewById(R.id.switchVibration)
        switchVibration.isChecked = sharedPreferences.getBoolean("vibrationSwitch")
        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.saveBoolean("vibrationSwitch", isChecked)
        }

        // Check which language is activated
        toggleLanguageGroup = root.findViewById(R.id.toggleLanguageGroup)
        toggleLanguageGroup.check(sharedPreferences.getInt("keyboardLanguage"))
        toggleLanguageGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            Toast.makeText(context, resources.getString(R.string.toggleLanguage), Toast.LENGTH_LONG)
            sharedPreferences.saveInt("keyboardLanguage",checkedId)
        }

        // Button in order to establish KeyCip keyboard as main one
        establishLKeyboard = root.findViewById(R.id.establishLKeyboard)
        establishLKeyboard.setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        // Button that redirects to the youtube channel
        youtubeButton = root.findViewById(R.id.youtubeButton)
        youtubeButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCqvsnBVY6Gwq5VKuy6NfLGQ")))
        }

        return root
    }
}
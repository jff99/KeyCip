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

package jorgeff99.keymyinfo.fragments.tutorial

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application.Companion.sharedPreferences
import kotlinx.android.synthetic.main.fragment_grant_keyboard.*
import java.util.*

class GrantKeyboardFragment : Fragment() {
    private lateinit var grantPermissionKeyboardButton: MaterialButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_grant_keyboard, container, false)
        var directed = false
        grantPermissionKeyboardButton = root.findViewById(R.id.grantPermissionKeyboardButton)
        grantPermissionKeyboardButton.setOnClickListener {

            // Defines the language of the keyboard depending of the language of the user's phone
            if(Locale.getDefault().language != "es"){
                sharedPreferences.saveInt("keyboardLanguage", R.id.btnEnglishUKKeyboard)
            }

            else{
                sharedPreferences.saveInt("keyboardLanguage", R.id.btnSpanishKeyboard)
            }

            // Display the keyboard settings in the user's settings
            if(directed){
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                val finishFragment = FinishTutorialFragment()
                transaction?.replace(R.id.fragment_container,finishFragment)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
            else{
                directed = true
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }

        }
        return root
    }
}
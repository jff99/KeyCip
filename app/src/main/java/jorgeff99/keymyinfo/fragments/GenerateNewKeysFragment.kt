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
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.GenKeyActivity
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.WelcomeActivity
import jorgeff99.keymyinfo.common.Application.Companion.sharedPreferences
import jorgeff99.keymyinfo.fragments.tutorial.GrantKeyboardFragment

class GenerateNewKeysFragment : Fragment() {
    private lateinit var root: View
    private lateinit var yesButton: MaterialButton
    private lateinit var noButton: MaterialButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_generate_new_keys, container, false)

        yesButton = root.findViewById(R.id.yesButton)
        yesButton.setOnClickListener {
            sharedPreferences.saveInt("introduction", -1)
            sharedPreferences.saveInt("genKey", -1)
            sharedPreferences.saveInt("permission", -1)
            val intent = Intent(activity, WelcomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        noButton = root.findViewById(R.id.noButton)
        noButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragmentHome = HomeFragment()
            transaction?.replace(R.id.fragment_container,fragmentHome)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return root
    }
}
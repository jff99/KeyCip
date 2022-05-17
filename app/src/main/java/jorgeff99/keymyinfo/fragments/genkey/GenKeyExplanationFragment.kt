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

package jorgeff99.keymyinfo.fragments.genkey

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.fragments.tutorial.WhatIsFragment

class GenKeyExplanationFragment : Fragment() {
    private lateinit var btnContinue: MaterialButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment nt
        var root = inflater.inflate(R.layout.fragment_gen_key_explanation, container, false)
        btnContinue = root.findViewById(R.id.genKeyButton)
        btnContinue.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragmentGenKeyAuth = GenKeyAuthFragment()
            transaction?.replace(R.id.fragment_container,fragmentGenKeyAuth)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        return root
    }
}
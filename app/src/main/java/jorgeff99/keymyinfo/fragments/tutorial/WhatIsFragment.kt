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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.ConceptActivity
import jorgeff99.keymyinfo.GenKeyActivity
import jorgeff99.keymyinfo.R

class WhatIsFragment : Fragment() {

    private lateinit var btnConcept: MaterialButton
    private lateinit var btnGenKey: MaterialButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment nt
        val root = inflater.inflate(R.layout.fragment_what_is, container, false)
        btnConcept = root.findViewById(R.id.explainButton)
        btnConcept.setOnClickListener {
            val intent = Intent(activity, ConceptActivity::class.java)
            startActivity(intent)
        }

        btnGenKey= root.findViewById(R.id.genKeyButton)
        btnGenKey.setOnClickListener {
            val intent = Intent(activity, GenKeyActivity::class.java)
            startActivity(intent)
        }

        return root
    }
}
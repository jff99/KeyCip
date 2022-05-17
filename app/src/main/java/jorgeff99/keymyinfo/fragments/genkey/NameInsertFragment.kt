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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application

/*
* The main objective of this fragment is implement the logic for the name insert in the tutorial
* screen
*/

class NameInsertFragment : Fragment() {
    private lateinit var nameInsert: TextInputLayout
    private lateinit var insertNameButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root: View = inflater.inflate(R.layout.fragment_name_insert, container, false)
        nameInsert = root.findViewById(R.id.insertNameText)
        insertNameButton = root.findViewById(R.id.insertNameButton)

        insertNameButton.setOnClickListener {
            // Check if the name for the user inserted is not empty
            if(nameInsert.editText?.text.toString().isNotEmpty()){
                Application.sharedPreferences.saveName(nameInsert.editText?.text.toString())
                Application.sharedPreferences.saveFingerPrint(0)
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                val fragment = LoadingGenKeyFragment()
                transaction?.replace(R.id.fragment_container, fragment)
                transaction?.commit()
            }

            else{
                Snackbar.make(root, R.string.errorInsertedNameEmpty, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
        return root
    }

}
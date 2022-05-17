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

package jorgeff99.keymyinfo.fragments.operation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.adapter.ContactAdapter
import jorgeff99.keymyinfo.common.Application
import kotlinx.android.synthetic.main.fragment_operation_button.*


class EditTextFragment : Fragment() {
    private lateinit var editTextMessage: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get the arguments from the previous fragments
        var selectedOptionsOriginal: ArrayList<String> = (requireArguments().getStringArrayList("selectedOptions"))!!.clone() as ArrayList<String>

        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_edit_text, container, false)

        // Blank all the fragment containers no needed in this step
        Application().replaceBlankFragment(requireActivity(), selectedOptionsOriginal.size)

        // Update the progress bar
        Application().replaceProgressBar(requireActivity(),selectedOptionsOriginal)

        editTextMessage = root.findViewById(R.id.editTextOperation)
        editTextMessage.doOnTextChanged { text, start, count, after ->
            val bundle = Bundle()

            // Shadow copy of the list argument passed to the fragment
            var selectedOptions: ArrayList<String> = selectedOptionsOriginal.clone() as ArrayList<String>

            // Add the text introduced to the list in order to pass it as argument
            selectedOptions.add(text.toString())
            bundle.putStringArrayList("selectedOptions", selectedOptions)

            // Depending of the operation selected, the next fragment will be replaced
            val fragment = whichFragment(selectedOptionsOriginal[0])
            fragment.arguments = bundle

            replaceFragment(fragment, R.id.fragment_container2)
        }
        return root
    }

    // Return the next fragment
    private fun whichFragment(operation: String): Fragment {
        // If Encrypt has been selected, the next operation needs to be the selection of which key to encrypt
        if(resources.getString(R.string.encrypt) in operation){
            return EncryptWhichKeyFragment()
        }
        // If Check Sign has been selected, the next operation needs to be the selection of which key to check sign
        if(resources.getString(R.string.checkSign) in operation){
            return SignWhichKeyFragment()
        }
        // Finish button in another case
        return OperationButtonFragment()
    }

    // Replace fragment logic, in order to avoid repetition
    private fun replaceFragment(fragment: Fragment, container: Int){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(container, fragment)
        transaction?.commit()
    }

}
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
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application
import jorgeff99.keymyinfo.fragments.genkey.PinInsertFragment

class OperationButtonFragment : Fragment() {

    private var selectedOptionsOriginal: ArrayList<String> = ArrayList<String>()
    private lateinit var operationButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_operation_button, container, false)

        // Get the seletedOptions of previous fragments
        selectedOptionsOriginal = (requireArguments()!!.getStringArrayList("selectedOptions"))!!

        // Replace with blank fragments those container no required
        Application().replaceBlankFragment(requireActivity(), selectedOptionsOriginal.size)

        // Update progress bar
        Application().replaceProgressBar(requireActivity(),selectedOptionsOriginal)

        operationButton = root.findViewById(R.id.operationButton)
        operationButton.text = selectedOptionsOriginal[0]

        operationButton.setOnClickListener {
            //Ask for pin insert
            val fragment = PinInsertFragment.newInstance(2, selectedOptionsOriginal)
            replaceFragment(fragment, R.id.operation)
        }

        return root
    }

    // Replace fragment logic
    private fun replaceFragment(fragment: Fragment, container: Int){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(container, fragment)
        transaction?.commit()
    }

}
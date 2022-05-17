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
import android.widget.TextView
import com.google.android.material.chip.ChipGroup
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application

class TypeOperationFragment : Fragment() {
    private lateinit var selectedOptionsOriginal: ArrayList<String>
    private lateinit var title: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_type_operation, container, false)
        selectedOptionsOriginal = (requireArguments()!!.getStringArrayList("selectedOptions"))!!.clone() as ArrayList<String>
        Application().replaceBlankFragment(requireActivity(),selectedOptionsOriginal.size)

        title = root.findViewById(R.id.whichTypeTitle)
        checkOperation(selectedOptionsOriginal[0])

        val chipGroup: ChipGroup = root.findViewById(R.id.chipGroupTypeOperation)
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            replaceFrameLayoutOperation(checkedId)
        }

        Application().replaceProgressBar(requireActivity(),selectedOptionsOriginal)

        return root
    }

    private fun checkOperation(operation: String?) {
        when (operation) {
            resources.getString(R.string.encrypt) -> {
                title.text = getString(R.string.typeEncryptQuestion)
            }
            resources.getString(R.string.sign) -> {
                title.text = getString(R.string.typeSignQuestion)
            }
            else -> {
                title.text = getString(R.string.typeEncryptSignQuestion)
            }
        }
    }

    private fun replaceFrameLayoutOperation(checkedId: Int?) {
        val bundle = Bundle()

        when (checkedId) {
            R.id.chipTypeText -> {
                val selectedOptions: ArrayList<String> = selectedOptionsOriginal.clone() as ArrayList<String>
                selectedOptions.add("text")
                bundle.putStringArrayList("selectedOptions", selectedOptions)
                val fragment = EditTextFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.fragment_container1)
            }
            R.id.chipTypePhoto -> {
                val selectedOptions: ArrayList<String> = selectedOptionsOriginal.clone() as ArrayList<String>
                selectedOptions.add("photoVideo")
                bundle.putStringArrayList("selectedOptions", selectedOptions)
                val fragment = AddButtonFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.fragment_container1)
            }
            else -> {
                val selectedOptions: ArrayList<String> = selectedOptionsOriginal.clone() as ArrayList<String>
                selectedOptions.add("file")
                bundle.putStringArrayList("selectedOptions", selectedOptions)
                val fragment = AddButtonFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.fragment_container1)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, container: Int){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(container, fragment)
        transaction?.commit()
    }


}
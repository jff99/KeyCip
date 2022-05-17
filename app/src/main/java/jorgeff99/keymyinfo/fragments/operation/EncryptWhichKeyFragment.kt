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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.ChipGroup
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application

class EncryptWhichKeyFragment : Fragment() {

    private lateinit var selectedOptionsOriginal: ArrayList<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_encrypt_which_key, container, false)

        // Get the arguments from the previous fragments
        selectedOptionsOriginal = (requireArguments()!!.getStringArrayList("selectedOptions"))!!.clone() as ArrayList<String>

        // Blank all the fragment containers no needed in this step
        Application().replaceBlankFragment(requireActivity(), selectedOptionsOriginal.size)

        // Add the help dialog at the end of the page
        val bundle = Bundle()
        bundle.putStringArrayList("selectedOptions",  selectedOptionsOriginal)
        var fragment = GuideOperationFragment()
        fragment.arguments = bundle
        replaceFragment(fragment, R.id.fragment_container3)

        // Update progress bar
        Application().replaceProgressBar(requireActivity(),selectedOptionsOriginal)

        // Listener for chip group in order to select whether encrypt using user's key or contact key
        val chipGroup: ChipGroup = root.findViewById(R.id.chipGroupWhichKey)
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            replaceFrameLayoutOperation(checkedId)
        }
        return root
    }

    /*
    Depending on which key method has been selected
    */
    private fun replaceFrameLayoutOperation(checkedId: Int?) {
        val bundle = Bundle()
        val selectedOptions: ArrayList<String> = selectedOptionsOriginal.clone() as ArrayList<String>
        when (checkedId) {
            // Encrypt using user's key, next fragment is the operation button
            R.id.chipEncryptForMe -> {
                selectedOptions.add("me")
                bundle.putStringArrayList("selectedOptions", selectedOptions)
                val fragment = OperationButtonFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.fragment_container2bis)
            }

            // Encrypt using contact's key, next fragment is the contaqct selector
            else -> {
                selectedOptions.add("specific")
                bundle.putStringArrayList("selectedOptions", selectedOptions)
                val fragment = SpinnerFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.fragment_container2bis)
            }
        }
    }

    // Replace fragment logic, in order to avoid repetition
    private fun replaceFragment(fragment: Fragment, container: Int){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(container, fragment)
        transaction?.commit()
    }

}
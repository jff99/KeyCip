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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.chip.ChipGroup
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application
import jorgeff99.keymyinfo.fragments.EncryptFragment
import jorgeff99.keymyinfo.fragments.genkey.NameInsertFragment


class OperationLayoutFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_operation_layout, container, false)

        // Replace no needed fragment for this section
        Application().replaceBlankFragment(requireActivity(), 0)

        //Listener for chip
        val chipGroup: ChipGroup = root.findViewById(R.id.chipGroupOperation)
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            replaceFrameLayoutOperation(checkedId)
        }

        // Add guide notes to help user
        val bundle = Bundle()
        bundle.putStringArrayList("selectedOptions",  ArrayList<String>())
        var fragment = GuideOperationFragment()
        fragment.arguments = bundle
        replaceFragment(fragment, R.id.fragment_container3)

        // Update progress bar
        var fragmentProgressBar = ProgressBarFragment()
        fragmentProgressBar.arguments = bundle
        replaceFragment(fragmentProgressBar, R.id.fragment_container4)

        return root
    }


    private fun replaceFrameLayoutOperation(checkedId: Int?) {
        val selectedOptions: ArrayList<String> = ArrayList<String>()
        val bundle = Bundle()

        when (checkedId) {
            R.id.chipEncrypt -> {
                selectedOptions.add(resources.getString(R.string.encrypt))
                bundle.putStringArrayList("selectedOptions", selectedOptions)
                val fragment = TypeOperationFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.typeOperation)
            }
            R.id.chipDecrypt -> {
                selectedOptions.add(resources.getString(R.string.decrypt))
                bundle.putStringArrayList("selectedOptions", selectedOptions)
                val fragment = TextZipFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.typeOperation)
            }
            R.id.chipSign -> {
                selectedOptions.add(resources.getString(R.string.sign))
                bundle.putStringArrayList("selectedOptions", selectedOptions)
                val fragment = TypeOperationFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.typeOperation)
            }
            R.id.chipSignEncrypt -> {
                selectedOptions.add("Cifrar y firmar")
                bundle.putStringArrayList("selectedOptions", selectedOptions)
                val fragment = TypeOperationFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.typeOperation)
            }
            else -> {
                selectedOptions.add(resources.getString(R.string.checkSign))
                bundle.putStringArrayList("selectedOptions", selectedOptions)
                val fragment = TextZipFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.typeOperation)
            }
        }
    }

    // Replace fragment logic
    private fun replaceFragment(fragment: Fragment, container: Int){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(container, fragment)
        transaction?.commit()
    }



}
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application

class SpinnerFragment : Fragment() {
    private lateinit var selectedOptionsOriginal: ArrayList<String>
    private lateinit var spinner: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_spinner, container, false)

        // Get arguments of previous fragments
        selectedOptionsOriginal = (requireArguments()!!.getStringArrayList("selectedOptions"))!!.clone() as ArrayList<String>

        //Replace blank fragments the containers no needed
        Application().replaceBlankFragment(requireActivity(), selectedOptionsOriginal.size)

        //Update progress bar
        Application().replaceProgressBar(requireActivity(),selectedOptionsOriginal)

        val contactList = Application.contactDatabase.getContactDao().getContacts()
        spinner = root.findViewById(R.id.spinnerContact)
        val contactNameList = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        contactNameList.add(resources.getString(R.string.selectContact))
        for (element in contactList){
            contactNameList.add(element.name)
        }

        if(selectedOptionsOriginal[0] == resources.getString(R.string.checkSign)){
            contactNameList.add(resources.getString(R.string.me))
        }

        spinner.adapter = contactNameList
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(spinner.selectedItem.toString() != resources.getString(R.string.selectContact)){
                    val bundle = Bundle()
                    val selectedOptions: ArrayList<String> = selectedOptionsOriginal.clone() as ArrayList<String>
                    selectedOptions.add(spinner.selectedItem.toString())
                    bundle.putStringArrayList("selectedOptions", selectedOptions)
                    val fragment = OperationButtonFragment()
                    fragment.arguments = bundle
                    replaceFragment(fragment, R.id.fragment_container2bis2)
                }
            }
        }
        return root
    }

    private fun replaceFragment(fragment: Fragment, container: Int){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(container, fragment)
        transaction?.commit()
    }

}
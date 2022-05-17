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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application


class AddButtonFragment : Fragment() {

    private lateinit var selectedOptionsOriginal: ArrayList<String>
    private lateinit var addedName: TextView
    private lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_add_button, container, false)

        // Get the arguments from the previous fragments
        selectedOptionsOriginal = (requireArguments()!!.getStringArrayList("selectedOptions"))!!.clone() as ArrayList<String>

        // Blank all the fragment containers no needed in this step
        Application().replaceBlankFragment(requireActivity(), selectedOptionsOriginal.size)

        // Update the progress bar
        Application().replaceProgressBar(requireActivity(),selectedOptionsOriginal)

        addedName = root.findViewById(R.id.fileNameAdded)
        val addButton: MaterialButton = root.findViewById(R.id.addFileButton)

        // File Explorer listener
        addButton.setOnClickListener {
            startFileChooser(selectedOptionsOriginal[1])
        }

        return root
    }

    //  File Chooser logic
    private var fileChooser = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        // Error during file chooser
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data!!.data!!
                Toast.makeText(
                    this.context,
                    resources.getString(R.string.addedFile),
                    Toast.LENGTH_LONG
                ).show()

            addedName.text = resources.getString(R.string.added)
            replaceFrameLayoutOperation(result.data!!.data!!.toString())
        }

        // Successfully added file
        else{
            Toast.makeText(
                this.context,
                resources.getString(R.string.addedError),
                Toast.LENGTH_LONG
            ).show()

            addedName.text = ""
        }
    }

    /*
        Depending on the operation selected (Encrypt, decrypt or sign), replaces the next fragment container
    */
    private fun replaceFrameLayoutOperation(fileUri: String) {
        val bundle = Bundle()
        val selectedOptions: ArrayList<String> = selectedOptionsOriginal.clone() as ArrayList<String>
        selectedOptions.add(fileUri)
        bundle.putStringArrayList("selectedOptions", selectedOptions)

        when {
            resources.getString(R.string.encrypt) in selectedOptionsOriginal[0] -> {
                val fragment = EncryptWhichKeyFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.fragment_container2)
            }
            resources.getString(R.string.sign) in selectedOptionsOriginal[0] || resources.getString(R.string.decrypt) in selectedOptionsOriginal[0] -> {
                val fragment = OperationButtonFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.fragment_container2)
            }
            else -> {
                val fragment = SignWhichKeyFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, R.id.fragment_container2)
            }
        }

    }

    // Replace fragment logic, in order to avoid repetition
    private fun replaceFragment(fragment: Fragment, container: Int){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(container, fragment)
        transaction?.commit()
    }

    // Start file chooser
    private fun startFileChooser(type: String){
        val intent = Intent()
        intent.type = checkTypeIntent(type)
        intent.action = Intent.ACTION_GET_CONTENT
        fileChooser.launch(intent)
    }

    // MIME type transformation for type file chooser
    private fun checkTypeIntent(type: String): String{
        if(type == "photoVideo"){
            return "image/*|video/*"
        }
        if(type == "zip"){
            return "application/zip"
        }
        return "*/*"
    }

}
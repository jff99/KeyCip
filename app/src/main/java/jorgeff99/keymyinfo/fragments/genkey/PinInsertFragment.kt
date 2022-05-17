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
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application.Companion.autenticator
import jorgeff99.keymyinfo.common.Application.Companion.sharedPreferences
import jorgeff99.keymyinfo.fragments.operation.BlankFragment
import java.util.concurrent.Executor
import jorgeff99.keymyinfo.common.Application
import jorgeff99.keymyinfo.fragments.operation.LoadingOperation


private const val ARG_PARAM1 = "time"
private const val ARG_PARAM2 = "selectedOptions"

class PinInsertFragment : Fragment() {
    private var time: Int? = null
    private var selectedOptions: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            time = it.getInt(ARG_PARAM1)
            selectedOptions = it.getStringArrayList(ARG_PARAM2)
        }
    }
    private lateinit var insertPinTitle: TextView
    private lateinit var insertPinExplanation: TextView
    private lateinit var insertPinButton: MaterialButton
    private lateinit var editPassword: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_pin_insert, container, false)
        insertPinTitle = root.findViewById(R.id.insertPinTitle)
        insertPinExplanation = root.findViewById(R.id.insertPinExplanation)
        insertPinButton = root.findViewById(R.id.insertPinButton)
        editPassword = root.findViewById(R.id.insertPinText)
        when (time) {
            0 -> {
                insertPinExplanation.text = getString(R.string.insertPin)
                insertPinButton.setOnClickListener {
                    if (checkInsertedPIN(editPassword.editText?.text.toString(), null, root) == 0) {
                        sharedPreferences.savePin(editPassword.editText?.text.toString())
                        transaction(root, 1, true)
                    }
                }
            }
            1 -> {
                insertPinExplanation.text = getString(R.string.confirmPin)
                insertPinButton.setOnClickListener {
                    if (checkInsertedPIN(
                            editPassword.editText?.text.toString(),
                            sharedPreferences.getPin(),
                            root
                        ) == 0
                    ) {
                        if (editPassword.editText?.text.toString() == sharedPreferences.getPin()) {
                            if (autenticator.checkFingerprint(root) == 0) {
                                MaterialAlertDialogBuilder(root.context)
                                    .setTitle(resources.getString(R.string.fingerprintTitle))
                                    .setMessage(resources.getString(R.string.fingerprintExplanatioon))
                                    .setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
                                        // Respond to negative button press
                                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                                        val fragment = NameInsertFragment()
                                        transaction?.replace(R.id.fragment_container, fragment)
                                        transaction?.commit()
                                    }
                                    .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                                        // Respond to positive button press
                                        autenticator.showFingerprint(root, this, null, null)
                                    }
                                    .show()

                            } else {
                                Toast.makeText(root.context,
                                    "Pin insertado", Toast.LENGTH_SHORT)
                                    .show()
                                sharedPreferences.saveFingerPrint(-1)
                                val transaction = activity?.supportFragmentManager?.beginTransaction()
                                val fragment = NameInsertFragment()
                                transaction?.replace(R.id.fragment_container, fragment)
                                transaction?.commit()
                            }

                        } else {
                            transaction(root, 0, true)
                            Snackbar.make(
                                root,
                                R.string.errorInsertedPinDifferent,
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
            2->{
                insertPinTitle.text = resources.getString(R.string.insertPinOperation)
                Application().replaceBlankFragment(requireActivity(), 0)
                insertPinExplanation.text = getString(R.string.insertPinOperation)
                val bundle = Bundle()
                bundle.putStringArrayList("selectedOptions", selectedOptions)

                if(sharedPreferences.getFingerPrint() == 0){
                    autenticator.showFingerprint(root, this, null, selectedOptions)
                }

                insertPinButton.setOnClickListener {
                    if (checkInsertedPIN(
                            editPassword.editText?.text.toString(),
                            sharedPreferences.getPin(),
                            root
                        ) == 0
                    ) {

                        if (editPassword.editText?.text.toString() == sharedPreferences.getPin()){
                            val transaction = activity?.supportFragmentManager?.beginTransaction()
                            val fragment = LoadingOperation()
                            fragment.arguments = bundle
                            transaction?.replace(R.id.operation, fragment)
                            transaction?.commit()
                        }

                        else {
                            Snackbar.make(
                                root,
                                R.string.errorInsertedPinDifferent,
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }

        }

        return root
    }

    private fun checkInsertedPIN(insertedPin: String, expectedPin: String?, root: View): Int {
        return if (insertedPin.isEmpty()) {
            Snackbar.make(root, R.string.errorInsertedPinEmpty, Snackbar.LENGTH_SHORT)
                .show()
            -1
        } else if (insertedPin.length != 5) {
            Snackbar.make(root, R.string.errorInsertedPin, Snackbar.LENGTH_SHORT)
                .show()
            -1
        } else {
            0
        }
    }

    private fun transaction(root: View, time: Int, goBack: Boolean) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val fragment = PinInsertFragment.newInstance(time, null)
        transaction?.replace(R.id.fragment_container, fragment)
        if (goBack) {
            transaction?.addToBackStack(null)
        }
        transaction?.commit()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param time Parameter 1.
         * @return A new instance of fragment PinInsertFragment.
         */
        @JvmStatic
        fun newInstance(time: Int, selectedOptions: ArrayList<String>?) =
            PinInsertFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, time)
                    putStringArrayList(ARG_PARAM2, selectedOptions)
                }
            }
    }
}
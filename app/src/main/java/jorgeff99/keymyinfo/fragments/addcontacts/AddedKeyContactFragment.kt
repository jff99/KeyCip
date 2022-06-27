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

package jorgeff99.keymyinfo.fragments.addcontacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application.Companion.contactDatabase
import jorgeff99.keymyinfo.common.RSA
import jorgeff99.keymyinfo.data.database.entities.ContactEntity
import jorgeff99.keymyinfo.fragments.HomeFragment
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class AddedKeyContactFragment : Fragment() {

    // Name of the contact to insert
    private lateinit var nameInserted: EditText

    // Button to add contact
    private lateinit var addContactButton: MaterialButton

    // Button to cancel adding contact button
    private lateinit var cancelAddContact: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_added_key_contact, container, false)

        //Get the key passed as an argument
        val bundle = this.arguments
        val pathPublicKey = bundle!!.getString("publicKey", "")

        // Find both nameInsert (Edit Text) and addContact (Button)
        nameInserted = root.findViewById(R.id.nameInsert)
        addContactButton = root.findViewById(R.id.addContactButton)

        addContactButton.setOnClickListener {
            //Check if the name inserted is equal to me (not allowed)
            if(nameInserted.text.toString().lowercase() == context?.resources?.getString(R.string.me).toString().lowercase()){
                Toast.makeText(
                    this.context,
                    context?.resources?.getString(R.string.meContact),
                    Toast.LENGTH_LONG
                ).show()
            }
            // Case in which user has not entered anything
            else if (nameInserted.text.toString().isEmpty()) {
                Toast.makeText(
                    this.context,
                    context?.resources?.getString(R.string.nameLimitationSize),
                    Toast.LENGTH_LONG
                ).show()
            }
            else{
                // Get the list of user whose name is the one inserted by the user
                val contactList =
                    contactDatabase.getContactDao().getContactsByName(nameInserted.text.toString())
                // If it is empty, it means that there is already a contact whose name is the one inserted (do not add the contact)
                if (contactList.isNotEmpty()) {
                    val nameContact = contactList[0].name
                    Toast.makeText(
                        this.context,
                        "${context?.resources?.getString(R.string.repeatedKey)} $nameContact",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    // Save current day in order to save it in the Files Database
                    val currentTime: Date = Calendar.getInstance().time
                    val formatDate: DateFormat = SimpleDateFormat("dd/MM/yyyy")

                    val ruta = (requireContext().filesDir).toString()

                    val insertedContact = ContactEntity(
                        nameInserted.text.toString(),
                        "$ruta/keysContact/${nameInserted.text.toString().lowercase()}.cer",
                        false,
                        formatDate.format(currentTime.time)
                    )

                    //convert the string given to key
                    RSA().fromStringToPublicKey(
                        root.context,
                        pathPublicKey,
                        nameInserted.text.toString().lowercase()
                    )

                    lifecycleScope.launch {
                        contactDatabase.getContactDao().insertContact(insertedContact)
                    }

                    Toast.makeText(
                        this.context,
                        context?.resources?.getString(R.string.addedContact),
                        Toast.LENGTH_LONG
                    ).show()

                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    val homeFragment = HomeFragment()
                    transaction?.replace(R.id.fragment_container, homeFragment)
                    transaction?.commit()
                }
            }
        }

        cancelAddContact = root.findViewById(R.id.cancelButton)
        cancelAddContact.setOnClickListener {
            // Return to Home Fragment
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val homeFragment = HomeFragment()
            transaction?.replace(R.id.fragment_container, homeFragment)
            transaction?.commit()
        }

        return root
    }


}
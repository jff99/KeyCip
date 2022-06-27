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


package jorgeff99.keymyinfo.fragments.editcontacts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application
import jorgeff99.keymyinfo.common.RSA
import jorgeff99.keymyinfo.data.database.entities.ContactEntity
import jorgeff99.keymyinfo.fragments.HomeFragment
import jorgeff99.keymyinfo.fragments.addcontacts.AddedKeyContactFragment
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/*
* The main objective of this class is define the operations in order to edit a contact
* when individual contact information is displayed
*/

class EditContactsFragment : Fragment() {

    // Edit contact where the user enters the new contact name
    private lateinit var contactName: EditText

    // Button in order to change the public key
    private lateinit var changePublicKey: MaterialButton

    // Button in order to confirm changes of the contact
    private lateinit var confirmButton: MaterialButton

    // Button in order to cancel changes of the contact
    private lateinit var cancelButton: MaterialButton

    // Dialog in order to scan QR or add manually public key
    private lateinit var changeContactSelector: BottomSheetDialog
    private lateinit var root: View

    // Variables used in order to fin out whether any of the fields have changed
    private var changedKey = ""
    private var nameIntent: String? = ""
    private var dateIntent: String? = ""
    private var pathPublicKey: String? = ""

    private var performedChanges = false

    // Launch of the camera in order to scan QR for public key
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(
                this.context,
                context?.resources?.getString(R.string.cancelScan),
                Toast.LENGTH_LONG
            ).show()
        } else {

            if (checkScanned(result.contents) == 0) {
                Toast.makeText(
                    this.context,
                    context?.resources?.getString(R.string.scanSuccesful),
                    Toast.LENGTH_LONG
                ).show()
                changedKey = result.contents
                changeContactSelector.dismiss()

            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit_contacts, container, false)

        // Get original contact data (before the changes occur) as arguments passed
        nameIntent = arguments?.getString("name")
        dateIntent = arguments?.getString("date")
        pathPublicKey = arguments?.getString("pathPublic")

        // Find Edit Text for the new name of the contact
        contactName = root.findViewById(R.id.contactName)

        // Defines as hint the original name
        contactName.hint = nameIntent

        // Listener for Button to confirm changes
        changePublicKey = root.findViewById(R.id.changePublicKey)
        changePublicKey.setOnClickListener {

            // Create addContact selector
            changeContactSelector = activity?.let { it1 -> BottomSheetDialog(it1) }!!
            // Inflate layout
            val viewAddContact = layoutInflater.inflate(R.layout.bottom_sheet_add_contact, null)

            // Logic in order to select how to add the new public Key
            if (changeContactSelector != null) {
                val btnCloseAddContactSelector: ImageButton =
                    viewAddContact.findViewById(R.id.closeSheet)
                btnCloseAddContactSelector.setOnClickListener {
                    changeContactSelector.dismiss()
                }

                val btnCamaraSelector: MaterialButton =
                    viewAddContact.findViewById(R.id.addContactCamera)
                btnCamaraSelector.setOnClickListener {
                    barcodeLauncher.launch(ScanOptions())
                }

                val btnManual: MaterialButton = viewAddContact.findViewById(R.id.addContactManual)
                btnManual.setOnClickListener {
                    startKeyChooser()
                }

                changeContactSelector.setContentView(viewAddContact)
                changeContactSelector.show()
            }
        }

        // Confirm Button listener
        confirmButton = root.findViewById(R.id.confirmEditContactButton)
        confirmButton.setOnClickListener {

            var errorName = false

            if(contactName.text.toString()
                    .isNotEmpty() && contactName.text.toString() != nameIntent && contactName.text.toString().lowercase() == context?.resources?.getString(R.string.me).toString().lowercase()){
                Toast.makeText(
                    this.context,
                    context?.resources?.getString(R.string.meContact),
                    Toast.LENGTH_LONG
                ).show()
            }

            else {
                // Check if a new key  has been inserted
                if (changedKey.isNotEmpty()) {
                    RSA().fromStringToPublicKey(root.context, changedKey, nameIntent!!)
                    performedChanges = true
                }

                // Check if a name has been inserted and is different to the original name
                if (contactName.text.toString()
                        .isNotEmpty() && contactName.text.toString() != nameIntent
                ) {
                    // Call to changeName in order to check if the new name is correct
                    errorName = changeName(contactName.text.toString())
                    performedChanges = true
                }

                if (performedChanges) {
                    //Update the last day modified
                    val currentTime: Date = Calendar.getInstance().time
                    val formatDate: DateFormat = SimpleDateFormat("dd/MM/yyyy")

                    lifecycleScope.launch {
                        //Update the information in the contact database
                        Application.contactDatabase.getContactDao()
                            .updateDate(
                                formatDate.format(currentTime.time).toString(),
                                contactName.text.toString()
                            )
                    }
                }

                if (!errorName) {
                    Toast.makeText(
                        this.context,
                        context?.resources?.getString(R.string.savedContact),
                        Toast.LENGTH_LONG
                    ).show()
                    returnHome()
                }
            }
        }

        // Cancel button listener
        cancelButton = root.findViewById(R.id.cancelEditContactButton)
        cancelButton.setOnClickListener {

            // Check whether information has been inserted (confirm dialog)
            if (changedKey.isNotEmpty() || contactName.text.toString().isNotEmpty()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.editContactConfirmation))
                    .setMessage(resources.getString(R.string.supportingEditContactConfirmation))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.continueButton)) { dialog, which ->
                        returnHome()
                        Toast.makeText(
                            this.context,
                            resources.getString(R.string.changesNotSaved),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .show()
            } else {
                returnHome()
            }

        }

        return root
    }

    /*
    Checks if the QR scanned contains valid key. Returns -1 in case it does not contain a valid key
    and 0 in another case
    */
    private fun checkScanned(scanned: String): Int {
        // Check if BEGIN RSA PUBLIC KEY and END RSA PUBLIC KEY are contained
        if (!scanned.contains("-----BEGIN RSA PUBLIC KEY-----\n") || !scanned.contains("\n-----END RSA PUBLIC KEY-----\n") || scanned.length != 453) {
            Toast.makeText(
                this.context,
                resources.getString(R.string.incorrectQR),
                Toast.LENGTH_LONG
            ).show()
            return -1
        }

        // Get the path to the app directory
        val ruta = (requireContext().filesDir).toString()

        // Compare the scanned key with the users one (avoid adding the same key)
        if (scanned == RSA().getStringPublicKey(root.context, "$ruta/keys/publicRSA.cer")) {
            Toast.makeText(
                this.context,
                resources.getString(R.string.errorYourKey),
                Toast.LENGTH_LONG
            ).show()
            return -1
        }

        val contactList = Application.contactDatabase.getContactDao().getContacts()
        for (contact in contactList) {
            if (RSA().getStringPublicKey(context, contact.pathPublicKey) == scanned) {
                Toast.makeText(
                    requireContext(),
                    "${resources.getString(R.string.errorContactKeyOne)} " + contact.name + " ${
                        resources.getString(
                            R.string.errorContactKeyTwo
                        )
                    }",
                    Toast.LENGTH_LONG
                ).show()

                return -1
            }
        }

        return 0
    }

    /*
    Start the File Explorer intent
    */
    private fun startKeyChooser() {
        var intent = Intent()
        intent.setType("*/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        fileChooser.launch(intent)
    }

    // File Explorer logic in order to determine if the file selected is corrected
    private var fileChooser = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {

            // Get the extension of the selected file
            val fileInfo = DocumentFile.fromSingleUri(root.context, result.data!!.data!!)

            // Public key format need to be .cer
            if (!(fileInfo?.name?.contains(".cer"))!!) {
                Toast.makeText(
                    this.context,
                    resources.getString(R.string.errorFormatKey),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val keyBytes =
                    activity?.contentResolver?.openInputStream(result.data!!.data!!)?.readBytes()
                val publicKey = RSA().getStringPublicKeyBytes(root.context, keyBytes!!)
                if (checkScanned(publicKey) == 0) {
                    Toast.makeText(
                        this.context,
                        resources.getString(R.string.correctKeyAdded),
                        Toast.LENGTH_LONG
                    ).show()

                    changedKey = publicKey
                    changeContactSelector.dismiss()
                }
            }
        }
    }

    /*
    Replaces fragment in order to go back to Home Fragment
    */
    private fun returnHome() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val fragmentHome = HomeFragment()
        transaction?.replace(R.id.fragment_container, fragmentHome)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    /*
    Return true the new name is correct (is no repeated), -1 in another false
    */
    private fun changeName(insertedName: String): Boolean {

        // Get the list of the ncontact whose name is the one inserted by the user
        val contactList =
            Application.contactDatabase.getContactDao().getContactsByName(insertedName)

        // If list is empty means that name is not repeated (update contact)
        if (contactList.isNotEmpty()) {
            val nameContact = contactList[0].name
            Toast.makeText(
                this.context,
                "${resources.getString(R.string.repeatedKey)} $nameContact",
                Toast.LENGTH_LONG
            ).show()

            return true
        } else {
            performedChanges = true
            lifecycleScope.launch {
                //Update the name in the Contact Database
                Application.contactDatabase.getContactDao()
                    .updateName(contactName.text.toString(), nameIntent!!)
                Application.contactDatabase.getContactDao().updateKey(
                    pathPublicKey!!.replace(
                        nameIntent!!.lowercase(),
                        contactName.text.toString().lowercase()
                    ), contactName.text.toString()
                )
                // Rename the actual file name containing the key of the contact
                File(pathPublicKey).renameTo(
                    File(
                        pathPublicKey!!.replace(
                            nameIntent!!.lowercase(),
                            contactName.text.toString().lowercase()
                        )
                    )
                )
            }

            return false
        }

    }

}
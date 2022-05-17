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

package jorgeff99.keymyinfo.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.adapter.ContactAdapter
import jorgeff99.keymyinfo.common.Application
import jorgeff99.keymyinfo.common.Application.Companion.sharedPreferences
import jorgeff99.keymyinfo.common.RSA
import jorgeff99.keymyinfo.fragments.addcontacts.AddedKeyContactFragment
import jorgeff99.keymyinfo.fragments.addcontacts.QrViewFragment
import java.io.File
import java.io.FileInputStream


class HomeFragment : Fragment() {
    private lateinit var nameCard: TextView
    private lateinit var btnSendKey: MaterialButton
    private lateinit var btnGenerateNewKey: MaterialButton
    private lateinit var btnAddContact: MaterialButton
    private lateinit var qrCode: ImageView
    private lateinit var searchBar: EditText
    private lateinit var root: View
    private lateinit var addContactSelector:BottomSheetDialog

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this.context, resources.getString(R.string.cancelScan), Toast.LENGTH_LONG).show()
        } else {

            if(checkScanned(result.contents) == 0){
                Toast.makeText(
                    this.context,
                    resources.getString(R.string.correctScan),
                    Toast.LENGTH_LONG
                ).show()
                val arguments = Bundle()
                arguments.putString("publicKey", result.contents)

                addContactSelector.dismiss()
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                val fragmentAddedContact = AddedKeyContactFragment()
                fragmentAddedContact.setArguments(arguments)
                transaction?.addToBackStack("")
                transaction?.replace(R.id.fragment_container,fragmentAddedContact)

                transaction?.commit()
            }
        }
    }

    private var fileChooser = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val fileInfo = DocumentFile.fromSingleUri(root.context, result.data!!.data!!)
            if(!(fileInfo?.name?.contains(".cer"))!!){
                Toast.makeText(
                    this.context,
                    resources.getString(R.string.selectCer),
                    Toast.LENGTH_LONG
                ).show()
            }

            else{
                val keyBytes = activity?.contentResolver?.openInputStream(result.data!!.data!!)?.readBytes()
                val publicKey = RSA().getStringPublicKeyBytes(root.context, keyBytes!!)
                if(checkScanned(publicKey) == 0){
                    Toast.makeText(
                        this.context,
                        resources.getString(R.string.correctKeyAdded),
                        Toast.LENGTH_LONG
                    ).show()
                    val arguments = Bundle()
                    arguments.putString("publicKey", publicKey)

                    addContactSelector.dismiss()
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    val fragmentAddedContact = AddedKeyContactFragment()
                    fragmentAddedContact.setArguments(arguments)
                    transaction?.addToBackStack("")
                    transaction?.replace(R.id.fragment_container,fragmentAddedContact)

                    transaction?.commit()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false)

        nameCard = root.findViewById(R.id.nameCard)
        nameCard.text = "${resources.getString(R.string.name)} " + sharedPreferences.getName()

        qrCode = root.findViewById(R.id.QRCode)
        qrCode.setImageBitmap(RSA().getBitMap(root.context, 103))
        qrCode.setOnClickListener{
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragmentShowQR = QrViewFragment()
            transaction?.replace(R.id.fragment_container,fragmentShowQR)
            transaction?.addToBackStack("")
            transaction?.commit()
        }

        btnSendKey = root.findViewById(R.id.sendKey)
        btnSendKey.setOnClickListener {
            val publicKeyPath: File = File(root.context.filesDir, "keys")
            val publicKeyFile = File(publicKeyPath, "publicRSA.cer")

            val shareIntent = Intent().apply {
                this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, R.string.shareKeyDialog)
                this.putExtra(Intent.EXTRA_STREAM,  FileProvider.getUriForFile(requireContext(),context?.packageName + ".fileprovider", File(requireContext().filesDir.toString() + "/keys/publicRSA.cer")))
                this.type = "*/*"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.shareKeyDialog)))
        }

        btnGenerateNewKey = root.findViewById(R.id.generateNewKey)
        btnGenerateNewKey.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragmentNewKey = GenerateNewKeysFragment()
            transaction?.replace(R.id.fragment_container,fragmentNewKey)
            transaction?.commit()
        }

        btnAddContact = root.findViewById(R.id.qrAddContact)
        btnAddContact.setOnClickListener {
            // Create addContact selector
            addContactSelector = activity?.let { it1 -> BottomSheetDialog(it1) }!!
            // Inflate layout
            val viewAddContact = layoutInflater.inflate(R.layout.bottom_sheet_add_contact, null)

            if (addContactSelector != null) {
                val btnCloseAddContactSelector:ImageButton = viewAddContact.findViewById(R.id.closeSheet)
                btnCloseAddContactSelector.setOnClickListener {
                    addContactSelector.dismiss()
                }

                val btnCamaraSelector:MaterialButton = viewAddContact.findViewById(R.id.addContactCamera)
                btnCamaraSelector.setOnClickListener {
                    barcodeLauncher.launch(ScanOptions())
                }

                val btnManual:MaterialButton = viewAddContact.findViewById(R.id.addContactManual)
                btnManual.setOnClickListener {
                    startKeyChooser()
                }

                addContactSelector.setContentView(viewAddContact)
                addContactSelector.show()
            }
        }

        var contactList = Application.contactDatabase.getContactDao().getContacts()

        val listView: ListView = root.findViewById(R.id.contactList)
        var adapter = ContactAdapter(requireActivity(), ArrayList(contactList))
        listView.adapter = adapter

        val nameContact: ListView = root.findViewById(R.id.contactList)
        nameContact.setOnItemClickListener{ parent, view, position, id->

            val bundle = Bundle()
            bundle.putString("name", contactList[position].name)
            bundle.putString("date", contactList[position].dateModified)
            bundle.putBoolean("favourite", contactList[position].favourite)
            bundle.putString("pathPublic", contactList[position].pathPublicKey)

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragmentIndividualContact = IndividualContactFragment()
            fragmentIndividualContact.arguments = bundle
            transaction?.replace(R.id.fragment_container,fragmentIndividualContact)
            transaction?.addToBackStack("")
            transaction?.commit()
        }

        searchBar = root.findViewById(R.id.searchContact)
        searchBar.doOnTextChanged { text, start, count, after ->
            contactList = Application.contactDatabase.getContactDao().getContactsByNameFavourite("%$text%", true) + Application.contactDatabase.getContactDao().getContactsByNameFavourite("%$text%", false)
            adapter = ContactAdapter(requireActivity(), ArrayList(contactList))
            listView.adapter = adapter
        }

        return root
    }

    private fun startKeyChooser(){
        var intent = Intent()
        intent.setType("*/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        fileChooser.launch(intent)
    }
    private fun checkScanned(scanned: String): Int{
        if(!scanned.contains("-----BEGIN RSA PUBLIC KEY-----\n" ) || !scanned.contains("\n-----END RSA PUBLIC KEY-----\n" ) || scanned.length != 453 ){
            Toast.makeText(
                this.context,
                resources.getString(R.string.incorrectQR),
                Toast.LENGTH_LONG
            ).show()
            return -1
        }
        val ruta = (requireContext().filesDir).toString()

        if(scanned == RSA().getStringPublicKey(root.context,"$ruta/keys/publicRSA.cer")){
            Toast.makeText(
                this.context,
                resources.getString(R.string.errorYourKey),
                Toast.LENGTH_LONG
            ).show()
            return -1
        }

            val contactList = Application.contactDatabase.getContactDao().getContacts()
            for (contact in contactList){
                if(RSA().getStringPublicKey(context, contact.pathPublicKey) == scanned) {
                    Toast.makeText(
                        requireContext(),
                        "${resources.getString(R.string.errorContactKeyOne)} " + contact.name + " ${resources.getString(R.string.errorContactKeyTwo)}",
                        Toast.LENGTH_LONG
                    ).show()

                    return -1
                }
            }

        return 0
    }

}
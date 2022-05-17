
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

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application
import jorgeff99.keymyinfo.common.RSA
import jorgeff99.keymyinfo.fragments.editcontacts.EditContactsFragment
import kotlinx.coroutines.launch
import java.io.File


class IndividualContactFragment : Fragment() {
    private lateinit var contactName: TextView
    private lateinit var date: TextView
    private lateinit var qrCode: ImageView
    private lateinit var favourite: ImageView
    private lateinit var shareContactKeyButton: MaterialButton
    private lateinit var editContactKeyButton: MaterialButton
    private lateinit var deleteContactKeyButton: MaterialButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_individual_contact, container, false)
        val nameIntent = arguments?.getString("name")
        val dateIntent = arguments?.getString("date")
        var isFavourite = arguments?.getBoolean("favourite")
        val pathPublicKey = arguments?.getString("pathPublic")

        contactName = root.findViewById(R.id.contactName)
        contactName.text = nameIntent

        date = root.findViewById(R.id.dateAdded)
        date.text = dateIntent

        qrCode = root.findViewById(R.id.qrCode)
        qrCode.setImageBitmap(RSA().getBitMapContact(context, 200, pathPublicKey!!))

        favourite = root.findViewById(R.id.starFavourite)
        checkIsFavourite(favourite, isFavourite!!)

        var message =""
            favourite.setOnClickListener{
            updateFavouriteState(favourite, isFavourite!!, nameIntent)
            isFavourite = !isFavourite!!

            message = if(isFavourite!!) resources.getString(R.string.favouriteContact) else resources.getString(R.string.noFavouriteContact)
                Toast.makeText(root.context,
                message, Toast.LENGTH_SHORT)
                .show()
        }

        shareContactKeyButton = root.findViewById(R.id.sharePublicKey)
        shareContactKeyButton.setOnClickListener{
            val publicKeyPath = File(root.context.filesDir, "keysContact")
            val publicKeyFile = File(publicKeyPath, "$nameIntent.cer")
            val uriPublicKey: Uri =
                FileProvider.getUriForFile(root.context, context?.packageName + ".fileprovider", publicKeyFile)

            val shareIntent = Intent().apply {
                this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, R.string.shareKeyDialog.toString() + nameIntent)
                this.putExtra(Intent.EXTRA_STREAM,  uriPublicKey)
                this.type = "*/*"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.shareKeyDialog)))
        }

        editContactKeyButton = root.findViewById(R.id.editContactButton)
        editContactKeyButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("name", nameIntent)
            bundle.putString("date", dateIntent)
            bundle.putString("pathPublic", pathPublicKey)

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragmentEditContact = EditContactsFragment()
            fragmentEditContact.arguments = bundle
            transaction?.replace(R.id.fragment_container,fragmentEditContact)
            transaction?.addToBackStack("")
            transaction?.commit()
        }

        deleteContactKeyButton = root.findViewById(R.id.deleteButton)
        deleteContactKeyButton.setOnClickListener {
            MaterialAlertDialogBuilder(root.context)
                .setTitle(resources.getString(R.string.confirmationDeleteDialog))
                .setMessage(resources.getString(R.string.supportingConfirmationDeleteDialog))
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.continueButton)) { dialog, which ->
                    lifecycleScope.launch {
                        Application.contactDatabase.getContactDao().delete(nameIntent!!)
                        File(pathPublicKey).delete()
                    }

                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    val fragmentHome = HomeFragment()
                    transaction?.replace(R.id.fragment_container,fragmentHome)
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
                .show()

        }

        return root
    }

    private fun checkIsFavourite(starFavourite: ImageView, state: Boolean){
        if (state) starFavourite.setImageResource(R.drawable.ic_baseline_star_full) else starFavourite.setImageResource(R.drawable.ic_baseline_star)
    }

    private fun updateFavouriteState(starFavourite: ImageView, state: Boolean, name: String?){
        checkIsFavourite(starFavourite, !state)

        lifecycleScope.launch {
            Application.contactDatabase.getContactDao().updateFavourite(!state, name!!)
        }
    }

}
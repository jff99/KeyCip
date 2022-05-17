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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.PermissionActivity
import jorgeff99.keymyinfo.R
import java.io.File


class ShareGeneratedPublicKeyFragment : Fragment() {
    // Share the public key generated during the tutorial button
    private lateinit var sharePublicKey: MaterialButton

    // Share the invitation to the App Store
    private lateinit var shareInvitation: MaterialButton

    // Continue button
    private lateinit var shareContinueButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_share_generated_public_key, container, false)

        // Share intent
        sharePublicKey = root.findViewById(R.id.shareKeyButton)
        sharePublicKey.setOnClickListener {
            val publicKeyPath: File = File(root.context.filesDir, "keys")
            val publicKeyFile = File(publicKeyPath, "publicRSA.cer")
            val shareIntent = Intent().apply {
                this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, R.string.shareKeyDialog)
                this.putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(
                        requireContext(),
                        context?.packageName + ".fileprovider",
                        File(requireContext().filesDir.toString() + "/keys/publicRSA.cer")
                    )
                )
                this.type = "*/*" //No MIME Type for .cer
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.shareKeyDialog)))
        }

        shareInvitation = root.findViewById(R.id.shareInvitationButton)
        shareInvitation.setOnClickListener {
            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareDownloadDialog))
                this.type = "text/html" //Html as contains a link to the Google Play
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }

        shareContinueButton = root.findViewById(R.id.continueShareButton)
        shareContinueButton.setOnClickListener {
            val intent = Intent(activity, PermissionActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return root
    }

}
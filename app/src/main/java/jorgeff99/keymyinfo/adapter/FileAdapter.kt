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

package jorgeff99.keymyinfo.adapter

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application
import jorgeff99.keymyinfo.data.database.entities.ContactEntity
import jorgeff99.keymyinfo.data.database.entities.FilesEntity
import jorgeff99.keymyinfo.fragments.FilesFragment
import jorgeff99.keymyinfo.fragments.GenerateNewKeysFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files

/*
* The main objective of this class is to create the adapter for the file database when
* accessing to this data is needed
*/

class FileAdapter(private val context: Activity, private val arrayList: ArrayList<FilesEntity>) :
    ArrayAdapter<FilesEntity>(context, R.layout.file_item) {

    // Attribute containing the imageView for the share button of the file list item
    private lateinit var share: ImageView

    // Attribute containing the imageView for the delete button of the file list item
    private lateinit var delete: ImageView

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val layoutInflater = context.layoutInflater
        val itemView = layoutInflater.inflate(R.layout.file_item, null)

        //Defines the name of the file list item
        val name: TextView = itemView.findViewById(R.id.fileName)
        name.text = arrayList[position].name

        //Defines the date of the file list item
        val date: TextView = itemView.findViewById(R.id.fileDate)
        date.text = arrayList[position].dateModified

        //Defines the icon of the file list item
        val icon: ImageView = itemView.findViewById(R.id.iconName)
        icon.setImageResource(checkIcon(arrayList[position].type))

        //Defines the click event for the file list item
        share = itemView.findViewById(R.id.shareIcon)
        share.setOnClickListener {

            // Sharing intent for type application/zip
            val shareIntent = Intent().apply {
                this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, R.string.shareKeyDialog)
                this.putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(
                        context,
                        context?.packageName + ".fileprovider",
                        File(arrayList[position].path)
                    )
                )
                this.type = "application/zip"
            }
            context.startActivity(Intent.createChooser(shareIntent, null))
        }

        //Defines the click event for the file list item
        delete = itemView.findViewById(R.id.deleteIcon)
        delete.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle(context.resources.getString(R.string.confirmDeleteFile))
                .setMessage(context.resources.getString(R.string.confirmDeleteFileExplanation))
                .setNegativeButton(context.resources.getString(R.string.no)) { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton(context.resources.getString(R.string.yes)) { dialog, which ->
                    // Respond to positive button press
                    GlobalScope.launch {
                        Application.filesDatabase.getFilesDao().delete(arrayList[position].name)
                        File(arrayList[position].path).delete()
                    }

                    //Update the File Fragment in order to refresh results
                    var activity = context as FragmentActivity
                    val transaction = activity.supportFragmentManager.beginTransaction()
                    val fragmentFiles = FilesFragment()
                    transaction.replace(R.id.fragment_container, fragmentFiles)
                    transaction.commit()
                }
                .show()
        }

        return itemView
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): FilesEntity {
        return arrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /*
    Returns the resource id of the icon in the list item
    */
    private fun checkIcon(type: String): Int {
        return when (type) {
            "photo" -> R.drawable.ic_baseline_insert_photo
            "video" -> R.drawable.ic_baseline_insert_photo
            "message" -> R.drawable.ic_baseline_message
            else -> R.drawable.ic_baseline_file
        }
    }

}
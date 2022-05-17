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

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.adapter.ContactAdapter
import jorgeff99.keymyinfo.adapter.FileAdapter
import jorgeff99.keymyinfo.common.Application

class FilesFragment : Fragment() {

    private lateinit var chipType: ChipGroup
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_files, container, false)

        // Get files list from the Files Database
        var contactList = Application.filesDatabase.getFilesDao().getFiles()
        val listView: ListView = root.findViewById(R.id.fileList)
        var adapter = FileAdapter(requireActivity(), ArrayList(contactList))
        listView.adapter = adapter

        return root
    }

}
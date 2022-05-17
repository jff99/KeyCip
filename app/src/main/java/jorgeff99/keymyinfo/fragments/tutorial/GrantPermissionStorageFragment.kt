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

package jorgeff99.keymyinfo.fragments.tutorial

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.R


class GrantPermissionStorageFragment : Fragment() {

    private lateinit var root:View
    private lateinit var grantPermissionButton: MaterialButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_grant_permission_storage, container, false)
        grantPermissionButton = root.findViewById(R.id.grantPermissionButton)
        grantPermissionButton.setOnClickListener {
            checkPermissions()
        }
        return root
    }

    // Open dialog to accept permission to internal storage
    private fun checkPermissions() {
        //If permission is not accepted yet
        if(ContextCompat.checkSelfPermission(root.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermission()
        }
        //If permission is accepted, change fragment
        else{
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragmentGrantPermission = GrantKeyboardFragment()
            transaction?.replace(R.id.fragment_container,fragmentGrantPermission)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }

    // Open dialog in order to request permission to user for internal storage
    private fun requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(root.context, getString(R.string.grantPermissionNotFirstTime), Toast.LENGTH_SHORT)
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            requireContext().startActivity(intent)

        }
        else{
            ActivityCompat.requestPermissions(activity as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 111 )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(root.context, getString(R.string.grantPermissionSuccessful), Toast.LENGTH_SHORT)
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                val fragmentGrantKeyboard = GrantKeyboardFragment()
                transaction?.replace(R.id.fragment_container,fragmentGrantKeyboard)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
            else{
                Toast.makeText(root.context, getString(R.string.grantPermissionNotSuccessful), Toast.LENGTH_SHORT)
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                val fragmentGrantPermission = GrantKeyboardFragment()
                transaction?.replace(R.id.fragment_container,fragmentGrantPermission)
                transaction?.addToBackStack(null)
                transaction?.commit()

            }
        }
    }
}
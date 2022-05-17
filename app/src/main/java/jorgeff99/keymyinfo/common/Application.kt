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

package jorgeff99.keymyinfo.common

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.SharedPreferences
import jorgeff99.keymyinfo.data.database.ContactDatabase
import jorgeff99.keymyinfo.data.database.FilesDatabase
import jorgeff99.keymyinfo.fragments.operation.BlankFragment
import jorgeff99.keymyinfo.fragments.operation.ProgressBarFragment

/*
* The main objective of this class is to define functions and objects which will used across all
* class
*/

class Application: Application(){

    companion object{
        // Object which includes the shared preferences of the application
        lateinit var sharedPreferences: SharedPreferences

        // Object which includes the class Fingerprint when fingerprint is needed
        lateinit var autenticator: Fingerprint

        // List containing all the fragments used when users make an operation
        lateinit var fragmentStructure: List<Int>

        //Contacts database
        lateinit var contactDatabase: ContactDatabase

        //Files database
        lateinit var filesDatabase: FilesDatabase
    }
    override fun onCreate(){
        super.onCreate()
        sharedPreferences = SharedPreferences(applicationContext)
        autenticator = Fingerprint(applicationContext)
        contactDatabase = Room.databaseBuilder(applicationContext, ContactDatabase::class.java, "contacts").allowMainThreadQueries().build()
        filesDatabase = Room.databaseBuilder(applicationContext, FilesDatabase::class.java, "files").allowMainThreadQueries().build()
        fragmentStructure = listOf(R.id.operation, R.id.typeOperation, R.id.fragment_container1, R.id.fragment_container2, R.id.fragment_container2bis, R.id.fragment_container2bis2, R.id.fragment_container3, R.id.fragment_container4)
    }

    /*
    Replaces the containers with a Blank Fragment depending on the selected options in each fragment
    in the Operation Fragment
    */
    fun replaceBlankFragment(activity: FragmentActivity, position: Int){
        for(i in fragmentStructure.indices){
            if(i>position){
                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.replace(fragmentStructure[i], BlankFragment())
                transaction.commit()
            }
        }
    }

    /*
    Replaces the progress bar text and form depending on the step when selecting the operation
    */
    fun replaceProgressBar(activity: FragmentActivity, selectedOptions: ArrayList<String> ){
        val bundle = Bundle()
        bundle.putStringArrayList("selectedOptions", selectedOptions)
        var fragmentProgressBar = ProgressBarFragment()
        fragmentProgressBar.arguments = bundle
        replaceFragment(activity, fragmentProgressBar, R.id.fragment_container4)
    }

    /*
    Replaces a fragment for a given container
    */
     fun replaceFragment(activity: FragmentActivity, fragment: Fragment, container: Int){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(container, fragment)
        transaction?.commit()
    }
}
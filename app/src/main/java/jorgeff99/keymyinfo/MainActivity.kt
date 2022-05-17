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

package jorgeff99.keymyinfo

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import jorgeff99.keymyinfo.common.Application.Companion.sharedPreferences
import jorgeff99.keymyinfo.fragments.*
import jorgeff99.keymyinfo.fragments.addcontacts.AddedKeyContactFragment
import jorgeff99.keymyinfo.fragments.tutorial.GrantPermissionStorageFragment

class MainActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment()
    private val keyboardFragment = KeyboardFragment()
    private val encryptFragment = EncryptFragment()
    private val filesFragment = FilesFragment()
    private val settingsFragment = SettingsFragment()
    private val addedKeyContact = AddedKeyContactFragment()


    //Tutorial Fragments
    private val introductionTutorialFragment = SettingsFragment()
    private val genKeyFragment = GenKeyActivity()
    private val permissionFragment = GrantPermissionStorageFragment()

    private lateinit var navBar: BottomNavigationView
    private lateinit var floatingButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(whichActivity() == -1){
            if(savedInstanceState == null){
                navBar = findViewById(R.id.navigation)
                floatingButton = findViewById(R.id.floatingButton)


                replaceFragment(homeFragment)

                navBar.setOnItemSelectedListener {
                    when(it.itemId){
                        R.id.home -> replaceFragment(homeFragment)
                        R.id.keyboardMenu -> replaceFragment(keyboardFragment)
                        R.id.encrypt ->  {
                            floatingButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                            floatingButton.refreshDrawableState()
                            replaceFragment(encryptFragment)}
                        R.id.filesMenu -> replaceFragment(filesFragment)
                        R.id.settingsMenu-> replaceFragment(settingsFragment)
                    }
                    true
                }

                floatingButton.setOnClickListener {
                    replaceFragment(encryptFragment)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_contact,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.cameraAddContact -> Toast.makeText(this,"Camera",Toast.LENGTH_SHORT).show()
            R.id.manualAddContact -> Toast.makeText(this,"Manual",Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun replaceFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.commit()
    }

    private fun whichActivity(): Int{
        when {
            sharedPreferences.getInt("introduction") == -1 -> {
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
                return 0
            }
            sharedPreferences.getInt("genKey") == -1 -> {
                val intent = Intent(this, GenKeyActivity::class.java)
                startActivity(intent)
                finish()
                return 0
            }
            sharedPreferences.getInt("permission") == -1 -> {
                val intent = Intent(this, PermissionActivity::class.java)
                startActivity(intent)
                finish()
                return 0
            }
            else -> {
                return -1
            }
        }
    }

    private fun whichFragment(fragment: Int?){
        if(fragment == null){
            replaceFragment(homeFragment)
        }

        else{
            replaceFragment(addedKeyContact)
        }
    }
}
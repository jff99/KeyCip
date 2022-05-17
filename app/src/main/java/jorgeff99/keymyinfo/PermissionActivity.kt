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

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jorgeff99.keymyinfo.common.Application
import jorgeff99.keymyinfo.fragments.genkey.GenKeyExplanationFragment
import jorgeff99.keymyinfo.fragments.tutorial.GrantPermissionStorageFragment

class PermissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Saves the last entire process done (so that when the app launchs again, this step of the tutorial is finished)
        Application.sharedPreferences.saveInt("genKey", 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        if(savedInstanceState == null){
            val transaction = supportFragmentManager.beginTransaction()
            val fragmentGrantPermission = GrantPermissionStorageFragment()
            transaction.replace(R.id.fragment_container,fragmentGrantPermission)
            transaction.commit()
        }
    }
}
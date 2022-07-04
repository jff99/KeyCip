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

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jorgeff99.keymyinfo.common.Application.Companion.sharedPreferences
import jorgeff99.keymyinfo.fragments.genkey.GenKeyExplanationFragment
import jorgeff99.keymyinfo.fragments.tutorial.WelcomeFragment

class GenKeyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Saves the last entire process done (so that when the app launchs again, this step of the tutorial is finished)
        sharedPreferences.saveInt("introduction", 0)
        super.onCreate(savedInstanceState)
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                window.navigationBarColor = getResources().getColor(R.color.backgroundDark)
            }
        }
        setContentView(R.layout.activity_gen_key)

        if(savedInstanceState == null){
            val transaction = supportFragmentManager.beginTransaction()
            val fragmentGenKey = GenKeyExplanationFragment()
            transaction.replace(R.id.fragment_container,fragmentGenKey)
            transaction.commit()
        }
    }
}
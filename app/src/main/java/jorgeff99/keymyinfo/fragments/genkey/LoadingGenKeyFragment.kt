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

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.Application
import jorgeff99.keymyinfo.common.RSA
import java.util.concurrent.Executors

/*
* The main objective of this fragment is to display a loading screen while the pair of
* private and public keys are generated
*/

class LoadingGenKeyFragment : Fragment() {
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_loading_gen_key, container, false)
        generateKey()
        return root
    }

    private fun generateKey(){
        executor.execute(){
            RSA().generateKey(this.context)
            handler.post{
                Application.sharedPreferences.saveFingerPrint(0)
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                val fragment = ShareGeneratedPublicKeyFragment()
                transaction?.replace(R.id.fragment_container, fragment)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        }

    }

}
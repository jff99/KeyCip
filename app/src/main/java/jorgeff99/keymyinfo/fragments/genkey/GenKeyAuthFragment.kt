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

import android.app.KeyguardManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.R
import java.util.concurrent.Executor

class GenKeyAuthFragment : Fragment() {

    private lateinit var btnGenKeyAuth: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_gen_key_auth, container, false)
        val biometricManager = BiometricManager.from(root.context)
        btnGenKeyAuth = root.findViewById(R.id.genKeyAuthButton)

        btnGenKeyAuth.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragmentGenKeyAuth = PinInsertFragment.newInstance(0, null)
            transaction?.replace(R.id.fragment_container,fragmentGenKeyAuth)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        return root
    }

}
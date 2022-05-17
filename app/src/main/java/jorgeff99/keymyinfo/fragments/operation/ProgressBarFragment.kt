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

package jorgeff99.keymyinfo.fragments.operation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import jorgeff99.keymyinfo.R

class ProgressBarFragment : Fragment() {
    private lateinit var selectedOptions: ArrayList<String>
    private lateinit var textIndicator: TextView
    private lateinit var progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_progress_bar, container, false)
        selectedOptions = (requireArguments()!!.getStringArrayList("selectedOptions"))!!
        textIndicator = root.findViewById(R.id.textIndicator)
        progressBar = root.findViewById(R.id.progressBar)

        checkProgress()

        return root
    }

    // Update text progress bar
    private fun checkProgress() {
        progressBar.progress = selectedOptions.size + 1
        if(selectedOptions.isEmpty()){
            textIndicator.text = "${resources.getString(R.string.step)} ${selectedOptions.size + 1} ${resources.getString(R.string.of)} 4"
        }
        else if(resources.getString(R.string.encrypt) in selectedOptions[0] || resources.getString(R.string.checkSign) in selectedOptions[0] ){
            if(selectedOptions.size == 4 && selectedOptions[3] == "me" ){
                textIndicator.text = "${resources.getString(R.string.step)} ${selectedOptions.size + 2} ${resources.getString(R.string.of)} 6"
            }
            else{
                textIndicator.text = "${resources.getString(R.string.step)} ${selectedOptions.size + 1} ${resources.getString(R.string.of)} 6"
            }
        }
        else{
            textIndicator.text = "${resources.getString(R.string.step)} ${selectedOptions.size + 1} ${resources.getString(R.string.of)} 4"
        }


    }


}
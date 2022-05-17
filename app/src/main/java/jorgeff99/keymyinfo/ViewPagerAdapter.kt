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
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import jorgeff99.keymyinfo.common.Application.Companion.sharedPreferences

class ViewPagerAdapter(
    private val questionTitleList: List<String>,
    private val questionExplanationList: List<String>,
    private val questionImageList: List<Int?>,
    private val buttonList: List<Boolean>
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.concept_view_page, parent, false)
        return ViewPagerHolder(view)
    }

    //Binding of each question
    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        val questionTitle = questionTitleList[position]
        val questionExplanation = questionExplanationList[position]
        val questionImage = questionImageList[position]
        val buttonList = buttonList[position]

        holder.bind(questionTitle,questionExplanation,questionImage,buttonList)
    }

    override fun getItemCount(): Int {
        return questionTitleList.size
    }


    class ViewPagerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val questionTitleView = itemView.findViewById<TextView>(R.id.questionTitle)
        private val questionExplanationView = itemView.findViewById<TextView>(R.id.questionExplanation)
        private val questionImageView = itemView.findViewById<ImageView>(R.id.questionPic)
        private val questionButtonView = itemView.findViewById<MaterialButton>(R.id.questionButton)

        // Check which picture to display is required and if button to continue is necessary
        fun bind(questionTitle: String, questionExplanation: String, questionPic: Int?, questionButton: Boolean) {
            questionTitleView.text = questionTitle
            questionExplanationView.text = questionExplanation
            if(questionPic != null){
                questionImageView.visibility = View.VISIBLE
                questionImageView.setImageResource(questionPic)
            }
            if(questionPic == null){
                questionImageView.visibility = View.INVISIBLE
            }
            if(!questionButton) {
                questionButtonView.visibility = View.INVISIBLE
            }
            if(questionButton){
                questionButtonView.visibility = View.VISIBLE
            }

            questionButtonView.setOnClickListener {

                if(sharedPreferences.getInt("tutorial") == 0){
                    sharedPreferences.saveInt("tutorial", -1)
                    val intent = Intent(itemView.context, MainActivity::class.java)
                    itemView.context.startActivity(intent)
                }

                else{
                    sharedPreferences.saveInt("tutorial", -1)
                    val intent = Intent(itemView.context, GenKeyActivity::class.java)
                    itemView.context.startActivity(intent)
                }

            }
        }
    }
}
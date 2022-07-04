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
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class ConceptActivity : AppCompatActivity() {

    //Attribute to represent the viewpager View containing the questions and answer of the tutorial
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                window.navigationBarColor = getResources().getColor(R.color.backgroundDark)
            }
        }

        setContentView(R.layout.activity_concept)

        // List containing all the question of the tutorial
        val questionTitleList = listOf(
            getString(R.string.whatIsCipher),
            getString(R.string.whatIsDecipher),
            getString(R.string.whatIsSign),
            getString(R.string.signCipher),
            getString(R.string.whatIsPublicKey),
            getString(R.string.whatIsPrivateKey),
            getString(R.string.location),
            getString(R.string.complicated)
        )

        // List containing all the answers of the tutorial
        val questionExplanationList = listOf(
            getString(R.string.whatIsCipherExplanation),
            getString(R.string.whatIsDecipherExplanation),
            getString(R.string.whatIsSignExplanation),
            getString(R.string.signCipherExplanation),
            getString(R.string.whatIsPublicKeyExplanation),
            getString(R.string.whatIsPrivateKeyExplanation),
            getString(R.string.locationExplanation),
            getString(R.string.complicatedExplanation)
        )

        // List containing the pictures to display in each question (null represents no questio)
        val questionPicList = listOf(
            null,
            R.drawable.decipher,
            R.drawable.sign,
            null,
            null,
            R.drawable.private_key,
            null,
            R.drawable.complicated
        )

        //This list indicates whether the finish button has to be displayed or not
        val buttonList = listOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true
        )

        val indicator = findViewById<DotsIndicator>(R.id.pageIndicator)
        val adapter = ViewPagerAdapter(questionTitleList,questionExplanationList,questionPicList,buttonList)
        viewPager2 = findViewById(R.id.viewPager)
        viewPager2.adapter = adapter
        indicator.setViewPager2(viewPager2)
    }
}
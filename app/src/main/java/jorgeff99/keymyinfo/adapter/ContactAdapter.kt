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

package jorgeff99.keymyinfo.adapter

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.data.database.entities.ContactEntity

/*
* The main objective of this class is to create the adapter for the contacts database when
* accessing to this data is needed
*/

class ContactAdapter(
    private val context: Activity,
    private val arrayList: ArrayList<ContactEntity>
) : ArrayAdapter<ContactEntity>(context, R.layout.list_item) {

    /*
    Attribute containing different colors in order to display a random color for the icon of each contact
    */
    private val colorIcon = arrayOf("#F44336", "#009688", "#673AB7", "#FF5722")

    /*
    Returns the view of each of the contact list items of the contacts list.
    */
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val layoutInflater = context.layoutInflater
        val itemView = layoutInflater.inflate(R.layout.list_item, null)

        /*
        Defines the name of the contact list item
        */
        val name: TextView = itemView.findViewById(R.id.contactName)
        name.text = arrayList[position].name

        /*
        Defines the state of the star of the contact, which represents whether is favourite or not
        */
        val star: ImageView = itemView.findViewById(R.id.favouriteButton)
        checkIsFavourite(star, arrayList[position].favourite)

        /*
        Defines the icon of the contact list item, whose background color is generated randomly
        */
        val iconName: TextView = itemView.findViewById(R.id.iconName)
        iconName.text = arrayList[position].name[0].uppercase()
        iconName.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(colorIcon[(0 until 4).random()]))

        return itemView
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): ContactEntity {
        return arrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /*
    Returns the view of each of the contact list items of the contacts list.
    */
    private fun checkIsFavourite(starFavourite: ImageView, state: Boolean) {
        if (state) starFavourite.setImageResource(R.drawable.ic_baseline_star_full) else starFavourite.setImageResource(
            R.drawable.ic_baseline_star
        )
    }

}
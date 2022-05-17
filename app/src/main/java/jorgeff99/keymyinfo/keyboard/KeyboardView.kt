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


package jorgeff99.keymyinfo.keyboard

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import jorgeff99.keymyinfo.R
import java.util.zip.Inflater


class KeyboardView(context: Context?) : LinearLayout(context) {
    init{
        var root = inflate(context, R.layout.keyboard, this)
    }
}
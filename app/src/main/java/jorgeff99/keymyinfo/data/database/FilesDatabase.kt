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

package jorgeff99.keymyinfo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import jorgeff99.keymyinfo.data.database.dao.ContactDao
import jorgeff99.keymyinfo.data.database.dao.FilesDao
import jorgeff99.keymyinfo.data.database.entities.FilesEntity

@Database(entities = [FilesEntity::class], version = 1)
abstract class FilesDatabase: RoomDatabase() {
    abstract fun getFilesDao(): FilesDao
}
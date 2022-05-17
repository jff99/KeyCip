
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

package jorgeff99.keymyinfo.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import jorgeff99.keymyinfo.data.database.entities.ContactEntity
import jorgeff99.keymyinfo.data.database.entities.FilesEntity
import java.util.*

@Dao
interface FilesDao {
    @Query("SELECT * FROM files_table ORDER BY dateCreated")
    fun getFiles(): List<FilesEntity>

    @Query("SELECT * FROM files_table WHERE type LIKE :type ORDER BY dateCreated ")
    fun getFilesByType(type: String): List<FilesEntity>

    @Insert
    suspend fun insertFile(file: FilesEntity)

    @Query("DELETE FROM files_table WHERE name = :deleteName")
    suspend fun delete(deleteName: String)

}
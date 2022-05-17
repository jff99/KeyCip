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
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import jorgeff99.keymyinfo.data.database.entities.ContactEntity
import java.util.*

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact_table ORDER BY name")
    fun getContacts(): List<ContactEntity>

    @Query("SELECT * FROM contact_table WHERE name LIKE :name ORDER BY name")
    fun getContactsByName(name: String): List<ContactEntity>

    @Query("SELECT name FROM contact_table ORDER BY name")
    fun getContactsName(): List<String>

    @Query("SELECT * FROM contact_table WHERE name LIKE :name AND favourite = :isFavourite ORDER BY name")
    fun getContactsByNameFavourite(name: String, isFavourite: Boolean): List<ContactEntity>

    @Insert
    suspend fun insertContact(contact: ContactEntity)

    @Query("DELETE FROM contact_table WHERE name = :deleteName")
    suspend fun delete(deleteName: String)

    @Query("UPDATE contact_table SET name = :updateName WHERE name = :nameContact")
    suspend fun updateName(updateName: String, nameContact: String)

    @Query("UPDATE contact_table SET pathPublicKey = :updateKey WHERE name = :nameContact")
    suspend fun updateKey(updateKey: String, nameContact: String)

    @Query("UPDATE contact_table SET favourite = :favourite WHERE name = :nameContact")
    suspend fun updateFavourite(favourite: Boolean, nameContact: String)

    @Query("UPDATE contact_table SET dateModified = :date WHERE name = :nameContact")
    suspend fun updateDate(date: String, nameContact: String)
}
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

package jorgeff99.keymyinfo.common

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/*
* The main objective of this class is to define the operations in order to zip and unzip files
*/

class Zip {
    /*
    * Zip the given files included in filesToZip in .zip file, whose name is zipName
    */
    fun zip(filesToZip: List<String>, zipName: String) {
        val outputZip = ZipOutputStream(BufferedOutputStream(FileOutputStream(zipName)))
        for (file in filesToZip) {
            val fileStream = FileInputStream(file)
            val inputFile = BufferedInputStream(fileStream)
            val output = ZipEntry(file.substring(file.lastIndexOf("/")))
            outputZip.putNextEntry(output)
            inputFile.copyTo(outputZip, 1024)
            inputFile.close()
        }

        outputZip.close()
    }

    /*
    * Unzip the .zip file (whose name is zipName) in zipDirectory
    */
    fun unZip(zipName: String, zipDirectory: String, context: Context) {
        val f = File(zipDirectory)
        if (!f.isDirectory) {
            f.mkdirs()
        }

        val zipInputStream = ZipInputStream(context.contentResolver.openInputStream(Uri.parse(zipName)))
        try {
            var zipEntry: ZipEntry? = null
            while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                val path: String = zipDirectory.toString() + zipEntry!!.name
                val fileoutputStream = FileOutputStream(path, false)
                try {
                    var read: Int = zipInputStream.read()
                    while (read != -1) {
                        fileoutputStream.write(read)
                        read = zipInputStream.read()
                    }
                    zipInputStream.closeEntry()
                } finally {
                    fileoutputStream.close()
                }
            }
        } finally {
            zipInputStream.close()
        }
    }
}
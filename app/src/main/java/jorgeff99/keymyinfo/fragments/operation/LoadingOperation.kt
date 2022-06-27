package jorgeff99.keymyinfo.fragments.operation

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import jorgeff99.keymyinfo.R
import jorgeff99.keymyinfo.common.*
import jorgeff99.keymyinfo.fragments.HomeFragment
import jorgeff99.keymyinfo.fragments.genkey.ShareGeneratedPublicKeyFragment
import kotlinx.android.synthetic.main.fragment_encrypt.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class LoadingOperation : Fragment() {
    private lateinit var selectedOptionsOriginal: ArrayList<String>
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var datePath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_loading_operation, container, false)
        selectedOptionsOriginal = (requireArguments()!!.getStringArrayList("selectedOptions"))!!
        operation()

        return root

    }

    private fun whichOperation(): ArrayList<String> {
        val operation = selectedOptionsOriginal[0]
        if (operation == resources.getString(R.string.encrypt)) {
            return encrypt()
        }

        if (resources.getString(R.string.decrypt) in operation) {
            return decrypt()
        }

        if (resources.getString(R.string.sign) in operation) {
            return sign()
        }

        if (resources.getString(R.string.checkSign) in operation) {
            return verifySign()
        }

        return ArrayList()
    }

    private fun operation() {
        executor.execute() {

            val f = File(requireContext().filesDir, "files")
            if (!f.isDirectory) {
                f.mkdirs()
            }

            val zips = File(requireContext().filesDir, "files/zips")
            if (!zips.isDirectory) {
                zips.mkdirs()
            }

            val temp = File(requireContext().filesDir, "files/temp")
            if (temp.isDirectory) {
                temp.deleteRecursively()
            }
            temp.mkdirs()

            val result = whichOperation()
            handler.post {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                val bundle = Bundle()
                bundle.putStringArrayList("finalOptions", result)

                val fragment = EndingOperationFragment()
                fragment.arguments = bundle
                transaction?.replace(R.id.operation, fragment)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        }
    }

    private fun encrypt(): ArrayList<String> {
        var file: File?
        var name: String
        val result = ArrayList<String>(2)
        result.add(selectedOptionsOriginal[0])
        result.add(selectedOptionsOriginal[1])


        if (selectedOptionsOriginal[1] == "text") {
            file = File(requireContext().filesDir.toString() + "/files/temp/" + "toEncrypt.txt")
            file.writeText(selectedOptionsOriginal[2])
            name = "toEncrypt.txt"
        } else {
            file = FileManager().getFile(requireContext(), Uri.parse(selectedOptionsOriginal[2]))
            val fileInfo =
                DocumentFile.fromSingleUri(requireContext(), Uri.parse(selectedOptionsOriginal[2]))
            val bytes = file?.readBytes()
            val splitName = fileInfo!!.name!!.split(".")
            val destination =
                File(requireContext().filesDir.toString() + "/files/temp/" + "toEncrypt." + splitName[splitName.size - 1])
            destination.writeBytes(bytes!!)
            name = "toEncrypt." + splitName[splitName.size - 1]
        }

        val currentTime: Date = Calendar.getInstance().time
        val formatDate: DateFormat = SimpleDateFormat("dd_MM_yyyy_HH:mm:ss")
        datePath = formatDate.format(currentTime.time).toString()

        if (file!!.length() < 245 && selectedOptionsOriginal[1] == "text") {
            result.add("RSA")
            val bytes = RSA().encrypt(
                requireContext().filesDir.toString() + "/files/temp/$name",
                whichKey(selectedOptionsOriginal[3]),
                datePath,
                requireContext()
            )
            val filesToZip = getFilesFromDirectory(
                requireContext().filesDir.toString() + "/files/temp/encrypted$datePath",
                "encrypted"
            )
            Zip().zip(
                filesToZip,
                requireContext().filesDir.toString() + "/files/zips/" + "encrypted$datePath.zip"
            )
            result.add(requireContext().filesDir.toString() + "/files/zips/" + "encrypted$datePath.zip")
            result.add(getEncoded(bytes))
        } else {
            result.add("AES")
            AES().encryptFile(
                requireContext().filesDir.toString() + "/files/temp/$name",
                datePath,
                requireContext()
            )
            RSA().encrypt(
                requireContext().filesDir.toString() + "/files/temp/key.pem",
                whichKey(selectedOptionsOriginal[3]),
                datePath,
                requireContext()
            )
            File(requireContext().filesDir.toString() + "/files/temp/iv").copyTo(
                File(requireContext().filesDir.toString() + "/files/temp/encrypted$datePath/iv"),
                true
            )
            val filesToZip = getFilesFromDirectory(
                requireContext().filesDir.toString() + "/files/temp/encrypted$datePath",
                "encrypted"
            )
            Zip().zip(
                filesToZip,
                requireContext().filesDir.toString() + "/files/zips/" + "encrypted$datePath.zip"
            )
            file = File(requireContext().filesDir.toString() + "/files/temp/key.pem")
            file.delete()
            result.add(requireContext().filesDir.toString() + "/files/zips/" + "encrypted$datePath.zip")
        }

        file = File(requireContext().filesDir.toString() + "/files/temp/$name")
        file.delete()

        return result
    }

    private fun decrypt(): ArrayList<String> {
        var file: File?
        var extension: String
        val result = ArrayList<String>(2)
        result.add(selectedOptionsOriginal[0])
        result.add(selectedOptionsOriginal[1])

        if (selectedOptionsOriginal[1] == "text") {
            file = File(requireContext().filesDir.toString() + "/files/temp/" + "encrypted.txt")
            file.writeText(selectedOptionsOriginal[2])
        } else {
            Zip().unZip(
                selectedOptionsOriginal[2],
                requireContext().filesDir.toString() + "/files/temp",
                requireContext()
            )
        }

        val currentTime: Date = Calendar.getInstance().time
        val formatDate: DateFormat = SimpleDateFormat("dd_MM_yyyy_HH:mm:ss")
        datePath = formatDate.format(currentTime.time).toString()

        val decrypted = File(requireContext().filesDir, "files/temp/decrypted$datePath")
        if (!decrypted.isDirectory) {
            decrypted.mkdirs()
        }

        val filesToZip =
            getFilesFromDirectory(requireContext().filesDir.toString() + "/files/temp", "decrypt")

        if (checkPartialInList(filesToZip, listOf("encrypted.", "encryptedKey.pem", "iv"))) {
            extension = getExtension(filesToZip, "encrypted.")
            RSA().decrypt("encryptedKey.pem", datePath, context)
            val bytes = AES().decrypt(
                context?.filesDir.toString() + "/files/temp/" + "encrypted.$extension",
                datePath, requireContext()
            )
            result.add("AES")
            result.add("true")
            result.add(context?.filesDir.toString() + "/files/temp/decrypted$datePath" + "/decrypted.$extension")
            result.add(String(bytes!!))
        } else if (selectedOptionsOriginal[1] == "text" || checkPartialInList(
                filesToZip,
                listOf("encrypted.")
            )
        ) {
            extension = "txt"
            val bytes = RSA().decrypt("encrypted.$extension", datePath, context)
            result.add("RSA")
            result.add("true")
            result.add(context?.filesDir.toString() + "/files/temp/decrypted$datePath" + "/decrypted.$extension")
            result.add(bytes.decodeToString())
        } else {
            result.add("None")
            result.add("false")
        }

        return result
    }

    private fun sign(): ArrayList<String> {
        var file: File?
        var name: String
        var extension: String
        val result = ArrayList<String>(2)
        result.add(selectedOptionsOriginal[0])
        result.add(selectedOptionsOriginal[1])

        if (selectedOptionsOriginal[1] == "text") {
            file = File(requireContext().filesDir.toString() + "/files/temp/" + "toSign.txt")
            file.writeText(selectedOptionsOriginal[2])
            name = "toSign.txt"
            extension = "txt"
        } else {
            file = FileManager().getFile(requireContext(), Uri.parse(selectedOptionsOriginal[2]))
            val fileInfo =
                DocumentFile.fromSingleUri(requireContext(), Uri.parse(selectedOptionsOriginal[2]))
            val bytes = file?.readBytes()
            val splitName = fileInfo!!.name!!.split(".")
            val destination =
                File(requireContext().filesDir.toString() + "/files/temp/" + "toSign." + splitName[splitName.size - 1])
            destination.writeBytes(bytes!!)
            name = "toSign." + splitName[splitName.size - 1]
            extension = splitName[splitName.size - 1]
        }

        val currentTime: Date = Calendar.getInstance().time
        val formatDate: DateFormat = SimpleDateFormat("dd_MM_yyyy_HH:mm:ss")
        datePath = formatDate.format(currentTime.time).toString()

        result.add("RSA")
        val bytes = RSA().sign(
            requireContext().filesDir.toString() + "/files/temp/$name",
            datePath,
            requireContext()
        )
        File(requireContext().filesDir.toString() + "/files/temp/$name").copyTo(
            File(requireContext().filesDir.toString() + "/files/temp/signed$datePath/original.$extension"),
            true
        )
        val filesToZip = getFilesFromDirectory(
            requireContext().filesDir.toString() + "/files/temp/signed$datePath",
            "signed"
        )
        Zip().zip(
            filesToZip,
            requireContext().filesDir.toString() + "/files/zips/" + "signed$datePath.zip"
        )
        result.add(requireContext().filesDir.toString() + "/files/zips/" + "signed$datePath.zip")
        result.add(
            "${resources.getString(R.string.originalMessage)} " + String(File(requireContext().filesDir.toString() + "/files/temp/signed$datePath/original.$extension").readBytes()) + "\n*******BEGIN SIGNED DATA*******\n"
                    + getEncoded(bytes)
        )

        file = File(requireContext().filesDir.toString() + "/files/temp/$name")
        file.delete()

        return result

    }

    private fun verifySign(): ArrayList<String> {
        var file: File?
        var name: String
        var extension: String = ""
        var bytes: ByteArray? = null
        val result = ArrayList<String>(2)
        result.add(selectedOptionsOriginal[0])
        result.add(selectedOptionsOriginal[1])

        if (selectedOptionsOriginal[1] == "text") {

            if (!selectedOptionsOriginal[2].contains("\n*******BEGIN SIGNED DATA*******\n")) {
                result.add("error")
                return result
            }

            file = File(requireContext().filesDir.toString() + "/files/temp/" + "signed.txt")
            file.writeBytes(getDecoded(selectedOptionsOriginal[2].split("\n*******BEGIN SIGNED DATA*******\n")[1].toByteArray()))

            file = File(requireContext().filesDir.toString() + "/files/temp/" + "original.txt")
            file.writeText(
                selectedOptionsOriginal[2].split("\n*******BEGIN SIGNED DATA*******\n")[0].replace(
                    "${resources.getString(R.string.originalMessage)} ",
                    ""
                )
            )
            bytes =
                selectedOptionsOriginal[2].split("\n*******BEGIN SIGNED DATA*******\n")[0].replace(
                    "${
                        resources.getString(R.string.originalMessage)
                    } ", ""
                ).toByteArray()
            extension = "txt"
        } else {
            Zip().unZip(
                selectedOptionsOriginal[2],
                requireContext().filesDir.toString() + "/files/temp",
                requireContext()
            )
        }

        val currentTime: Date = Calendar.getInstance().time
        val formatDate: DateFormat = SimpleDateFormat("dd_MM_yyyy_HH:mm:ss")
        datePath = formatDate.format(currentTime.time).toString()

        val filesToZip =
            getFilesFromDirectory(requireContext().filesDir.toString() + "/files/temp", "decrypt")

        var listPossibleContact = whichKeyVerify()

        for (element in listPossibleContact) {
            if (checkPartialInList(
                    filesToZip,
                    listOf("encrypted.", "encryptedKey.pem", "iv", "signed.")
                )
            ) {
                extension = getExtension(filesToZip, "encrypted.")
                RSA().decrypt("encryptedKey.pem", datePath, context)
                bytes = AES().decrypt(
                    context?.filesDir.toString() + "/files/temp/" + "encrypted.$extension",
                    datePath, requireContext()
                )!!
                var verified = RSA().verifySigned(
                    element,
                    bytes,
                    getDecoded(getEncodedBytes(File(requireContext().filesDir.toString() + "/files/temp/" + "signed.$extension").readBytes())!!),
                    requireContext()
                )

                if (verified) {
                    result.add("true")
                    result.add(element)
                    return result
                }

            } else if (checkPartialInList(filesToZip, listOf("signed.", "original."))) {
                extension = getExtension(filesToZip, "original.")
                var verified = RSA().verifySigned(
                    element,
                    File(requireContext().filesDir.toString() + "/files/temp/" + "original.$extension").readBytes(),
                    getDecoded(getEncodedBytes(File(requireContext().filesDir.toString() + "/files/temp/" + "signed.$extension").readBytes())),
                    requireContext()
                )

                if (verified) {
                    result.add("true")
                    result.add(element)
                    return result
                }
            } else {
                result.add("error")
                return result
            }
        }
        result.add("false")
        return result
    }


    private fun whichKeyVerify(): ArrayList<String> {
        var result: ArrayList<String> = ArrayList<String>()
        if (selectedOptionsOriginal[3] == "general") {
            result =
                Application.contactDatabase.getContactDao().getContactsName() as ArrayList<String>
            result.replaceAll(String::lowercase)
            result.add("publicRSA")
        } else {
            if (selectedOptionsOriginal[4] == resources.getString(R.string.me)) {
                result.add("publicRSA")
            } else {
                result.add(selectedOptionsOriginal[4].lowercase())
            }
        }

        return result
    }


    private fun whichKey(option: String): String {
        if (option == "me") {
            return requireContext().filesDir.toString() + "/keys/publicRSA.cer"
        }
        return requireContext().filesDir.toString() + "/keysContact/" + selectedOptionsOriginal[4].lowercase() + ".cer"
    }

    private fun getFilesFromDirectory(path: String, operation: String): List<String> {
        val files = File(path).listFiles()
        val fileNames = mutableListOf<String>()

        if (operation == "decrypt") {
            files.forEach { element ->
                fileNames.add(requireContext().filesDir.toString() + "/files/temp" + element.name)
            }

            return fileNames
        }

        files.forEach { element ->
            fileNames.add(requireContext().filesDir.toString() + "/files/temp/$operation$datePath/" + element.name)
        }

        return fileNames

    }

    private fun checkPartialInList(listToCheck: List<String>, checkItems: List<String>): Boolean {
        for (checkElement in checkItems) {
            var condition = false
            for (element in listToCheck) {
                if (element.contains(checkElement)) {
                    condition = true
                }
            }
            if (!condition) {
                return condition
            }
        }
        return true
    }

    private fun getExtension(listToCheck: List<String>, checkItem: String): String {
        for (element in listToCheck) {
            if (element.contains(checkItem)) {
                return element.substring(element.lastIndexOf("/")).split(".")[1]
            }
        }

        return ""
    }

    private fun getEncoded(bytes: ByteArray): String {
        return if (Build.VERSION.SDK_INT >= 26) {
            Base64.getEncoder().encodeToString(bytes).toString()
        } else {
            android.util.Base64.encode(bytes, android.util.Base64.DEFAULT).toString()
        }
    }

    private fun getEncodedBytes(bytes: ByteArray): ByteArray {
        return if (Build.VERSION.SDK_INT >= 26) {
            Base64.getEncoder().encode(bytes)
        } else {
            android.util.Base64.encode(bytes, android.util.Base64.DEFAULT)
        }
    }

    fun getDecoded(bytes: ByteArray): ByteArray {
        return if (Build.VERSION.SDK_INT >= 26) {
            Base64.getDecoder().decode(bytes)
        } else {
            android.util.Base64.decode(bytes, android.util.Base64.DEFAULT)
        }
    }
}
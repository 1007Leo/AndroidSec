package com.example.inventory.data

import android.content.Context
import android.net.Uri
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

class ItemFileOperator(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private fun getEncryptedFile(tempFile: File): EncryptedFile {
        return EncryptedFile.Builder(
            context,
            tempFile,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
        ).build()
    }

    fun saveItemToFile(item: Item, uri: Uri) {
        val tempFile = File(context.cacheDir, "temp_item_file")
        val encryptedFile = getEncryptedFile(tempFile)

        encryptedFile.openFileOutput().use { outputStream ->
            outputStream.write(Gson().toJson(item.copy(id = 0)).toByteArray(StandardCharsets.UTF_8))
            outputStream.flush()
            outputStream.close()
        }

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            FileInputStream(tempFile).use { inputStream ->
                inputStream.copyTo(outputStream)
                inputStream.close()
            }
            outputStream.flush()
            outputStream.close()
        }

        tempFile.delete()
    }

    fun loadItemFromFile(uri: Uri): Item? {
        val tempFile = File(context.cacheDir, "temp_item_file")
        val encryptedFile = getEncryptedFile(tempFile)

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
                outputStream.flush()
                outputStream.close()
            }
            inputStream.close()
        }

        encryptedFile.openFileInput().use { inputStream ->
            return Gson().fromJson(
                inputStream.readBytes().toString(StandardCharsets.UTF_8),
                Item::class.java
            ).also {
                inputStream.close()
                tempFile.delete()
            }
        }
    }
}
package xyz.penpencil.competishun.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    private const val REQUEST_CODE_PICK_FILE = 1
    private const val MAX_FILE_SIZE_MB = 5

    fun pickFile(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "application/pdf"))
        }
        fragment.startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val fileName = getFileName(context, uri)
            val file = File(context.cacheDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        return result ?: uri.path?.substringAfterLast('/') ?: "Unknown"
    }

    fun getFileSizeMB(context: Context, uri: Uri): Long {
        var size = 0L
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                size = it.getLong(it.getColumnIndex(OpenableColumns.SIZE))
            }
        }
        return size / (1024 * 1024)
    }
}

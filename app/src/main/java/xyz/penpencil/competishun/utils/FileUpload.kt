package xyz.penpencil.competishun.utils

import com.apollographql.apollo3.api.Upload
import okio.BufferedSink
import okio.source
import java.io.File

class FileUpload(
    private val file: File,
    override val contentType: String
) : Upload {

    override val contentLength: Long
        get() = file.length()

    override val fileName: String?
        get() = file.name

    override fun writeTo(sink: BufferedSink) {
        file.source().use { source ->
            sink.writeAll(source)
        }
    }
}

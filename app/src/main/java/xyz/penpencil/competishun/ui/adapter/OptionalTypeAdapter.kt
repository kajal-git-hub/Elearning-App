package xyz.penpencil.competishun.ui.adapter

import com.apollographql.apollo3.api.Optional
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.lang.reflect.ParameterizedType

class OptionalTypeAdapter<T>(private val delegate: TypeAdapter<T>) : TypeAdapter<Optional<T>>() {
    override fun write(out: JsonWriter, value: Optional<T>?) {
        when (value) {
            is Optional.Present -> delegate.write(out, value.value)
            is Optional.Absent -> out.nullValue()
            else -> out.nullValue()
        }
    }

    override fun read(`in`: JsonReader): Optional<T> {
        return if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            Optional.Absent
        } else {
            Optional.Present(delegate.read(`in`))
        }
    }
}

class OptionalTypeAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType
        if (rawType != Optional::class.java) {
            return null
        }

        val parameterizedType = type.type as? ParameterizedType
        val typeArgument = parameterizedType?.actualTypeArguments?.get(0) ?: return null
        val delegate = gson.getAdapter(TypeToken.get(typeArgument))
        return OptionalTypeAdapter(delegate) as TypeAdapter<T>
    }
}

fun createGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapterFactory(OptionalTypeAdapterFactory())
        .create()
}

package com.zaidan.quraneasy.feature.quran.data.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson
import com.zaidan.quraneasy.feature.quran.data.remote.dto.SajdaDto

class SajdaAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): SajdaDto? {
        return when (reader.peek()) {
            JsonReader.Token.BOOLEAN -> {
                // If the API sends 'false', consume it and return null
                reader.nextBoolean()
                null
            }
            JsonReader.Token.BEGIN_OBJECT -> {
                // If it's an object, parse it
                var id = 0
                var recommended = false
                var obligatory = false

                reader.beginObject()
                while (reader.hasNext()) {
                    when (reader.nextName()) {
                        "id" -> id = reader.nextInt()
                        "recommended" -> recommended = reader.nextBoolean()
                        "obligatory" -> obligatory = reader.nextBoolean()
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()
                SajdaDto(id, recommended, obligatory)
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    @ToJson
    fun toJson(value: SajdaDto?): Any? {
        return value ?: false
    }
}
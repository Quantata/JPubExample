package com.nancone.criminalintent.database

import androidx.room.TypeConverter
import java.util.*

/**
 * 기본 Data Type(=Int, String ..) 외에 다른 타입을 저장할때
 * 해당 타입 데이터를 저장하거나 가져오는 방법을 Room 에게 알려줘야 함
 * => CrimeTypeConverters 에서 함수 선언 후 Database 에서 어노테이션 등록해줘야 함.
 */
class CrimeTypeConverters {

    // Date 형태 -> Room 에서 저장할 수 있도록
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    // Room 저장에서 -> Data 형태로 변환할 수 있도록
    @TypeConverter
    fun toDate(millisSinceEpoch: Long?) : Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun toUUID(uuid: String) : UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?) : String? {
        return uuid?.toString()
    }
}
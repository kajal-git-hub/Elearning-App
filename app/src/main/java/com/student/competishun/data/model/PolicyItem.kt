package com.student.competishun.data.model

import android.os.Parcel
import android.os.Parcelable

class PolicyItem(
    val question: String,
    var details: String,
    var isExpanded: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        (parcel.readByte() != 0.toByte()).toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(question)
        parcel.writeByte(if (isExpanded) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PolicyItem> {
        override fun createFromParcel(parcel: Parcel): PolicyItem = PolicyItem(parcel)
        override fun newArray(size: Int): Array<PolicyItem?> = arrayOfNulls(size)
    }
}

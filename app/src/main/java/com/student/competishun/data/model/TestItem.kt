package com.student.competishun.data.model

import android.os.Parcel
import android.os.Parcelable

open class TestItem(
    val title: String,
    var isFilter: Boolean = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TestItem> {
        override fun createFromParcel(parcel: Parcel): TestItem = TestItem(parcel)
        override fun newArray(size: Int): Array<TestItem?> = arrayOfNulls(size)
    }
}

package xyz.penpencil.competishun.data.model
import android.os.Parcel
import android.os.Parcelable

data class FAQItem(
    val question: String,
    var details:String,
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

    companion object CREATOR : Parcelable.Creator<FAQItem> {
        override fun createFromParcel(parcel: Parcel): FAQItem = FAQItem(parcel)
        override fun newArray(size: Int): Array<FAQItem?> = arrayOfNulls(size)
    }
}

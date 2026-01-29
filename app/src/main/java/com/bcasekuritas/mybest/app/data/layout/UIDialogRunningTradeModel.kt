package com.bcasekuritas.mybest.app.data.layout

import android.os.Parcel
import android.os.Parcelable

data class UIDialogRunningTradeModel(
    val category: Int = 0,
    val indexSector: Long = 0,
    val priceFrom: Double = 0.0,
    val priceTo: Double = 0.0,
    val changeFrom: Double = 0.0,
    val changeTo: Double = 0.0,
    val volumeFrom: Double = 0.0,
    val volumeTo: Double = 0.0,
    val stockCodes: List<String> = emptyList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.createStringArrayList() ?: emptyList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(category)
        parcel.writeLong(indexSector)
        parcel.writeDouble(priceFrom)
        parcel.writeDouble(priceTo)
        parcel.writeDouble(changeFrom)
        parcel.writeDouble(changeTo)
        parcel.writeDouble(volumeFrom)
        parcel.writeDouble(volumeTo)
        parcel.writeStringList(stockCodes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UIDialogRunningTradeModel> {
        override fun createFromParcel(parcel: Parcel): UIDialogRunningTradeModel {
            return UIDialogRunningTradeModel(parcel)
        }

        override fun newArray(size: Int): Array<UIDialogRunningTradeModel?> {
            return arrayOfNulls(size)
        }
    }
}
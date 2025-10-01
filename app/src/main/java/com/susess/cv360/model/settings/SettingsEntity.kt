package com.susess.cv360.model.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class SettingsEntity(
    @PrimaryKey
    var id: UUID,

    @ColumnInfo(name = "setting_key")
    var settingKey: String,

    @ColumnInfo(name = "facility_name")
    var facilityName:String,

    @ColumnInfo(name = "facility_id")
    var facilityId: String,

    @ColumnInfo(name = "tank_name")
    var tankName: String,

    @ColumnInfo(name = "tank_id")
    var tankId: String,

    @ColumnInfo(name = "product_name")
    var productName: String,

    @ColumnInfo(name = "product_id")
    var productId: String,

    @ColumnInfo(name = "product_key")
    var productKey: String,

    @ColumnInfo(name = "unit_measurement")
    var unitMeasurement: String

)

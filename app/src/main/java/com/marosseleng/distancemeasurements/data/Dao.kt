package com.marosseleng.distancemeasurements.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * @author Maroš Šeleng
 */
@androidx.room.Dao
interface Dao {
    @Query("SELECT * FROM measurements ORDER BY timestamp DESC")
    suspend fun getAllMeasurements(): List<Measurement>
    @Query("SELECT * FROM measurements WHERE measurement_type = :type ORDER BY timestamp DESC")
    suspend fun getMeasurementsByType(type: Int): List<Measurement>
    @Query("SELECT * FROM measurements WHERE id = :measurementId LIMIT 1")
    suspend fun getMeasurementById(measurementId: Long): Measurement?
    @Query("SELECT * FROM measured_values WHERE measurement_id = :measurementId ORDER BY timestamp")
    suspend fun getMeasurementValues(measurementId: Long): List<MeasuredValue>

    @Query("SELECT COUNT(*) FROM measurements")
    suspend fun getMeasurementCount(): Int

    @Insert
    suspend fun insertMeasurement(measurement: Measurement): Long
    @Insert
    suspend fun insertMeasuredValues(vararg values: MeasuredValue)

    @Delete
    suspend fun deleteValuesAndMeasurement(values: List<MeasuredValue>, measurement: Measurement)
}
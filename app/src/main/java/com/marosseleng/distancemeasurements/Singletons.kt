package com.marosseleng.distancemeasurements

import android.net.wifi.WifiManager
import androidx.core.content.getSystemService

/**
 * @author Maroš Šeleng
 */
val application = Application.instance
val database = application.database
val dao = database.dao()
val wifiManager by lazy { application.getSystemService<WifiManager>() }
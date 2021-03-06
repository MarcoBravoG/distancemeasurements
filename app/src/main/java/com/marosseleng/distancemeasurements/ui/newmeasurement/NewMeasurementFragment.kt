/*
 * Copyright 2019 Maroš Šeleng
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.marosseleng.distancemeasurements.ui.newmeasurement

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.wifi.SupplicantState
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.marosseleng.distancemeasurements.*
import com.marosseleng.distancemeasurements.delegates.PermissionsDelegate
import com.marosseleng.distancemeasurements.delegates.ShouldShowRationaleDelegate
import com.marosseleng.distancemeasurements.tags.LOCATION_PERMISSION_RATIONALE
import com.marosseleng.distancemeasurements.tags.WIFI_DISABLED
import com.marosseleng.distancemeasurements.tags.WIFI_NOT_CONNECTED
import com.marosseleng.distancemeasurements.ui.common.PositiveButtonClickedListener
import com.marosseleng.distancemeasurements.ui.measurementdetail.LocationPermissionRationaleDialogFragment
import com.marosseleng.distancemeasurements.ui.measurementdetail.WifiDisabledDialogFragment
import com.marosseleng.distancemeasurements.ui.measurementdetail.WifiNotConnectedDialogFragment
import com.marosseleng.distancemeasurements.ui.numbers.*
import kotlinx.android.synthetic.main.fragment_new_measurement.*
import timber.log.Timber

class NewMeasurementFragment : Fragment(), PositiveButtonClickedListener {

    private val validRadioButtonIds = setOf(R.id.newBluetooth, R.id.newRss, R.id.newRtt)

    private val mBluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        bluetoothManager?.adapter
    }

    private val hasFineLocationPermission: Boolean by PermissionsDelegate(Manifest.permission.ACCESS_FINE_LOCATION)
    private val shouldShowRationale: Boolean by ShouldShowRationaleDelegate(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_measurement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectMeasurementType.isEnabled = radios.checkedRadioButtonId in validRadioButtonIds

        radios.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId in validRadioButtonIds) {
                selectMeasurementType.isEnabled = true
            } else {
                if (BuildConfig.DEBUG) {
                    throw IllegalStateException("Unknown radio button id checked: $checkedId")
                }
            }
        }

        selectMeasurementType.setOnClickListener {
            handleButtonClick(radios.checkedRadioButtonId)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newBluetooth.isVisible = activity?.packageManager?.hasBluetoothLeFeature() == true
        newRtt.isVisible = activity?.packageManager?.hasWifiRttFeature() == true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.isEmpty() || grantResults.isEmpty()) {
            return
        }
        val result = grantResults[0]
        if (result == PERMISSION_GRANTED) {
            findNavController().navigate(getActionForRequestCode(requestCode))
        } else if (!shouldShowRationale) {
            Snackbar
                .make(
                    newMeasurementContent,
                    R.string.new_measurement_permission_denied,
                    Snackbar.LENGTH_SHORT
                )
                .show()
        }
    }

    private fun getActionForRequestCode(requestCode: Int) = when (requestCode) {
        WIFI_RSSI_LOCATION_PERMISSION -> R.id.newRss
        WIFI_RTT_LOCATION_PERMISSION -> R.id.newRtt
        else -> R.id.newBluetooth
    }


    private fun getRequestCodeForAction(action: Int) = when (action) {
        R.id.newRss -> WIFI_RSSI_LOCATION_PERMISSION
        R.id.newRtt -> WIFI_RTT_LOCATION_PERMISSION
        else -> BLE_LOCATION_PERMISSION
    }

    private fun hasLocationPermission(action: Int): Boolean {
        val requestCode = getRequestCodeForAction(action)
        if (!hasFineLocationPermission) {
            if (shouldShowRationale) {
                fragmentManager?.run {
                    LocationPermissionRationaleDialogFragment()
                        .apply { setTargetFragment(this@NewMeasurementFragment, requestCode) }
                        .show(this, LOCATION_PERMISSION_RATIONALE)
                }
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
            }
            return false
        } else {
            return true
        }
    }

    private fun handleButtonClick(checkedItemId: Int) {
        if (!hasLocationPermission(checkedItemId)) {
            Timber.i("Location permission not granted. Handling permission request…")
            return
        }
        when (checkedItemId) {
            R.id.newBluetooth -> {
                if (mBluetoothAdapter?.isEnabled != true) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, BLE_ENABLE_BLUETOOTH)
                } else {
                    findNavController().navigate(NewMeasurementFragmentDirections.actionNewMeasurementToNewBluetooth())
                }
            }
            R.id.newRss -> {
                val fm = fragmentManager ?: return
                if (wifiManager?.isWifiEnabled != true) {
                    // WiFi disabled
                    WifiDisabledDialogFragment()
                        .apply { setTargetFragment(this@NewMeasurementFragment, WIFI_SETTINGS) }
                        .show(fm, WIFI_DISABLED)
                } else if (wifiManager?.connectionInfo == null ||
                    wifiManager?.connectionInfo?.supplicantState != SupplicantState.COMPLETED
                ) {
                    // not connected to a network
                    WifiNotConnectedDialogFragment()
                        .apply { setTargetFragment(this@NewMeasurementFragment, WIFI_SETTINGS) }
                        .show(fm, WIFI_NOT_CONNECTED)
                } else {
                    findNavController().navigate(NewMeasurementFragmentDirections.actionNewMeasurementToNewRss())
                }
            }
            R.id.newRtt -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    return
                } else if (wifiRttManager?.isAvailable != true) {
                    // WiFi RTT not available
                    val fm = fragmentManager ?: return
                    WifiDisabledDialogFragment()
                        .apply { setTargetFragment(this@NewMeasurementFragment, WIFI_SETTINGS) }
                        .show(fm, WIFI_DISABLED)
                } else {
                    findNavController().navigate(NewMeasurementFragmentDirections.actionNewMeasurementToNewRtt())
                }
            }
            else -> {
                if (BuildConfig.DEBUG) {
                    throw IllegalStateException("Unknown checkedItemId: $checkedItemId")
                }
            }
        }
    }

    override fun onPositiveButtonClicked(requestCode: Int) {
        when (requestCode) {
            WIFI_SETTINGS -> {
                val enableBtIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(enableBtIntent)
            }
            else -> requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
        }
    }
}
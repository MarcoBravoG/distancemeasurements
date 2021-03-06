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

package com.marosseleng.distancemeasurements.ui.measurementdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.marosseleng.distancemeasurements.R
import com.marosseleng.distancemeasurements.ui.common.NegativeButtonClickedListener
import com.marosseleng.distancemeasurements.ui.common.PositiveButtonClickedListener
import kotlinx.android.synthetic.main.dialog_location_permission_rationale.*

class LocationPermissionRationaleDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_location_permission_rationale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        positiveButton.setOnClickListener {
            dismiss()
            val target = targetFragment ?: return@setOnClickListener
            if (target is PositiveButtonClickedListener) {
                target.onPositiveButtonClicked(targetRequestCode)
            }
        }

        negativeButton.setOnClickListener {
            dismiss()
            val target = targetFragment ?: return@setOnClickListener
            if (target is NegativeButtonClickedListener) {
                target.onNegativeButtonClicked(targetRequestCode)
            }
        }
    }
}

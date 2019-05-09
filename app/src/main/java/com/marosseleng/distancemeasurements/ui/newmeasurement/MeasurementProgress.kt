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

/**
 * @author Maroš Šeleng
 */

interface MeasurementFinished

interface SavingFinished

sealed class MeasurementProgress {
    object NotStarted : MeasurementProgress()
    object Started : MeasurementProgress()
    object Saving : MeasurementProgress(), MeasurementFinished
    object Saved : MeasurementProgress(), MeasurementFinished, SavingFinished
    object NotSaved : MeasurementProgress(), MeasurementFinished, SavingFinished
}
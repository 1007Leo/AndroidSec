/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.healthconnect.ui

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.healthconnect.data.ItemsRepository
import com.example.healthconnect.ui.edit.EditViewModel
import com.example.healthconnect.ui.home.HomeViewModel


object AppViewModelProvider {
    private val itemsRepository = ItemsRepository()
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                this.createSavedStateHandle(),
                itemsRepository,
            )
        }
        initializer {
            EditViewModel(
                this.createSavedStateHandle(),
                itemsRepository,
            )
        }
    }
}

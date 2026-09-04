package com.knownassurajit.app.game.impstr.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.knownassurajit.app.game.impstr.data.CatalogCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonalizationViewModel
    @Inject
    constructor(
        private val coordinator: CatalogCoordinator,
    ) : ViewModel() {
        val uiState = coordinator.state

        fun refresh() = coordinator.refresh(forceNetwork = true)

        fun onLocationPermissionResult(granted: Boolean) = coordinator.onLocationPermissionResult(granted)

        fun dismissLocationPrompt() = coordinator.dismissLocationPrompt()
    }

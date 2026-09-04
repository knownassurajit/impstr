package com.knownassurajit.app.game.impstr.data

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalizationStore
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val dataStore =
            PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
                produceFile = { context.preferencesDataStoreFile(FILE_NAME) },
            )

        suspend fun snapshot(): PersonalizationPrefs {
            val prefs = dataStore.data.first()
            return PersonalizationPrefs(
                locationConsent = prefs[Keys.LocationConsent] ?: false,
                promptDismissed = prefs[Keys.PromptDismissed] ?: false,
                countryCode = prefs[Keys.CountryCode],
                countryCachedAt = prefs[Keys.CountryCachedAt] ?: 0L,
                lastSyncAt = prefs[Keys.LastSyncAt] ?: 0L,
            )
        }

        suspend fun setLocationConsent(consent: Boolean) {
            dataStore.edit { prefs ->
                prefs[Keys.LocationConsent] = consent
                if (consent) prefs[Keys.PromptDismissed] = true
            }
        }

        suspend fun setPromptDismissed() {
            dataStore.edit { it[Keys.PromptDismissed] = true }
        }

        suspend fun setCountryCode(code: String?) {
            dataStore.edit { prefs ->
                if (code.isNullOrBlank()) {
                    prefs.remove(Keys.CountryCode)
                    prefs.remove(Keys.CountryCachedAt)
                } else {
                    prefs[Keys.CountryCode] = code.uppercase().take(2)
                    prefs[Keys.CountryCachedAt] = System.currentTimeMillis()
                }
            }
        }

        suspend fun setLastSyncAt(epochMs: Long) {
            dataStore.edit { it[Keys.LastSyncAt] = epochMs }
        }

        private object Keys {
            val LocationConsent = booleanPreferencesKey("location_consent")
            val PromptDismissed = booleanPreferencesKey("location_prompt_dismissed")
            val CountryCode = stringPreferencesKey("country_code")
            val CountryCachedAt = longPreferencesKey("country_cached_at")
            val LastSyncAt = longPreferencesKey("catalog_last_sync_at")
        }

        companion object {
            private const val FILE_NAME = "impstr_personalization"
        }
    }

data class PersonalizationPrefs(
    val locationConsent: Boolean,
    val promptDismissed: Boolean,
    val countryCode: String?,
    val countryCachedAt: Long,
    val lastSyncAt: Long,
)

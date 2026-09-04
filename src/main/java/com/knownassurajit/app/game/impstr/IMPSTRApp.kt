package com.knownassurajit.app.game.impstr

import android.app.Application
import com.knownassurajit.app.game.impstr.data.CatalogCoordinator
import com.knownassurajit.app.game.impstr.data.StorageHygiene
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class IMPSTRApp : Application() {
    @Inject
    lateinit var catalogCoordinator: CatalogCoordinator

    @Inject
    lateinit var storageHygiene: StorageHygiene

    override fun onCreate() {
        super.onCreate()
        storageHygiene.prune()
        catalogCoordinator.warmUp()
    }
}

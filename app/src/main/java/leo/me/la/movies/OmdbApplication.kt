package leo.me.la.movies

import android.app.Application
import leo.me.la.data.dataModule
import leo.me.la.domain.domainModule
import leo.me.la.presentation.presentationModule
import leo.me.la.remote.remoteModule
import org.koin.android.ext.android.startKoin

internal class OmdbApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin(this, listOf(
            appModule,
            domainModule,
            dataModule,
            presentationModule,
            remoteModule
        ))
    }
}

package leo.me.la.movies

import android.app.Application
import leo.me.la.cache.cacheModule
import leo.me.la.data.dataModule
import leo.me.la.domain.domainModule
import leo.me.la.presentation.presentationModule
import leo.me.la.remote.remoteModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

internal class OmdbApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            androidContext(this@OmdbApplication)
            androidLogger()
            modules(
                appModule,
                cacheModule,
                domainModule,
                dataModule,
                presentationModule,
                remoteModule
            )
        }
    }
}

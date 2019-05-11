package leo.me.la.movies

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import leo.me.la.cache.cacheModule
import leo.me.la.data.dataModule
import leo.me.la.domain.domainModule
import leo.me.la.presentation.presentationModule
import leo.me.la.remote.remoteModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

internal class OmdbApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
        // Start Koin
        startKoin {
            androidContext(this@OmdbApplication)
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

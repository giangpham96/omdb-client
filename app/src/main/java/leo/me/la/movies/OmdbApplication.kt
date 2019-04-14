package leo.me.la.movies

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import leo.me.la.cache.cacheModule
import leo.me.la.data.dataModule
import leo.me.la.domain.domainModule
import leo.me.la.presentation.presentationModule
import leo.me.la.remote.remoteModule
import org.koin.android.ext.android.startKoin

internal class OmdbApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
        // Start Koin
        startKoin(this, listOf(
            appModule,
            cacheModule,
            domainModule,
            dataModule,
            presentationModule,
            remoteModule
        ))
    }
}

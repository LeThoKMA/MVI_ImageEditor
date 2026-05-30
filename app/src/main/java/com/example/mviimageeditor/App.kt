package com.example.mviimageeditor

import android.app.Application
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import com.example.mviimageeditor.module.dataModule
import com.example.mviimageeditor.module.databaseModule
import com.example.mviimageeditor.module.dispatcherModule
import com.example.mviimageeditor.module.networkModule
import com.example.mviimageeditor.module.viewModelModule
import com.google.android.filament.utils.Utils
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class App :
    Application(),
    KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                databaseModule,
                networkModule,
                dataModule,
                viewModelModule,
                dispatcherModule,
            )
        }
        ImageLoader
            .Builder(this)
            .memoryCache {
                MemoryCache
                    .Builder()
                    .maxSizePercent(this, 0.5)
                    .build()
            }.diskCache {
                DiskCache
                    .Builder()
                    .directory(
                        this.cacheDir
                            .resolve("image_cache")
                            .path
                            .toPath(),
                    ).maxSizePercent(0.25)
                    .build()
            }.build()
        //Filament
        Utils.init()
    }
}

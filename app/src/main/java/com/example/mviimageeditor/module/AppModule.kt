package com.example.mviimageeditor.module

import androidx.lifecycle.SavedStateHandle
import com.example.mviimageeditor.MyPreference
import com.example.mviimageeditor.download.DownloadService
import com.example.mviimageeditor.download.DownloadServiceImpl
import com.example.mviimageeditor.repository.authorize.AuthorizeRepository
import com.example.mviimageeditor.repository.authorize.AuthorizeRepositoryImpl
import com.example.mviimageeditor.repository.detail.DetailRepository
import com.example.mviimageeditor.repository.detail.DetailRepositoryImpl
import com.example.mviimageeditor.repository.favorite.FavoriteRepository
import com.example.mviimageeditor.repository.favorite.FavoriteRepositoryImpl
import com.example.mviimageeditor.repository.home.HomeRepository
import com.example.mviimageeditor.repository.home.HomeRepositoryImpl
import com.example.mviimageeditor.repository.search.SearchRepository
import com.example.mviimageeditor.repository.search.SearchRepositoryImpl
import com.example.mviimageeditor.ui.authorize.AuthorizeViewModel
import com.example.mviimageeditor.ui.create.CaptureImageViewmodel
import com.example.mviimageeditor.ui.detail.DetailViewModel
import com.example.mviimageeditor.ui.home.HomeViewModel
import com.example.mviimageeditor.ui.search.SearchViewModel
import com.example.mviimageeditor.utils.RETROFIT
import com.example.mviimageeditor.utils.RETROFIT_AUTHORIZE
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val databaseModule =
    module {
        single {
            DatabaseModule.provideDatabase(androidApplication())
        }
        single { DatabaseModule.provideCollectionDao(get()) }
    }

val dispatcherModule =
    module {
        single<DispatcherProvider> { DispatcherProviderImpl() }
    }

val networkModule =
    module {
        single(named(RETROFIT)) { NetworkModule.provideRetrofitInterface(androidContext()) }
        single { NetworkModule.providePostApi(get(named(RETROFIT))) }

        single(named(RETROFIT_AUTHORIZE)) { NetworkModule.provideRetrofitAuthorizeInterface() }
        single { NetworkModule.providePostApiAuthorize(get(named(RETROFIT_AUTHORIZE))) }
    }

val dataModule =
    module {
        single { MyPreference(get()) }
        single<DownloadService> { DownloadServiceImpl(androidContext()) }

        // Repository Module
        single<AuthorizeRepository> { AuthorizeRepositoryImpl(get(), get()) }
        single<HomeRepository> { HomeRepositoryImpl(get(), get()) }
        single<DetailRepository> { DetailRepositoryImpl(get(), get()) }
        // single<CreateImageRepository> { CreateImageRepositoryImpl(get()) }
        single<FavoriteRepository> { FavoriteRepositoryImpl(get()) }
        single<SearchRepository> { SearchRepositoryImpl(get()) }
    }

val viewModelModule =
    module {
        viewModel {
            AuthorizeViewModel(get(), get())
        }
//        viewModel { MainViewModel() }
        viewModel { HomeViewModel(get()) }
        viewModel { (state: SavedStateHandle) ->
            DetailViewModel(get(), state)
        }
        viewModel { CaptureImageViewmodel() }
//        viewModel { FavoriteViewModel(get()) }
        viewModel { SearchViewModel(get()) }
    }

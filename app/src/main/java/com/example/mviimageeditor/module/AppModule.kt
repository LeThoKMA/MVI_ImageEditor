package com.example.mviimageeditor.di.module


import com.example.mviimageeditor.module.NetworkModule
import com.example.imageEditor2.repository.authorize.AuthorizeRepository
import com.example.imageEditor2.repository.authorize.AuthorizeRepositoryImpl
import com.example.imageEditor2.repository.favorite.FavoriteRepository
import com.example.imageEditor2.repository.favorite.FavoriteRepositoryImpl
import com.example.imageEditor2.repository.home.HomeRepository
import com.example.imageEditor2.repository.home.HomeRepositoryImpl
import com.example.imageEditor2.repository.search.SearchRepository
import com.example.imageEditor2.repository.search.SearchRepositoryImpl
import com.example.mviimageeditor.utils.RETROFIT
import com.example.mviimageeditor.utils.RETROFIT_AUTHORIZE
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val myModule =
    module {
        // Api module
        single(named(RETROFIT)) { NetworkModule.provideRetrofitInterface(androidContext()) }
        single { NetworkModule.providePostApi(get(named(RETROFIT))) }

        // Repository Module
        single<AuthorizeRepository> { AuthorizeRepositoryImpl(get()) }
        single<HomeRepository> { HomeRepositoryImpl(get()) }
//        single<DetailRepository> { DetailRepositoryImpl(get()) }
        //single<CreateImageRepository> { CreateImageRepositoryImpl(get()) }
        single<FavoriteRepository> { FavoriteRepositoryImpl(get()) }
        single<SearchRepository> { SearchRepositoryImpl(get()) }

//        viewModel {
//            AuthorizeViewModel(get(), get())
//        }
//        viewModel { MainViewModel() }
//        viewModel { HomeViewModel(get()) }
//        viewModel { DetailViewModel(get()) }
//        viewModel { CreateImageViewModel(get()) }
//        viewModel { FavoriteViewModel(get()) }
//        viewModel { SearchViewModel(get()) }
    }

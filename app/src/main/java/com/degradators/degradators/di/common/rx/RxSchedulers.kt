package com.degradators.degradators.di.common.rx

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class RxSchedulers {
    open fun io() = Schedulers.io()
    open fun mainThread(): Scheduler = AndroidSchedulers.mainThread()
}
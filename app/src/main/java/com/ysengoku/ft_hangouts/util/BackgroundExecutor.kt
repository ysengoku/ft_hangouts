package com.ysengoku.ft_hangouts.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

object BackgroundExecutor {
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun execute(task: () -> Unit) = executor.execute(task)
    fun main(task: () -> Unit) = mainHandler.post(task)
}

package com.ysengoku.ft_hangouts.data

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

object DbExecutor {
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun execute(task: () -> Unit) = executor.execute(task)
    fun main(task: () -> Unit) = mainHandler.post(task)
}

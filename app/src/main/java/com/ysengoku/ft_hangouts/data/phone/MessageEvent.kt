package com.ysengoku.ft_hangouts.data.phone

import com.ysengoku.ft_hangouts.data.model.Message
import com.ysengoku.ft_hangouts.util.BackgroundExecutor

object MessageEvent {
    private val listeners = mutableSetOf<(Message, String) -> Unit>()

    fun register(listener: (Message, String) -> Unit) { listeners.add(listener) }
    fun unregister(listener: (Message, String) -> Unit) { listeners.remove(listener) }

    fun notifyReceived(message: Message, senderName: String) {
        BackgroundExecutor.main { listeners.toList().forEach { it(message, senderName) } }
    }
}

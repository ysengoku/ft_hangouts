package com.ysengoku.ft_hangouts.ui.components

import android.view.animation.PathInterpolator
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ysengoku.ft_hangouts.R

class Snackbar(private val view: View) {
    private val textView = view.findViewById<TextView>(R.id.top_toast_content)

    private val defaultMargin = (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

    private val interpolator = PathInterpolator(0.4f, 0f, 0.2f, 1f) // M3 standard easing
    private val hide = Runnable {
        view.animate().alpha(0f).setDuration(150).setInterpolator(interpolator)
            .withEndAction { view.visibility = View.GONE }
    }

    private fun offscreen() = (view.height + (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin).toFloat()

    fun show(content: CharSequence, durationMs: Long = 3500, anchor: View?) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = if (anchor == null) {
            defaultMargin
        } else {
            val anchorMargin = (anchor.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            anchor.height + anchorMargin + defaultMargin
        }

        view.removeCallbacks(hide)
        view.animate().cancel()
        textView.text = content
        view.alpha = 1f
        view.visibility = View.VISIBLE
        view.post {
            view.translationY = offscreen()
            view.animate().translationY(0f).setDuration(350).setInterpolator(interpolator)
            view.postDelayed(hide, durationMs)
        }
    }
}

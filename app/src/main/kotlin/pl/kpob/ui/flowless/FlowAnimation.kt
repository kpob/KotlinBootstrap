package pl.kpob.ui.flowless

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewAnimationUtils
import flowless.Direction
import java.io.Serializable


/**
 * Created by krzysztofpobiarzyn on 09.01.2017.
 */
abstract class FlowAnimation : Serializable {

    companion object {
        @SuppressLint("NewApi")
        private fun createRevealWithDelay(view: View, centerX: Int, centerY: Int, startRadius: Float, endRadius: Float): Animator {
            val delayAnimator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, startRadius)
            delayAnimator.duration = 100
            val revealAnimator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius)
            return AnimatorSet().apply { playSequentially(delayAnimator, revealAnimator) }
        }


        val NONE: FlowAnimation = object : FlowAnimation() {
            override fun createAnimation(previousView: View, newView: View, direction: Direction): Animator? = null

            override fun showChildOnTopWhenAdded(direction: Direction): Boolean = true
        }

        val SEGUE: FlowAnimation = object : FlowAnimation() {
            override fun createAnimation(previousView: View, newView: View, direction: Direction): Animator? {
                if (direction === Direction.REPLACE) {
                    return null
                }
                val backward = direction === Direction.BACKWARD
                val fromTranslation = if (backward) previousView.width else -previousView.width
                val toTranslation = if (backward) -newView.width else newView.width
                return AnimatorSet().apply {
                    play(ObjectAnimator.ofFloat(previousView, View.TRANSLATION_X, fromTranslation.toFloat()))
                    play(ObjectAnimator.ofFloat(newView, View.TRANSLATION_X, toTranslation.toFloat(), 0f))
                }
            }

            override fun showChildOnTopWhenAdded(direction: Direction): Boolean = true

        }

        val REVEAL: FlowAnimation = object : FlowAnimation() {
            @SuppressLint("NewApi")
            override fun createAnimation(previousView: View, newView: View, direction: Direction): Animator? {

                val cx = newView.measuredWidth / 5
                val cy = newView.measuredHeight - newView.measuredHeight / 5

                // get the initial radius for the clipping circle
                val finalRadius = Math.max(newView.width, newView.height) / 2f

                return createRevealWithDelay(newView, cx, cy, 0f, finalRadius)
            }

            override fun showChildOnTopWhenAdded(direction: Direction): Boolean = true

        }

        val CROSSFADE: FlowAnimation = object : FlowAnimation() {
            override fun createAnimation(previousView: View, newView: View, direction: Direction): Animator? {
                if (direction === Direction.REPLACE) {
                    return null
                }
                return AnimatorSet().apply {
                    play(ObjectAnimator.ofFloat(previousView, View.ALPHA, 1f, 0f))
                    play(ObjectAnimator.ofFloat(newView, View.TRANSLATION_X, 0f, 1f))
                }
            }

            override fun showChildOnTopWhenAdded(direction: Direction): Boolean = true

        }
    }

    abstract fun createAnimation(previousView: View, newView: View, direction: Direction): Animator?

    abstract fun showChildOnTopWhenAdded(direction: Direction): Boolean

    override fun equals(`object`: Any?): Boolean = `object` is FlowAnimation || this === `object`

    override fun hashCode(): Int = FlowAnimation::class.java.name.hashCode()

}
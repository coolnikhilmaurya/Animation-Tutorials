package com.smarttoolfactory.tutorial3_1transitions.transition

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.smarttoolfactory.tutorial3_1transitions.R

/**
 * Alternative to other version of Transition instead of setting
 * ```  transitionValues.values[PROPERTY_NAME] = property``` directly
 * sets property on **View** after capturing start values which causes
 * [captureEndValues] to be invoked with different set of values
 */
class CustomRotationTransition2 : Transition {

    private var startRotation: Float = 0f
    private var endRotation: Float = 0f
    var forceValues: Boolean = false

    /**
     * Logs lifecycle and parameters to console wheb set to true
     */
    var debugMode = false

    constructor(startRotation: Float, endRotation: Float, forceValues: Boolean = false) {
        this.startRotation = startRotation
        this.endRotation = endRotation
        this.forceValues = forceValues
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomRotationTransition)
        startRotation =
            a.getFloat(R.styleable.CustomRotationTransition_startRotation, startRotation)
        endRotation = a.getFloat(R.styleable.CustomRotationTransition_startRotation, endRotation)
        a.recycle()

    }

    override fun captureStartValues(transitionValues: TransitionValues) {

        captureValues(transitionValues)

        if (debugMode) {
            println("⚠️ ${this::class.java.simpleName}  captureStartValues() view: ${transitionValues.view} ")
            transitionValues.values.forEach { (key, value) ->
                println("Key: $key, value: $value")
            }
        }

        if (forceValues) {
            transitionValues.view.rotation = endRotation
        }
    }

    // Capture the value of property for a target in the ending Scene.
    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)

        if (debugMode) {
            println("🔥 ${this::class.java.simpleName}  captureEndValues() view: ${transitionValues.view} ")
            transitionValues.values.forEach { (key, value) ->
                println("Key: $key, value: $value")
            }
        }
    }

    private fun captureValues(transitionValues: TransitionValues) {
        transitionValues.values[PROPNAME_ROTATION] = transitionValues.view.rotation
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {

        if (debugMode) {
            println(
                "🎃 ${this::class.java.simpleName}  createAnimator() " +
                        "forceValues: $forceValues" +
                        "\nSTART VALUES: $startValues" +
                        "\nEND VALUES: $endValues "
            )
        }

        if (endValues == null || startValues == null) return null // no values

        val startRotation = startValues.values[PROPNAME_ROTATION] as Float
        val endRotation = endValues.values[PROPNAME_ROTATION] as Float

        if (startRotation == endRotation) return null // no rotation to run

        val view = startValues.view

        val propRotation =
            PropertyValuesHolder.ofFloat(PROPNAME_ROTATION, startRotation, endRotation)

        val animator = ValueAnimator.ofPropertyValuesHolder(propRotation)

        animator.addUpdateListener { valueAnimator ->
            view.pivotX = view.width / 2f
            view.pivotY = view.height / 2f
            view.rotation = valueAnimator.getAnimatedValue(PROPNAME_ROTATION) as Float
        }

        return animator
    }

    companion object {
        private const val PROPNAME_ROTATION = "PROPNAME_ROTATION"
    }
}
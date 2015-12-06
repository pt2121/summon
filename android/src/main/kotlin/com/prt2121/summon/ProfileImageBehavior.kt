package com.prt2121.summon

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by pt2121 on 12/5/15.
 */
class ProfileImageBehavior(val context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<CircleImageView>() {
  private var startXPosition: Int = 0
  private var startToolbarPosition: Float = 0.toFloat()
  private var startYPosition: Int = 0
  private var finalYPosition: Int = 0
  private var finalHeight: Int = 0
  private var startHeight: Int = 0
  private var finalXPosition: Int = 0

  override fun layoutDependsOn(parent: CoordinatorLayout, child: CircleImageView, dependency: View): Boolean {
    return dependency is Toolbar
  }

  override fun onDependentViewChanged(parent: CoordinatorLayout, child: CircleImageView, dependency: View): Boolean {
    shouldInitProperties(child, dependency)
    val maxScrollDistance = (startToolbarPosition - statusBarHeight).toInt()
    val expandedPercentageFactor = dependency.y / maxScrollDistance
    val distanceYToSubtract = ((startYPosition - finalYPosition) * (1f - expandedPercentageFactor)) + (child.height / 2)
    val distanceXToSubtract = ((startXPosition - finalXPosition) * (1f - expandedPercentageFactor)) + (child.width / 2)
    val heightToSubtract = ((startHeight - finalHeight) * (1f - expandedPercentageFactor))
    child.y = startYPosition - distanceYToSubtract
    child.x = startXPosition - distanceXToSubtract
    val lp = child.layoutParams as CoordinatorLayout.LayoutParams
    lp.width = (startHeight - heightToSubtract).toInt()
    lp.height = (startHeight - heightToSubtract).toInt()
    child.layoutParams = lp
    return true
  }

  private fun shouldInitProperties(child: CircleImageView, dependency: View) {
    if (startYPosition == 0) startYPosition = (child.y + (child.height / 2)).toInt()
    if (finalYPosition == 0) finalYPosition = (dependency.height / 2)
    if (startHeight == 0) startHeight = child.height
    if (finalHeight == 0) finalHeight = context.resources.getDimensionPixelOffset(R.dimen.image_final_width)
    if (startXPosition == 0) startXPosition = (child.x + (child.width / 2)).toInt()
    if (finalXPosition == 0) finalXPosition = context.resources.getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) + (finalHeight / 2)
    if (startToolbarPosition.toInt() == 0) startToolbarPosition = dependency.y + (dependency.height / 2)
  }

  val statusBarHeight: Int
    get() {
      val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
      return if (resourceId > 0) {
        context.resources.getDimensionPixelSize(resourceId)
      } else 0
    }

}
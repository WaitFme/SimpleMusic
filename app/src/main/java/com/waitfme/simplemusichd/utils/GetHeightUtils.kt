package com.waitfme.simplemusichd.utils

import android.content.Context
import android.view.ViewConfiguration

open class GetHeightUtils {
    companion object{
        // 获取状态栏高度
        fun getStatusBarByResId(context: Context): Int {
            var height = 0
            //获取状态栏资源id
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                try {
                    height = context.resources.getDimensionPixelSize(resourceId)
                } catch (ignored: Exception) {
                }
            }
            return height
        }

        // 获取navigationBar的高度
        fun getNavigationBarHeight(context: Context): Int {
            val hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey()
            //val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            //判断是否有虚拟按钮
            return if (!hasMenuKey) {
//            val resources = resources
                val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
                //获取NavigationBar的高度
                context.resources.getDimensionPixelSize(resourceId)
            } else {
                0
            }
        }
    }
}
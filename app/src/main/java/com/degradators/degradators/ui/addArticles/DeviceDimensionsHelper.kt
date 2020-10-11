package com.degradators.degradators.ui.addArticles

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics

import android.util.TypedValue


object DeviceDimensionsHelper {
    // DeviceDimensionsHelper.getDisplayWidth(context) => (display width in pixels)
    fun getDisplayWidth(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return displayMetrics.widthPixels
    }

    // DeviceDimensionsHelper.getDisplayHeight(context) => (display height in pixels)
    fun getDisplayHeight(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return displayMetrics.heightPixels
    }

    // DeviceDimensionsHelper.convertDpToPixel(25f, context) => (25dp converted to pixels)
    fun convertDpToPixel(dp: Float, context: Context): Float {
        val r: Resources = context.getResources()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics())
    }

    // DeviceDimensionsHelper.convertPixelsToDp(25f, context) => (25px converted to dp)
    fun convertPixelsToDp(px: Float, context: Context): Float {
        val r: Resources = context.getResources()
        val metrics: DisplayMetrics = r.getDisplayMetrics()
        return px / (metrics.densityDpi / 160f)
    }
}
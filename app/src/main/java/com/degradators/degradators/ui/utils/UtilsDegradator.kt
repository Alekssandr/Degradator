package com.degradators.degradators.ui.utils

import android.content.Context
import android.content.res.Resources
import com.degradators.degradators.R
import java.util.*

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS
private const val WEEK_MILLIS = 24 * 7*  HOUR_MILLIS
private const val MONTH_MILLIS = 2592000000
private const val YEAR_MILLIS = 2592000000 * 365

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels

fun Date.getTimeAgo(context: Context): String? {
    var time = time
    if (time < 1000000000000L) {
        // if timestamp given in seconds, convert to millis
        time *= 1000
    }
    val now: Long = System.currentTimeMillis()
    if (time > now || time <= 0) {
        return context.getString(R.string.just_now)
    }

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> {
            context.getString(R.string.just_now)
        }
        diff < 2 * MINUTE_MILLIS -> {
            context.getString(R.string.minute_ago)
        }
        diff < 50 * MINUTE_MILLIS -> {
            (diff / MINUTE_MILLIS).toString() + " " +   context.getString(R.string.min_ago)
        }
        diff < 90 * MINUTE_MILLIS -> {
            context.getString(R.string.hour_ago)
        }
        diff < 24 * HOUR_MILLIS -> {
            (diff / HOUR_MILLIS).toString() + " " + context.getString(R.string.hours_ago)
        }
        diff < 48 * HOUR_MILLIS -> {
            context.getString(R.string.yesterday)
        }
        diff < 2 * WEEK_MILLIS -> {
            (diff / DAY_MILLIS).toString() + " " +  context.getString(R.string.days_ago)
        }
        diff < MONTH_MILLIS -> {
            (diff / WEEK_MILLIS).toString() + " " + context.getString(R.string.weeks_ago)
        }
        diff < 2 * MONTH_MILLIS -> {
            context.getString(R.string.month)
        }
        diff < YEAR_MILLIS -> {
            (diff / MONTH_MILLIS).toString() + " " +  context.getString(R.string.months_ago)
        }
        else -> {
            context.getString(R.string.more_than_year)
        }
    }
}
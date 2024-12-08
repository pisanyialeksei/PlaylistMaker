package com.study.playlistmaker

import android.content.Context
import android.util.TypedValue
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

val gson = Gson()

fun formatMsToDuration(ms: Long): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(ms)
}

fun Float.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    ).toInt()
}

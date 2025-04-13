package com.study.playlistmaker.utils

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Locale

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

fun showToast(context: Context?, message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, length).show()
}

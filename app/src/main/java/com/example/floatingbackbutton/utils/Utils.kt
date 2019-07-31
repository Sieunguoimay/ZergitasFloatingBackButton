package com.example.floatingbackbutton.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

object Utils{
    fun linear(v:Float, a1:Float, a2:Float, b1:Float, b2:Float):Float{
        return ((v-a1)/(a2-a1))*(b2-b1)+b1
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission(context: Context, permissions: Array<String>): Array<String>? {
        var builder = StringBuilder()
        for (permission in permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                builder.append(permission).append(";")
            }
        }
        val s = builder.toString()
        return if (s.equals(""))  null
        else s.split(";").toTypedArray()
    }

    fun DPToPX(dps: Int, context:Context): Int {
        val scale = context.getResources().getDisplayMetrics().density
        return (dps * scale + 0.5f).toInt()
    }
}
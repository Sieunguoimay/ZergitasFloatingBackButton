package com.example.floatingbackbutton.services

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

private const val TAG = "FW_SERVICE_CONNECTION"
class FloatinWindowServiceConnection(var context:Context,var connectionCallback:ConnectionCallback):ServiceConnection {

    var service:FloatingWindowService?=null










    override fun onServiceDisconnected(p0: ComponentName?) {
        Log.d(TAG,"Floating Window Service disconnected")
    }
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        Log.d(TAG,"Floating Window Service bound")
        service = (p1 as FloatingWindowService.FloatingWindowServiceBinder).getServiceInstance()
        connectionCallback.callback()
    }

    fun createBindAndStartService(){
        val serviceIntent = Intent(context,FloatingWindowService::class.java)
        context.startService(serviceIntent)
        context.bindService(serviceIntent,this, Context.BIND_AUTO_CREATE)
    }
    fun bindServiceIfExistsOnCreate(){
        if(FloatingWindowService.serviceRunning) {
            val serviceIntent = Intent(context, FloatingWindowService::class.java)
            context.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
        }
    }
    fun unBindOnDestroyIfRunning(){
        if(service!=null) {
            context.unbindService(this)
            service = null
            Log.d(TAG, "Floating Window Service unbound")
        }
    }
    fun unBindAndStopService(){
        service?.stopFloatingWindowService()
        unBindOnDestroyIfRunning()
    }





    interface ConnectionCallback{
        fun callback()
    }
}
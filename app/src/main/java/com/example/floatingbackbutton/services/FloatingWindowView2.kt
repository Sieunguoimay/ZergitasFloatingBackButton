package com.example.floatingbackbutton.services

import android.content.Context
import android.graphics.Point
import android.graphics.PorterDuff
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import com.example.floatingbackbutton.services.FloatingWindowAccessibilityService.Companion.showBackButton
import com.example.floatingbackbutton.utils.Utils


private const val TAG = "FW_VIEW"
class FloatingWindowView2(var service:FloatingWindowAccessibilityService) {

    var screenSize = Point()
    var margin:Int = 0
    init{
        service.windowManager.defaultDisplay.getSize(screenSize)
    }
    var attachToEdge:Boolean = false
    var lockPosition:Boolean = false

    private val views = arrayOf(
        service.ivBackButton,
        service.ivHomeButton,
        service.ivRecentButton,
        service.ivSettingButton
    )
    fun updateViewOnColorChanged(color: Int,context: Context) {
        if(service.isViewDetached)return
        for(v in views)
            v.setColorFilter(ResourcesCompat.getColor(context.resources,color,null), PorterDuff.Mode.SRC_ATOP)
    }
    fun updateViewOnTransparentChanged(transparent: Int) {
        if(service.isViewDetached)return
        for(v in views)
            v.alpha = transparent.toFloat()/100.0f
    }
    fun updateViewOnSizeChanged(size: Int,visibleCount:Int) {
        if(service.isViewDetached)return
//        for(v in views)
//            v.post {
//                v.scaleX = (size.toFloat() / v.width)
//                v.scaleY = (size.toFloat() / v.width)
//            }
        service.windowParams.width = size+margin*2
        service.windowParams.height = (size+margin*2)*visibleCount
        service.windowManager.updateViewLayout(service.rlViewLayout,service.windowParams)
    }
    fun updateViewOnMarginChanged(margin: Int,visibleCount:Int,size:Int) {
        if(service.isViewDetached)return
        for(v in views) Utils.setMargins(v,0,margin,0,margin)
        this.margin = margin

        service.windowParams.height = (size+margin*2)*visibleCount
        service.windowManager.updateViewLayout(service.rlViewLayout,service.windowParams)
    }

    fun updateButtonVisibility(whichButton: Int, state: Boolean,visibleCount:Int,size:Int) {
        if(service.isViewDetached)return
        when(whichButton){
            0->{
                service.ivBackButton.visibility = if(state){ View.VISIBLE}else{View.GONE}
            }
            1->{
                service.ivHomeButton.visibility = if(state){ View.VISIBLE}else{View.GONE}
            }
            2->{
                service.ivRecentButton.visibility = if(state){ View.VISIBLE}else{View.GONE}
            }
            3->{
                service.ivSettingButton.visibility = if(state){ View.VISIBLE}else{View.GONE}
            }
        }
        service.windowParams.width = size+margin*2
        service.windowParams.height = (size+margin*2)* visibleCount
        service.llFloatingWindow.weightSum = visibleCount.toFloat()
        service.windowManager.updateViewLayout(service.rlViewLayout,service.windowParams)
    }


    fun updateViewOnToggleAttachToEdge(state: Boolean) {
        attachToEdge = state
        moveWindowToEdge()
    }
    fun updateViewOnToggleVibration(state: Boolean) {
        service.vibrateEnabled = state
    }
    fun updateViewOnToggleLockPosition(state: Boolean) {
        lockPosition = state
    }
    fun updateViewOnToggleBackButton(state: Boolean) {
        if(state){
            if(service.isViewDetached) {
                service.windowManager.addView(service.rlViewLayout, service.windowParams)
                moveWindowToEdge()
            }
        }else{
            if(!service.isViewDetached)
                service.windowManager.removeView(service.rlViewLayout)
        }
        showBackButton = state
    }


    fun moveWindowToEdge(){
        if(service.isViewDetached)return
        if(!attachToEdge)return
        val handler = Handler()
        val thread = object:Thread(){
            var running = true
            var posX = service.windowParams.x.toFloat()
            override fun run() {
                var lastTime:Long = System.currentTimeMillis()
                while(running){

                    val currentTime = System.currentTimeMillis()
                    val dt = currentTime - lastTime
                    lastTime = currentTime

                    handler.post{
                        val rightPoint = posX+service.windowParams.width/2
                        val leftPoint = posX-service.windowParams.width/2


                        val speed =
                            if(posX<0){
                                -screenSize.x - leftPoint
                            }else{
                                screenSize.x - rightPoint
                            }

                        if(posX<screenSize.x/2&&posX>-screenSize.x/2){
                            posX+=(speed*2.0f)*dt.toFloat()/1000.0f
                            if(!service.isViewDetached){
                                service.windowManager.updateViewLayout(service.rlViewLayout,service.windowParams)
                                service.windowParams.x = posX.toInt()
                            }
                        }else{
                            running = false
                            Log.d(TAG,"Finish")
                        }
                    }

                    try {
                        sleep(10)
                    } catch (ex: InterruptedException) {
                        ex.printStackTrace()
                    }

                }
            }
        }
        thread.start()
    }
}
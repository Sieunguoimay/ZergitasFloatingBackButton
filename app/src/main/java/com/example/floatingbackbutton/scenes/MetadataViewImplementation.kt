package com.example.floatingbackbutton.scenes

import android.app.ActionBar
import android.graphics.PorterDuff
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.example.floatingbackbutton.R
import com.example.floatingbackbutton.contracts.MetadataContract
import com.example.floatingbackbutton.services.FloatingWindowAccessibilityService
import com.example.floatingbackbutton.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appearance.*
import kotlinx.android.synthetic.main.floating_button_chooser.*
import kotlinx.android.synthetic.main.switches.*
import kotlin.math.min

private const val TAG = "METADATA_VIEW_IMPL"
class MetadataViewImplementation(var activity: MainActivity): MetadataContract.View{


    private var currentColor:Int = 0
    private var ivTick: ImageView
    private var size:Int = 0

    init{
        ivTick = ImageView(activity)
        ivTick.layoutParams = ViewGroup.LayoutParams(Utils.DPToPX(30,activity),Utils.DPToPX(30,activity))
        ivTick.setImageResource(R.drawable.check_ic)

    }
    override fun showSavedMessage(what: String) {
    }

    override fun showErrorMessage(what: String) {
    }




    override fun updateViewOnColorChanged(color: Int,colorIndex:Int) {

        if(activity.serviceConnection.service!=null)
            activity.serviceConnection.service!!.floatingWindowView.updateViewOnColorChanged(color,activity)


        if(FloatingWindowAccessibilityService.accessibilityServiceInstance!=null)
            FloatingWindowAccessibilityService.accessibilityServiceInstance!!.floatingWindowView.updateViewOnColorChanged(color,activity)

//        ivShadow.setColorFilter(ResourcesCompat.getColor(activity.resources,color,null))


        activity.rlColors[currentColor].removeView(ivTick)
        activity.rlColors[colorIndex].addView(ivTick)

        currentColor = colorIndex
    }

    override fun updateViewOnTransparentChanged(transparent: Int) {
        if(activity.serviceConnection.service!=null)
            activity.serviceConnection.service!!.floatingWindowView.updateViewOnTransparentChanged(transparent)

        if(FloatingWindowAccessibilityService.accessibilityServiceInstance!=null)
            FloatingWindowAccessibilityService.accessibilityServiceInstance!!.floatingWindowView.updateViewOnTransparentChanged(transparent)

        activity.sb_transparent.progress = min(transparent, 100)
    }

    override fun updateViewOnSizeChanged(size: Int,visibleCount:Int) {
        this.size = 30+size*2
        if(activity.serviceConnection.service!=null)
            activity.serviceConnection.service!!.floatingWindowView.updateViewOnSizeChanged(30+size*2,visibleCount)

        if(FloatingWindowAccessibilityService.accessibilityServiceInstance!=null)
            FloatingWindowAccessibilityService.accessibilityServiceInstance!!.floatingWindowView.updateViewOnSizeChanged(30+size*2,visibleCount)

        activity.sb_size.progress = min(size, 100)
    }

    override fun updateViewOnMarginChanged(margin: Int,visibleCount:Int) {
        if(activity.serviceConnection.service!=null)
            activity.serviceConnection.service!!.floatingWindowView.updateViewOnMarginChanged(margin)
        if(FloatingWindowAccessibilityService.accessibilityServiceInstance!=null)
            FloatingWindowAccessibilityService.accessibilityServiceInstance!!.floatingWindowView.updateViewOnMarginChanged(margin,visibleCount,size)
        activity.sb_margin.progress = min(margin, 50)
    }

    override fun updateButtonVisibility(whichButton: Int, state: Boolean,visibleCount:Int) {
        when(whichButton){
            0->{
                Glide.with(activity).load(if(state){R.drawable.ic_tick}else{R.drawable.ic_tick_no}).into(activity.iv_check_box_back)
                activity.iv_check_box_back.setColorFilter(ResourcesCompat.getColor(activity.resources,R.color.colorPrimary,null))
            }
            1->{
                Glide.with(activity).load(if(state){R.drawable.ic_tick}else{R.drawable.ic_tick_no}).into(activity.iv_check_box_home)
                activity.iv_check_box_home.setColorFilter(ResourcesCompat.getColor(activity.resources,R.color.colorPrimary,null))
            }
            2->{
                Glide.with(activity).load(if(state){R.drawable.ic_tick}else{R.drawable.ic_tick_no}).into(activity.iv_check_box_recent)
                activity.iv_check_box_recent.setColorFilter(ResourcesCompat.getColor(activity.resources,R.color.colorPrimary,null))
            }
            3->{
                Glide.with(activity).load(if(state){R.drawable.ic_tick}else{R.drawable.ic_tick_no}).into(activity.iv_check_box_setting)
                activity.iv_check_box_setting.setColorFilter(ResourcesCompat.getColor(activity.resources,R.color.colorPrimary,null))
            }
        }
        if(activity.serviceConnection.service!=null)
            activity.serviceConnection.service!!.floatingWindowView.updateButtonVisibility(whichButton,state,visibleCount,size)

        if(FloatingWindowAccessibilityService.accessibilityServiceInstance!=null)
            FloatingWindowAccessibilityService.accessibilityServiceInstance!!.floatingWindowView.updateButtonVisibility(whichButton,state,visibleCount,size)
    }


    override fun updateViewOnToggleBackButton(state: Boolean) {
//        if(state){
//            if(activity.serviceConnection.service == null)
//                activity.serviceConnection.createBindAndStartService()
//        }else{
//            if(activity.serviceConnection.service != null)
//                activity.serviceConnection.unBindAndStopService()
//        }
        if(FloatingWindowAccessibilityService.accessibilityServiceInstance!=null)
            FloatingWindowAccessibilityService.accessibilityServiceInstance!!.floatingWindowView.updateViewOnToggleBackButton(state)
        activity.sc_show_back_button.isChecked = state
    }

    override fun updateViewOnToggleVibration(state: Boolean) {
        if(activity.serviceConnection.service != null)
            activity.serviceConnection.service!!.floatingWindowView.updateViewOnToggleVibration(state)

        if(FloatingWindowAccessibilityService.accessibilityServiceInstance!=null)
            FloatingWindowAccessibilityService.accessibilityServiceInstance!!.floatingWindowView.updateViewOnToggleVibration(state)

        activity.sc_vibrate.isChecked = state
    }

    override fun updateViewOnToggleAttachToEdge(state: Boolean) {
        if(activity.serviceConnection.service != null)
            activity.serviceConnection.service!!.floatingWindowView.updateViewOnToggleAttachToEdge(state)

        if(FloatingWindowAccessibilityService.accessibilityServiceInstance!=null)
            FloatingWindowAccessibilityService.accessibilityServiceInstance!!.floatingWindowView.updateViewOnToggleAttachToEdge(state)
        activity.sc_attach_to_edge.isChecked = state
    }

    override fun updateViewOnToggleLockPosition(state: Boolean) {
        if(activity.serviceConnection.service != null)
            activity.serviceConnection.service!!.floatingWindowView.updateViewOnToggleLockPosition(state)

        if(FloatingWindowAccessibilityService.accessibilityServiceInstance!=null)
            FloatingWindowAccessibilityService.accessibilityServiceInstance!!.floatingWindowView.updateViewOnToggleLockPosition(state)
        activity.sc_lock_position.isChecked = state
    }
}
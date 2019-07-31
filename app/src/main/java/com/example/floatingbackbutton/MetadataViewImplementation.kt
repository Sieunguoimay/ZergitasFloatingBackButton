package com.example.floatingbackbutton

import android.graphics.PorterDuff
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.floatingbackbutton.contracts.MetadataContract
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appearance.*
import kotlinx.android.synthetic.main.switches.*
import kotlin.math.min

private const val TAG = "METADATA_VIEW_IMPL"
class MetadataViewImplementation(var activity: MainActivity): MetadataContract.View{

    val v = activity.iv_sample_view
    override fun showSavedMessage(what: String) {
    }

    override fun showErrorMessage(what: String) {
    }




    override fun updateViewOnColorChanged(color: Int) {
        v.setColorFilter(ResourcesCompat.getColor(activity.resources,color,null),PorterDuff.Mode.SRC_ATOP)
    }

    override fun updateViewOnTransparentChanged(transparent: Int) {
        v.alpha = transparent.toFloat()/100.0f
        activity.sb_transparent.progress = min(transparent, 100)
    }

    override fun updateViewOnSizeChanged(size: Int) {
        v.post {
            v.scaleX = (size.toFloat()/v.width)
            v.scaleY = (size.toFloat()/v.width)
        }
        activity.sb_size.progress = min(size, 100)
    }

    override fun updateViewOnMarginChanged(margin: Int) {
        Log.d(TAG,"How to update view margin??? "+margin)
        activity.sb_margin.progress = min(margin, 100)
    }




    override fun updateViewOnToggleBackButton(state: Boolean) {
        v.visibility = if(state){ View.VISIBLE}else{View.INVISIBLE}
        activity.sc_show_back_button.isChecked = state
    }

    override fun updateViewOnToggleVibration(state: Boolean) {
        activity.sc_vibrate.isChecked = state
    }

    override fun updateViewOnToggleAttachToEdge(state: Boolean) {
        activity.sc_attach_to_edge.isChecked = state
    }

    override fun updateViewOnToggleLockPosition(state: Boolean) {
        activity.sc_lock_position.isChecked = state
    }
}
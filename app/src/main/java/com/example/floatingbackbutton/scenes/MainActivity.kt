package com.example.floatingbackbutton.scenes

import android.content.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import com.example.floatingbackbutton.R
import com.example.floatingbackbutton.contracts.MetadataPresenter
import com.example.floatingbackbutton.models.MetadataModel
import com.example.floatingbackbutton.services.FloatingWindowService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appearance.*
import kotlinx.android.synthetic.main.switches.*
import android.net.Uri
import android.os.IBinder
import android.os.Vibrator
import android.provider.Settings
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.example.floatingbackbutton.services.FloatinWindowServiceConnection
import com.example.floatingbackbutton.services.FloatingWindowAccessibilityService.Companion.accessibilityServiceInstance
import kotlinx.android.synthetic.main.floating_button_chooser.*




private const val TAG = "MAIN_ACTIVITY"
class MainActivity : AppCompatActivity()
{

    private lateinit var metadataPresenter:MetadataPresenter

    lateinit var serviceConnection:FloatinWindowServiceConnection


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG,"Hello Floating Back Button")
        checkPermission()
        serviceConnection = FloatinWindowServiceConnection(this,serviceConnectionCallback)
        //serviceConnection.bindServiceIfExistsOnCreate()
        initView()
    }

    override fun onStart() {
        super.onStart()
        openAccessibilityServiceSetting()
    }
    override fun onStop(){
        Log.d(TAG,"Floating Back Button Stopped")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG,"Goodbye Floating Back Button")
        //serviceConnection.unBindOnDestroyIfRunning()
        super.onDestroy()
    }

    private fun checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 0)
            }
        }
    }
    private fun openAccessibilityServiceSetting(){
        if(accessibilityServiceInstance == null){
            val dialog = AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("Turn on Accessibility Service to use this app")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    dialog.dismiss()
                    startActivityForResult(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 0)
                }.show()
        }
    }


    private val serviceConnectionCallback = object:FloatinWindowServiceConnection.ConnectionCallback{
        override fun callback() {
            metadataPresenter.loadSettings()
        }
    }


    private val onClickListener = View.OnClickListener { p0 ->
        when(p0?.id){
            rl_reset_settings_button.id ->{
                metadataPresenter.resetSettings()
            }
            rl_color_0.id->metadataPresenter.setColor(0)
            rl_color_1.id->metadataPresenter.setColor(1)
            rl_color_2.id->metadataPresenter.setColor(2)
            rl_color_3.id->metadataPresenter.setColor(3)
            rl_color_4.id->metadataPresenter.setColor(4)
            rl_color_5.id->metadataPresenter.setColor(5)

            iv_check_box_back.id->metadataPresenter.setButtonVisibility(0)
            iv_check_box_home.id->metadataPresenter.setButtonVisibility(1)
            iv_check_box_recent.id->metadataPresenter.setButtonVisibility(2)
            iv_check_box_setting.id->metadataPresenter.setButtonVisibility(3)
        }
    }
    private val seekBarListener= object:SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            if(!p2) return

            when(p0?.id){
                sb_transparent.id->{
                    metadataPresenter.setTransparent(p1)
                }
                sb_size.id->{
                    metadataPresenter.setSize(p1)
                }
                sb_margin.id->{
                    metadataPresenter.setMargin(p1)
                }
            }
        }
        override fun onStartTrackingTouch(p0: SeekBar?) {}
        override fun onStopTrackingTouch(p0: SeekBar?) {}
    }
    private val switchListener = CompoundButton.OnCheckedChangeListener { p0, p1 ->
        when(p0?.id){
            sc_show_back_button.id->{
                metadataPresenter.toggleBackButton(p1)
            }
            sc_vibrate.id->{
                metadataPresenter.toggleVibration(p1)
            }
            sc_attach_to_edge.id->{
                metadataPresenter.toggleAttachToEdge(p1)
            }
            sc_lock_position.id->{
                metadataPresenter.toggleLockPosition(p1)
            }
        }
    }

    lateinit var rlColors: Array<RelativeLayout>
    lateinit var ivColors: Array<ImageView>

    private fun initView(){
        metadataPresenter = MetadataPresenter(
            MetadataViewImplementation(this),
            MetadataModel(this))


        rl_reset_settings_button.setOnClickListener(onClickListener)
        sb_transparent.setOnSeekBarChangeListener(seekBarListener)
        sb_size.setOnSeekBarChangeListener(seekBarListener)
        sb_margin.setOnSeekBarChangeListener(seekBarListener)
        sc_show_back_button.setOnCheckedChangeListener(switchListener)
        sc_attach_to_edge.setOnCheckedChangeListener(switchListener)
        sc_lock_position.setOnCheckedChangeListener(switchListener)
        sc_vibrate.setOnCheckedChangeListener(switchListener)
        iv_check_box_back.setOnClickListener(onClickListener)
        iv_check_box_home.setOnClickListener(onClickListener)
        iv_check_box_recent.setOnClickListener(onClickListener)
        iv_check_box_setting.setOnClickListener(onClickListener)

        ivColors = arrayOf(
            iv_color_0,
            iv_color_1,
            iv_color_2,
            iv_color_3,
            iv_color_4,
            iv_color_5
        )
        rlColors = arrayOf(
            rl_color_0,
            rl_color_1,
            rl_color_2,
            rl_color_3,
            rl_color_4,
            rl_color_5
        )
        for((i,v)in rlColors.withIndex()){
            v.setOnClickListener(onClickListener)
            (v.getChildAt(0) as ImageView).setColorFilter(ResourcesCompat.getColor(resources,MetadataPresenter.DefaultSettings.availableColors[i],null))
        }

        metadataPresenter.loadSettings()
    }


}

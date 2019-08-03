package com.example.floatingbackbutton.services

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.MotionEventCompat.getSource
import com.example.floatingbackbutton.R
import com.example.floatingbackbutton.contracts.MetadataPresenter
import com.example.floatingbackbutton.scenes.MainActivity
import java.nio.file.Files.size





private const val TAG = "FW_ACC_SERVICE"
class FloatingWindowAccessibilityService:AccessibilityService(){



    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
//        var eventText: String = when (p0?.eventType) {
//            AccessibilityEvent.TYPE_VIEW_CLICKED -> "Clicked: "
//            AccessibilityEvent.TYPE_VIEW_FOCUSED -> "Focused: "
//            else -> ""
//        }
//
//        eventText += p0?.contentDescription
//        Toast.makeText(applicationContext,eventText,Toast.LENGTH_SHORT).show()
//        Log.d(TAG,eventText)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        accessibilityServiceInstance = this
        initView()
        setting()
        Log.d(TAG,"Accessibility service connected")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        applicationContext.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        when(intent?.action){
            FloatingBackAccessibilityServiceActions.BACK->{
                back()
            }
            FloatingBackAccessibilityServiceActions.HOME->home()
            FloatingBackAccessibilityServiceActions.RECENT->recent()
            FloatingBackAccessibilityServiceActions.SETTING->setting()
        }
        return Service.START_STICKY
    }


    override fun onUnbind(intent: Intent?): Boolean {
        accessibilityServiceInstance = null
        Log.d(TAG,"Accessibility service disconnected")
        return super.onUnbind(intent)
    }

    companion object{
        @SuppressLint("StaticFieldLeak")
        var accessibilityServiceInstance:FloatingWindowAccessibilityService?= null
        var showBackButton  = true
    }

    lateinit var windowManager: WindowManager
    lateinit var windowParams: WindowManager.LayoutParams
    lateinit var rlViewLayout: RelativeLayout

    var x:Int = 0
    var y:Int = 0
    var touchX:Float = 0.0f
    var touchY:Float = 0.0f
    private var touchToMove:Boolean = false

    lateinit var floatingWindowView:FloatingWindowView2
    lateinit var llFloatingWindow: LinearLayout
    lateinit var ivBackButton: ImageView
    lateinit var ivHomeButton: ImageView
    lateinit var ivRecentButton: ImageView
    lateinit var ivSettingButton: ImageView

    lateinit var notification: FloatingWindowNotification
    lateinit var vibrator: Vibrator
    var vibrateEnabled:Boolean = false
    var isViewDetached:Boolean = true




    @SuppressLint("ClickableViewAccessibility")
    private fun initView(){
        notification = FloatingWindowNotification(applicationContext,this)
        notification.startForeground()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE)as Vibrator



        //Utils.DPToPX(buttonWidth,applicationContext),Utils.DPToPX(buttonWidth*4,applicationContext)
        windowParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY}
            else { WindowManager.LayoutParams.TYPE_SYSTEM_ALERT} ,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT)
        windowParams.x = 0
        windowParams.y = 0
        windowParams.gravity = Gravity.CENTER or Gravity.CENTER


        rlViewLayout = RelativeLayout(applicationContext)
        rlViewLayout.setBackgroundColor(Color.argb(0,0,255,255))
        rlViewLayout.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT)
        rlViewLayout.setOnTouchListener(onTouchListener)
        rlViewLayout.addOnAttachStateChangeListener(object:View.OnAttachStateChangeListener{
            override fun onViewDetachedFromWindow(p0: View?) {
                Log.d(TAG,"View detached from window manager")
                isViewDetached = true
            }

            override fun onViewAttachedToWindow(p0: View?) {
                Log.d(TAG,"View attached from window manager")
                isViewDetached = false
            }
        })
        isViewDetached = false

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if(showBackButton)
            windowManager.addView(rlViewLayout,windowParams)





        val v = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.floating_window,rlViewLayout)
        llFloatingWindow = v.findViewById(R.id.ll_floating_window)
        ivBackButton = v.findViewById(R.id.iv_floating_window_back)
        ivHomeButton = v.findViewById(R.id.iv_floating_window_home)
        ivRecentButton = v.findViewById(R.id.iv_floating_window_recent)
        ivSettingButton = v.findViewById(R.id.iv_floating_window_setting)

        ivBackButton.setOnTouchListener(onTouchListener)
        ivHomeButton.setOnTouchListener(onTouchListener)
        ivRecentButton.setOnTouchListener(onTouchListener)
        ivSettingButton.setOnTouchListener(onTouchListener)
        floatingWindowView = FloatingWindowView2(this)
    }
    private val onTouchListener = View.OnTouchListener { p0, p1 ->

        when(p1.action){
            MotionEvent.ACTION_DOWN->{
                x = windowParams.x
                y = windowParams.y

                touchX = p1.rawX
                touchY = p1.rawY
                touchToMove = false

                when(p0?.id){
                    R.id.iv_floating_window_back-> {
                        Log.d(TAG,"Back Down")
                    }
                    R.id.iv_floating_window_home-> {
                        Log.d(TAG,"Home Down")
                    }
                    R.id.iv_floating_window_recent-> {
                        Log.d(TAG,"Recent down")
                    }
                    R.id.iv_floating_window_setting-> {
                        Log.d(TAG,"Setting down")
                    }
                }
            }

            MotionEvent.ACTION_MOVE->{
                if(!floatingWindowView.lockPosition){

                    val delX = p1.rawX-touchX
                    val delY = p1.rawY-touchY
                    windowParams.x = (x+delX).toInt()
                    windowParams.y = (y+delY).toInt()
                    windowManager.updateViewLayout(rlViewLayout,windowParams)

                    if(delX*delX+delY*delY>100)
                        touchToMove = true
                }
            }
            MotionEvent.ACTION_UP-> {

                if (!touchToMove) {
                    when (p0?.id) {
                        R.id.iv_floating_window_back -> back()
                        R.id.iv_floating_window_home -> home()
                        R.id.iv_floating_window_recent -> recent()
                        R.id.iv_floating_window_setting -> setting()
                    }
                }

                if(!floatingWindowView.lockPosition&&floatingWindowView.attachToEdge){
                    floatingWindowView.moveWindowToEdge()
                }
            }
        }
        true
    }
    private fun back(){
        Log.d(TAG,"Back")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            performGlobalAction(GLOBAL_ACTION_BACK)
        }
        vibrate()
    }
    private fun home(){
        Log.d(TAG,"Home")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            performGlobalAction(GLOBAL_ACTION_HOME)
        }
        vibrate()
    }
    private fun recent(){
        Log.d(TAG,"Recent")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            performGlobalAction(GLOBAL_ACTION_RECENTS)
        }
        vibrate()
    }
    private fun setting(){
        Log.d(TAG,"Setting")
        val intent = Intent(applicationContext, MainActivity::class.java)
            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        startActivity(intent)
        vibrate()
    }


    private fun vibrate(){
        if(!vibrateEnabled)return
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }else{
            vibrator.vibrate(50)
        }

    }

    object FloatingBackAccessibilityServiceActions {
        const val BACK = "floatingBackButton.BACK"
        const val HOME = "floatingBackButton.HOME"
        const val RECENT = "floatingBackButton.RECENT"
        const val SETTING = "floatingBackButton.SETTING"
    }
}
package com.example.floatingbackbutton.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.*
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.floatingbackbutton.R
import com.example.floatingbackbutton.scenes.MainActivity
import com.example.floatingbackbutton.utils.Utils
import android.os.IBinder
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout


private const val TAG = "FLOATING_WINDOW_SERVICE"
class FloatingWindowService: Service() {
    inner class FloatingWindowServiceBinder: Binder(){
        fun getServiceInstance():FloatingWindowService = this@FloatingWindowService
    }
    var binder:FloatingWindowServiceBinder? = null
    override fun onBind(p0: Intent?): IBinder? {
        if(binder==null) binder = FloatingWindowServiceBinder()
        return binder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            FloatingBackServiceActions.BACK->back()
            FloatingBackServiceActions.HOME->home()
            FloatingBackServiceActions.RECENT->recent()
            FloatingBackServiceActions.SETTING->setting()
        }
        applicationContext.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        return START_STICKY
    }

    companion object{
        var serviceRunning = false
    }


    lateinit var windowManager: WindowManager
    lateinit var windowParams: WindowManager.LayoutParams
    lateinit var rlViewLayout: RelativeLayout
    var x:Int = 0
    var y:Int = 0
    var touchX:Float = 0.0f
    var touchY:Float = 0.0f
    private var buttonWidth = 60
    private var touchToMove:Boolean = false

    lateinit var floatingWindowView:FloatingWindowView
    lateinit var llFloatingWindow: LinearLayout
    lateinit var ivBackButton: ImageView
    lateinit var ivHomeButton: ImageView
    lateinit var ivRecentButton: ImageView
    lateinit var ivSettingButton: ImageView

    lateinit var notification: FloatingWindowNotification
    lateinit var vibrator:Vibrator
    var vibrateEnabled:Boolean = false

    override fun onDestroy() {
        Log.d(TAG,"FloatingWindowService destroyed")
        serviceRunning = false
        notification.stopForeground()
        super.onDestroy()
    }

    @SuppressLint("PrivateApi")
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"FloatingWindowService created")
        serviceRunning = true
        notification = FloatingWindowNotification(applicationContext,this)
        notification.startForeground()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE)as Vibrator

        initView()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(){
        //Utils.DPToPX(buttonWidth,applicationContext),Utils.DPToPX(buttonWidth*4,applicationContext)
        windowParams = WindowManager.LayoutParams(WRAP_CONTENT,WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY}
            else { WindowManager.LayoutParams.TYPE_SYSTEM_ALERT} ,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT)
        windowParams.x = 0
        windowParams.y = 0
        windowParams.gravity = Gravity.CENTER or Gravity.CENTER


        rlViewLayout = RelativeLayout(applicationContext)
        rlViewLayout.setBackgroundColor(Color.argb(0,0,255,255))
        rlViewLayout.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT)
        rlViewLayout.setOnTouchListener(onTouchListener)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
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
        floatingWindowView = FloatingWindowView(this)
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
        Toast.makeText(applicationContext,"back",Toast.LENGTH_SHORT).show()
        vibrate()
    }
    private fun home(){
        Log.d(TAG,"Home")
        Toast.makeText(applicationContext,"home",Toast.LENGTH_SHORT).show()

        val i = Intent(Intent.ACTION_MAIN)
        i.addCategory(Intent.CATEGORY_HOME)
        startActivity(i)

        vibrate()
    }
    private fun recent(){
        Log.d(TAG,"Recent")
        Toast.makeText(applicationContext,"recent",Toast.LENGTH_SHORT).show()
        vibrate()
    }
    private fun setting(){
        Log.d(TAG,"Setting")
        Toast.makeText(applicationContext,"setting",Toast.LENGTH_SHORT).show()
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

    fun stopFloatingWindowService(){
        windowManager.removeView(rlViewLayout)
        stopSelf()
    }
    object FloatingBackServiceActions {
        const val BACK = "floatingBackButton.BACK"
        const val HOME = "floatingBackButton.HOME"
        const val RECENT = "floatingBackButton.RECENT"
        const val SETTING = "floatingBackButton.SETTING"
    }
}
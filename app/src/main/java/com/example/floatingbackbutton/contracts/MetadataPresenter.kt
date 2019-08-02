package com.example.floatingbackbutton.contracts

import android.util.Log
import com.example.floatingbackbutton.R
import com.example.floatingbackbutton.contracts.MetadataPresenter.DefaultSettings.buttonDefaultVisibilities
import com.example.floatingbackbutton.contracts.MetadataPresenter.SettingOptionNames.buttonNames
import kotlin.math.acos

private const val TAG = "METADATA_PRESENTER"
class MetadataPresenter(
    var view:MetadataContract.View,
    var model:MetadataContract.Model
): MetadataContract.Presenter{

    init{
        Log.d(TAG,"Metadata presenter created")
    }

    override fun resetSettings() {
        Log.d(TAG,"Reset them all ")
        setColor(DefaultSettings.COLOR)
        setTransparent(DefaultSettings.TRANSPARENT)
        setSize(DefaultSettings.SIZE)
        setMargin(DefaultSettings.MARGIN)
        resetButtonVisibilities()

        toggleBackButton(DefaultSettings.SHOW_BACK_BUTTON)
        toggleVibration(DefaultSettings.VIBRATE)
        toggleAttachToEdge(DefaultSettings.ATTACH_TO_EDGE)
        toggleLockPosition(DefaultSettings.LOCK_POSITION)
    }

    override fun loadSettings() {
        loadButtonVisibilities()

        val colorIndex = model.getInt(SettingOptionNames.COLOR,DefaultSettings.COLOR)
        view.updateViewOnColorChanged(DefaultSettings.availableColors[colorIndex],colorIndex)
        view.updateViewOnTransparentChanged(model.getInt(SettingOptionNames.TRANSPARENT,DefaultSettings.TRANSPARENT))
        view.updateViewOnSizeChanged(model.getInt(SettingOptionNames.SIZE,DefaultSettings.SIZE),visibleButtonCount+if(buttonVisibilities[3]){1}else{0})
        view.updateViewOnMarginChanged(model.getInt(SettingOptionNames.MARGIN,DefaultSettings.MARGIN))

        view.updateViewOnToggleBackButton(model.getBoolean(SettingOptionNames.SHOW_BACK_BUTTON,DefaultSettings.SHOW_BACK_BUTTON))
        view.updateViewOnToggleVibration(model.getBoolean(SettingOptionNames.VIBRATE,DefaultSettings.VIBRATE))
        view.updateViewOnToggleAttachToEdge(model.getBoolean(SettingOptionNames.ATTACH_TO_EDGE,DefaultSettings.ATTACH_TO_EDGE))
        view.updateViewOnToggleLockPosition(model.getBoolean(SettingOptionNames.LOCK_POSITION,DefaultSettings.LOCK_POSITION))
    }



    override fun setColor(colorIndex: Int) {
        view.updateViewOnColorChanged(DefaultSettings.availableColors[colorIndex],colorIndex)
        model.saveInt(SettingOptionNames.COLOR,colorIndex)
    }

    override fun setTransparent(transparent: Int) {
        view.updateViewOnTransparentChanged(transparent)
        model.saveInt(SettingOptionNames.TRANSPARENT,transparent)
    }

    override fun setSize(size: Int) {
        view.updateViewOnSizeChanged(size,visibleButtonCount+if(buttonVisibilities[3]){1}else{0})
        model.saveInt(SettingOptionNames.SIZE,size)
    }

    override fun setMargin(margin: Int) {
        view.updateViewOnMarginChanged(margin)
        model.saveInt(SettingOptionNames.MARGIN,margin)
    }

    override fun setButtonVisibility(whichButton:Int){
        buttonVisibilities[whichButton] = !buttonVisibilities[whichButton]
        if(whichButton<3){
            if(buttonVisibilities[whichButton])
                visibleButtonCount++
            else
                visibleButtonCount--
        }
        if(visibleButtonCount==0){
            visibleButtonCount = 1
            buttonVisibilities[whichButton] = !buttonVisibilities[whichButton]
        }
        model.saveBoolean(buttonNames[whichButton],buttonVisibilities[whichButton])
        view.updateButtonVisibility(whichButton,buttonVisibilities[whichButton],visibleButtonCount+if(buttonVisibilities[3]){1}else{0})
    }



    override fun toggleBackButton(state: Boolean) {
        view.updateViewOnToggleBackButton(state)
        model.saveBoolean(SettingOptionNames.SHOW_BACK_BUTTON,state)
    }

    override fun toggleVibration(state: Boolean) {
        view.updateViewOnToggleVibration(state)
        model.saveBoolean(SettingOptionNames.VIBRATE,state)
    }

    override fun toggleAttachToEdge(state: Boolean) {
        view.updateViewOnToggleAttachToEdge(state)
        model.saveBoolean(SettingOptionNames.ATTACH_TO_EDGE,state)
    }

    override fun toggleLockPosition(state: Boolean) {
        view.updateViewOnToggleLockPosition(state)
        model.saveBoolean(SettingOptionNames.LOCK_POSITION,state)
    }


    private fun resetButtonVisibilities(){
        visibleButtonCount = 3
        for((i,v)in buttonDefaultVisibilities.withIndex()){
            buttonVisibilities[i] = v
            model.saveBoolean(buttonNames[i],buttonVisibilities[i])
            view.updateButtonVisibility(i,buttonVisibilities[i],visibleButtonCount+if(buttonVisibilities[3]){1}else{0})
        }
    }
    private fun loadButtonVisibilities(){
        visibleButtonCount = 0
        for((i,n)in buttonNames.withIndex()){
            buttonVisibilities[i] = model.getBoolean(n, buttonDefaultVisibilities[i])
            view.updateButtonVisibility(i,buttonVisibilities[i],visibleButtonCount+if(buttonVisibilities[3]){1}else{0})
            if(i<3){
                if(buttonVisibilities[i])
                    visibleButtonCount++
            }
        }
    }

    private object SettingOptionNames{
        var COLOR = "Color"
        var TRANSPARENT = "Transparent"
        var SIZE = "Size"
        var MARGIN = "margin"

        var SHOW_BACK_BUTTON = "ShowBackButton"
        var VIBRATE = "Vibrate"
        var ATTACH_TO_EDGE = "AttachToEdge"
        var LOCK_POSITION = "LockPosition"

        var buttonNames = arrayOf(
            "DISPLAY_BACK_BUTTON",
            "DISPLAY_HOME_BUTTON",
            "DISPLAY_RECENT_BUTTON",
            "DISPLAY_SETTING_BUTTON"
        )
    }
    object DefaultSettings{
        var COLOR = 1
        var TRANSPARENT = 80
        var SIZE = 50
        var MARGIN = 10

        var SHOW_BACK_BUTTON = true
        var VIBRATE = true
        var ATTACH_TO_EDGE = true
        var LOCK_POSITION = false



        var availableColors = arrayOf(
            R.color.colorChooser0,
            R.color.colorChooser1,
            R.color.colorChooser2,
            R.color.colorChooser3,
            R.color.colorChooser4,
            R.color.colorChooser5
        )
        var buttonDefaultVisibilities = arrayOf(true, true, true, true)
    }
    private var buttonVisibilities = arrayOf(true, true, true, true)
    private var visibleButtonCount = 0
}
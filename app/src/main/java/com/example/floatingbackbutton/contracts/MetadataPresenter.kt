package com.example.floatingbackbutton.contracts

import android.util.Log
import com.example.floatingbackbutton.R
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

        toggleBackButton(DefaultSettings.SHOW_BACK_BUTTON)
        toggleVibration(DefaultSettings.VIBRATE)
        toggleAttachToEdge(DefaultSettings.ATTACH_TO_EDGE)
        toggleLockPosition(DefaultSettings.LOCK_POSITION)
    }

    override fun loadSettings() {
        view.updateViewOnColorChanged(DefaultSettings.availableColors[model.getInt(SettingOptionNames.COLOR,DefaultSettings.COLOR)])
        view.updateViewOnTransparentChanged(model.getInt(SettingOptionNames.TRANSPARENT,DefaultSettings.TRANSPARENT))
        view.updateViewOnSizeChanged(model.getInt(SettingOptionNames.SIZE,DefaultSettings.SIZE))
        view.updateViewOnMarginChanged(model.getInt(SettingOptionNames.MARGIN,DefaultSettings.MARGIN))

        view.updateViewOnToggleBackButton(model.getBoolean(SettingOptionNames.SHOW_BACK_BUTTON,DefaultSettings.SHOW_BACK_BUTTON))
        view.updateViewOnToggleVibration(model.getBoolean(SettingOptionNames.VIBRATE,DefaultSettings.VIBRATE))
        view.updateViewOnToggleAttachToEdge(model.getBoolean(SettingOptionNames.ATTACH_TO_EDGE,DefaultSettings.ATTACH_TO_EDGE))
        view.updateViewOnToggleLockPosition(model.getBoolean(SettingOptionNames.LOCK_POSITION,DefaultSettings.LOCK_POSITION))
    }



    override fun setColor(colorIndex: Int) {
        view.updateViewOnColorChanged(DefaultSettings.availableColors[colorIndex])
        model.saveInt(SettingOptionNames.COLOR,colorIndex)
    }

    override fun setTransparent(transparent: Int) {
        view.updateViewOnTransparentChanged(transparent)
        model.saveInt(SettingOptionNames.TRANSPARENT,transparent)
    }

    override fun setSize(size: Int) {
        view.updateViewOnSizeChanged(size)
        model.saveInt(SettingOptionNames.SIZE,size)
    }

    override fun setMargin(margin: Int) {
        view.updateViewOnMarginChanged(margin)
        model.saveInt(SettingOptionNames.MARGIN,margin)
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


    private object SettingOptionNames{
        var COLOR = "Color"
        var TRANSPARENT = "Transparent"
        var SIZE = "Size"
        var MARGIN = "margin"

        var SHOW_BACK_BUTTON = "ShowBackButton"
        var VIBRATE = "Vibrate"
        var ATTACH_TO_EDGE = "AttachToEdge"
        var LOCK_POSITION = "LockPosition"
    }
    private object DefaultSettings{
        var COLOR = 1
        var TRANSPARENT = 50
        var SIZE = 30
        var MARGIN = 10

        var SHOW_BACK_BUTTON = true
        var VIBRATE = true
        var ATTACH_TO_EDGE = true
        var LOCK_POSITION = false

        var availableColors = Array(6){
            R.color.colorChooser0
            R.color.colorChooser1
            R.color.colorChooser2
            R.color.colorChooser3
            R.color.colorChooser4
            R.color.colorChooser5
        }
    }
}
package com.example.floatingbackbutton.contracts

class MetadataContract {

    interface View{
        fun showSavedMessage(what:String)
        fun showErrorMessage(what:String)

        fun updateViewOnColorChanged(color:Int,colorIndex: Int)
        fun updateViewOnTransparentChanged(transparent: Int)
        fun updateViewOnSizeChanged(size:Int,visibleCount:Int)
        fun updateViewOnMarginChanged(margin: Int)

        fun updateViewOnToggleBackButton(state:Boolean)
        fun updateViewOnToggleVibration(state:Boolean)
        fun updateViewOnToggleAttachToEdge(state:Boolean)
        fun updateViewOnToggleLockPosition(state:Boolean)

        fun updateButtonVisibility(whichButton:Int,state:Boolean,visibleCount:Int)
    }



    interface Presenter{
        fun resetSettings()
        fun loadSettings()

        fun setColor(colorIndex:Int)
        fun setTransparent(transparent:Int)
        fun setSize(size:Int)
        fun setMargin(margin:Int)
        fun setButtonVisibility(whichButton:Int)


        fun toggleBackButton(state:Boolean)
        fun toggleVibration(state:Boolean)
        fun toggleAttachToEdge(state:Boolean)
        fun toggleLockPosition(state:Boolean)
    }




    interface Model{
        fun saveInt(key:String,data:Int)
        fun saveBoolean(key:String,data:Boolean)

        fun getInt(key:String,defaultValue:Int):Int
        fun getBoolean(key:String,defaultValue:Boolean):Boolean
    }
}
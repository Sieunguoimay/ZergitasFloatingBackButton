package com.example.floatingbackbutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.SeekBar
import com.example.floatingbackbutton.contracts.MetadataContract
import com.example.floatingbackbutton.contracts.MetadataPresenter
import com.example.floatingbackbutton.models.MetadataModel
import com.example.floatingbackbutton.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appearance.*
import kotlinx.android.synthetic.main.switches.*

private const val TAG = "MAIN_ACTIVITY"
class MainActivity : AppCompatActivity()
{

    private lateinit var metadataPresenter:MetadataPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG,"Hello Floating Back Button")
        checkPermission()
        initView()
    }

    override fun onStop(){
        Log.d(TAG,"Goodbye Floating Back Button")
        super.onStop()
    }


    private fun checkPermission(){

    }

    private val onClickListener = object: View.OnClickListener{
        override fun onClick(p0: View?) {
            when(p0?.id){
                R.id.rl_reset_settings_button->{
                    metadataPresenter.resetSettings()
                }
            }
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
    private val switchListener = object:CompoundButton.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
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
    }

    private fun initView(){
        metadataPresenter = MetadataPresenter(
            MetadataViewImplementation(this),
            MetadataModel(this))

        metadataPresenter.loadSettings()

        rl_reset_settings_button.setOnClickListener(onClickListener)
        sb_transparent.setOnSeekBarChangeListener(seekBarListener)
        sb_size.setOnSeekBarChangeListener(seekBarListener)
        sb_margin.setOnSeekBarChangeListener(seekBarListener)
        sc_show_back_button.setOnCheckedChangeListener(switchListener)
        sc_attach_to_edge.setOnCheckedChangeListener(switchListener)
        sc_lock_position.setOnCheckedChangeListener(switchListener)
        sc_vibrate.setOnCheckedChangeListener(switchListener)
    }

}

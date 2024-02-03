package com.example.pitchcontroller

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pitchcontroller.data.Presets
import com.example.pitchcontroller.databinding.ActivityMainBinding
import com.example.pitchcontroller.view.DynamicsProcessingService
import me.tankery.lib.circularseekbar.CircularSeekBar

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    lateinit var binding: ActivityMainBinding
    lateinit var seekBars: List<SeekBar>
    lateinit var textViews: List<TextView>

    var audioPlayer: DynamicsProcessingService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAudioPermission()

        audioPlayer = DynamicsProcessingService(this, R.raw.cookie)
        audioPlayer?.mediaPlayer?.start()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()

    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer?.mediaPlayer?.release()
    }

    private fun requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 아직 부여되지 않았다면 사용자에게 요청합니다.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        } else {
            // 권한이 이미 부여되었습니다. 추가적인 작업을 계속합니다.
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여됨. 필요한 작업을 계속합니다.
            } else {
                // 사용자가 권한을 거부했습니다. 권한이 없이는 기능을 사용할 수 없으므로 안내 메시지를 보여주거나 기능을 비활성화할 수 있습니다.
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun initLayout() {
        setSwitch()
        setSeekBar()
        setSpinner()
        setGauge()
    }

    private fun setSwitch() {
        binding.apply {
            switch1.setOnCheckedChangeListener { _, isChecked ->
                audioPlayer?.setEqualizerEnabled(isChecked)
            }

            switch2.setOnCheckedChangeListener { _, isChecked ->
                audioPlayer?.setBassBoostEnable(isChecked)
            }

            switch3.setOnCheckedChangeListener { _, isChecked ->
                audioPlayer?.setVirtualizerEnable(isChecked)
            }

            switch4.setOnCheckedChangeListener { _, isChecked ->
                audioPlayer?.setLoudnessEnhancerEnable(isChecked)
            }
        }
    }

    private fun setSeekBar() {
        binding.apply {
            seekBars = listOf(
                seekBar1,
                seekBar2,
                seekBar3,
                seekBar4,
                seekBar5,
                seekBar6,
                seekBar7,
                seekBar8,
                seekBar9,
                seekBar10
            )
            textViews = listOf(
                textView1,
                textView2,
                textView3,
                textView4,
                textView5,
                textView6,
                textView7,
                textView8,
                textView9,
                textView10,
            )
            seekBars.forEachIndexed { index, seekBar ->
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        val num = progress - 15
                        audioPlayer?.setEqualizerGainByIndex(index, num.toFloat())
                        textViews[index].text = num.toString()
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        // 사용자가 터치를 시작할 때의 로직을 여기에 작성합니다.
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        // 터치를 멈췄을 때의 로직을 여기에 작성합니다.
                    }
                })
            }
        }
    }

    private fun setSpinner() {
        val adapter = ArrayAdapter(
            this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            Presets.presetList
        )

        binding.spinner1.adapter = adapter
        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                for (i in 0..<seekBars.size) {
                    seekBars[i].progress = Presets.presets[p2].gains[i].toInt() + 15
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun setGauge() {
        binding.apply {
            val unitArr = arrayOf<(strength:Int) -> Unit>(
                {
                    audioPlayer?.setBassBoostStrength(it)
                }, {
                    audioPlayer?.setLoudnessEnhancerStrength(it)
                }, {
                    audioPlayer?.setVirtualizerStrength(it)
                }
            )

            val seekBars = arrayOf(
                seekbarBaseboost, seekbarLoudness, seekbarVirtualizer
            )

            seekBars.forEachIndexed { index, seekBar ->
                seekBar.setOnSeekBarChangeListener(object :
                    CircularSeekBar.OnCircularSeekBarChangeListener {
                    override fun onProgressChanged(
                        circularSeekBar: CircularSeekBar?,
                        progress: Float,
                        fromUser: Boolean
                    ) {
                        unitArr[index](progress.toInt())
                        Log.d(TAG, progress.toString())
                    }

                    override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}

                })
            }
        }
    }
}
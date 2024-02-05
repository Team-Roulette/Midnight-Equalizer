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
import androidx.appcompat.app.AppCompatActivity
import com.example.pitchcontroller.databinding.ActivityMainBinding
import com.example.pitchcontroller.repositories.presetList
import com.example.pitchcontroller.repositories.presets
import com.example.pitchcontroller.utils.DynamicsProcessingService
import me.tankery.lib.circularseekbar.CircularSeekBar
import android.content.Intent
import android.graphics.PorterDuff
import android.media.AudioManager

import android.media.audiofx.Visualizer
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.pitchcontroller.utils.ForegroundService
import com.example.pitchcontroller.viewmodels.MainViewModel
import com.example.pitchcontroller.views.WaveformView

private const val TAG = "MainActivity"
private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {

    private lateinit var seekBars: List<SeekBar>
    private lateinit var gains: List<TextView>
    private lateinit var frequencies: List<TextView>
    private lateinit var visualizer: Visualizer
    private lateinit var waveformView: WaveformView

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var audioSessionId = 0
    private val permissions = arrayOf(
        android.Manifest.permission.POST_NOTIFICATIONS,
        android.Manifest.permission.READ_MEDIA_AUDIO,
        android.Manifest.permission.RECORD_AUDIO
    )
    private val multiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            val resultPermission = it.all{map ->
                map.value
            }
            if(!resultPermission){
                Toast.makeText(this, "모든 권한 승인되어야 함", Toast.LENGTH_SHORT).show()
            }
        }
    var audioPlayer: DynamicsProcessingService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioSessionId = AudioManager.AUDIO_SESSION_ID_GENERATE
        binding = ActivityMainBinding.inflate(layoutInflater)
        checkPermissions()
        setContentView(binding.root)
    }



    private fun checkPermissions() {
        Log.i("test", "checkPermissions")
        if (!permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }) multiplePermissionLauncher.launch(permissions)
        else init()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.i("test", "onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // 권한이 부여되었을 때의 로직
                    Log.i("test", "Granted")
                    init()
                } else {
                    // 권한이 거부되었을 때의 로직
                    Log.i("test", "Not Granted")
                }
                return
            }
            // 다른 'case' 라인을 여기에 추가할 수 있습니다.
        }
    }
    private fun initVisualizer() {
        waveformView = binding.waveformView
        visualizer = Visualizer(audioSessionId).apply {
            captureSize = Visualizer.getCaptureSizeRange()[1]
            scalingMode = Visualizer.SCALING_MODE_NORMALIZED

            setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(visualizer: Visualizer, waveform: ByteArray, samplingRate: Int) {
                    waveformView.updateWaveform(waveform)
                }
                override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
                    // FFT 데이터 처리 (선택적)
                }
            }, Visualizer.getMaxCaptureRate(), true, false)
            enabled = true // Visualizer 시작
        }
        visualizer.enabled = true // Visualizer 시작
    }

    @SuppressLint("ResourceType")
    private fun init() {


        Log.i("test", "init")
        val serviceIntent = Intent(this, ForegroundService::class.java)
        startForegroundService(serviceIntent)

        initVisualizer()

        setSwitch()
        setSeekBar()
        setSpinner()
    }

    private fun setSwitch() {

        binding.apply {
            switch1.setOnCheckedChangeListener { _, isChecked ->
                mainViewModel.setEqualizerEnabled(isChecked)
            }

            switch2.setOnCheckedChangeListener { _, isChecked ->
                mainViewModel.setBassBoostEnabled(isChecked)
            }

            switch3.setOnCheckedChangeListener { _, isChecked ->
                mainViewModel.setLoudnessEnabled(isChecked)
            }

            switch4.setOnCheckedChangeListener { _, isChecked ->
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
            gains = listOf(
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
            frequencies = listOf(
                textView31,
                textView62,
                textView125,
                textView250,
                textView500,
                textView1k,
                textView2K,
                textView4K,
                textView8K,
                textView16K,
                )
            frequencies.forEach{textView ->
                textView.setTextColor(ContextCompat.getColor(this@MainActivity,R.color.white))
            }

            gains.forEach { textView ->
                textView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
            }


            setSeekBarEnabled({ mainViewModel.setBassBoostStrength(it) }, seekbarBaseboost)
            setSeekBarEnabled({ mainViewModel.setLoudnessStrength(it) }, seekbarLoudness)


            // SeekBar의 진행 막대와 썸 색상을 변경합니다.
            seekBars.forEach { seekBar ->
                // 진행 막대 색상 변경
                seekBar.progressDrawable.setColorFilter(
                    ContextCompat.getColor(this@MainActivity, R.color.white),
                    PorterDuff.Mode.SRC_IN
                )
                // 썸 색상 변경
                seekBar.thumb.setColorFilter(
                    ContextCompat.getColor(this@MainActivity, R.color.white),
                    PorterDuff.Mode.SRC_IN
                )
            }
            seekBars.forEachIndexed { index, seekBar ->
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        val num = progress - 15
                        mainViewModel.setEqualizerGainByIndex(index, num.toFloat())
                        gains[index].text = num.toString()
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
            R.layout.row_spinner,
            presetList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner1.adapter = adapter
        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    presets[p2].map.toList().forEachIndexed { index, pair ->
                        seekBars[index].progress = pair.second.toInt() + 15
                    }

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setSeekBarEnabled(method: (strength: Int) -> Unit, view: CircularSeekBar) {

        view.setOnSeekBarChangeListener(object :
            CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean
            ) {
                method(progress.toInt())
                Log.d(TAG, progress.toString())
            }
            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {}
            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::visualizer.isInitialized) {
            visualizer.release() // 리소스 정리
        }

        mainViewModel.releaseEqualizer()
    }
}
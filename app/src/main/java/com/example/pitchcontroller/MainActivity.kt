package com.example.pitchcontroller

import android.annotation.SuppressLint
import android.content.Context
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
import android.net.Uri
import android.os.Build
import android.os.PersistableBundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.example.pitchcontroller.utils.ForegroundService
import com.example.pitchcontroller.views.viewmodels.MainViewModel
import com.example.pitchcontroller.views.WaveformView

private const val TAG = "MainActivity"
private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {

    private lateinit var seekBars: List<SeekBar>
    private lateinit var gains: List<TextView>
    private lateinit var frequencies: List<TextView>
    private lateinit var visualizer: Visualizer
    private lateinit var waveformView: WaveformView
    private lateinit var audioManager: AudioManager
    private var currentVolume = 0
    private var isNotMuted = true
    private var returningFromSettings = false

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var audioSessionId = 0
    private val permissions =
        when(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true -> arrayOf(
                android.Manifest.permission.POST_NOTIFICATIONS,
                android.Manifest.permission.READ_MEDIA_AUDIO,
                android.Manifest.permission.RECORD_AUDIO
            )
            false -> arrayOf(
                android.Manifest.permission.RECORD_AUDIO
            )
        }
        private val multiplePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                Log.i("test", "multiplePermissionLauncher.launch")
                val resultPermission = it.all{map ->
                    map.value
                }
                if(!resultPermission){
                    Toast.makeText(this, "All Permissions must be allowed!", Toast.LENGTH_SHORT).show()
                    permissionCheckAlertDialog()
                }
                else init()
            }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            checkPermissions()
            setContentView(binding.root)
        }

        private fun checkPermissions() {
            Log.i("test", "checkPermissions")
            when {
                (permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }) -> init()
                (ActivityCompat.shouldShowRequestPermissionRationale (this, android.Manifest.permission.POST_NOTIFICATIONS)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_AUDIO)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) -> permissionCheckAlertDialog()
                else -> multiplePermissionLauncher.launch(permissions)

            }
        }


    fun permissionCheckAlertDialog(){
        Log.i("test", "permissionCheckAlertDialog")
        val builder = AlertDialog.Builder(this).setCancelable(false)
        builder.setMessage("All Permissions must be allowed.").setTitle("Permission Check").setPositiveButton("OK"){
                _, _ ->
            multiplePermissionLauncher.launch(permissions)
        }.setNeutralButton("Go to Settings") { dlg, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
            returningFromSettings = true
            dlg.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        Log.i("test", "onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // 권한이 부여되었을 때의 로직
                    Log.i("test", "Granted")
//                    init()
                } else {
                    // 권한이 거부되었을 때의 로직
                    Log.i("test", "Not Granted")
                }
                return
            }
            // 다른 'case' 라인을 여기에 추가할 수 있습니다.
        }
    }

    @SuppressLint("ResourceType")
    private fun init() {
        Log.i("test", "init")
        audioSessionId = AudioManager.AUDIO_SESSION_ID_GENERATE
        val serviceIntent = Intent(this, ForegroundService::class.java)
        startForegroundService(serviceIntent)

        initVisualizer()
        initAudioManager()

        setSwitch()
        setSeekBar()
        setSpinner()
    }

    private fun initVisualizer() {
        if (::visualizer.isInitialized && visualizer.enabled) {
            visualizer.release()
        }
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

    private fun initAudioManager() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        Log.d(TAG, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toString())
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
                isNotMuted = isChecked
                if(isNotMuted){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_SHOW_UI)
                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI)
                }
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
            seekbarVirtualizer.setOnSeekBarChangeListener(object :
                CircularSeekBar.OnCircularSeekBarChangeListener {
                override fun onProgressChanged(
                    circularSeekBar: CircularSeekBar?,
                    progress: Float,
                    fromUser: Boolean
                ) {
                    if(isNotMuted)
                        currentVolume = (progress * 15 / 100).toInt()
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_SHOW_UI)
                }
                override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {}
                override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}
            })


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

    override fun onResume() {
        super.onResume()
        if (returningFromSettings) {
            checkPermissions()
        }
        returningFromSettings = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::visualizer.isInitialized) {
            visualizer.release() // 리소스 정리
        }
        val serviceIntent = Intent(this, ForegroundService::class.java)
        stopService(serviceIntent)
        mainViewModel.releaseEqualizer()
    }
}
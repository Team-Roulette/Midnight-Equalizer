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
import com.example.pitchcontroller.databinding.ActivityMainBinding
import com.example.pitchcontroller.models.presetList
import com.example.pitchcontroller.models.presets
import com.example.pitchcontroller.utils.DynamicsProcessingService
import me.tankery.lib.circularseekbar.CircularSeekBar
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi

private const val TAG = "MainActivity"
const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    lateinit var seekBars: List<SeekBar>
    lateinit var textViews: List<TextView>

    var audioPlayer: DynamicsProcessingService? = null
  
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaProjection: MediaProjection
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var config: AudioPlaybackCaptureConfiguration
    private lateinit var audioRecord: AudioRecord
    private val bufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_STEREO,
        AudioFormat.ENCODING_PCM_16BIT
    )
    private val audioBuffer = ShortArray(bufferSize)
    private val screenCapture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.i("test", "${it.resultCode}")
        if(it.resultCode == RESULT_OK) {
            mediaProjection = mediaProjectionManager.getMediaProjection(it.resultCode, it.data!!)
            initAudioPlaybackCapture()
        }
    }
    private val permissions = arrayOf(
        android.Manifest.permission.POST_NOTIFICATIONS,
        android.Manifest.permission.READ_MEDIA_AUDIO,
//        android.Manifest.permission.CAPTURE_AUDIO_OUTPUT,
        android.Manifest.permission.RECORD_AUDIO
    )
    private val multiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            val resultPermission = it.all{map ->
                map.value
            }
            if(!resultPermission){
                //finish()
                Toast.makeText(this, "모든 권한 승인되어야 함", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioPlayer = DynamicsProcessingService()
        //audioPlayer?.mediaPlayer?.start()
        binding = ActivityMainBinding.inflate(layoutInflater)
        checkPermissions()
        init()
        setContentView(binding.root)
        initLayout()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissions() {
        Log.i("test", "checkPermissions")
        if (!permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }) multiplePermissionLauncher.launch(permissions)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.i("test", "onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // 권한이 부여되었을 때의 로직
                    Log.i("test", "Granted")
                } else {
                    // 권한이 거부되었을 때의 로직
                    Log.i("test", "Not Granted")
                }
                return
            }
            // 다른 'case' 라인을 여기에 추가할 수 있습니다.
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun init() {
        Log.i("test", "init")
        val serviceIntent = Intent(this, ForegroundService::class.java)
        startForegroundService(serviceIntent)
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent()
        screenCapture.launch(screenCaptureIntent)
        binding.apply {
            btn.setOnClickListener {
//                AudioController.requestAudioFocus()
                changeAudioCaptureState()
            }
        }
//        val mediaSession = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
//        val listener = ComponentName(this, NotificationListenerService::class.java)
//        mediaSession.getActiveSessions(listener).forEach {
//            Log.i("test", it.toString())
//        }

//        val equalizer = Equalizer(0, myMediaPlayer.audioSessionId)
//        equalizer.enabled = true // 이퀄라이저 활성화
//        val bands = equalizer.numberOfBands // 사용 가능한 밴드의 수
//
//        for (i in 0 until bands) {
//            equalizer.setBandLevel(i.toShort(), myBandLevel) // 각 밴드의 레벨 조정
//        }
    }

    private fun changeAudioCaptureState() {
        Log.i("test", "startAudioCapture")
        initAudioPlaybackCapture()
        if (AudioCaptureData.isReady.value == false) return
        Log.i("test", "AudioCaptureData.isReady")

        if (AudioCaptureData.isRecording.value == false) {
            AudioCaptureData.isRecording.postValue(true)
            audioRecord.startRecording()

            // 오디오 처리 및 출력 준비
//            val audioTrack = AudioTrack.Builder()
//                .
//            audioTrack.play()

            Thread {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO)
                // 오디오 레코드 시작
                while (AudioCaptureData.isRecording.value!! && audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    Log.i("test", "Record State : Recording")
                    // 오디오 데이터 읽기
                    val readResult = audioRecord.read(audioBuffer, 0, audioBuffer.size)
                    if (readResult > 0) {
                        // 오디오 데이터 처리 (여기서는 단순히 읽은 바이트 수를 로그로 기록)
                        Log.i("test", "Read $readResult bytes from audio record")
                    }

                    Thread.sleep(1000)
                }
                // 오디오 레코드 정지 및 자원 해제
                audioRecord.stop()
                audioRecord.release()
            }.start()
            binding.btn.text = "Now Recording..."
        }
        else {
            AudioCaptureData.isRecording.postValue(false)
            binding.btn.text = "Start Recording"
        }
    }

    @SuppressLint("MissingPermission")
    private fun initAudioPlaybackCapture() {
        Log.i("test", "initAudioPlaybackCapture")
        config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        checkPermissions()

        audioRecord = AudioRecord.Builder()
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(44100)
                    .setChannelMask(AudioFormat.CHANNEL_IN_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(1024)
            .setAudioPlaybackCaptureConfig(config) // 오디오 재생 캡처 구성 설정
            .build()
        AudioCaptureData.isReady.postValue(true)
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
                audioPlayer?.setBassBoostEnabled(isChecked)
            }

            switch3.setOnCheckedChangeListener { _, isChecked ->
                audioPlayer?.setVirtualizerEnable(isChecked)
            }

            switch4.setOnCheckedChangeListener { _, isChecked ->
                audioPlayer?.setLoudnessEnhancerEnabled(isChecked)
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
            presetList
        )


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
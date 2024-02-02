package com.example.pitchcontroller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.pitchcontroller.databinding.ActivityMainBinding

const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissions()
        init()
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

                    audioRecord.
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
                    .build())
            .setBufferSizeInBytes(1024)
            .setAudioPlaybackCaptureConfig(config) // 오디오 재생 캡처 구성 설정
            .build()
        AudioCaptureData.isReady.postValue(true)
    }
}
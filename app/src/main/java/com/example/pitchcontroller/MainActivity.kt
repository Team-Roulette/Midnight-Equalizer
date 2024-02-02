package com.example.pitchcontroller

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import com.example.pitchcontroller.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    lateinit var seekBars:List<SeekBar>
    lateinit var textViews:List<TextView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initlayout()

    }

    @SuppressLint("ResourceType")
    private fun initlayout() {
        binding.apply {
            seekBars=listOf(
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
            textViews=listOf(
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
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        textViews[index].text = (progress - 15).toString()
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

        val presetList = arrayListOf<String>("Normal", "Classical", "Dance", "Flat", "Heavy Metal", "Hip Hop", "Jazz", "Pop", "Rock", "Acoustic", "Vocal Boost", "Treble Boost", "Bass Boost", "Deep House", "EDM", "R&B", "Dream")
        val adapter = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, presetList)
        binding.spinner1.adapter = adapter
        binding.spinner1.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }




    }
}
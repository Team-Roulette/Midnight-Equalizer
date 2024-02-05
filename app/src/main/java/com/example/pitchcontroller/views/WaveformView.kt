package com.example.pitchcontroller.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class WaveformView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private var waveform: ByteArray? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A237E") // 더 부드러운 파란색
        strokeWidth = 1f // 더 얇은 선
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND // 선의 끝을 둥글게 처리
    }

    fun updateWaveform(waveform: ByteArray) {
        this.waveform = waveform
        invalidate() // 뷰를 다시 그리도록 요청
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        waveform?.let {
            // 웨이브폼 데이터를 사용하여 시각화를 그립니다.
            drawWaveform(it, canvas)
        }
    }

    private fun drawWaveform(waveform: ByteArray, canvas: Canvas) {
//        1. 두꺼운 선으로 나타내는 방법
//        val width = width.toFloat()
//        val height = height.toFloat()
//        val centerY = height / 2
//
//        for (i in waveform.indices) {
//            val x = i.toFloat() * width / waveform.size
//            val y = waveform[i].toFloat() / Byte.MAX_VALUE * centerY + centerY
//            canvas.drawLine(x, centerY, x, y, paint)
//        }
//        2. 선으로 나타내는 방법
//        val path = Path()
//        path.moveTo(0f, height / 2f)
//
//        val sliceWidth = width.toFloat() / waveform.size
//        for (i in waveform.indices) {
//            val x = sliceWidth * i
//            val y = (waveform[i].toFloat() / Byte.MAX_VALUE) * height / 4 + height / 2
//            if (i == 0) {
//                path.moveTo(x, y)
//            } else {
//                path.lineTo(x, y)
//            }
//        }
//        canvas.drawPath(path, paint)
//      3.
//        val path = Path()
//        if (waveform.isNotEmpty()) {
//            path.moveTo(0f, height / 2f)
//            for (i in waveform.indices) {
//                val x = i.toFloat() * width / waveform.size
//                val y = waveform[i].toFloat() / Byte.MAX_VALUE * height / 2 + height / 2
//                if (i == 0) {
//                    path.moveTo(x, y)
//                } else {
//                    val prevX = (i - 1).toFloat() * width / waveform.size
//                    val prevY = waveform[i - 1].toFloat() / Byte.MAX_VALUE * height / 2 + height / 2
//                    val ctrlX = (x + prevX) / 2
//                    path.quadTo(ctrlX, prevY, x, y)
//                }
//            }
//        }
//        canvas.drawPath(path, paint)
        val paint = Paint().apply {
            color = Color.parseColor("#FF018786") // 색상 변경
            strokeWidth = 5f // 선의 두께
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val path = Path()
        val widthRatio = width.toFloat() / (waveform.size / 5) // 화면 너비 대비 데이터 포인트의 비율 조정

        if (waveform.isNotEmpty()) {
            // 초기 시작점 설정
            path.moveTo(0f, (height / 2f + ((waveform[0] / 128f) * height / 2f)) / 2)

            // 이전 점과 현재 점 사이에 베지어 곡선을 그림
            for (i in 1 until waveform.size step 2) {
                val x = i * widthRatio
                val y = height / 2f + ((waveform[i] / 128f) * height / 2f)
                val prevX = (i - 1) * widthRatio
                val prevY = height / 2f + ((waveform[i - 1] / 128f) * height / 2f)

                // 현재 점과 이전 점 사이의 중간 지점을 제어점으로 사용
                val controlX = (x + prevX) / 2
                val controlY = (y + prevY) / 2

                // 베지어 곡선의 시작점, 제어점, 끝점 설정
                path.quadTo(prevX, prevY, controlX, controlY)
            }
        }

        canvas.drawPath(path, paint)
    }
}
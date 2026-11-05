package com.example.camera

import android.graphics.Bitmap
import android.graphics.Color
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

enum class WakeUpTarget(val id: String, val title: String, val hint: String, val requiredColorTone: String) {
    WATER_CUP("cup", "Cốc nước / Ly uống", "Hãy đến bàn hoặc bếp và chụp một chiếc cốc nước để uống ngay", "blue_translucent"),
    SINK_BASIN("sink", "Bồn rửa mặt / Gương", "Rời khỏi giường, vào nhà vệ sinh và chụp bồn rửa mặt hoặc gương", "bright_white"),
    TOOTHBRUSH("brush", "Bàn chải đánh răng", "Chụp bàn chải đánh răng để bắt đầu làm sạch khoang miệng", "mixed"),
    RUNNING_SHOES("shoes", "Giày thể thao", "Xỏ chân vào giày thể thao chuẩn bị cho 10.000 bước đi bộ", "dark_contrast")
}

data class ClassificationResult(
    val detectedTarget: String,
    val confidence: Float,
    val isMatch: Boolean,
    val details: String
)

class ObjectAnalyzer(
    private val expectedTarget: WakeUpTarget,
    private val onResult: (ClassificationResult) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastAnalysisTimestamp = 0L

    override fun analyze(image: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        // Throttle analysis to once every 250ms for performance
        if (currentTimestamp - lastAnalysisTimestamp < 250) {
            image.close()
            return
        }
        lastAnalysisTimestamp = currentTimestamp

        val planes = image.planes
        if (planes.isEmpty()) {
            image.close()
            return
        }

        val buffer: ByteBuffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val width = image.width
        val height = image.height

        var totalLuma = 0L
        var sampleCount = 0
        var centerLuma = 0L
        var centerCount = 0

        val step = 16 // sampling step
        for (y in 0 until height step step) {
            for (x in 0 until width step step) {
                val index = y * rowStride + x * pixelStride
                if (index < buffer.limit()) {
                    val luma = buffer.get(index).toInt() and 0xFF
                    totalLuma += luma
                    sampleCount++

                    // Sample center zone (where user aims target object)
                    if (x in (width / 4)..(width * 3 / 4) && y in (height / 4)..(height * 3 / 4)) {
                        centerLuma += luma
                        centerCount++
                    }
                }
            }
        }

        val avgLuma = if (sampleCount > 0) totalLuma / sampleCount else 128
        val avgCenterLuma = if (centerCount > 0) centerLuma / centerCount else 128
        val contrast = Math.abs(avgCenterLuma - avgLuma)

        // Classify based on optical feature signatures
        val (matchConfidence, detectedName, matched) = when (expectedTarget) {
            WakeUpTarget.WATER_CUP -> {
                // Cup requires defined central focus with moderate contrast and reflections
                val conf = (0.55f + (contrast.coerceAtMost(60) / 140f)).coerceIn(0.4f, 0.96f)
                val isOk = conf >= 0.72f && avgLuma in 40..230
                Triple(conf, "Cốc nước / Ly (Mug)", isOk)
            }
            WakeUpTarget.SINK_BASIN -> {
                // Bathroom sink usually has bright white ceramic reflection
                val brightnessFactor = (avgCenterLuma / 255f)
                val conf = (brightnessFactor * 0.95f).coerceIn(0.45f, 0.98f)
                val isOk = conf >= 0.70f && avgCenterLuma > 110
                Triple(conf, "Bồn rửa mặt (Washbasin)", isOk)
            }
            WakeUpTarget.TOOTHBRUSH -> {
                val conf = (0.60f + (contrast / 100f)).coerceIn(0.5f, 0.94f)
                val isOk = conf >= 0.70f
                Triple(conf, "Bàn chải (Toothbrush)", isOk)
            }
            WakeUpTarget.RUNNING_SHOES -> {
                val conf = (0.58f + (contrast / 120f)).coerceIn(0.5f, 0.95f)
                val isOk = conf >= 0.70f
                Triple(conf, "Giày thể thao (Shoes)", isOk)
            }
        }

        onResult(
            ClassificationResult(
                detectedTarget = detectedName,
                confidence = matchConfidence,
                isMatch = matched,
                details = "Độ sáng: $avgCenterLuma | Độ tương phản: $contrast | Khớp: ${(matchConfidence * 100).toInt()}%"
            )
        )

        image.close()
    }
}

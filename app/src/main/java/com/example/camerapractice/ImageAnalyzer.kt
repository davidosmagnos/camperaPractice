package com.example.camerapractice

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.media.Image
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.ByteArrayOutputStream

class ImageAnalyzer(context : Context,surface:SurfaceView) : ImageAnalysis.Analyzer{

    val options: ObjectDetector.ObjectDetectorOptions = ObjectDetector.ObjectDetectorOptions.builder()
    .setMaxResults(1)
    .setScoreThreshold(0.5f)
    .build()
    val detector: ObjectDetector = ObjectDetector.createFromFileAndOptions(context,"Dog_Detector_metadata.tflite",options)
    val box = SurfaceOverlay(surface)
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val frame = imageProxy.image
        val bitmap = frame?.toBitmap()
        val tensorIm = TensorImage.fromBitmap(bitmap)
        val results:List<Detection> = detector.detect(tensorIm)
        box.draw(results)
        imageProxy.close()
    }
    fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val vuBuffer = planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

}
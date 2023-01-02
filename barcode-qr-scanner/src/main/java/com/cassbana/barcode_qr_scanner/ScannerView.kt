package com.cassbana.barcode_qr_scanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.cassbana.barcode_qr_scanner.databinding.ScannerViewBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Keep
class ScannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ScannerViewBinding =
        ScannerViewBinding.inflate(LayoutInflater.from(context), this, true)

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var cameraController: LifecycleCameraController

    private val detectedValues by lazy { mutableListOf<String>() }

    fun start(
        lifecycleOwner: LifecycleOwner,
        onBarcodeDetected: (barcode: String) -> Unit,
        onPermissionNeeded: (permissions: Array<String>) -> Unit
    ) {
        // check permissions
        if (allPermissionsGranted()) {
            startCamera(lifecycleOwner, onBarcodeDetected)
        } else {
            onPermissionNeeded(REQUIRED_PERMISSIONS)
        }
    }

    private fun startCamera(
        lifecycleOwner: LifecycleOwner,
        onBarcodeDetected: (barcode: String) -> Unit
    ) {
        cameraExecutor = Executors.newSingleThreadExecutor()

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)



        startScanning(barcodeScanner, onBarcodeDetected)

        val previewView = binding.previewView
        cameraController.bindToLifecycle(lifecycleOwner)
        previewView.controller = cameraController
        handleLifeCycle(lifecycleOwner)


    }

    private fun handleLifeCycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    cameraExecutor.shutdown()
                    barcodeScanner.close()
                    detectedValues.clear()
                }
            }
        })
    }

    private fun startScanning(
        barcodeScanner: BarcodeScanner,
        onBarcodeDetected: (barcode: String) -> Unit
    ) {
        detectedValues.clear()
        cameraController = LifecycleCameraController(context)
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(context)
            ) { result: MlKitAnalyzer.Result? ->
                val barcodeResults = result?.getValue(barcodeScanner)
                if ((barcodeResults == null) ||
                    (barcodeResults.size == 0) ||
                    (barcodeResults.first() == null)
                ) {
                    return@MlKitAnalyzer
                }

                Log.d("Omar", barcodeResults.toTypedArray().map { it.rawValue }.toString())
                println(barcodeResults.toTypedArray())

                detectedValues.add(barcodeResults.first().rawValue!!)
                if (detectedValues.size >= 50) {
                    val numbersByElement = detectedValues.groupingBy { it }.eachCount()
                    onBarcodeDetected(numbersByElement.maxBy { it.value }.key)
                    cameraController.unbind()
                    cameraExecutor.shutdown()
                    barcodeScanner.close()
                    return@MlKitAnalyzer
                }
            }
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        val REQUIRED_PERMISSIONS by lazy {
            mutableListOf(
                Manifest.permission.CAMERA
            ).toTypedArray()
        }
    }
}
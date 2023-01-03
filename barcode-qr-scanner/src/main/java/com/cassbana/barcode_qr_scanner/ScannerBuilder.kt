package com.cassbana.barcode_qr_scanner

import android.content.Context
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.core.content.ContextCompat
import com.cassbana.barcode_qr_scanner.algorithm.Algorithm
import com.cassbana.barcode_qr_scanner.algorithm.CollectingResultAlgorithm
import com.cassbana.barcode_qr_scanner.algorithm.Majority
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerBuilder(
    internal var onBarcodeDetected: (barcode: String) -> Unit,
    internal val algorithm: Algorithm = Algorithm.MajorityOfN(50),
    private val stopScanningOnResult: Boolean = false
) {
    private var cameraExecutor: ExecutorService? = null
    private var controller: CameraController? = null

    // TODO:: Add option to specify format
    private val barcodeScanner: BarcodeScanner by lazy {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()
        )
    }

    init {
        onBarcodeDetected = { barcode: String ->
            onBarcodeDetected(barcode)
            if (stopScanningOnResult) {
                removeAnalyzer()
            }
        }
    }

    private val collectingResultAlgorithm: CollectingResultAlgorithm by lazy {
        when (algorithm) {
            is Algorithm.MajorityOfN -> Majority(algorithm.n, onBarcodeDetected)
        }
    }

    private fun removeAnalyzer() {
        controller?.clearImageAnalysisAnalyzer()
    }


    fun startScanning(cameraController: CameraController, context: Context) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        controller = cameraController
        controller?.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(context)
            ) { result: MlKitAnalyzer.Result? ->
                collectingResultAlgorithm.onNewResult(result, barcodeScanner)
            }
        )
    }

    fun onDestroy() {
        controller?.clearImageAnalysisAnalyzer()
        controller = null
        collectingResultAlgorithm.clear()
        cameraExecutor?.shutdown()
    }


}
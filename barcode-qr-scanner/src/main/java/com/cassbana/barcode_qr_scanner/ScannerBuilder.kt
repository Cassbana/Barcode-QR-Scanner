package com.cassbana.barcode_qr_scanner

import android.content.Context
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.core.content.ContextCompat
import com.cassbana.barcode_qr_scanner.algorithm.Algorithm
import com.cassbana.barcode_qr_scanner.algorithm.CollectingResultAlgorithm
import com.cassbana.barcode_qr_scanner.algorithm.DuplicateSequence
import com.cassbana.barcode_qr_scanner.algorithm.Majority
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Builder for the scanning feature.
 * @param onBarcodeDetected function that is called once the barcode has been detected.
 * @param algorithm specify the algorithm used to collect result. for more info check the ReadMe file.
 * @param stopScanningOnResult stop after detection or not, if true, call ScannerView.startScanning() to start scanning again.
 */
class ScannerBuilder(
    internal val onBarcodeDetected: (barcode: String) -> Unit,
    internal val algorithm: Algorithm = Algorithm.MajorityOfN(20),
    private val stopScanningOnResult: Boolean = false
) {
    private var cameraExecutor: ExecutorService? = null
    private var controller: CameraController? = null
    private var isStopped: Boolean = false

    // TODO:: Add option to specify format
    private val barcodeScanner: BarcodeScanner by lazy {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()
        )
    }

    private val callback = { barcode: String ->
        if (!isStopped) {
            onBarcodeDetected(barcode)
        }
        if (stopScanningOnResult) {
            pauseScanning()
        }
    }

    private val collectingResultAlgorithm: CollectingResultAlgorithm by lazy {
        when (algorithm) {
            is Algorithm.MajorityOfN -> Majority(algorithm.n, callback)
            is Algorithm.DuplicateSequence -> DuplicateSequence(algorithm.n, callback)
        }
    }

    private fun removeAnalyzer() {
        controller?.clearImageAnalysisAnalyzer()
    }

    fun startScanning(cameraController: CameraController, context: Context) {
        isStopped = false
        removeAnalyzer()
        cameraExecutor = Executors.newSingleThreadExecutor()
        controller = cameraController
        controller?.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(context)
            ) { result: MlKitAnalyzer.Result? ->
                if (!isStopped) {
                    collectingResultAlgorithm.onNewResult(result, barcodeScanner)
                }
            }
        )
    }

    fun pauseScanning() {
        isStopped = true
    }

    fun resumeScanning() {
        isStopped = false
    }

    fun onDestroy() {
        controller?.clearImageAnalysisAnalyzer()
        controller = null
        collectingResultAlgorithm.clear()
        cameraExecutor?.shutdown()
    }
}

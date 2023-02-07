package com.cassbana.barcode_qr_scanner.algorithm

import androidx.camera.mlkit.vision.MlKitAnalyzer
import com.google.mlkit.vision.barcode.BarcodeScanner

abstract class CollectingResultAlgorithm {
    protected abstract val detectedValues: MutableList<String>
    internal abstract fun onNewResult(
        analyzerResult: MlKitAnalyzer.Result?,
        scanner: BarcodeScanner
    )

    protected abstract fun calculate()
    internal open fun clear() {
        detectedValues.clear()
    }
}

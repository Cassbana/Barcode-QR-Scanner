package com.cassbana.barcode_qr_scanner.algorithm

import androidx.camera.mlkit.vision.MlKitAnalyzer
import com.cassbana.barcode_qr_scanner.Format
import com.cassbana.barcode_qr_scanner.FormatUtil
import com.google.mlkit.vision.barcode.BarcodeScanner

class DuplicateSequence(
    private val n: Int,
    private val onBarcodeDetected: (Barcode: String) -> Unit,
    private val format: Format
) : CollectingResultAlgorithm() {

    override val detectedValues: MutableList<String> by lazy {
        mutableListOf()
    }

    override fun onNewResult(analyzerResult: MlKitAnalyzer.Result?, scanner: BarcodeScanner) {
        val barcodeResults = analyzerResult?.getValue(scanner)

        if (((barcodeResults.isNullOrEmpty()) || (barcodeResults.first() == null)) ||
            !FormatUtil.matches(format = format, barcodeResults.first())
        ) {
            return
        }
        barcodeResults.first().rawValue?.let {
            if (detectedValues.isEmpty()) {
                detectedValues.add(it)
                return@let
            }
            if (detectedValues.lastOrNull() == it) {
                detectedValues.add(it)
                calculate()
            } else {
                clear()
            }
        }
    }

    override fun calculate() {
        if (detectedValues.size >= n) {
            onBarcodeDetected(detectedValues.first())
            clear()
        }
    }
}

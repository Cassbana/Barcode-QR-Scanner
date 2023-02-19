package com.cassbana.barcode_qr_scanner.algorithm

import androidx.camera.mlkit.vision.MlKitAnalyzer
import com.cassbana.barcode_qr_scanner.Format
import com.cassbana.barcode_qr_scanner.FormatUtil
import com.google.mlkit.vision.barcode.BarcodeScanner

class Majority(
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
            detectedValues.add(it)
            calculate()
        }
    }

    override fun calculate() {
        if (detectedValues.size >= n) {
            val numbersByElement = detectedValues.groupingBy { it }.eachCount()
            numbersByElement.maxByOrNull { it.value }?.key?.let { onBarcodeDetected(it) }
            clear()
        }
    }
}

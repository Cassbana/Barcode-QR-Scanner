package com.cassbana.barcode_qr_scanner

import com.google.mlkit.vision.barcode.common.Barcode

enum class Format {
    ALL_FORMATS,
    BARCODE,
    QR_CODE
}

internal object FormatUtil {
    fun matches(format: Format, barcode: Barcode): Boolean {
        if (barcode.format == Barcode.FORMAT_UNKNOWN) return false
        if (format == Format.ALL_FORMATS) return true

        return if (format == Format.QR_CODE && barcode.format == Barcode.FORMAT_QR_CODE) {
            true
        } else !(format != Format.QR_CODE && barcode.format == Barcode.FORMAT_QR_CODE)
    }
}

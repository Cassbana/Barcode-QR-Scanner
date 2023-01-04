package com.cassbana.barcode_qr_scanner.algorithm

sealed class Algorithm {
    class MajorityOfN(val n: Int) : Algorithm()
    class DuplicateSequence(val n: Int): Algorithm()
}
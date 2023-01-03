package com.cassbana.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cassbana.barcode_qr_scanner.ScannerBuilder
import com.cassbana.barcode_qr_scanner.algorithm.Algorithm
import com.cassbana.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        startCamera()

        viewBinding.button.setOnClickListener {
            viewBinding.scanner.startScanning()
        }
    }

    private fun startCamera() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_PERMISSIONS
            )
            return
        }
        viewBinding.scanner.start(
            lifecycleOwner = this@MainActivity,
            scannerBuilder = ScannerBuilder(
                onBarcodeDetected = {
                    Toast.makeText(this, "Detected $it", Toast.LENGTH_SHORT).show()
                },
                algorithm = Algorithm.MajorityOfN(50),
                stopScanningOnResult = false
            )
        )
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
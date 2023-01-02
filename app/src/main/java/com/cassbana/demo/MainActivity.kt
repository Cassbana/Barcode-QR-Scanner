package com.cassbana.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.cassbana.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        startCamera()

        viewBinding.button.setOnClickListener {
            startCamera()
        }
    }

    private fun startCamera() {
        viewBinding.scanner.start(
            this@MainActivity,
            onBarcodeDetected = {
                Toast.makeText(this, "Detected $it", Toast.LENGTH_SHORT).show()
            }, onPermissionNeeded = {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    it,
                    REQUEST_CODE_PERMISSIONS
                )
            })
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
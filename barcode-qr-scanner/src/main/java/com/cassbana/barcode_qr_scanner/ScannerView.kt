package com.cassbana.barcode_qr_scanner

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.annotation.RequiresPermission
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.cassbana.barcode_qr_scanner.databinding.ScannerViewBinding

@Keep
class ScannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var binding: ScannerViewBinding? =
        ScannerViewBinding.inflate(LayoutInflater.from(context), this, true)

    private lateinit var builder: ScannerBuilder
    private lateinit var cameraController: LifecycleCameraController


    @RequiresPermission(Manifest.permission.CAMERA)
    fun start(scannerBuilder: ScannerBuilder, lifecycleOwner: LifecycleOwner) {
        builder = scannerBuilder
        startCamera(lifecycleOwner)
        startScanning()
    }


    private fun startCamera(lifecycleOwner: LifecycleOwner) {
        cameraController = LifecycleCameraController(context)
        cameraController.bindToLifecycle(lifecycleOwner)
        binding?.previewView?.controller = cameraController
        handleLifeCycle(lifecycleOwner)
    }


    private fun handleLifeCycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    builder.onDestroy()
                    binding = null
                }
            }
        })
    }

    fun startScanning() {
        builder.startScanning(cameraController, context)
    }

}
# Barcode/QR Scanner
A barcode and QR code scanner lifecycle aware android view based on CameraX API and ML Kit's Barcode Scanner.
Check: [ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)

**Please note: this is an initial release, there is still more work to be done and the library needs to be customizable.**

## Download
Include jitpack
``` gradle
 allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add in your app module
``` gradle
 implementation 'com.github.Cassbana:Barcode-QR-Scanner:0.1.0'
```

## Usage

Add the scanner view to your layout
``` xml
<com.cassbana.barcode_qr_scanner.ScannerView
        android:id="@+id/scanner"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

Start scanning
``` kotlin
scanner.start(
            lifecycleOwner = this@MainActivity,
            onBarcodeDetected = {
                Toast.makeText(this, "Detected $it", Toast.LENGTH_SHORT).show()
            },
            onPermissionNeeded = { permissions ->
            // request the required permissions and call 'start' function again when granted.
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    permissions,
                    REQUEST_CODE_PERMISSIONS
                )
            })
```
**When you get the required permissions call the start function again**

# Supported Formats

 -  Code 128 (`FORMAT_CODE_128`)
-   Code 39 (`FORMAT_CODE_39`)
-   Code 93 (`FORMAT_CODE_93`)
-   Codabar (`FORMAT_CODABAR`)
-   EAN-13 (`FORMAT_EAN_13`)
-   EAN-8 (`FORMAT_EAN_8`)
-   ITF (`FORMAT_ITF`)
-   UPC-A (`FORMAT_UPC_A`)
-   UPC-E (`FORMAT_UPC_E`)
-   QR Code (`FORMAT_QR_CODE`)
-   PDF417 (`FORMAT_PDF417`)
-   Aztec (`FORMAT_AZTEC`)

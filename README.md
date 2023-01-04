
# Barcode/QR Scanner  
A barcode and QR code scanner lifecycle aware android view based on CameraX API and ML Kit's Barcode Scanner.  
Check: [ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)  
  
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
 implementation 'com.github.Cassbana:Barcode-QR-Scanner:0.2.0'
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
	lifecycleOwner = this,  
	scannerBuilder = ScannerBuilder(  
        	onBarcodeDetected = { Toast.makeText(this, "Detected $it", Toast.LENGTH_SHORT).show() },  
		algorithm = Algorithm.MajorityOfN(50),  
		stopScanningOnResult = false
	)  
)
 ```  
 
**Please note that the camera permission should be requested at run time before calling the start function.**  

# Builder
 **LifeCycleOwner**: The view is lifecycle aware, you can pass your activity or fragment lifecycle's owner.
 
 **onBarcodeDetected**: Lambda function that's invoked once the scanning has been done.
 
 **stopScannningOnResult**: Specify whether you want the scanning process to stop after finished or not. Default value is false, If you need to start scanning again call the following function.
 ```kotlin
scanner.startScanning()
 ``` 
 
 ## Collecting Result Algorithm 
 The scanner is fast, it can detect multiple values in a second but if the camera is shaken or out of focus the result might be inaccurate, to compensate that we have collecting result algorithms.
 
### MajorityOfN
The scanner collects `n` results and outputs the most common result.
```kotlin
ScannerBuilder(
	onBarcodeDetected = { ... },
	algorithm = Algorithm.MajorityOfN(n = 10)
)
```
`MajorityOfN(n = 20)` is the default collecting algorithm.

### DuplicateSequence
The scanner collects results till we have `n` duplicate sequence.
```kotlin
ScannerBuilder(
	onBarcodeDetected = { ... },
	algorithm = Algorithm.DuplicateSequence(n = 10)
)
```



# Supported Formats  
  
 - Code 128 (`FORMAT_CODE_128`)  
- Code 39 (`FORMAT_CODE_39`)  
- Code 93 (`FORMAT_CODE_93`)  
- Codabar (`FORMAT_CODABAR`)  
- EAN-13 (`FORMAT_EAN_13`)  
- EAN-8 (`FORMAT_EAN_8`)  
- ITF (`FORMAT_ITF`)  
- UPC-A (`FORMAT_UPC_A`)  
- UPC-E (`FORMAT_UPC_E`)  
- QR Code (`FORMAT_QR_CODE`)  
- PDF417 (`FORMAT_PDF417`)  
- Aztec (`FORMAT_AZTEC`)

package com.indrif.vms.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.ocrsampleapp.Points
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.prefs.PreferenceHandler
import com.indrif.vms.models.FaceDetectionModel
import com.indrif.vms.models.Model
import com.indrif.vms.models.TextRecognitionModel
import com.indrif.vms.utils.AppConstants
import com.indrif.vms.utils.CommonUtils
import com.otaliastudios.cameraview.Facing
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_id_proof_selection.*
import okhttp3.RequestBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class IdProofSelectionActivity : BaseActivty() {
    var selectedIdProof = ""
    private var count: Int = 0
    private var userChoosenTask: String? = null
    private var mImageUri: Uri? = null
    private var textRecognitionModels = ArrayList<TextRecognitionModel>()
    public var mutableImage: Bitmap? = null
    public var mutableImageTest: Bitmap? = null
    private var uri: Uri? = null
    private var fulltext = ArrayList<String>()
    private var name = ArrayList<String>()
    private var employer = ArrayList<String>()
    private var dob = ArrayList<String>()
    private var blocktext = ArrayList<Model>()
    private var linetext = ArrayList<Model>()
    private var resultUri: Uri? = null
    private var id = ArrayList<String>()
    private var pd: ProgressDialog? = null
    var wpermit: Int = 0
    var wpermitend: Int = 0
    var sectorleft:Int =0
    var btm: Int = 0
    var top: Int = 0
    var dobbtm: Int = 0
    var dobtop: Int = 0
    var btm1: Int = 0
    var top1: Int = 0
    var ftop = 0
    var fbottom = 0
    var left = 0
    var userComingBy = ""
    private var cameraFacing: Facing = Facing.FRONT

    private val faceDetectionModels = ArrayList<FaceDetectionModel>()
    private var cropped: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id_proof_selection)
        setAdapter()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_check_in_back -> {
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }
            R.id.iv_check_in_home -> {
                val i = Intent(applicationContext, DashBoardActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }

            R.id.btn_scan_id -> {
                if (selectedIdProof.equals("SELECT ID TYPE") || selectedIdProof.equals("OTHER")) {
                    if (selectedIdProof.equals("OTHER")) {
                        val intent = Intent(this, UserProfileActivity::class.java)
                        val args = Bundle()
                        args.putString("userComingFrom", "IdProofOtherSelection")
                        args.putString("userComingBy", userComingBy)
                        args.putString("IdType", "OTHER")
                        intent.putExtra("BUNDLE", args)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                    } else
                        CommonUtils.showSnackbarMessage(context, resources.getString(R.string.id_type_strg), R.color.colorPrimary)
                } else {
                    permissionCheck(0, 0)
                }
            }
        }
    }
    private fun setAdapter() {
        tv_selected_site.text = PreferenceHandler.readString(applicationContext, PreferenceHandler.SELECTED_SITE, "")
        userComingBy = intent.getStringExtra("userComingBy")
        if (userComingBy.equals("checkIn", false))
            tv_site.text = "Check In"
        else if (userComingBy.equals("checkOut", false))
            tv_site.text = "Check Out"
        val idProofArray = resources.getStringArray(R.array.id_array)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, idProofArray)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        sp_id_proof.setAdapter(adapter)
        sp_id_proof?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedIdProof = idProofArray[position]
            }
        }


    }
    private fun permissionCheck(check: Int, intentfor: Int) {
        when (check) {
            0 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (CommonUtils.requestAllPermissions(this, AppConstants.REQUEST_STORAGE_PERMISSION_CODE)) {
                    selectionIntent(intentfor)
                }
            } else {
                selectionIntent(intentfor)
            }
            1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (CommonUtils.requestAllPermissions(this, AppConstants.REQUEST_READ_STORAGE_PERMISSION_CODE)) {
                    selectionIntent(intentfor)
                }
            } else {
                selectionIntent(intentfor)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            AppConstants.REQUEST_STORAGE_PERMISSION_CODE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCheck(0, 0)
                }

            AppConstants.REQUEST_CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask == "Take Photo")
                        selectionIntent(0)
                }
            }
            AppConstants.REQUEST_READ_STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(getString(R.string.choose_photo), ignoreCase = true))
                        selectionIntent(1)
                }
            }
        }
    }
    private fun selectionIntent(selected: Int) {
        when (selected) {
            0 -> cameraIntent()

        }
    }
    private fun cameraIntent() {
        try {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
            mImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
            startActivityForResult(intent, AppConstants.REQUEST_CAMERAINTENT)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.REQUEST_CAMERAINTENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        CropImage.activity(mImageUri).start(this)
                    } catch (e: Exception) {
                    }
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    resultUri = result.uri
                    showProgressDialog()
                    var image = MediaStore.Images.Media.getBitmap(contentResolver, resultUri)
                    analyzeImageforface(image)
                    analyzeImage(image)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }
    // for face
    private fun analyzeImageforface(image: Bitmap?) {
        if (image == null) {
            Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            return
        }
        faceDetectionModels.clear()
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(image)
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
            .build()
        val faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        faceDetector.detectInImage(firebaseVisionImage)
            .addOnSuccessListener {
                val mutableImage = image.copy(Bitmap.Config.ARGB_8888, true)
                detectFaces(it, mutableImage)
            }
            .addOnFailureListener {
                Toast.makeText(this, "There was some error, Please try again", Toast.LENGTH_SHORT).show()
            }
    }
    private fun detectFaces(faces: List<FirebaseVisionFace>?, image: Bitmap?) {
        if (faces == null || image == null) {
            Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            return
        }
        val canvas = Canvas(image)
        val facePaint = Paint()
        facePaint.color = Color.GREEN
        facePaint.style = Paint.Style.STROKE
        facePaint.strokeWidth = 8F
        val faceTextPaint = Paint()
        faceTextPaint.color = Color.RED
        faceTextPaint.textSize = 40F
        faceTextPaint.typeface = Typeface.DEFAULT_BOLD
        val landmarkPaint = Paint()
        landmarkPaint.color = Color.RED
        landmarkPaint.style = Paint.Style.FILL
        landmarkPaint.strokeWidth = 8F
        try {
            ftop = faces.get(0).boundingBox.top - 30
            fbottom = (faces.get(0).boundingBox.bottom) - 50
            var startx = faces.get(0).boundingBox.left - 45
            var starty = faces.get(0).boundingBox.top - 140
            var width = (faces.get(0).boundingBox.right + 40 - faces.get(0).boundingBox.left) + 60
            var height = ((faces.get(0).boundingBox.bottom + 50) - (faces.get(0).boundingBox.top - 100)) + 50
            cropped = Bitmap.createBitmap(image, startx, starty, width, height)
        } catch (e: Exception) {
            hideProgressDialog()
            CommonUtils.showToastMessage(this, "Please Try Again.")
        }


    }
    // for text
    private fun analyzeImage(image: Bitmap?) {
        if (image == null) {
            Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            return
        }
        mutableImageTest = image
        textRecognitionModels.clear()
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(image)
        val textRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
        textRecognizer.processImage(firebaseVisionImage)
            .addOnSuccessListener {
                recognizeText(it)
            }
            .addOnFailureListener {
                Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            }
    }
    private fun recognizeText(result: FirebaseVisionText?/*, image: Bitmap?*/) {
        if (result == null) {
            Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            return
        }
        clearData()
        var index = 0
        for (block in result.textBlocks) {
            val blockText = block.text
            val blockConfidence = block.confidence
            val blockLanguages = block.recognizedLanguages
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            var right = blockFrame!!.right
            var left = blockFrame!!.left
            var top = blockFrame!!.top
            var bottom = blockFrame!!.bottom
            blocktext.add(Model(blockText, Points(blockFrame!!.bottom, blockFrame!!.left, blockFrame!!.right, blockFrame!!.top)))
            for (line in block.lines) {
                val lineText = line.text
                val lineConfidence = line.confidence
                val lineLanguages = line.recognizedLanguages
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                fulltext.add(lineText)
                Log.d("Text", lineText)
                linetext.add(Model(lineText, Points(lineFrame!!.bottom, lineFrame!!.left, lineFrame!!.right, lineFrame!!.top)))
                for (element in line.elements) {
                    val elementText = element.text
                    val elementConfidence = element.confidence
                    val elementLanguages = element.recognizedLanguages
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                    textRecognitionModels.add(TextRecognitionModel(index++, element.text))
                }
            }
        }
        if (selectedIdProof == "NRIC") {
            for (index in linetext.indices) {
                if (linetext.get(index).text!!.contains("IDENTITY") || linetext.get(index).text!!.contains("1DENTITY") || linetext.get(index).text!!.contains("CARD") || linetext.get(index).text!!.contains("CARD NO")) {
                    id.add(linetext.get(index).text!!)
                }
            }
            var sleft = 0
            var ctop = 0
            for (index in linetext.indices) {
                if (linetext.get(index).text == "Name" || linetext.get(index).text == "Nam") {
                    btm = linetext.get(index).zzbat.bottom
                    top = linetext.get(index).zzbat.top
                }
            }

            for (index in linetext.indices) {
                if (linetext.get(index).text == "Race") {
                    btm1 = linetext.get(index).zzbat.bottom
                    top1 = linetext.get(index).zzbat.top

                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).text == "Date of birth" || linetext.get(index).text == "Dateof birth" || linetext.get(index).text == "Date ofbirth" || linetext.get(index).text == " of birth" || linetext.get(index).text == "Date of") {
                    dobbtm = linetext.get(index).zzbat.bottom
                    dobtop = linetext.get(index).zzbat.top

                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).text == "Country of birth" || linetext.get(index).text == "Countryof birth" || linetext.get(index).text == "Country of") {
                    ctop = linetext.get(index).zzbat.top

                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).text == "Sex" || linetext.get(index).text == "Se") {
                    sleft = linetext.get(index).zzbat.left
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).zzbat.top > ftop && linetext.get(index).zzbat.bottom < fbottom) {
                    name.add(linetext.get(index).text!!)
                }

            }
            for (index in linetext.indices) {
                if (linetext.get(index).text!!.contains("-")) {
                    dob.add(linetext.get(index).text!!)
                }
            }
            var c = 0;
            clearimage(mImageUri!!)
            var stream = ByteArrayOutputStream()
            if(cropped == null)
                return
            cropped!!.compress(Bitmap.CompressFormat.PNG, 100, stream);
            var byteArray = stream!!.toByteArray()
            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
             val imgUri =CommonUtils.getImageUri(this, bmp)
            hideProgressDialog()
            val intent = Intent(this, UserProfileActivity::class.java)
            val args = Bundle()
            args.putString("userComingFrom", "MainActivity")
            args.putString("userComingBy", userComingBy)
            args.putString("selectedIdProof", selectedIdProof)
            args.putStringArrayList("ID", id)
            args.putStringArrayList("Name", name)
            args.putString("imgUri", imgUri.toString())
            intent.putExtra("BUNDLE", args)
            args.putStringArrayList("DOB", dob)
            intent.putExtra("BUNDLE", args)
            startActivity(intent)
        }
        else if (selectedIdProof == "S-PASS") {
            for (index in linetext.indices) {
                if (linetext.get(index).text == "Employer" || (linetext.get(index).text) =="Empioyer" || (linetext.get(index).text) =="Emp" || (linetext.get(index).text) =="Employ") {
                    btm = linetext.get(index).zzbat.bottom
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).text == "Name" || linetext.get(index).text =="Nam" || linetext.get(index).text == "me") {
                    top = linetext.get(index).zzbat.top
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).zzbat.top > btm && linetext.get(index).zzbat.top < top) {
                    employer.add(linetext.get(index).text!!)
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).text!!.contains("S Pass No") || linetext.get(index).text!!.contains("SPass No") || linetext.get(index).text!!.contains("SPass") || linetext.get(index).text!!.contains("SPassN")|| linetext.get(index).text!!.contains("SPassNo") || linetext.get(index).text!!.contains("S PassN"))
                {
                    top1 = linetext.get(index).zzbat.top
                    fbottom = linetext.get(index).zzbat.bottom
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).zzbat.top > top && linetext.get(index).zzbat.top < top1) {
                    name.add(linetext.get(index).text!!)
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).text!!.contains("Sector") || linetext.get(index).text!!.contains("Sec") || linetext.get(index).text!!.contains("Sect") || linetext.get(index).text!!.contains("ector")|| linetext.get(index).text!!.contains("tor")) {
                    left = linetext.get(index).zzbat.left
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).zzbat.top > fbottom && linetext.get(index).zzbat.right < left) {
                    id.add(linetext.get(index).text!!)
                }
            }
            clearimage(mImageUri!!)
            var stream = ByteArrayOutputStream()
            if(cropped == null)
                return
            cropped!!.compress(Bitmap.CompressFormat.PNG, 100, stream);
            var byteArray = stream!!.toByteArray()
            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            val imgUri =CommonUtils.getImageUri(this, bmp)
            if(employer.contains("EAGLE") || employer.contains("SECURITY ") || employer.contains("SERVICES ")) {
                employer.clear()
                employer.add("EAGLE SECURITY SERVICES PTE. LTD.")
            }
            hideProgressDialog()
            val intent = Intent(this, UserProfileActivity::class.java)
            val args = Bundle()
            args.putString("userComingFrom", "MainActivity")
            args.putString("userComingBy", userComingBy)
            args.putString("selectedIdProof", selectedIdProof)
            args.putStringArrayList("Employer", employer)
            args.putStringArrayList("Name", name)
            args.putStringArrayList("ID", id)
            args.putString("imgUri", imgUri.toString())
            intent.putExtra("BUNDLE", args)
            startActivity(intent)
        }
        else if (selectedIdProof == "DRIVING LICENSE") {
            for (ind in linetext.indices) {
                if (linetext.get(ind).text!!.contains("Number") || linetext.get(ind).text!!.contains("mber") || linetext.get(ind).text!!.contains("Numb") || linetext.get(ind).text!!.contains("umber")  || linetext.get(ind).text!!.contains("Nmber")/*|| linetext.get(ind).text!!.contains("Lice") || linetext.get(ind).text!!.contains("Liem")*/) {
                    btm = linetext.get(ind).zzbat.bottom
                    var separated = listOf<String>()
                    if(linetext.get(ind).text!!.contains("Number"))
                     separated = linetext.get(ind).text!!.split("Number")
                    else if(linetext.get(ind).text!!.contains("mber"))
                        separated = linetext.get(ind).text!!.split("mber")
                    else if(linetext.get(ind).text!!.contains("Numb"))
                        separated = linetext.get(ind).text!!.split("Numb")
                    else if(linetext.get(ind).text!!.contains("umber"))
                        separated = linetext.get(ind).text!!.split("umber")
                    separated[0]
                    separated[1]
                    var lnumber = separated[1]
                    if (lnumber.contains(":")) {
                        var licensenum = linetext.get(ind).text!!.split(":")
                        licensenum[0]
                        licensenum[1]
                        id.add(licensenum[1])
                    } else {
                        id.add(lnumber)
                    }

                }
            }
            for (ind in linetext.indices) {
                if (linetext.get(ind).text!!.contains("Date")||linetext.get(ind).text!!.contains("Dat") || linetext.get(ind).text!!.contains("te")) {
                    top = linetext.get(ind).zzbat.top
                }
            }
            for (ind in linetext.indices) {
                if (linetext.get(ind).zzbat.top > btm && linetext.get(ind).zzbat.bottom < top) {
                    name.add(linetext.get(ind).text!!)
                }
            }
            for (ind in linetext.indices) {
                if (linetext.get(ind).text!!.contains("Birth")) {
                    var d = linetext.get(ind).text!!
                    var dobs = d.split("Date")
                    dobs[0]
                    dobs[1]
                    var dateofbirth = dobs[1]
                    if (dateofbirth.contains(":")) {
                        var datofbirth = dateofbirth.split(":")
                        datofbirth[0]
                        datofbirth[1]
                        var db = datofbirth[1]
                        dob.add(db)
                    } else {
                        dob.add(dateofbirth)
                    }
                }
            }
            var c = 0;
            clearimage(mImageUri!!)
            var stream = ByteArrayOutputStream()
            if(cropped == null){
                return
            }
            cropped!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            var byteArray = stream!!.toByteArray()
            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            val imgUri =CommonUtils.getImageUri(this, bmp)
            hideProgressDialog()
            val intent = Intent(this, UserProfileActivity::class.java)
            val args = Bundle()
            args.putString("userComingFrom", "MainActivity")
            args.putString("userComingBy", userComingBy)
            args.putString("selectedIdProof", selectedIdProof)
            args.putStringArrayList("DOB", dob)
            args.putStringArrayList("Name", name)
            args.putStringArrayList("ID", id)
            args.putString("imgUri", imgUri.toString())
            intent.putExtra("BUNDLE", args)
            intent.putExtra("BUNDLE", args)
            startActivity(intent)
        }
        else if (selectedIdProof == "WORK PERMIT") {
            for (index in linetext.indices) {
                if (linetext.get(index).text == "Name" || linetext.get(index).text == "Nam" ) {
                    btm = linetext.get(index).zzbat.top
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).text == "Employer" || linetext.get(index).text == "Emplo" || linetext.get(index).text == "Employor" || linetext.get(index).text == "Emp" ||  linetext.get(index).text == "Empioyer") {
                    btm1 = linetext.get(index).zzbat.top
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).text!!.contains("Sector")) {
                    top1 = linetext.get(index).zzbat.top
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).text == "Occupation") {
                    top = linetext.get(index).zzbat.top
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).text == "Work Permit No." || linetext.get(index).text == "Work Permit No" || linetext.get(index).text == "Work Permit" ) {
                    wpermit = linetext.get(index).zzbat.top
                    wpermitend = linetext.get(index).zzbat.right
                }
            }
            for (index in linetext.indices) {
                if (linetext.get(index).text == "Sector:" || linetext.get(index).text == "Sector" || linetext.get(index).text == "Sect") {
                    sectorleft = linetext.get(index).zzbat.left
                }
            }
            if(top!=0) {
                for (index in linetext.indices) {
                    if (linetext.get(index).zzbat.top > top1 && linetext.get(index).zzbat.bottom < top) {
                        name.add(linetext.get(index).text!!)
                    }
                }
                for (index in linetext.indices) {
                    if (linetext.get(index).zzbat.top > btm1 && linetext.get(index).zzbat.bottom < top1) {
                        employer.add(linetext.get(index).text!!)
                    }
                }
                for (index in linetext.indices)
                {
                    if (linetext.get(index).zzbat.top > wpermit && linetext.get(index).zzbat.right < wpermitend  ) {
                        id.add(linetext.get(index).text!!)
                    }
                }
            }
            else
            {
                for (index in linetext.indices) {
                    if (linetext.get(index).zzbat.top > btm1 && linetext.get(index).zzbat.bottom < btm) {
                        employer.add(linetext.get(index).text!!)
                    }
                }
                for (index in linetext.indices) {
                    if (linetext.get(index).zzbat.top > btm && linetext.get(index).zzbat.bottom < wpermit) {
                        name.add(linetext.get(index).text!!)
                    }
                }
                for (index in linetext.indices)
                {
                    if (linetext.get(index).zzbat.top > wpermit && linetext.get(index).zzbat.right < wpermitend  ) {
                            id.add(linetext.get(index).text!!)
                        }
                }
            }
            var c = 0;
            clearimage(mImageUri!!)
            var stream = ByteArrayOutputStream()
            if(cropped == null)
                return
            cropped!!.compress(Bitmap.CompressFormat.PNG, 100, stream);
            var byteArray = stream!!.toByteArray()
            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            val imgUri =CommonUtils.getImageUri(this, bmp)
            hideProgressDialog()
            val intent = Intent(this, UserProfileActivity::class.java)
            val args = Bundle()
            args.putString("userComingFrom", "MainActivity")
            args.putString("userComingBy", userComingBy)
            args.putString("selectedIdProof", selectedIdProof)
            args.putStringArrayList("Employer", employer)
            args.putStringArrayList("Name", name)
            args.putStringArrayList("ID", id)
            args.putString("imgUri", imgUri.toString())
            intent.putExtra("BUNDLE", args)
            intent.putExtra("BUNDLE", args)
            /*  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
            startActivity(intent)

        }
    }


    private  fun getSelectedIdData(selectedIdProof :String){
        when (selectedIdProof) {
            "NRIC" ->{
                for (index in linetext.indices) {
                    if (linetext.get(index).text!!.contains("IDENTITY")) {
                        id.add(linetext.get(index).text!!)
                    }
                }
                var sleft = 0
                var ctop = 0
                for (index in linetext.indices) {
                    if (linetext.get(index).text == "Name") {
                        btm = linetext.get(index).zzbat.bottom
                        top = linetext.get(index).zzbat.top
                    }
                }
                for (index in linetext.indices) {
                    if (linetext.get(index).text == "Race") {
                        btm1 = linetext.get(index).zzbat.bottom
                        top1 = linetext.get(index).zzbat.top

                    }
                }
                for (index in linetext.indices) {
                    if (linetext.get(index).text == "Date of birth") {
                        dobbtm = linetext.get(index).zzbat.bottom
                        dobtop = linetext.get(index).zzbat.top

                    }
                }
                for (index in linetext.indices) {
                    if (linetext.get(index).text == "Country of birth") {
                        ctop = linetext.get(index).zzbat.top

                    }
                }
                for (index in linetext.indices) {
                    if (linetext.get(index).text == "Sex") {
                        sleft = linetext.get(index).zzbat.left
                    }
                }
                for (index in linetext.indices) {
                    if (linetext.get(index).zzbat.top > ftop && linetext.get(index).zzbat.bottom < fbottom) {
                        name.add(linetext.get(index).text!!)
                    }

                }
                for (index in linetext.indices) {
                    if (linetext.get(index).text!!.contains("-")) {
                        dob.add(linetext.get(index).text!!)
                    }
                }
                var c = 0;
                var stream = ByteArrayOutputStream()
                if(cropped == null)
                    return
                cropped!!.compress(Bitmap.CompressFormat.PNG, 100, stream);
                var byteArray = stream!!.toByteArray()
                val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                val imgUri =CommonUtils.getImageUri(this, bmp)
                startActivity(imgUri)
        }
            "S-PASS" ->{
            }
            "DRIVING LICENSE" ->{
            }
            "WORK PERMIT" ->{
            }
        }
    }
    private fun startActivity(imgUri:Uri){
        hideProgressDialog()
        val intent = Intent(this, UserProfileActivity::class.java)
        val args = Bundle()
        args.putString("userComingFrom", "MainActivity")
        args.putString("userComingBy", userComingBy)
        args.putString("selectedIdProof", selectedIdProof)
        args.putStringArrayList("ID", id)
        args.putStringArrayList("Name", name)
        args.putString("imgUri", imgUri.toString())
        intent.putExtra("BUNDLE", args)
        args.putStringArrayList("DOB", dob)
        intent.putExtra("BUNDLE", args)
        startActivity(intent)
    }
    private fun clearData() {
        blocktext.clear()
        linetext.clear()
        name.clear()
        dob.clear()
        id.clear()
        employer.clear()    }
    private fun clearimage(uri:Uri)
    {
        contentResolver.delete(uri, null, null);
    }
}


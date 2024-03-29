package com.indrif.vms.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
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
import com.indrif.vms.models.FaceDetectionModel
import com.indrif.vms.models.Model
import com.indrif.vms.models.TextRecognitionModel
import com.indrif.vms.utils.AppConstants
import com.indrif.vms.utils.CommonUtils
import com.theartofdev.edmodo.cropper.CropImage
import java.io.ByteArrayOutputStream
import com.otaliastudios.cameraview.Facing

import java.io.IOException

class MainActivity : BaseActivty() {
    private var count:Int = 0
    private var userChoosenTask: String? = null
    private var mImageUri: Uri? = null
    private var textRecognitionModels = ArrayList<TextRecognitionModel>()
    private var mutableImage: Bitmap?=null
    private var mutableImageTest:Bitmap?=null
    private var uri: Uri?=null
    private var fulltext=ArrayList<String>()
    private var name=ArrayList<String>()
    private var dob=ArrayList<String>()
    private var blocktext = ArrayList<Model>()
    private var linetext = ArrayList<Model>()
    private var resultUri:Uri?=null
    private var id=ArrayList<String>()
    private var pd: ProgressDialog?=null
    var btm:Int=0
    var top:Int=0
    var dobbtm:Int=0
    var dobtop:Int=0
    var btm1:Int=0
    var top1:Int=0
    var ftop=0
    var fbottom=0
    private var cameraFacing: Facing = Facing.FRONT

    private val faceDetectionModels = ArrayList<FaceDetectionModel>()
    private var cropped:Bitmap?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionCheck(0,0)
    }
    override fun onClick(v: View) {
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
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCheck(0, 0)
                }

            AppConstants.REQUEST_CAMERA_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask == "Take Photo")
                        selectionIntent(0)
                }
            }
            AppConstants.REQUEST_READ_STORAGE_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                if (resultCode == RESULT_OK ) {
                    resultUri = result.uri
                    analyzeImageforface(MediaStore.Images.Media.getBitmap(contentResolver, resultUri))

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }
    private fun analyzeImage(image: Bitmap?) {

        if (image == null) {
            Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            return
        }
        mutableImageTest=image

        textRecognitionModels.clear()
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(image)
        val textRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
        textRecognizer.processImage(firebaseVisionImage)
            .addOnSuccessListener {
                mutableImage = image.copy(Bitmap.Config.ARGB_8888, true)
                recognizeText(it, mutableImage)

                // var layout = findViewById(R.id.cl_main) as ConstraintLayout
                // var crop=CropView(applicationContext)
                //   crop.setImageBitmap(mutableImage)
                // layout.addView(crop,ConstraintLayout.LayoutParams(100,400))
            }
            .addOnFailureListener {
                Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            }

    }

    private fun recognizeText(result: FirebaseVisionText?, image: Bitmap?) {
        if (result == null || image == null) {
            Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            return
        }

        blocktext.clear()
        linetext.clear()
        name.clear()
        dob.clear()
        var canvasicon = Canvas(image)
        val rectPaint = Paint()
        rectPaint.color = Color.RED
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = 6F
        val textPaint = Paint()
        textPaint.color = Color.RED
        textPaint.textSize = 40F

        var index = 0
        for (block in result.textBlocks) {
            val blockText = block.text
            val blockConfidence = block.confidence
            val blockLanguages = block.recognizedLanguages
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            var right=blockFrame!!.right
            var left=blockFrame!!.left
            var top=blockFrame!!.top
            var bottom=blockFrame!!.bottom

            val newParams = ConstraintLayout.LayoutParams(
                right-left, bottom-top
            )
            newParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            newParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            newParams.leftMargin = left
            newParams.topMargin = top
            /* anotherButton.layoutParams = newParams
             anotherButton.background = resources.getDrawable(R.drawable.textview_background)
             anotherButton.setPadding(3, 3, 3, 3)
             layout.addView(anotherButton, newParams)*/
            canvasicon.drawRect(block.boundingBox, rectPaint)
            Log.d("Text",blockText)
            //   canvasicon.drawText(index.toString(), block.cornerPoints!![2].x.toFloat(), block.cornerPoints!![2].y.toFloat(), textPaint)
            /* customicon=IconCropView(applicationContext)
             var layout = findViewById(R.id.cl_main) as ConstraintLayout
               val newParams = ConstraintLayout.LayoutParams(
                 right-left, bottom-top
             )
             newParams.leftToLeft=ConstraintLayout.LayoutParams.PARENT_ID;
             newParams.topToTop=ConstraintLayout.LayoutParams.PARENT_ID;
             newParams.leftMargin = left
             newParams.topMargin = top
             layout.removeView(customicon)
             layout.addView(customicon,newParams)*/
            blocktext.add(
                Model(blockText,
                    Points(blockFrame!!.bottom,blockFrame!!.left,blockFrame!!.right,blockFrame!!.top)
                )
            )
            for (line in block.lines) {
                val lineText = line.text
                val lineConfidence = line.confidence
                val lineLanguages = line.recognizedLanguages
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                fulltext.add(lineText)
                Log.d("Text",lineText)
                linetext.add(Model(lineText,
                    Points(lineFrame!!.bottom,lineFrame!!.left,lineFrame!!.right,lineFrame!!.top)
                ))
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
        /*  var smallest = Integer.MAX_VALUE
          var smaller = Integer.MAX_VALUE
          for (index in linetext.indices) {

              if (linetext.get(index).zzbat.top < smallest) {
                  smaller = smallest;
                  smallest = linetext.get(index).zzbat.top
              } else if (linetext.get(index).zzbat.top < smaller && linetext.get(index).zzbat.top > smallest) {
                  smaller = linetext.get(index).zzbat.top

              }
          }*/

        for(index in linetext.indices)
        {
            if(linetext.get(index).text!!.contains("IDENTITY"))
            {
                id.add(linetext.get(index).text!!)
            }
        }
        var sleft=0
        var ctop=0
        for ( index in linetext.indices)
        {
            if(linetext.get(index).text=="Name")
            {
                btm=linetext.get(index).zzbat.bottom
                top=linetext.get(index).zzbat.top

            }
        }

        for ( index in linetext.indices)
        {
            if(linetext.get(index).text=="Race")
            {
                btm1=linetext.get(index).zzbat.bottom
                top1=linetext.get(index).zzbat.top

            }
        }
        for ( index in linetext.indices)
        {
            if(linetext.get(index).text=="Date of birth")
            {
                dobbtm=linetext.get(index).zzbat.bottom
                dobtop=linetext.get(index).zzbat.top

            }
        }
        for ( index in linetext.indices)
        {
            if(linetext.get(index).text=="Country of birth")
            {
                ctop=linetext.get(index).zzbat.top

            }
        }
        for ( index in linetext.indices)
        {
            if(linetext.get(index).text=="Sex")
            {
                sleft=linetext.get(index).zzbat.left

            }
        }
        for ( index in linetext.indices)
        {
            if(linetext.get(index).zzbat.top>ftop && linetext.get(index).zzbat.bottom<fbottom )
            {
                name.add(linetext.get(index).text!!)
            }

        }
        for (index in linetext.indices)
        {
            if (linetext.get(index).zzbat.top>dobbtm && linetext.get(index).zzbat.bottom<ctop)
            {
                dob.add(linetext.get(index).text!!)
            }
        }

        var c=0;
        var stream =  ByteArrayOutputStream();
        cropped!!.compress(Bitmap.CompressFormat.PNG, 100, stream);
        var byteArray = stream!!.toByteArray();
        val intent = Intent(applicationContext, UserDetailActivity::class.java)
        val args = Bundle()
        args.putString("userComingFrom", "MainActivity")
        args.putStringArrayList("Dob",id)
        args.putStringArrayList("Name",name)
        intent.putExtra("BUNDLE", args)
        intent.putExtra("image",byteArray);
        pd!!.hide()
        startActivity(intent)

    }

    // for face

    private fun analyzeImageforface(image: Bitmap?) {
        if (image == null) {
            Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            return
        }
        pd!!.show()
        faceDetectionModels.clear()

        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(image)
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()
        val faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        faceDetector.detectInImage(firebaseVisionImage)
            .addOnSuccessListener {
                val mutableImage = image.copy(Bitmap.Config.ARGB_8888, true)

                detectFaces(it, mutableImage)
            }
            .addOnFailureListener {
                Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
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
        ftop=faces.get(0).boundingBox.top-30
        fbottom=(faces.get(0).boundingBox.bottom)-50
        var startx= faces.get(0).boundingBox.left-45
        var starty= faces.get(0).boundingBox.top-140
        var width = (faces.get(0).boundingBox.right+40- faces.get(0).boundingBox.left)+60
        var height = ((faces.get(0).boundingBox.bottom+50)-(faces.get(0).boundingBox.top-100))+50
        cropped = Bitmap.createBitmap(image,startx,starty,width,height)
        for ((index, face) in faces.withIndex()) {

            canvas.drawRect(face.boundingBox, facePaint)

            canvas.drawText("Face$index", (face.boundingBox.centerX() - face.boundingBox.width() / 2) + 8F, (face.boundingBox.centerY() + face.boundingBox.height() / 2) - 8F, faceTextPaint)

            if (face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE) != null) {
                val leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)!!
                canvas.drawCircle(leftEye.position.x, leftEye.position.y, 8F, landmarkPaint)
            }
            if (face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE) != null) {
                val rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)!!
                canvas.drawCircle(rightEye.position.x, rightEye.position.y, 8F, landmarkPaint)
            }
            if (face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE) != null) {
                val nose = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)!!
                canvas.drawCircle(nose.position.x, nose.position.y, 8F, landmarkPaint)
            }
            if (face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR) != null) {
                val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)!!
                canvas.drawCircle(leftEar.position.x, leftEar.position.y, 8F, landmarkPaint)
            }
            if (face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR) != null) {
                val rightEar = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR)!!
                canvas.drawCircle(rightEar.position.x, rightEar.position.y, 8F, landmarkPaint)
            }
            if (face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT) != null && face.getLandmark(
                    FirebaseVisionFaceLandmark.MOUTH_BOTTOM) != null && face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT) != null) {
                val leftMouth = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT)!!
                val bottomMouth = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM)!!
                val rightMouth = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT)!!
                canvas.drawLine(leftMouth.position.x, leftMouth.position.y, bottomMouth.position.x, bottomMouth.position.y, landmarkPaint)
                canvas.drawLine(bottomMouth.position.x, bottomMouth.position.y, rightMouth.position.x, rightMouth.position.y, landmarkPaint)
            }

            faceDetectionModels.add(FaceDetectionModel(index, "Smiling Probability  ${face.smilingProbability}"))
            faceDetectionModels.add(FaceDetectionModel(index, "Left Eye Open Probability  ${face.leftEyeOpenProbability}"))
            faceDetectionModels.add(FaceDetectionModel(index, "Right Eye Open Probability  ${face.rightEyeOpenProbability}"))
        }
        analyzeImage(MediaStore.Images.Media.getBitmap(contentResolver, resultUri))
    }

}


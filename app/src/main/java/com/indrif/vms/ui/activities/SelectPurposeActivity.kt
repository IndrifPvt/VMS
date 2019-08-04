package com.indrif.vms.ui.activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.prefs.PreferenceHandler
import com.indrif.vms.utils.ApiConstants
import com.indrif.vms.utils.CommonUtils
import com.indrif.vms.utils.dialog.CustomAlertDialogListener
import com.softuvo.utils.FileUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_select_purpose.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SelectPurposeActivity : BaseActivty() {
    var selectedPurpose = ""
    lateinit var adapter:ArrayAdapter<CharSequence>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_purpose)
        inItData()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_check_in_user -> {
                checkInUser()
            }

            R.id.iv_select_purpose_back -> {
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }
            R.id.iv_select_purpose_home -> {
                val i = Intent(applicationContext, DashBoardActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }
        }
    }

    private fun checkInUser() {
        var checkInCheckOutHashMap: (HashMap<String, RequestBody>) = HashMap()
        var args = intent.getBundleExtra("BUNDLE")
        checkInCheckOutHashMap["idType"] = RequestBody.create(MediaType.parse("text/plain"), args.getString("idType"))
        checkInCheckOutHashMap["name"] = RequestBody.create(MediaType.parse("text/plain"), args.getString("name"))
        checkInCheckOutHashMap["idNumber"] =
            RequestBody.create(MediaType.parse("text/plain"), args.getString("idNumber"))
        checkInCheckOutHashMap["dob"] = RequestBody.create(MediaType.parse("text/plain"), args.getString("dob"))
        checkInCheckOutHashMap["employer"] =
            RequestBody.create(MediaType.parse("text/plain"), args.getString("employer"))
        checkInCheckOutHashMap["siteId"] = RequestBody.create(
            MediaType.parse("text/plain"),
            PreferenceHandler.readString(applicationContext, PreferenceHandler.SITE_ID, "")
        )
        checkInCheckOutHashMap["purpose"] = RequestBody.create(MediaType.parse("text/plain"), selectedPurpose.trim())
        checkInCheckOutHashMap["phoneNumber"] =
            RequestBody.create(MediaType.parse("text/plain"), et_contact_no.text.toString().trim())
        checkInCheckOutHashMap["remarks"] =
            RequestBody.create(MediaType.parse("text/plain"), et_remarks.text.toString().trim())
        checkInCheckOutHashMap["unit"] =
            RequestBody.create(MediaType.parse("text/plain"), tv_select_unit_value.text.toString().trim())
        checkInCheckOutHashMap["level"] =
            RequestBody.create(MediaType.parse("text/plain"), tv_select_level_value.text.toString().trim())
        checkInCheckOutHashMap["block"] =
            RequestBody.create(MediaType.parse("text/plain"), tv_select_block_value.text.toString().trim())
        var imageUri = Uri.parse(intent.extras.getString("imageUri"))
        if (imageUri != null && !imageUri.toString().equals("null",false)) {
            val file = FileUtils.getFile(context, imageUri)
            val userImageBody = RequestBody.create(MediaType.parse("image/*"), file)
            val userImagePart = MultipartBody.Part.createFormData("image", file?.name, userImageBody)
            callCheckInCheckOut(checkInCheckOutHashMap, userImagePart)
        } else {
            CheckInCheckOutWithoutImage(checkInCheckOutHashMap)
        }
    }


    private fun inItData() {
        CommonUtils.setImage(context, profile_image, intent.extras.getString("imageUri"), R.drawable.dummy_user)
        var intent = intent
        var args = intent.getBundleExtra("BUNDLE")
        var userName = args.getString("name")
         var  firstUserName = userName.split(" ")
        tv_user_name.text = firstUserName[0]
        if (args.getString("userComingBy").equals("checkIn", false))
            btn_check_in_user.text = "CHECK IN"
        else{
            btn_check_in_user.text = "CHECK OUT"
            getUserById(args.getString("idNumber"))}
        setAdapter()

    }

    private fun getUserById(idNumber:String) {
        var mountMap=HashMap<String, String>()
        mountMap.put("idNumber",idNumber)
       // mountMap.put("siteId",PreferenceHandler.readString(applicationContext, PreferenceHandler.SITE_ID,""))

        try {
            showProgressDialog()
            compositeDrawable.add(
                repository.getUserById(mountMap)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgressDialog()
                            if (result.code == ApiConstants.SUCCESS_CODE) {
                                if(result.data.users.size>0) {
                                    var userObj = result.data.users[0]
                                    selectedPurpose = userObj.purpose
                                    var spinnerPosition = adapter.getPosition(selectedPurpose)
                                    sp_purpose.setSelection(spinnerPosition)
                                    et_contact_no.setText(userObj.phoneNumber)
                                        et_remarks.setText(userObj.remarks)
                                        tv_select_unit_value.setText(userObj.unit)
                                        tv_select_level_value.setText(userObj.level)
                                        tv_select_block_value.setText(userObj.block)
                                }
                                else
                                    CommonUtils.showSnackbarMessage(context, "No Data found regarding your id Number", R.color.colorPrimary)

                            } else
                                CommonUtils.showSnackbarMessage(context, result.data.status, R.color.colorPrimary)

                        } catch (e: Exception) {
                            hideProgressDialog()
                            e.printStackTrace()
                        }
                    }, { error ->
                        hideProgressDialog()
                        error.printStackTrace()
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setAdapter() {
        val idProofArray = resources.getStringArray(R.array.purpose_array)
         adapter = ArrayAdapter(this, R.layout.spinner_item, idProofArray)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        sp_purpose.setAdapter(adapter)
        sp_purpose.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPurpose = idProofArray[position]
            }

        }
    }

    private var clickListner: CustomAlertDialogListener = object :
        CustomAlertDialogListener {
        override fun OnClick(dialog: Dialog) {
        }

        override fun OnCallBackClick() {
            val intent = Intent(applicationContext, DashBoardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("EXIT", true)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
        }
    }

    private fun callCheckInCheckOut(
        checkInCheckOutHashMap: HashMap<String, RequestBody>,
        userImagePart: MultipartBody.Part
    ) {
        var intent = intent
        var args = intent.getBundleExtra("BUNDLE")

        if (args.getString("userComingBy").equals("checkIn", false)) {
            try {
                showProgressDialog()
                compositeDrawable.add(
                    repository.checkInUser(map = checkInCheckOutHashMap, userImagePart = userImagePart)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            try {
                                if (result.code == ApiConstants.SUCCESS_CODE) {
                                    hideProgressDialog()
                                    CommonUtils.showMessagePopup(
                                        context,
                                        result.message,
                                        result.data.status,
                                        R.mipmap.success,
                                        clickListner,
                                        View.GONE
                                    )
                                } else {
                                    hideProgressDialog()
                                    CommonUtils.showSnackbarMessage(this, result.data.status, R.color.colorPrimary)
                                }
                            } catch (e: Exception) {
                                CommonUtils.showSnackbarMessage(context, e.message.toString(), R.color.colorPrimary)
                                hideProgressDialog()
                                e.printStackTrace()
                            }
                        }, { error ->
                            hideProgressDialog()
                            CommonUtils.showSnackbarMessage(context, error.message.toString(), R.color.colorPrimary)
                            error.printStackTrace()
                        })
                )
            } catch (e: Exception) {
                hideProgressDialog()
                e.printStackTrace()
                CommonUtils.showSnackbarMessage(context, e.message.toString(), R.color.colorPrimary)
            }
        } else if (args.getString("userComingBy").equals("checkOut", false)) {
            val mountMap = HashMap<String, String>()
            mountMap.put("idNumber", args.getString("idNumber"))
            mountMap.put("siteId", PreferenceHandler.readString(applicationContext, PreferenceHandler.SITE_ID, ""))
            try {
                showProgressDialog()
                compositeDrawable.add(
                    repository.checkOutUser(mountMap)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            try {
                                hideProgressDialog()
                                if (result.code == ApiConstants.SUCCESS_CODE) {
                                    hideProgressDialog()
                                    CommonUtils.showMessagePopup(
                                        this,
                                        result.message,
                                        result.data.status,
                                        R.mipmap.success,
                                        clickListner,
                                        View.GONE
                                    )
                                } else {
                                    hideProgressDialog()
                                    CommonUtils.showSnackbarMessage(this, result.data.status, R.color.colorPrimary)
                                }

                            } catch (e: Exception) {
                                hideProgressDialog()
                                e.printStackTrace()
                            }
                        }, { error ->
                            hideProgressDialog()
                            error.printStackTrace()
                        })
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun CheckInCheckOutWithoutImage(checkInCheckOutHashMap: HashMap<String, RequestBody>) {
        var intent = intent
        var args = intent.getBundleExtra("BUNDLE")

        if (args.getString("userComingBy").equals("checkIn", false)) {
            try {
                showProgressDialog()
                compositeDrawable.add(
                    repository.checkInUserWithoutImage(map = checkInCheckOutHashMap)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            try {
                                if (result.code == ApiConstants.SUCCESS_CODE) {
                                    hideProgressDialog()
                                    CommonUtils.showMessagePopup(context, result.message, result.data.status, R.mipmap.success, clickListner, View.GONE)
                                } else {
                                    hideProgressDialog()
                                    CommonUtils.showSnackbarMessage(this, result.data.status, R.color.colorPrimary)
                                }
                            } catch (e: Exception) {
                                CommonUtils.showSnackbarMessage(context, e.message.toString(), R.color.colorPrimary)
                                hideProgressDialog()
                                e.printStackTrace()
                            }
                        }, { error ->
                            hideProgressDialog()
                            CommonUtils.showSnackbarMessage(context, error.message.toString(), R.color.colorPrimary)
                            error.printStackTrace()
                        })
                )
            } catch (e: Exception) {
                hideProgressDialog()
                e.printStackTrace()
                CommonUtils.showSnackbarMessage(context, e.message.toString(), R.color.colorPrimary)
            }
        } else if (args.getString("userComingBy").equals("checkOut", false)) {
            val mountMap = HashMap<String, String>()
            mountMap.put("idNumber", args.getString("idNumber"))
            mountMap.put("siteId", PreferenceHandler.readString(applicationContext, PreferenceHandler.SITE_ID, ""))
            try {
                showProgressDialog()
                compositeDrawable.add(
                    repository.checkOutUser(mountMap)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            try {
                                hideProgressDialog()
                                if (result.code == ApiConstants.SUCCESS_CODE) {
                                    hideProgressDialog()
                                    CommonUtils.showMessagePopup(this, result.message, result.data.status, R.mipmap.success, clickListner, View.GONE)
                                } else {
                                    hideProgressDialog()
                                    CommonUtils.showSnackbarMessage(this, result.data.status, R.color.colorPrimary)
                                }

                            } catch (e: Exception) {
                                hideProgressDialog()
                                e.printStackTrace()
                            }
                        }, { error ->
                            hideProgressDialog()
                            error.printStackTrace()
                        })
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


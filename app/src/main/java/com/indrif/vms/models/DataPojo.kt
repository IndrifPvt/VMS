package com.indrif.vms.models

import java.io.Serializable


class DataPojo :Serializable{
    var status:String=""
    var user_id:String=""
    var token:String=""
    var device_token:String=""
    var remarks:String = ""
    var remarks_complete_job:String = ""
    var isUpdated:String = ""
    var complete_job:String = ""
    var message:String = ""
    var sign_image:ArrayList<SignaturePojo> = ArrayList()

}



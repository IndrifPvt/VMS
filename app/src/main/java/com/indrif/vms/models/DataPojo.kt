package com.indrif.vms.models

import java.io.Serializable


class DataPojo :Serializable{
    var status:String=""
    var user_id:String=""
    var token:String=""
    var device_token:String=""
    var message:String = ""
    var admin_id:String = ""
    var sites:List<SiteData> = ArrayList()
    var users:ArrayList<User> = ArrayList()
}



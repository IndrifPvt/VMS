package com.indrif.vms.models

import java.io.Serializable

class ResponseModel(
        var message: String = "",
        var code: Int =0,
        var status: String = "",
        val data: DataPojo
): Serializable


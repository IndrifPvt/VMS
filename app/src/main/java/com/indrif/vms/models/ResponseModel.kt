package com.indrif.vms.models

import java.io.Serializable

class ResponseModel(
        var message: String = "",
        var code: Int =0,
        val data: DataPojo
): Serializable


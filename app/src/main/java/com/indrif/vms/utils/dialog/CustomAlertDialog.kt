package com.indrif.vms.utils.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.Window
import com.indrif.vms.R
import kotlinx.android.synthetic.main.popup_layout_.*

class CustomAlertDialog private constructor(builder: Builder) {

    class Builder(private val activity: Activity) {
        private var title: String? = null
        private var message: String? = null
        private var positiveBtnText: String? = null
        private var negativeBtnText: String? = null
        private var icon: Int = 0
        private var visibility: Icon? = null
        private var pListener: CustomAlertDialogListener? = null
        private var nListener: CustomAlertDialogListener? = null
        private var pBtnColor: Int = 0
        private var nBtnColor: Int = 0
        private var bgColor: Int = 0
        private var cancel: Boolean = false
        private var negartiveButtonVisibility: Int = View.VISIBLE
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setBackgroundColor(bgColor: Int): Builder {
            this.bgColor = bgColor
            return this
        }

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        fun setPositiveBtnText(positiveBtnText: String): Builder {
            this.positiveBtnText = positiveBtnText
            return this
        }

        fun setPositiveBtnBackground(pBtnColor: Int): Builder {
            this.pBtnColor = pBtnColor
            return this
        }

        fun setNegativeBtnText(negativeBtnText: String): Builder {
            this.negativeBtnText = negativeBtnText
            return this
        }

        fun setNegativeBtnBackground(nBtnColor: Int): Builder {
            this.nBtnColor = nBtnColor
            return this
        }

        fun setNegativeBtnVisibility(visibility: Int): Builder {
            this.negartiveButtonVisibility = visibility
            return this
        }

        //setIcon
        fun setIcon(icon: Int, visibility: Icon): Builder {
            this.icon = icon
            this.visibility = visibility
            return this
        }


        //set Positive listener
        fun OnPositiveClicked(pListener: CustomAlertDialogListener): Builder {
            this.pListener = pListener
            return this
        }

        //set Negative listener
        fun OnNegativeClicked(nListener: CustomAlertDialogListener): Builder {
            this.nListener = nListener
            return this
        }

        fun isCancellable(cancel: Boolean): Builder {
            this.cancel = cancel
            return this
        }

        fun build(): CustomAlertDialog {
            val dialog: Dialog
            dialog = Dialog(activity, R.style.PopTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(cancel)
            dialog.setContentView(R.layout.popup_layout_)
            dialog.tv_title.text = title
            dialog.tv_description.text = message
            if (positiveBtnText != null)
                dialog.btn_ok.text = positiveBtnText
            if (pBtnColor != 0) {
                val bgShape = dialog.btn_ok.background as GradientDrawable
                bgShape.setColor(pBtnColor)
            }
            if (nBtnColor != 0) {
                val bgShape = dialog.btn_cancel.background as GradientDrawable
                bgShape.setColor(nBtnColor)
            }

            if (negativeBtnText != null)
                dialog.btn_cancel.text = negativeBtnText

            dialog.btn_cancel.visibility = negartiveButtonVisibility

            dialog.iv_icon.setImageResource(icon)
            if (visibility == Icon.Visible)
                dialog.iv_icon.visibility = View.VISIBLE
            else
                dialog.iv_icon.visibility = View.GONE
            if (bgColor != 0)
                dialog.vw_background.setBackgroundColor(bgColor)
            if (pListener != null) {
                dialog.btn_ok.setOnClickListener {
                    pListener!!.OnClick(dialog)
                    dialog.dismiss()
                }
            } else {
                dialog.btn_ok.setOnClickListener { dialog.dismiss() }
            }

            if (nListener != null) {

                dialog.btn_cancel.setOnClickListener {
                    nListener!!.OnClick(dialog)

                    dialog.dismiss()
                }
            }

            dialog.show()
            return CustomAlertDialog(this)
        }
    }

}
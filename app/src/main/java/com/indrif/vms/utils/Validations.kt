package com.indrif.vms.utils

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.indrif.vms.R


class Validations {

    companion object {
        /**
         * @param editText
         * - EditText field which need to be validated
         * @return - Returns true if editText is Null or empty
         */
        fun isNullOrEmpty(editText: EditText): Boolean {
            return editText.text == null || editText.text.toString().trim { it <= ' ' }.length == 0
        }


        fun isNullOrEmpty(textView: TextView): Boolean {
            return textView.text == null || textView.text.toString().trim { it <= ' ' }.length == 0
        }


        fun isContainSpecialCharacter(string: String): Boolean {
            return string.matches("[a-zA-Z0-9.? ]*".toRegex())
        }

        private fun validateFullnameLength(applicationContext: Context, mEtUsername: EditText, errMessage: String): Boolean {
            if (isNullOrEmpty(mEtUsername) && mEtUsername.text.toString().trim { it <= ' ' }.length < 2) {
                CommonUtils.showSnackbarMessage(
                    applicationContext,
                    errMessage,
                    R.color.colorAccent
                )
                requestFocus(applicationContext, mEtUsername)
                return false
            }
            return true
        }

        private fun requestFocus(applicationContext: Context, view: View) {
            if (view.requestFocus()) {
//                val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        private fun validatePasswordContainSpecialCharacter(applicationContext: Context, mEtPassword: EditText, errMessage: String): Boolean {
            if (isContainSpecialCharacter(mEtPassword.text.toString().trim { it <= ' ' })) {
                CommonUtils.showSnackbarMessage(
                    applicationContext,
                    errMessage,
                    R.color.colorAccent
                )
                mEtPassword.requestFocus()
                return false
            }
            return true
        }

        private fun validateEmailAddress(applicationContext: Context, view: View, errMessage: String): Boolean {
            val email = (view as EditText).text.toString().trim { it <= ' ' }

            if (email.contains("[a-zA-Z]+") || email.contains("@")) {
                if (email.isEmpty() || !isValidEmail(email)) {
                    CommonUtils.showSnackbarMessage(
                        applicationContext,
                        errMessage,
                        R.color.colorAccent
                    )
                    requestFocus(applicationContext, view)
                    return false
                }
            } else {
                CommonUtils.showSnackbarMessage(
                    applicationContext,
                    errMessage,
                    R.color.colorAccent
                )
                requestFocus(applicationContext, view)
                return false
            }

            return true
        }

        fun isValidEmail(email: String): Boolean {
            return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }


        private fun validateConfirmPassword(applicationContext: Context, mEtPassword: EditText, mEtCnfPassword: EditText, errMessage: String): Boolean {
            if (!mEtPassword.text.toString().trim().equals(mEtCnfPassword.text.toString().trim())) {
                CommonUtils.showSnackbarMessage(
                    applicationContext,
                    errMessage,
                    R.color.colorAccent
                )
                mEtCnfPassword.requestFocus()
                return false
            }
            return true
        }

        private fun validatePasswordLength(applicationContext: Context, mEtPassword: EditText, errMessage: String): Boolean {
            if (mEtPassword.text.toString().trim { it <= ' ' }.length <= 7) {
                CommonUtils.showSnackbarMessage(
                    applicationContext,
                    errMessage,
                    R.color.colorAccent
                )
                mEtPassword.requestFocus()
                return false
            }
            return true
        }

        private fun validateFullname(applicationContext: Context, mEtUsername: EditText, errMessage: String): Boolean {
            if (isNullOrEmpty(mEtUsername)) {
                CommonUtils.showSnackbarMessage(
                    applicationContext,
                    errMessage,
                    R.color.colorAccent
                )
                mEtUsername.requestFocus()
                return false

            }
            return true
        }

        private fun validateCurrentPassword(applicationContext: Context, mEtCurrentPassword: EditText, errMessage: String): Boolean {
            if (isNullOrEmpty(mEtCurrentPassword)) {
                CommonUtils.showSnackbarMessage(
                    applicationContext,
                    errMessage,
                    R.color.colorAccent
                )
                mEtCurrentPassword.requestFocus()
                return false

            }
            return true
        }



        private fun validateLocation(applicationContext: Context, mEdLocation: EditText, errMessage: String): Boolean {
            if (isNullOrEmpty(mEdLocation)) {
                CommonUtils.showSnackbarMessage(
                    applicationContext,
                    errMessage,
                    R.color.colorAccent
                )
                mEdLocation.requestFocus()
                return false
            }
            return true
        }




        /**
         * Validating form Login
         *
         * @param applicationContext
         * @param mEtEmail
         * @param mEtPassword
         */
        fun isValidateLogin(applicationContext: Context, mEtEmail: EditText, mEtPassword: EditText): Boolean {
            if (!validateEmailAddress(
                    applicationContext,
                    mEtEmail,
                    applicationContext.resources.getString(R.string.err_msg_email_address)
                )
            ) {
                return false
            }
            if (!validateFullname(
                    applicationContext,
                    mEtPassword,
                    applicationContext.resources.getString(R.string.err_msg_password)
                )
            ) {
                return false
            }
            if (!CommonUtils.isInternetConnection(applicationContext)) {
                CommonUtils.showSnackbarMessage(
                    applicationContext,
                    applicationContext.resources.getString(R.string.err_msg_internet),
                    R.color.colorAccent
                )
                return false
            }
            return true
        }


        /**
         * Validating form SignUp
         *
         * @param applicationContext
         * @param mEtEmail
         * @param mEtPassword
         */
        fun isValidateSignUp(applicationContext: Context, mEtFullName: EditText, mEtEmail: EditText, mEtPassword: EditText, mEtCnfPassword: EditText): Boolean {
            if (!validateFullname(
                    applicationContext,
                    mEtFullName,
                    applicationContext.resources.getString(R.string.err_msg_full_name)
                )
            ) {
                return false
            }
            if (!validateFullnameLength(
                    applicationContext,
                    mEtFullName,
                    applicationContext.resources.getString(R.string.err_msg_full_name)
                )
            ) {
                return false
            }
            if (!validateEmailAddress(
                    applicationContext,
                    mEtEmail,
                    applicationContext.resources.getString(R.string.err_msg_email_address)
                )
            ) {
                return false
            }
            if (!validateFullname(
                    applicationContext,
                    mEtPassword,
                    applicationContext.resources.getString(R.string.err_msg_password)
                )
            ) {
                return false
            }
            if (!validatePasswordLength(
                    applicationContext,
                    mEtPassword,
                    applicationContext.resources.getString(R.string.err_msg_password_length)
                )
            ) {
                return false
            }
            if (!validatePasswordContainSpecialCharacter(
                    applicationContext,
                    mEtPassword,
                    applicationContext.resources.getString(R.string.err_msg_password_length)
                )
            ) {
                return false
            }
            if (!validateFullname(
                    applicationContext,
                    mEtCnfPassword,
                    applicationContext.resources.getString(R.string.err_msg_confirm_password)
                )
            ) {
                return false
            }
            if (!validateConfirmPassword(
                    applicationContext,
                    mEtPassword,
                    mEtCnfPassword,
                    applicationContext.resources.getString(R.string.err_msg_same_confirm_password)
                )
            ) {
                return false
            }
            if (!CommonUtils.isInternetConnection(applicationContext)) {
                CommonUtils.showSnackbarMessage(
                    applicationContext,
                    applicationContext.resources.getString(R.string.err_msg_internet),
                    R.color.colorAccent
                )
                return false
            }
            return true
        }




        fun isValidateChangePassword(applicationContext: Context, mEtCurrentPassword: EditText, mEtNewPassword: EditText, mEtVerifyPassword: EditText): Boolean {
            if (!validateCurrentPassword(
                    applicationContext,
                    mEtCurrentPassword,
                    applicationContext.resources.getString(R.string.err_msg_current_password)
                )
            ) {
                return false
            }
            if (!validatePasswordLength(applicationContext, mEtNewPassword, applicationContext.resources.getString(R.string.err_msg_password_length))) {
                return false
            }

            if (!validateConfirmPassword(
                    applicationContext,
                    mEtNewPassword,
                    mEtVerifyPassword,
                    applicationContext.resources.getString(R.string.err_msg_same_confirm_password)
                )
            ) {
                return false
            }
            return true
        }

        fun isValidateForgotPassword(applicationContext: Context, mEtEmail: EditText): Boolean {
            if (!validateEmailAddress(
                    applicationContext,
                    mEtEmail,
                    applicationContext.resources.getString(R.string.err_msg_email_address)
                )
            ) {
                return false
            }
            if (!CommonUtils.isInternetConnection(applicationContext)) {
                CommonUtils.showSnackbarMessage(
                    applicationContext,
                    applicationContext.resources.getString(R.string.err_msg_internet),
                    R.color.colorAccent
                )
                return false
            }
            return true
        }
    }
}
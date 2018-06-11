package com.vn.ntsc.widget.views.dialog;

import com.vn.ntsc.R;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/7/2017.
 */

public class ErrorString {
    public static int getDescriptionOfErrorCode(int code) {
        int message = R.string.common_alert;
        switch (code) {
            case ServerResponse.DefinitionCode.SERVER_EMAIL_NOT_FOUND:
                message = R.string.msg_common_email_not_found;
                break;
            case ServerResponse.DefinitionCode.SERVER_EMAIL_REGISTERED:
                message = R.string.msg_common_email_has_already;
                break;
            case ServerResponse.DefinitionCode.SERVER_INVALID_USER_NAME:
                message = R.string.msg_common_invalid_username;
                break;
            case ServerResponse.DefinitionCode.SERVER_INVALID_EMAIL:
                message = R.string.msg_common_invalid_email;
                break;
            case ServerResponse.DefinitionCode.SERVER_INVALID_PASSWORD:
                message = R.string.msg_common_invalid_password;
                break;
            case ServerResponse.DefinitionCode.SERVER_UNKNOWN_ERROR:
                message = R.string.msg_common_server_unknown_error;
                break;
            case ServerResponse.DefinitionCode.SERVER_INCORRECT_PASSWORD:
                message = R.string.msg_common_password_is_incorrect;
                break;
            case ServerResponse.DefinitionCode.SERVER_SEND_MAIL_FAIL:
                message = R.string.msg_common_send_email_fail;
                break;
            case ServerResponse.DefinitionCode.SERVER_INCORRECT_CODE:
                message = R.string.msg_common_incorrect_code;
                break;
            case ServerResponse.DefinitionCode.SERVER_WRONG_DATA_FORMAT:
                message = R.string.msg_common_data_format_wrong;
                break;
            case ServerResponse.DefinitionCode.SERVER_NOT_ENOUGH_MONEY:
                message = R.string.not_enough_point_title;
                break;
            case ServerResponse.DefinitionCode.SERVER_ALREADY_PURCHASE:
                message = R.string.purchase_already_perform;
                break;
            case ServerResponse.DefinitionCode.SERVER_OUT_OF_DATE_API:
                message = R.string.need_update_app;
                break;
            case ServerResponse.DefinitionCode.SERVER_LOOKED_USER:
                message = R.string.account_locked_user;
                break;
            case ServerResponse.DefinitionCode.SERVER_INVALID_BIRTHDAY:
                message = R.string.invalid_birthday;
                break;
            case ServerResponse.DefinitionCode.SERVER_OLD_VERSION:
                message = R.string.application_version_invalid;
                break;
            default:
                break;
        }
        return message;
    }
}

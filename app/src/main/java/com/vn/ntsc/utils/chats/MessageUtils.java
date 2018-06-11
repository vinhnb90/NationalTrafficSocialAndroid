package com.vn.ntsc.utils.chats;

import android.content.Context;
import android.text.TextUtils;

import com.vn.ntsc.R;

/**
 * Created by hnc on 28/08/2017.
 */

public class MessageUtils {

    // TYPE COMMON MESSAGE
    public static final String WINK = "WINK";
    public static final String PP = "PP";
    public static final String PE = "PE";
    public static final String AUDIO = "AUDIO";
    public static final String VIDEO = "VIDEO";
    public static final String PHOTO = "PHOTO";
    public static final String FILE = "FILE";
    public static final String GIFT = "GIFT";
    public static final String LOCATION = "LCT";
    public static final String STICKER = "STK";
    public static final String STARTVIDEO = "SVIDEO";
    public static final String ENDVIDEO = "EVIDEO";
    public static final String STARTVOICE = "SVOICE";
    public static final String ENDVOICE = "EVOICE";
    public static final String CMD = "CMD";
    public static final String TYPING = "TYPING";
    public static final String CALLREQUEST = "CALLREQ";

    // TYPE FILE MESSAGE
    public static final String FILE_AUDIO = "a";
    public static final String FILE_VIDEO = "v";
    public static final String FILE_PHOTO = "p";

    // TYPE STARTVIDEO, ENDVIDEO, STARTVOICE, ENDVOICE
    public static final int VOIP_ACTION_NONE = 0;
    public static final int VOIP_ACTION_VOICE_START = 1;
    public static final int VOIP_ACTION_VOICE_END = 2;
    public static final int VOIP_ACTION_VOICE_END_NO_ANSWER = 3;
    public static final int VOIP_ACTION_VOICE_END_BUSY = 4;
    public static final int VOIP_ACTION_VIDEO_START = 5;
    public static final int VOIP_ACTION_VIDEO_END = 6;
    public static final int VOIP_ACTION_VIDEO_END_NO_ANSWER = 7;
    public static final int VOIP_ACTION_VIDEO_END_BUSY = 8;


    //========================================== FILE ========================================

    /**
     * format: "last_msg": "57721e930cf2eb6d9632e6cf&59842944e4b07777a5887ef7&20170811064657832|v|598d52eee4b0ea99cee77e1d|0.000000"
     * format: "last_msg": "5937a2abe4b0cb80372950c1&57721e930cf2eb6d9632e6cf&20170607065516530|p|5937a352e4b00110da264bcb|14968185144811152646746.tmp|1"
     *
     * @param msg "57721e930cf2eb6d9632e6cf&59842944e4b07777a5887ef7&20170811064657832|v|598d52eee4b0ea99cee77e1d|0.000000"
     * @return type of file
     */
    public static String getFileTypeMsg(String msg) {
        if (msg == null || TextUtils.isEmpty(msg)) {
            return "";
        }

        if (msg.contains("|")) {
            String strArray[] = msg.split("\\|");
            return strArray[1]; // v or p
        }

        return msg;
    }

    /**
     * @param type FILE_AUDIO = "a";  String FILE_VIDEO = "v"; String FILE_PHOTO = "p";
     * @return message for type file
     */
    public static String getTextFromFileType(Context context, String type) {
        String text;
        switch (type.toLowerCase()) {
            case FILE_AUDIO:
                text = context.getString(R.string.sent_a_audio);
                break;
            case FILE_VIDEO:
                text = context.getString(R.string.sent_a_video);
                break;
            case FILE_PHOTO:
                text = context.getString(R.string.sent_a_photo);
                break;
            default:
                text = context.getString(R.string.sent_a_file);
        }
        return text;
    }


    //========================================== GIFT ===========================================

    /**
     * @param giftMsg "last_msg": "5937a8ffe4b00110da264bd3|anh long dz ha|kokokokikilo|10"
     * @return
     */
    public static String getTextFromGiftMsg(Context context, String giftMsg, boolean isOwn) {
        String[] strArray = giftMsg.split("\\|");

//        int point = 0;
//
//        if (strArray.length >= 4) {
//            point = Integer.parseInt(strArray[3]);
//        }
//
//        String format = context.getResources().getString(R.string.send_gift_price_not_free);
//        String pointStr = MessageFormat.format(format, point);

        return context.getString(isOwn ? R.string.gift_message_display_send : R.string.gift_message_display_receiver);
    }


    //========================== STARTVIDEO, ENDVIDEO, STARTVOICE, ENDVOICE =======================

    /**
     * @param msg "6|5153c388-7367-11e7-ab89-59fb99d5003d|6"
     * @return
     */
    public static String getTextVideoVoice(Context context, String msg) {
        String[] strArray = msg.split("\\|");
        int type = Integer.parseInt(strArray[0]);
        String duration = "";

        try {
            if (strArray.length == 1) {
                type = Integer.parseInt(msg);
            } else {
                type = Integer.parseInt(strArray[0]);
                duration = strArray[2];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (type) {
            case VOIP_ACTION_VIDEO_END:
            case VOIP_ACTION_VOICE_END:
                msg = context.getString(R.string.voip_action_video_voice_end_new, duration);
                break;
            case VOIP_ACTION_VOICE_END_NO_ANSWER:
            case VOIP_ACTION_VIDEO_END_NO_ANSWER:
                msg = context.getString(R.string.voip_action_video_voice_end_no_answer);
                break;
            case VOIP_ACTION_VIDEO_END_BUSY:
            case VOIP_ACTION_VOICE_END_BUSY:
                msg = context.getString(R.string.voip_action_video_voice_end_busy);
                break;
            case VOIP_ACTION_VIDEO_START:
            case VOIP_ACTION_VOICE_START:
                msg = context.getString(R.string.voip_action_video_voice_start);
                break;
            default:
                msg = strArray[1];
        }

        return msg;
    }
}

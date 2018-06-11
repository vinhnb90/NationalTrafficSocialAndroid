package com.vn.ntsc.repository.model.report;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 9/18/2017.
 */

public class ReportRequest extends ServerRequest {

    @SerializedName("subject_id")
    private String subject_id;        //Id of the subject will be reported
    @SerializedName("rpt_type")
    private int rpt_type;        //Type of report - define in array xml file
    @SerializedName("subject_type")
    private int subject_type;    //Type of this content - buzz, image, user


    public ReportRequest(String token, String subject_id, int rpt_type, int subject_type) {
        super("rpt");
        this.token = token;
        this.subject_id = subject_id;
        this.rpt_type = rpt_type;
        this.subject_type = subject_type;
    }
}

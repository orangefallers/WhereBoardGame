package com.ofcat.whereboardgame.firebase.dataobj;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by orangefaller on 2017/8/12.
 */

@IgnoreExtraProperties
public class IssueReportDTO {

    private String userIssueReport;
    private String systemIssueAnswer;
    private String issueTimeStamp;

    public String getUserIssueReport() {
        return userIssueReport;
    }

    public void setUserIssueReport(String userIssueReport) {
        this.userIssueReport = userIssueReport;
    }

    public String getSystemIssueAnswer() {
        return systemIssueAnswer;
    }

    public void setSystemIssueAnswer(String systemIssueAnswer) {
        this.systemIssueAnswer = systemIssueAnswer;
    }

    public String getIssueTimeStamp() {
        return issueTimeStamp;
    }

    public void setIssueTimeStamp(String issueTimeStamp) {
        this.issueTimeStamp = issueTimeStamp;
    }
}

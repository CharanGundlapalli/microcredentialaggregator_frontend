package com.simats.microcredential;

public class Certificate {

    private final String certificateUid;
    private final String title;
    private final String issueDate;
    private final String expiryDate;
    private final String verificationStatus;

    public Certificate(String certificateUid, String title, String issueDate, String expiryDate, String verificationStatus) {
        this.certificateUid = certificateUid;
        this.title = title;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.verificationStatus = verificationStatus;
    }

    public String getCertificateUid() {
        return certificateUid;
    }

    public String getTitle() {
        return title;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }
}

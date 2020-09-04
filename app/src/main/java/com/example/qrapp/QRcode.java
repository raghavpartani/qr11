package com.example.qrapp;

public  class QRcode {

    String qrcodename;
    int qrcodeimage;

    public QRcode(String qrcodename, int qrcodeimage) {
        this.qrcodename = qrcodename;
        this.qrcodeimage = qrcodeimage;
    }

    public  String getQrcodename() {
        return qrcodename;
    }

    public  int getQrcodeimage() {
        return qrcodeimage;
    }
}

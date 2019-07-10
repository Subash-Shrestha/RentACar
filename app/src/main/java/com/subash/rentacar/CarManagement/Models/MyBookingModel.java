package com.subash.rentacar.CarManagement.Models;

public class MyBookingModel {
    private String CarTitle;
    private String RegistrationNo;
    private String UploadedBy;

    public MyBookingModel() {
    }

    public MyBookingModel(String carTitle, String registrationNo, String uploadedBy) {
        CarTitle = carTitle;
        RegistrationNo = registrationNo;
        UploadedBy = uploadedBy;
    }

    public String getCarTitle() {
        return CarTitle;
    }

    public void setCarTitle(String carTitle) {
        CarTitle = carTitle;
    }

    public String getRegistrationNo() {
        return RegistrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        RegistrationNo = registrationNo;
    }

    public String getUploadedBy() {
        return UploadedBy;
    }

    public void setUploadedBy(String iploadedBy) {
        UploadedBy = iploadedBy;
    }
}

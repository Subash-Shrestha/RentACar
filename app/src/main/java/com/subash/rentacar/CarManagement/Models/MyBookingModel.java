package com.subash.rentacar.CarManagement.Models;

public class MyBookingModel {
    private String CarTitle;
    private String RegistrationNo;

    public MyBookingModel() {
    }

    public MyBookingModel(String carTitle, String registrationNo) {
        CarTitle = carTitle;
        RegistrationNo = registrationNo;
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
}

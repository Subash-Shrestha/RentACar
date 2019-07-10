package com.subash.rentacar.CarManagement.Models;


import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CarModel {
    private String CarTitle;
    private String Brand;
    private String Image;
    private String Model;
    private String RegistrationNo;
    private String UploadedBy;
    private String UploadedOn;


    public CarModel() {

    }

    public CarModel(String carTitle, String brand, String image, String model, String registrationNo, String uploadedBy, String uploadedOn) {
        CarTitle = carTitle;
        Brand = brand;
        Image = image;
        Model = model;
        RegistrationNo = registrationNo;
        UploadedBy = uploadedBy;
        UploadedOn = uploadedOn;
    }

    public String getCarTitle() {
        return CarTitle;
    }

    public void setCarTitle(String carTitle) {
        CarTitle = carTitle;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
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

    public void setUploadedBy(String uploadedBy) {
        UploadedBy = uploadedBy;
    }

    public String getUploadedOn() {
        return UploadedOn;
    }

    public void setUploadedOn(String uploadedOn) {
        UploadedOn = uploadedOn;
    }
}

package com.webhotel.webhotel.dto;

import java.time.LocalDate;

public class BookingDto {
    private Long id;
    private Long room;
    private Long userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    // private Double price;
    private String paymentIntentId;
    private boolean paymentRequired;

    private String name;
    private String surname;
    private String phone;
    private String adress;
    private String status;
    private Long hotelId;

    public Long getHotelId(){
        return hotelId;
    }
    public void setHotelId(Long hotelId){
        this.hotelId = hotelId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setPaymentRequired(boolean paymentRequired) {
        this.paymentRequired = paymentRequired;
    }

    public Long getRoomId() {
        return room;
    }

    public void setRoomId(Long roomId) {
        this.room = room;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // public Double getPrice() {
    // return price;
    // }

    // public void setPrice(Double price) {
    // this.price = price;
    // }

    // public String getPaymentIntentId() {
    // return paymentIntentId;
    // }

    // public void setPaymentIntentId(String paymentIntentId) {
    // this.paymentIntentId = paymentIntentId;
    // }

    public boolean isPaymentRequired() {
        return paymentRequired;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

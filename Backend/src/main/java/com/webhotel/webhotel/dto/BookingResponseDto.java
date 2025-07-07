package com.webhotel.webhotel.dto;

import java.time.LocalDate;

public class BookingResponseDto {
    private Long id;
    private String hotelName;
    private String roomName;
    private String username;
    private Long room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean canceled;
    private Double price;
    private String paymentIntentId;
    private String client_secret;

    private String name;
    private String surname;
    private String phone;
    private String adress;
    private String status;
private Long hotelId;


public Long getRoomId() {
        return room;
    }
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

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

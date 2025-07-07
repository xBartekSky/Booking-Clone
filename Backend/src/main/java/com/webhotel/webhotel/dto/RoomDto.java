package com.webhotel.webhotel.dto;

import java.math.BigDecimal;

public class RoomDto {
    private Long id;
    private String roomNumber;
    private String roomType;
    private BigDecimal pricePerNight;
    //private boolean isAvailable;
    private Long hotelId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

   

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
}

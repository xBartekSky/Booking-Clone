package com.webhotel.webhotel.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.webhotel.webhotel.dto.HotelImageDto;
import com.webhotel.webhotel.entity.HotelImage;

@Mapper(componentModel = "spring")
public interface HotelImageMapper {
    @Mapping(source = "hotel.id", target = "hotelId")
    HotelImageDto otelImageToHotelImageDto(HotelImage hotelImage);

    @Mapping(source = "hotelId", target = "hotel.id")
    HotelImage hotelImageDtoToHotelImage(HotelImageDto hotelImageDto);
}
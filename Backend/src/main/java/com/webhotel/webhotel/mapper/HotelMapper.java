package com.webhotel.webhotel.mapper;

import com.webhotel.webhotel.dto.CreateHotelDto;
import com.webhotel.webhotel.dto.HotelDto;
import com.webhotel.webhotel.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { HotelImageMapper.class })
public interface HotelMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "images", target = "images")
    HotelDto hotelToHotelDto(Hotel hotel);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "images", target = "images")
    Hotel HotelDtoToHotel(HotelDto hotelDto);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "images", ignore = true)
    Hotel createHotelDtoToHotel(CreateHotelDto createHotelDto);

}
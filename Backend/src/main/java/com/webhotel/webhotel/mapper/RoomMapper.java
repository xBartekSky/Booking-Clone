package com.webhotel.webhotel.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.webhotel.webhotel.dto.RoomDto;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(source = "hotel.id", target = "hotelId")
    RoomDto roomToRoomDto(Room room);

    @Mapping(source = "hotelId", target = "hotel.id")
    Room roomDtoToRoom(RoomDto roomDto);

    default Hotel mapHotelIdToHotel(Long hotelId) {
        if (hotelId == null) {
            return null;
        }
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        return hotel;
    }
}

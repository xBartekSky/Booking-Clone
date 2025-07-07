package com.webhotel.webhotel.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.webhotel.webhotel.dto.BookingDto;
import com.webhotel.webhotel.dto.BookingResponseDto;
import com.webhotel.webhotel.entity.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "room.hotel.name", target = "hotelName")
    @Mapping(source = "room.roomNumber", target = "roomName")
    @Mapping(source = "user.username", target = "username")
    BookingResponseDto toDto(Booking booking);

    @Mapping(target = "room", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "canceled", ignore = true)
    Booking dtoToBooking(BookingDto bookingDto);

    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "user.id", target = "userId")
    
    BookingDto bookingToDto(Booking booking);

    List<BookingResponseDto> toDtoList(List<Booking> bookings);
}

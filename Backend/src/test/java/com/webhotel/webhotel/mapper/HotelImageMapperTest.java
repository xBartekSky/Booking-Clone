package com.webhotel.webhotel.mapper;

import com.webhotel.webhotel.dto.HotelImageDto;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.HotelImage;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HotelImageMapperTest {

    private final HotelImageMapper mapper = Mappers.getMapper(HotelImageMapper.class);

    @Test
    void testHotelImageToHotelImageDto() {
        Hotel hotel = new Hotel();
        hotel.setId(10L);

        HotelImage entity = new HotelImage();
        entity.setId(1L);
        entity.setImageUrl("https://example.com/image.jpg");
        entity.setHotel(hotel);
        entity.setUploadedAt(LocalDateTime.of(2023, 1, 1, 12, 0));

        HotelImageDto dto = mapper.otelImageToHotelImageDto(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getImageUrl(), dto.getImageUrl());
        assertEquals(entity.getUploadedAt(), dto.getUploadedAt());
        assertEquals(hotel.getId(), dto.getHotelId());
    }

    @Test
    void testHotelImageDtoToHotelImage() {
        HotelImageDto dto = new HotelImageDto();
        dto.setId(2L);
        dto.setImageUrl("https://example.com/photo.png");
        dto.setUploadedAt(LocalDateTime.of(2022, 5, 5, 8, 30));
        dto.setHotelId(20L);

        HotelImage entity = mapper.hotelImageDtoToHotelImage(dto);

        assertEquals(2L, entity.getId());
        assertEquals(dto.getImageUrl(), entity.getImageUrl());
        assertEquals(dto.getUploadedAt(), entity.getUploadedAt());
        assertNotNull(entity.getHotel());
        assertEquals(dto.getHotelId(), entity.getHotel().getId());
    }
}

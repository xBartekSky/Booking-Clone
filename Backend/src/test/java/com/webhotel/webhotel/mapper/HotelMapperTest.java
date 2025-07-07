package com.webhotel.webhotel.mapper;

import com.webhotel.webhotel.dto.CreateHotelDto;
import com.webhotel.webhotel.dto.HotelDto;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HotelMapperTest {

    private final HotelMapper hotelMapper = Mappers.getMapper(HotelMapper.class);

    private Hotel hotel;
    private HotelDto hotelDto;
    private CreateHotelDto createHotelDto;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("owner");

        hotel = new Hotel();
        hotel.setId(10L);
        hotel.setName("Grand Hotel");
        hotel.setDescription("Luxury stay");
        hotel.setAddress("123 Street");
        hotel.setCity("Paris");
        hotel.setCountry("France");
        hotel.setPhoneNumber("123456789");
        hotel.setEmail("info@grand.com");
        hotel.setRating(4.5);
        hotel.setMainImageUrl("image.jpg");
        hotel.setHasWifi(true);
        hotel.setHasParking(true);
        hotel.setHasPool(true);
        hotel.setHasGym(true);
        hotel.setHasRestaurant(true);
        hotel.setHasBar(true);
        hotel.setHasSpa(true);
        hotel.setHasPetFriendly(false);
        hotel.setHasAirConditioning(true);
        hotel.setHasLaundryService(false);
        hotel.setCreatedAt(LocalDateTime.now().minusDays(1));
        hotel.setUpdatedAt(LocalDateTime.now());
        hotel.setOwner(owner);

        hotelDto = new HotelDto();
        hotelDto.setId(10L);
        hotelDto.setOwnerId(1L);
        hotelDto.setName("Grand Hotel");
        hotelDto.setDescription("Luxury stay");
        hotelDto.setAddress("123 Street");
        hotelDto.setCity("Paris");
        hotelDto.setCountry("France");
        hotelDto.setPhoneNumber("123456789");
        hotelDto.setEmail("info@grand.com");
        hotelDto.setRating(4.5);
        hotelDto.setMainImageUrl("image.jpg");
        hotelDto.setHasWifi(true);
        hotelDto.setHasParking(true);
        hotelDto.setHasPool(true);
        hotelDto.setHasGym(true);
        hotelDto.setHasRestaurant(true);
        hotelDto.setHasBar(true);
        hotelDto.setHasSpa(true);
        hotelDto.setHasPetFriendly(false);
        hotelDto.setHasAirConditioning(true);
        hotelDto.setHasLaundryService(false);
        hotelDto.setCreatedAt(hotel.getCreatedAt());
        hotelDto.setUpdatedAt(hotel.getUpdatedAt());
        hotelDto.setImages(List.of()); // For simplicity

        createHotelDto = new CreateHotelDto();
        createHotelDto.setName("Grand Hotel");
        createHotelDto.setDescription("Luxury stay");
        createHotelDto.setAddress("123 Street");
        createHotelDto.setCity("Paris");
        createHotelDto.setCountry("France");
        createHotelDto.setPhoneNumber("123456789");
        createHotelDto.setEmail("info@grand.com");
        createHotelDto.setRating(4.5);
        createHotelDto.setMainImageUrl("image.jpg");
        createHotelDto.setHasWifi(true);
        createHotelDto.setHasParking(true);
        createHotelDto.setHasPool(true);
        createHotelDto.setHasGym(true);
        createHotelDto.setHasRestaurant(true);
        createHotelDto.setHasBar(true);
    }

    @Test
    void testHotelToHotelDto() {
        HotelDto dto = hotelMapper.hotelToHotelDto(hotel);

        assertEquals(hotel.getId(), dto.getId());
        assertEquals(hotel.getOwner().getId(), dto.getOwnerId());
        assertEquals(hotel.getName(), dto.getName());
        assertEquals(hotel.getCity(), dto.getCity());
        assertEquals(hotel.getMainImageUrl(), dto.getMainImageUrl());
        assertEquals(hotel.isHasWifi(), dto.isHasWifi());
        assertEquals(hotel.isHasPool(), dto.isHasPool());
        assertEquals(hotel.getCreatedAt(), dto.getCreatedAt());
    }

    @Test
    void testHotelDtoToHotel() {
        Hotel result = hotelMapper.HotelDtoToHotel(hotelDto);

        assertEquals(hotelDto.getName(), result.getName());
        assertEquals(hotelDto.getOwnerId(), result.getOwner().getId());
        assertEquals(hotelDto.getCity(), result.getCity());
        assertEquals(hotelDto.getMainImageUrl(), result.getMainImageUrl());
        assertEquals(hotelDto.isHasWifi(), result.isHasWifi());
        assertEquals(hotelDto.getRating(), result.getRating());
    }

    @Test
    void testCreateHotelDtoToHotel() {
        Hotel result = hotelMapper.createHotelDtoToHotel(createHotelDto);

        assertEquals(createHotelDto.getName(), result.getName());
        assertEquals(createHotelDto.getCity(), result.getCity());
        assertEquals(createHotelDto.isHasWifi(), result.isHasWifi());
        assertEquals(createHotelDto.getMainImageUrl(), result.getMainImageUrl());

        // These are explicitly ignored
        assertNull(result.getOwner());
        assertTrue(result.getImages() == null || result.getImages().isEmpty());
    }
}

package com.webhotel.webhotel.service;

import com.webhotel.webhotel.dto.CreateHotelDto;
import com.webhotel.webhotel.dto.HotelDto;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.HotelMapper;
import com.webhotel.webhotel.repository.HotelRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel;
    private HotelDto hotelDto;
    private CreateHotelDto createHotelDto;
    private User owner;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setId(1L);
        owner.setUsername("owner");

        hotel = new Hotel();
        hotel.setId(10L);
        hotel.setName("Test Hotel");
        hotel.setOwner(owner);
        hotel.setCity("Paris");
        hotel.setAddress("123 Main Street");
        hotel.setCountry("France");
        hotel.setPhoneNumber("123-456-7890");
        hotel.setEmail("contact@testhotel.com");
        hotel.setRating(4.5);
        hotel.setMainImageUrl("http://example.com/image.jpg");
        hotel.setHasWifi(true);
        hotel.setHasParking(true);
        hotel.setHasPool(true);
        hotel.setHasSpa(true);
        hotel.setHasBar(true);
        hotel.setHasRestaurant(true);
        hotel.setHasGym(true);
        hotel.setHasAirConditioning(true);
        hotel.setHasPetFriendly(true);
        hotel.setHasLaundryService(true);
        hotel.setCreatedAt(LocalDateTime.now().minusDays(1));
        hotel.setUpdatedAt(LocalDateTime.now());

        hotelDto = new HotelDto();
        hotelDto.setId(10L);
        hotelDto.setName("Test Hotel");
        hotelDto.setOwnerId(1L);
        hotelDto.setCity("Paris");
        hotelDto.setAddress("123 Main Street");
        hotelDto.setCountry("France");
        hotelDto.setPhoneNumber("123-456-7890");
        hotelDto.setEmail("contact@testhotel.com");
        hotelDto.setRating(4.5);
        hotelDto.setMainImageUrl("http://example.com/image.jpg");
        hotelDto.setHasWifi(true);
        hotelDto.setHasParking(true);
        hotelDto.setHasPool(true);
        hotelDto.setHasSpa(true);
        hotelDto.setHasBar(true);
        hotelDto.setHasRestaurant(true);
        hotelDto.setHasGym(true);
        hotelDto.setHasAirConditioning(true);
        hotelDto.setHasPetFriendly(true);
        hotelDto.setHasLaundryService(true);
        hotelDto.setCreatedAt(hotel.getCreatedAt());
        hotelDto.setUpdatedAt(hotel.getUpdatedAt());

        createHotelDto = new CreateHotelDto();
        createHotelDto.setName("Test Hotel");
        createHotelDto.setCity("Paris");
        createHotelDto.setAddress("123 Main Street");
        createHotelDto.setCountry("France");
        createHotelDto.setPhoneNumber("123-456-7890");
        createHotelDto.setEmail("contact@testhotel.com");
        createHotelDto.setHasWifi(true);
        createHotelDto.setHasParking(true);
        createHotelDto.setHasPool(true);
        createHotelDto.setHasSpa(true);
        createHotelDto.setHasBar(true);
        createHotelDto.setHasRestaurant(true);
        createHotelDto.setHasGym(true);
        createHotelDto.setHasAirConditioning(true);
        createHotelDto.setHasPetFriendly(true);
        createHotelDto.setHasLaundryService(true);
    }

    @Test
    void testCreateHotel() {
        when(hotelMapper.createHotelDtoToHotel(createHotelDto)).thenReturn(hotel);
        when(hotelRepository.save(hotel)).thenReturn(hotel);
        when(hotelMapper.hotelToHotelDto(hotel)).thenReturn(hotelDto);

        HotelDto result = hotelService.createHotel(createHotelDto, owner);

        assertEquals("Test Hotel", result.getName());
        assertEquals("Paris", result.getCity());
        assertEquals("France", result.getCountry());
        assertEquals("contact@testhotel.com", result.getEmail());
        assertEquals("123-456-7890", result.getPhoneNumber());
        assertEquals("http://example.com/image.jpg", result.getMainImageUrl());
        assertEquals(true, result.isHasWifi());
        assertEquals(true, result.isHasParking());
        assertEquals(true, result.isHasPool());
        assertEquals(true, result.isHasSpa());
        assertEquals(true, result.isHasBar());
        assertEquals(true, result.isHasRestaurant());
        assertEquals(true, result.isHasGym());
        assertEquals(true, result.isHasAirConditioning());
        assertEquals(true, result.isHasPetFriendly());
        assertEquals(true, result.isHasLaundryService());
        assertEquals(1L, result.getOwnerId());
    }

    @Test
    void testGetAllHotels() {
        when(hotelRepository.findAll()).thenReturn(List.of(hotel));
        when(hotelMapper.hotelToHotelDto(hotel)).thenReturn(hotelDto);

        List<HotelDto> result = hotelService.getAllHotels();

        assertEquals(1, result.size());
        assertEquals(hotelDto, result.get(0));
    }

    @Test
    void testGetHotelByIdFound() {
        when(hotelRepository.findById(10L)).thenReturn(Optional.of(hotel));
        when(hotelMapper.hotelToHotelDto(hotel)).thenReturn(hotelDto);

        Optional<HotelDto> result = hotelService.getHotelById(10L);

        assertTrue(result.isPresent());
        assertEquals(hotelDto, result.get());
    }

    @Test
    void testGetHotelByIdNotFound() {
        when(hotelRepository.findById(10L)).thenReturn(Optional.empty());

        Optional<HotelDto> result = hotelService.getHotelById(10L);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteHotel() {
        doNothing().when(hotelRepository).deleteById(10L);

        hotelService.deleteHotel(10L);

        verify(hotelRepository, times(1)).deleteById(10L);
    }

    @Test
    void testSearchHotelsByName() {
        when(hotelRepository.findByNameIgnoreCaseContaining("test")).thenReturn(List.of(hotel));
        when(hotelMapper.hotelToHotelDto(hotel)).thenReturn(hotelDto);

        List<HotelDto> result = hotelService.searchHotelsByName("test");

        assertEquals(1, result.size());
        assertEquals(hotelDto, result.get(0));
    }

    @Test
    void testGetHotelsByOwnerId() {
        when(hotelRepository.findByOwnerId(1L)).thenReturn(List.of(hotel));
        when(hotelMapper.hotelToHotelDto(hotel)).thenReturn(hotelDto);

        List<HotelDto> result = hotelService.getHotelsByOwnerId(1L);

        assertEquals(1, result.size());
        assertEquals(hotelDto, result.get(0));
    }

    @Test
    void testSearchHotelsByCity() {
        when(hotelRepository.findByCityIgnoreCaseContaining("paris")).thenReturn(List.of(hotel));
        when(hotelMapper.hotelToHotelDto(hotel)).thenReturn(hotelDto);

        List<HotelDto> result = hotelService.searchHotelsByCity("paris");

        assertEquals(1, result.size());
        assertEquals(hotelDto, result.get(0));
    }

    @Test
    void testGetHotelAverageRating() {
        when(hotelRepository.findAverageRating(10L)).thenReturn(4.3);

        Double rating = hotelService.getHotelAverageRating(10L);

        assertEquals(4.3, rating);
    }
}

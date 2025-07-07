package com.webhotel.webhotel.controllers;

import com.webhotel.webhotel.config.AuthProvider;
import com.webhotel.webhotel.controllers.HotelController;
import com.webhotel.webhotel.dto.*;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.HotelImage;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.HotelMapper;
import com.webhotel.webhotel.repository.HotelRepository;
import com.webhotel.webhotel.repository.UserRepository;
import com.webhotel.webhotel.service.HotelService;
import com.webhotel.webhotel.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelControllerTest {

    private HotelService hotelService;
    private AuthProvider authProvider;
    private UserRepository userRepository;
    private RoomService roomService;
    private HotelMapper hotelMapper;
    private HotelRepository hotelRepository;
    private HotelController hotelController;

    @BeforeEach
    void setUp() {
        hotelService = mock(HotelService.class);
        authProvider = mock(AuthProvider.class);
        userRepository = mock(UserRepository.class);
        roomService = mock(RoomService.class);
        hotelMapper = mock(HotelMapper.class);
        hotelRepository = mock(HotelRepository.class);

        hotelController = new HotelController(hotelRepository);

        org.springframework.test.util.ReflectionTestUtils.setField(hotelController, "hotelService", hotelService);
        org.springframework.test.util.ReflectionTestUtils.setField(hotelController, "authProvider", authProvider);
        org.springframework.test.util.ReflectionTestUtils.setField(hotelController, "userRepository", userRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(hotelController, "roomService", roomService);
        org.springframework.test.util.ReflectionTestUtils.setField(hotelController, "hotelMapper", hotelMapper);
    }

    private Authentication mockAuth(String username) {
        Authentication auth = mock(Authentication.class);
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        when(auth.getPrincipal()).thenReturn(userDto);
        return auth;
    }

    private User mockUser(String username, Long id) {
        User user = new User();
        user.setUsername(username);
        user.setId(id);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        return user;
    }

    @Test
    void testCreateHotel() {
        String token = "token";
        String authHeader = "Bearer " + token;
        CreateHotelDto createHotelDto = new CreateHotelDto();
        HotelDto hotelDto = new HotelDto();
        hotelDto.setName("Hotel");

        Authentication auth = mockAuth("user");
        when(authProvider.validateToken(token)).thenReturn(auth);
        User user = mockUser("user", 1L);
        when(hotelService.createHotel(createHotelDto, user)).thenReturn(hotelDto);

        ResponseEntity<HotelDto> response = hotelController.createHotel(createHotelDto, authHeader);

        assertEquals(201, response.getStatusCode().value());

        assertEquals("Hotel", response.getBody().getName());
    }

    @Test
    void testGetAllHotels() {
        HotelDto hotelDto = new HotelDto();
        when(hotelService.getAllHotels()).thenReturn(List.of(hotelDto));
        ResponseEntity<List<HotelDto>> response = hotelController.getAllHotels();
        assertEquals(200, response.getStatusCode().value());

        assertEquals(1, response.getBody().size());
    }

    @Test
    void testSearchByCity() {
        when(hotelService.searchHotelsByCity("City")).thenReturn(Collections.emptyList());
        List<HotelDto> hotels = hotelController.searchHotelsByCity("City");
        assertNotNull(hotels);
    }

    @Test
    void testSearchByName() {
        when(hotelService.searchHotelsByName("Name")).thenReturn(Collections.emptyList());
        List<HotelDto> hotels = hotelController.searchHotelsByName("Name");
        assertNotNull(hotels);
    }

    @Test
    void testGetMyHotels() {
        String token = "token";
        String authHeader = "Bearer " + token;
        Authentication auth = mockAuth("user");
        when(authProvider.validateToken(token)).thenReturn(auth);
        User user = mockUser("user", 1L);
        when(hotelService.getHotelsByOwnerId(user.getId())).thenReturn(Collections.emptyList());

        ResponseEntity<List<HotelDto>> response = hotelController.getMyHotels(authHeader);
        assertEquals(200, response.getStatusCode().value());

    }

    @Test
    void testGetHotelById() {
        HotelDto hotelDto = new HotelDto();
        when(hotelService.getHotelById(1L)).thenReturn(Optional.of(hotelDto));
        ResponseEntity<HotelDto> response = hotelController.getHotelById(1L);
        assertEquals(200, response.getStatusCode().value());

    }

    @Test
    void testDeleteHotel() {
        String token = "token";
        String authHeader = "Bearer " + token;
        Authentication auth = mockAuth("user");
        when(authProvider.validateToken(token)).thenReturn(auth);
        User user = mockUser("user", 1L);

        HotelDto hotelDto = new HotelDto();
        hotelDto.setOwnerId(1L);
        when(hotelService.getHotelById(1L)).thenReturn(Optional.of(hotelDto));

        ResponseEntity<Void> response = hotelController.deleteHotel(1L, authHeader);
        assertEquals(204, response.getStatusCode().value());
        verify(hotelService).deleteHotel(1L);
    }

    @Test
    void testGetRoomsByHotelId() {
        when(roomService.getRoomsByHotelId(1L)).thenReturn(Collections.emptyList());
        ResponseEntity<List<RoomDto>> response = hotelController.getRoomsByHotelId(1L);
        assertEquals(200, response.getStatusCode().value());

    }

    @Test
    void testUploadImage() throws IOException {
        String token = "token";
        String authHeader = "Bearer " + token;
        MultipartFile file = mock(MultipartFile.class);

        Authentication auth = mockAuth("user");
        when(authProvider.validateToken(token)).thenReturn(auth);
        User user = mockUser("user", 1L);
        HotelDto hotelDto = new HotelDto();
        hotelDto.setOwnerId(1L);
        when(hotelService.getHotelById(1L)).thenReturn(Optional.of(hotelDto));
        when(hotelMapper.HotelDtoToHotel(hotelDto)).thenReturn(new Hotel());

        ResponseEntity<String> response = hotelController.uploadHotelImage(1L, file, authHeader);
        assertEquals(200, response.getStatusCode().value());

    }

    @Test
    void testUploadGalleryImage() throws IOException {
        String token = "token";
        String authHeader = "Bearer " + token;
        MultipartFile file = mock(MultipartFile.class);

        Authentication auth = mockAuth("user");
        when(authProvider.validateToken(token)).thenReturn(auth);
        User user = mockUser("user", 1L);
        HotelDto hotelDto = new HotelDto();
        hotelDto.setOwnerId(1L);
        when(hotelService.getHotelById(1L)).thenReturn(Optional.of(hotelDto));

        Hotel hotel = new Hotel();
        when(hotelMapper.HotelDtoToHotel(hotelDto)).thenReturn(hotel);

        ResponseEntity<String> response = hotelController.uploadGalleryImage(1L, file, authHeader);
        assertEquals(200, response.getStatusCode().value());

    }
}
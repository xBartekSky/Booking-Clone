package com.webhotel.webhotel.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webhotel.webhotel.config.AuthProvider;
import com.webhotel.webhotel.dto.RoomDto;
import com.webhotel.webhotel.dto.UserDto;
import com.webhotel.webhotel.entity.Booking;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.repository.BookingRepository;
import com.webhotel.webhotel.repository.UserRepository;
import com.webhotel.webhotel.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoomControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoomService roomService;

    @Mock
    private AuthProvider authProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private RoomController roomController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
    }

    @Test
    void testGetAllRooms() throws Exception {
        RoomDto dto = new RoomDto();
        dto.setId(1L);
        dto.setRoomNumber("101");
        dto.setRoomType("Double");
        dto.setPricePerNight(BigDecimal.valueOf(120.50));
        dto.setHotelId(10L);

        when(roomService.getAllRooms()).thenReturn(List.of(dto));

        mockMvc.perform(get("/rooms/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomNumber").value("101"))
                .andExpect(jsonPath("$[0].pricePerNight").value(120.50));
    }

    @Test
    void testCreateRoom() throws Exception {
        RoomDto input = new RoomDto();
        input.setRoomNumber("102");
        input.setRoomType("Single");
        input.setPricePerNight(BigDecimal.valueOf(80.00));
        input.setHotelId(5L);

        RoomDto saved = new RoomDto();
        saved.setId(2L);
        saved.setRoomNumber("102");
        saved.setRoomType("Single");
        saved.setPricePerNight(BigDecimal.valueOf(80.00));
        saved.setHotelId(5L);

        String fakeToken = "valid.token.here";
        String header = "Bearer " + fakeToken;

        Authentication auth = mock(Authentication.class);
        UserDto userDto = new UserDto();
        userDto.setUsername("admin");

        when(authProvider.validateToken(fakeToken)).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDto);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(new User()));
        when(roomService.saveRoom(any(RoomDto.class), any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/rooms/")
                .header("Authorization", header)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomNumber").value("102"));
    }

    @Test
    void testGetRoomById() throws Exception {
        RoomDto dto = new RoomDto();
        dto.setId(3L);
        dto.setRoomNumber("103");
        dto.setRoomType("Suite");
        dto.setPricePerNight(BigDecimal.valueOf(200));
        dto.setHotelId(7L);

        when(roomService.getRoomById(3)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/rooms/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomType").value("Suite"));
    }

    @Test
    void testDeleteRoom() throws Exception {
        doNothing().when(roomService).deleteRoom(4);
        mockMvc.perform(delete("/rooms/4"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUnavailableDates() throws Exception {
        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.of(2025, 7, 1));
        booking.setCheckOutDate(LocalDate.of(2025, 7, 5));
        booking.setCanceled(false);

        when(bookingRepository.findByRoomIdAndCanceledFalse(1L)).thenReturn(List.of(booking));

        mockMvc.perform(get("/rooms/1/unavailable-dates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].checkIn[0]").value(2025))
                .andExpect(jsonPath("$[0].checkIn[1]").value(7))
                .andExpect(jsonPath("$[0].checkIn[2]").value(1))
                .andExpect(jsonPath("$[0].checkOut[0]").value(2025))
                .andExpect(jsonPath("$[0].checkOut[1]").value(7))
                .andExpect(jsonPath("$[0].checkOut[2]").value(5));
    }

}

package com.webhotel.webhotel.controllers;

import com.webhotel.webhotel.config.AuthProvider;
import com.webhotel.webhotel.dto.CreateHotelDto;
import com.webhotel.webhotel.dto.HotelDto;
import com.webhotel.webhotel.dto.RoomDto;
import com.webhotel.webhotel.dto.UserDto;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.HotelImage;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.HotelMapper;
import com.webhotel.webhotel.repository.HotelRepository;
import com.webhotel.webhotel.repository.UserRepository;
import com.webhotel.webhotel.service.HotelService;
import com.webhotel.webhotel.service.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelRepository hotelRepository;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private HotelMapper hotelMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    HotelController(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody CreateHotelDto hotelDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        HotelDto createdHotel = hotelService.createHotel(hotelDto, user);

        return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        List<HotelDto> hotels = hotelService.getAllHotels();
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }

    @GetMapping("/searchByCity")
    public List<HotelDto> searchHotelsByCity(@RequestParam String city) {
        return hotelService.searchHotelsByCity(city);
    }

    @GetMapping("/searchByName")
    public List<HotelDto> searchHotelsByName(@RequestParam String name) {
        return hotelService.searchHotelsByName(name);
    }

    @GetMapping("/myHotels")
    public ResponseEntity<List<HotelDto>> getMyHotels(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<HotelDto> hotels = hotelService.getHotelsByOwnerId(user.getId());
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id) {
        Optional<HotelDto> hotel = hotelService.getHotelById(id);
        return hotel.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/protected/id/{id}")
    public ResponseEntity<HotelDto> getHotelByIdProtected(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<HotelDto> hotelOptional = hotelService.getHotelById(id);
        if (hotelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HotelDto hotel = hotelOptional.get();
        if (!hotel.getOwnerId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/deleteHotel/{id}")
    public ResponseEntity<Void> deleteHotel(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Sprawdzenie czy hotel należy do użytkownika
        Optional<HotelDto> hotelOptional = hotelService.getHotelById(id);

        if (hotelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HotelDto hotel = hotelOptional.get();
        if (!hotel.getOwnerId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<RoomDto>> getRoomsByHotelId(@PathVariable Long hotelId) {
        List<RoomDto> rooms = roomService.getRoomsByHotelId(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/protected/{hotelId}/rooms")
    public ResponseEntity<List<RoomDto>> getRoomsByHotelId(
            @PathVariable Long hotelId,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        Authentication auth = authProvider.validateToken(token);
        String username = ((UserDto) auth.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<HotelDto> hotelOpt = hotelService.getHotelById(hotelId);
        if (hotelOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HotelDto hotel = hotelOpt.get();
        if (!hotel.getOwnerId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<RoomDto> rooms = roomService.getRoomsByHotelId(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/{hotelId}/uploadImage")
    public ResponseEntity<String> uploadHotelImage(@PathVariable Long hotelId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {

        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<HotelDto> hotelOptional = hotelService.getHotelById(hotelId);
        if (hotelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HotelDto hotelDto = hotelOptional.get();
        if (!hotelDto.getOwnerId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String uploadDir = "uploads/images/main";
        String fileName = hotelId + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        Files.createDirectories(filePath.getParent());
        file.transferTo(filePath);

        Hotel hotel = hotelMapper.HotelDtoToHotel(hotelDto);
        hotel.setMainImageUrl("/images/main/" + fileName);
        hotelRepository.save(hotel);

        return ResponseEntity.ok("/images/main/" + fileName);
    }

    @PostMapping("/{hotelId}/uploadGalleryImage")
    public ResponseEntity<String> uploadGalleryImage(@PathVariable Long hotelId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {

        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<HotelDto> hotelOptional = hotelService.getHotelById(hotelId);
        if (hotelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HotelDto hotelDto = hotelOptional.get();
        if (!hotelDto.getOwnerId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String uploadDir = "uploads/images/gallery";
        String fileName = hotelId + "_gallery_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        Files.createDirectories(filePath.getParent());
        file.transferTo(filePath);

        Hotel hotel = hotelMapper.HotelDtoToHotel(hotelDto);

        HotelImage image = new HotelImage();
        image.setHotel(hotel);
        image.setImageUrl("/images/gallery/" + fileName);
        image.setUploadedAt(LocalDateTime.now());

        hotel.getImages().add(image);
        hotelRepository.save(hotel);

        return ResponseEntity.ok("/images/gallery/" + fileName);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable Long id,
            @RequestBody HotelDto hotelDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<HotelDto> existingHotelOpt = hotelService.getHotelById(id);
        if (existingHotelOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HotelDto existingHotel = existingHotelOpt.get();
        if (!existingHotel.getOwnerId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (hotelDto.getName() != null) {
            existingHotel.setName(hotelDto.getName());
        }
        if (hotelDto.getDescription() != null) {
            existingHotel.setDescription(hotelDto.getDescription());
        }
        if (hotelDto.getAddress() != null) {
            existingHotel.setAddress(hotelDto.getAddress());
        }
        if (hotelDto.getCity() != null) {
            existingHotel.setCity(hotelDto.getCity());
        }
        if (hotelDto.getCountry() != null) {
            existingHotel.setCountry(hotelDto.getCountry());
        }
        if (hotelDto.getPhoneNumber() != null) {
            existingHotel.setPhoneNumber(hotelDto.getPhoneNumber());
        }
        if (hotelDto.getEmail() != null) {
            existingHotel.setEmail(hotelDto.getEmail());
        }
        if (hotelDto.getRating() != null) {
            existingHotel.setRating(hotelDto.getRating());
        }
        if (hotelDto.getMainImageUrl() != null) {
            existingHotel.setMainImageUrl(hotelDto.getMainImageUrl());
        }
        if (hotelDto.isHasWifi() != existingHotel.isHasWifi()) {
            existingHotel.setHasWifi(hotelDto.isHasWifi());
        }
        if (hotelDto.isHasParking() != existingHotel.isHasParking()) {
            existingHotel.setHasParking(hotelDto.isHasParking());
        }
        if (hotelDto.isHasPool() != existingHotel.isHasPool()) {
            existingHotel.setHasPool(hotelDto.isHasPool());
        }
        if (hotelDto.isHasGym() != existingHotel.isHasGym()) {
            existingHotel.setHasGym(hotelDto.isHasGym());
        }
        if (hotelDto.isHasRestaurant() != existingHotel.isHasRestaurant()) {
            existingHotel.setHasRestaurant(hotelDto.isHasRestaurant());
        }
        if (hotelDto.isHasBar() != existingHotel.isHasBar()) {
            existingHotel.setHasBar(hotelDto.isHasBar());
        }
        if (hotelDto.isHasSpa() != existingHotel.isHasSpa()) {
            existingHotel.setHasSpa(hotelDto.isHasSpa());
        }
        if (hotelDto.isHasPetFriendly() != existingHotel.isHasPetFriendly()) {
            existingHotel.setHasPetFriendly(hotelDto.isHasPetFriendly());
        }
        if (hotelDto.isHasAirConditioning() != existingHotel.isHasAirConditioning()) {
            existingHotel.setHasAirConditioning(hotelDto.isHasAirConditioning());
        }
        if (hotelDto.isHasLaundryService() != existingHotel.isHasLaundryService()) {
            existingHotel.setHasLaundryService(hotelDto.isHasLaundryService());
        }
        existingHotel.setUpdatedAt(LocalDateTime.now());

        HotelDto updatedHotel = hotelService.updateHotel(id, existingHotel);

        return ResponseEntity.ok(updatedHotel);
    }

    @GetMapping("/{hotelId}/availableRooms")
    public ResponseEntity<List<RoomDto>> getAvailableRooms(
            @PathVariable Long hotelId,
            @RequestParam("checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam("checkOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        List<RoomDto> availableRooms = roomService.getAvailableRooms(hotelId, checkIn, checkOut);
        return ResponseEntity.ok(availableRooms);
    }

}

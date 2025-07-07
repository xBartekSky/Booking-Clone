package com.webhotel.webhotel.service;

import org.springframework.transaction.annotation.Transactional;

import com.webhotel.webhotel.dto.CreateHotelDto;
import com.webhotel.webhotel.dto.HotelDto;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.HotelMapper;
import com.webhotel.webhotel.repository.HotelRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelMapper hotelMapper;

    public HotelDto createHotel(CreateHotelDto hotelDto, User user) {
        Hotel hotel = hotelMapper.createHotelDtoToHotel(hotelDto);
        hotel.setOwner(user);
        hotel = hotelRepository.save(hotel);
        return hotelMapper.hotelToHotelDto(hotel);
    }

    public List<HotelDto> getAllHotels() {
        List<Hotel> hotels = (List<Hotel>) hotelRepository.findAll();
        List<HotelDto> HotelDtos = new ArrayList<>();
        for (Hotel hotel : hotels) {
            HotelDto HotelDto = hotelMapper.hotelToHotelDto(hotel);
            HotelDtos.add(HotelDto);
        }

        return HotelDtos;
    }

    public Optional<HotelDto> getHotelById(Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        return hotel.map(hotelMapper::hotelToHotelDto);
    }

    @Transactional
    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    public List<HotelDto> searchHotelsByName(String name) {
        List<Hotel> hotels = hotelRepository.findByNameIgnoreCaseContaining(name);
        List<HotelDto> HotelDtos = new ArrayList<>();
        for (Hotel hotel : hotels) {
            HotelDto HotelDto = hotelMapper.hotelToHotelDto(hotel);
            HotelDtos.add(HotelDto);
        }

        return HotelDtos;
    }

    public List<HotelDto> getHotelsByOwnerId(Long ownerId) {
        List<Hotel> hotels = hotelRepository.findByOwnerId(ownerId);
        List<HotelDto> hotelDtos = new ArrayList<>();

        for (Hotel hotel : hotels) {
            HotelDto hotelDto = hotelMapper.hotelToHotelDto(hotel);
            hotelDtos.add(hotelDto);
        }

        return hotelDtos;
    }

    public List<HotelDto> searchHotelsByCity(String city) {
        List<Hotel> hotels = hotelRepository.findByCityIgnoreCaseContaining(city);
        List<HotelDto> HotelDtos = new ArrayList<>();

        for (Hotel hotel : hotels) {
            HotelDto HotelDto = hotelMapper.hotelToHotelDto(hotel);
            HotelDtos.add(HotelDto);
        }

        return HotelDtos;
    }

    public Double getHotelAverageRating(Long hotelId) {
        return hotelRepository.findAverageRating(hotelId);
    }

    @Transactional
    public HotelDto updateHotel(Long id, HotelDto hotelDto) {
        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        if (hotelDto.getName() != null)
            existingHotel.setName(hotelDto.getName());
        if (hotelDto.getDescription() != null)
            existingHotel.setDescription(hotelDto.getDescription());
        if (hotelDto.getAddress() != null)
            existingHotel.setAddress(hotelDto.getAddress());
        if (hotelDto.getCity() != null)
            existingHotel.setCity(hotelDto.getCity());
        if (hotelDto.getCountry() != null)
            existingHotel.setCountry(hotelDto.getCountry());
        if (hotelDto.getPhoneNumber() != null)
            existingHotel.setPhoneNumber(hotelDto.getPhoneNumber());
        if (hotelDto.getEmail() != null)
            existingHotel.setEmail(hotelDto.getEmail());
        if (hotelDto.getRating() != null)
            existingHotel.setRating(hotelDto.getRating());
        if (hotelDto.getMainImageUrl() != null)
            existingHotel.setMainImageUrl(hotelDto.getMainImageUrl());

        existingHotel.setHasWifi(hotelDto.isHasWifi());
        existingHotel.setHasParking(hotelDto.isHasParking());
        existingHotel.setHasPool(hotelDto.isHasPool());
        existingHotel.setHasGym(hotelDto.isHasGym());
        existingHotel.setHasRestaurant(hotelDto.isHasRestaurant());
        existingHotel.setHasBar(hotelDto.isHasBar());
        existingHotel.setHasSpa(hotelDto.isHasSpa());
        existingHotel.setHasPetFriendly(hotelDto.isHasPetFriendly());
        existingHotel.setHasAirConditioning(hotelDto.isHasAirConditioning());
        existingHotel.setHasLaundryService(hotelDto.isHasLaundryService());

        existingHotel.setUpdatedAt(LocalDateTime.now());

        Hotel savedHotel = hotelRepository.save(existingHotel);
        return hotelMapper.hotelToHotelDto(savedHotel);
    }

}

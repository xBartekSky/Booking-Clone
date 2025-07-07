package com.webhotel.webhotel.mapper;

import com.webhotel.webhotel.dto.ReviewDto;
import com.webhotel.webhotel.entity.Review;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "hotel.id", target = "hotelId")
    ReviewDto ReviewtoDto(Review review);

    List<ReviewDto> ReviewtoDtoList(List<Review> reviews);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toEntity(ReviewDto dto);
}
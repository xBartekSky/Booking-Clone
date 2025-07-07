package com.webhotel.webhotel.mapper;

import com.webhotel.webhotel.dto.RegisterDto;
import com.webhotel.webhotel.dto.UserDto;
import com.webhotel.webhotel.dto.UserInfoDto;
import com.webhotel.webhotel.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testToUserDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setPassword("secret");

        UserDto dto = userMapper.toUserDto(user);

        assertEquals("johndoe", dto.getUsername());
        assertEquals("john@example.com", dto.getEmail());
        assertNull(dto.getToken()); // token is ignored by mapping
    }

    @Test
    void testRegisterToUser() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("newuser");
        dto.setEmail("newuser@example.com");
        dto.setPassword("password123");

        User user = userMapper.registerToUser(dto);

        assertEquals("newuser", user.getUsername());
        assertEquals("newuser@example.com", user.getEmail());

        // Fields ignored in mapping
        assertNull(user.getId());
        assertNull(user.getPassword()); // explicitly ignored
        assertNull(user.getAddress());
        assertNull(user.getName());
        assertNull(user.getSurname());
        assertNull(user.getPhoneNumber());
    }

    @Test
    void testUserToUserInfoDto() {
        User user = new User();
        user.setId(10L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setName("Alice");
        user.setSurname("Wonderland");
        user.setAddress("123 Street");
        user.setPhoneNumber("555-1234");

        UserInfoDto dto = userMapper.userToUserInfoDto(user);

        assertEquals(10L, dto.getId());
        assertEquals("alice", dto.getUsername());
        assertEquals("alice@example.com", dto.getEmail());
        assertEquals("Alice", dto.getName());
        assertEquals("Wonderland", dto.getSurname());
        assertEquals("123 Street", dto.getAddress());
        assertEquals("555-1234", dto.getPhoneNumber());
    }

    @Test
    void testUserInfoDtoToUser() {
        UserInfoDto dto = new UserInfoDto();
        dto.setId(99L); // should be ignored
        dto.setUsername("bob");
        dto.setEmail("bob@example.com");
        dto.setName("Bob");
        dto.setSurname("Builder");
        dto.setAddress("789 Build Lane");
        dto.setPhoneNumber("555-6789");

        User user = userMapper.userInfoDtoToUser(dto);

        assertEquals("bob", user.getUsername());
        assertEquals("bob@example.com", user.getEmail());
        assertEquals("Bob", user.getName());
        assertEquals("Builder", user.getSurname());
        assertEquals("789 Build Lane", user.getAddress());
        assertEquals("555-6789", user.getPhoneNumber());

        // Ignored fields
        assertNull(user.getId());
        assertNull(user.getPassword());
    }
}

package com.example.userservicedec24.dtos;

import com.example.userservicedec24.models.Role;
import com.example.userservicedec24.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDto {
    private String name;
    private String email;
    private List<String> roles;

    public static UserDto from(User user) {
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        userDto.setRoles(new ArrayList<>());

        for (Role role : user.getRoles()) {
            userDto.getRoles().add(role.getValue());
        }

        return userDto;
    }
}

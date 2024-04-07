package api.shopmanager.mapper;

import api.shopmanager.dto.RegistrationUserDto;
import api.shopmanager.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationUserDtoToUserMapper {

    private final PasswordEncoder passwordEncoder;

    public User registrationUserDto(RegistrationUserDto registrationUserDto){

        User user = new User();

        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));

        return user;
    }
}

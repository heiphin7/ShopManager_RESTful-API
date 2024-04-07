package api.shopmanager.dto;

import lombok.Data;

@Data
public class RegistrationUserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}

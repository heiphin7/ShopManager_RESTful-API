package api.shopmanager.contollers;

import api.shopmanager.dto.RegistrationUserDto;
import api.shopmanager.entity.User;
import api.shopmanager.mapper.RegistrationUserDtoToUserMapper;
import api.shopmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final RegistrationUserDtoToUserMapper mapper;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody RegistrationUserDto registrationUserDto) {

        try{

            /*
             * Проверка всех полей на пустоту
             */

            if (isAnyFieldEmpty(registrationUserDto)) {
                return new ResponseEntity<>("Все поля должны быть заполнены", HttpStatus.BAD_REQUEST);
            }

        /*
         Мы приступаем к следующим проверкам только ПОСЛЕ того, как проверили все поля на пустоту
         во избежание ошибки
         */

            if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
                return new ResponseEntity<>("Пароли не совпадают", HttpStatus.BAD_REQUEST);
            }

            if (isInvalidPassword(registrationUserDto.getPassword())) {
                return new ResponseEntity<>("Пароль не должен быть пустым", HttpStatus.BAD_REQUEST);
            }

            if (isInvalidUsername(registrationUserDto.getUsername())) {
                return new ResponseEntity<>("Имя пользователя не должно быть больше 5-ти символов без пробелов", HttpStatus.BAD_REQUEST);
            }

            if (!registrationUserDto.getEmail().matches(EMAIL_REGEX)) {
                return new ResponseEntity<>("Введите корректную почту!", HttpStatus.BAD_REQUEST);
            }

            Optional<User> optionalUser = userService.findByUsername(registrationUserDto.getUsername());
            if (optionalUser.isPresent()) {
                return new ResponseEntity<>("Имя пользователя занято!", HttpStatus.BAD_REQUEST);
            }

            // Используем mapper для преобразования RegistrationUserDto в обычного User-а для дальнейшего сохранения

            User user = mapper.registrationUserDto(registrationUserDto);
            userService.save(user);

            // Сообщение о успешной регистрации

            return ResponseEntity.ok("Успешная регистрация");

            // Делаем дополнительную проверку на Null, для лучшей валидации данных

        }catch (NullPointerException e){
            return new ResponseEntity<>("Все поля должны быть заполнены!", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            // Непридвиденная ошибка

            return new ResponseEntity<>("Произошла какая-то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private boolean isInvalidPassword(String password) {
        return password.isBlank() || password.isEmpty();
    }

    private boolean isInvalidUsername(String username) {
        return username.length() < 5 || username.isBlank();
    }

    private boolean isAnyFieldEmpty(RegistrationUserDto registrationUserDto) {
        return registrationUserDto.getUsername().isBlank() ||
                registrationUserDto.getPassword().isBlank() ||
                registrationUserDto.getConfirmPassword().isBlank() ||
                registrationUserDto.getEmail().isBlank();
    }
}

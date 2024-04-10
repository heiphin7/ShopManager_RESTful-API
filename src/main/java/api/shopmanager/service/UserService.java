package api.shopmanager.service;

import api.shopmanager.entity.User;
import api.shopmanager.repository.RoleRepository;
import api.shopmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public void save(User user){
        /*
        * Перед сохранением пользователя мы должны указать его роль.
        * Роль мы достаём по имени из roleRepo
        */
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));

        userRepository.save(user);
    }
    @Transactional
    public void deleteUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Пользователь не найден!")
        );
        userRepository.delete(user);
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

}

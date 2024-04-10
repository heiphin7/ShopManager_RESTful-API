package api.shopmanager.service;

import api.shopmanager.entity.Role;
import api.shopmanager.entity.User;
import api.shopmanager.repository.RoleRepository;
import api.shopmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataInitializerService implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRolesAndUsers();
    }

    private void initializeRolesAndUsers() {

            // Создание ролей
            Role role_user = new Role();
            role_user.setId(1L);
            role_user.setName("ROLE_USER");
            roleRepository.save(role_user);

            Role role_manager = new Role();
            role_manager.setId(2L);
            role_manager.setName("ROLE_MANAGER");
            roleRepository.save(role_manager);

            Role role_admin = new Role();
            role_manager.setId(3L);
            role_admin.setName("ROLE_ADMIN");
            roleRepository.save(role_admin);

            // Создаём 1 аккаунт админа

            User user = new User();
            user.setUsername("admin");
            user.setPassword(new BCryptPasswordEncoder().encode("admin123")); // Хеширование пароля
            user.setRoles(List.of(role_admin));
            userRepository.save(user);

    }
}

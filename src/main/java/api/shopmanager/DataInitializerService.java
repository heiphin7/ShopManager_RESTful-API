package api.shopmanager;

import api.shopmanager.entity.Role;
import api.shopmanager.entity.User;
import api.shopmanager.repository.RoleRepository;
import api.shopmanager.repository.UserRepository;
import api.shopmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class DataInitializerService implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeRolesAndUsers();
        initializeUser();
    }

    private void initializeRolesAndUsers() {
        // Проверка существования ролей

        Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_USER");
            return roleRepository.save(newRole);
        });

        Role roleManager = roleRepository.findByName("ROLE_MANAGER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_MANAGER");
            return roleRepository.save(newRole);
        });

        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_ADMIN");
            return roleRepository.save(newRole);
        });
    }

    private void initializeUser() {
        if (!userService.findByUsername("admin").isPresent()) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(bCryptPasswordEncoder.encode("admin"));
            user.setEmail("admin@admin.com");
            user.setCreated_at(new Date());

            // Получаем роль из репозитория и добавляем ее к пользователю
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
            user.setRoles(List.of(roleAdmin));

            // Сохраняем пользователя
            entityManager.persist(user);
            entityManager.flush(); // Принудительно сохраняем изменения в базе данных
        }
    }
}

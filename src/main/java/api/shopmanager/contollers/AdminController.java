package api.shopmanager.contollers;

import api.shopmanager.entity.CompletedOrder;
import api.shopmanager.entity.Order;
import api.shopmanager.entity.Role;
import api.shopmanager.entity.User;
import api.shopmanager.mapper.OrderMapper;
import api.shopmanager.repository.CompletedOrderRepository;
import api.shopmanager.repository.OrderRepository;
import api.shopmanager.repository.RoleRepository;
import api.shopmanager.repository.UserRepository;
import api.shopmanager.service.UserService;
import jdk.jfr.consumer.RecordedThread;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")

public class AdminController {

    // Админ - панель, где админ может управлять пользователями и завершать заказы

    private final CompletedOrderRepository completedOrderRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RoleRepository roleRepository;

    /*
     Метод для заверешния заказ
     Здесь, перейдя по пути /admin/complete/order/id, вы можете завершить заказ
     поместив его в "архив", то есть в таблицу Completed_Orders
    */

    @PutMapping("/complete/order/{id}")
    public ResponseEntity<?> completeOrder(@PathVariable Long id){

        try {
            Order order = orderRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Заказ для завершения не найден!")
            );

            CompletedOrder completedOrder = orderMapper.completedOrder(order);
            completedOrderRepository.save(completedOrder);

            orderRepository.delete(order);

            return ResponseEntity.ok("Заказ успешно помечен как выполненный и помечен в архив");
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>("Выбранный заказ не найден или он уже завершён", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>("Произошла какая - то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // Получение всех пользователей

    @GetMapping("/findAll/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> userList = userRepository.findAll();

            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            return new ResponseEntity<>("Произошла какая-то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Получение пользователя по его id
    @GetMapping("/find/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Пользователя не найден!")
            );

            return ResponseEntity.ok(user);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>("Не удалось найти пользователя!", HttpStatus.BAD_REQUEST);
        }catch (Exception e ){
            return new ResponseEntity<>("Произошла какая - то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // "Повышение" роли до manager
    @PutMapping("/set/manager/{id}")
    public ResponseEntity<?> setManager(@PathVariable Long id){
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Пользователь не найден!")
            );

            // Перед тем как устанавливать значение, мы сначала должны проверить
            // а не является ли уже данный пользователь такую роль

            List<Role> userRoles = user.getRoles();

            if(userRoles.stream().anyMatch(role -> role.getName().equals("ROLE_MANAGER"))){
                return new ResponseEntity<>("Данный пользователь уже имеет роль MANAGER", HttpStatus.BAD_REQUEST);
            }

            // Используя roleRepo находим и устанавливаем в качестве значения роль MANAGER

            user.setRoles(List.of(roleRepository.findByName("ROLE_MANAGER").get()));
            userService.save(user);

            return ResponseEntity.ok("Роль пользователя успешно изменена");
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>("Не удалось найти пользователя!", HttpStatus.BAD_REQUEST);
        }catch (Exception e ){
            return new ResponseEntity<>("Произошла какая - то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Установление нового админа
    @PutMapping("/set/admin/{id}")
    public ResponseEntity<?> setAdmin(@PathVariable Long id){
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Пользователь не найден!")
            );
            // Перед тем как устанавливать значение, мы сначала должны проверить
            // а не является ли уже данный пользователь такую роль

            List<Role> userRoles = user.getRoles();

            if(userRoles.stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))){
                return new ResponseEntity<>("Данный пользователь уже имеет роль ADMIN", HttpStatus.BAD_REQUEST);
            }

            // Используя roleRepo находим и устанавливаем в качестве значения роль MANAGER

            user.setRoles(List.of(roleRepository.findByName("ROLE_ADMIN").get()));
            userService.save(user);

            return ResponseEntity.ok("Роль пользователя успешно изменена");
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>("Не удалось найти пользователя!", HttpStatus.BAD_REQUEST);
        }catch (Exception e ){
            return new ResponseEntity<>("Произошла какая - то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Удаление пользователя по его id

    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id){
        try {
            userService.deleteUserById(id);

            return ResponseEntity.ok("Пользователь успешно удалён!");

            // Перехватываем IllegalArgumentException, так как при работе userService у нас может произойти данная ошибка
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>("Пользователь не найден!", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>("Произошла какая-то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
}

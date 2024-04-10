package api.shopmanager.config;

import api.shopmanager.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Внедряем кастомный UserDetailService для AuthenticationManager

    private final UserDetailService userDetailService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    
        httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeRequests(auth ->
                        auth.antMatchers("/registration", "/login").permitAll()
                                // Разрешаем изменять данные только manager или admin, а просто user может только просматривать
                                .antMatchers("/api/delete/**", "/api/update/**", "/api/save/**").hasAnyRole("MANAGER", "ADMIN")
                                .antMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                ).exceptionHandling(
                        // Ошибка UNAUTHORIZED при попытке обращения к защищенной области без аутнетификации
                        exep -> exep.authenticationEntryPoint(authenticationEntryPoint())
                );

        return httpSecurity.build();
    }

    // Обработчик ошибок, связанные с доступом и ролью пользователя
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, responce, accessDeniedException) ->
                responce.sendError(HttpServletResponse.SC_FORBIDDEN, "У вас недостаточно прав на проведение данной операции");
    }

    // Обработчик ошибок, связанный с аутентфикацией пользоваеля
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Для доступа к этому ресурсу необходима аутентификация");
        };
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

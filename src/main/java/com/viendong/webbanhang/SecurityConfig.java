package com.viendong.webbanhang;

import com.viendong.webbanhang.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(UserService userService, CustomAuthenticationSuccessHandler successHandler) {
        this.userService = userService;
        this.successHandler = successHandler;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService; // Trả về instance đã được inject
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
<<<<<<< HEAD
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "https://java-dhdl-playhouse.onrender.com")  // Allow both local and production origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*");
=======
                registry.addMapping("/**").allowedOrigins("http://localhost:3000");  // Allow frontend origin
>>>>>>> 30773b56cebec0a1c0a8481f7e309ffb08c20e61
            }
        };
    }


<<<<<<< HEAD

=======
>>>>>>> 30773b56cebec0a1c0a8481f7e309ffb08c20e61
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/", "/oauth/**", "/authentication/**", "/error", "/cart", "/cart/**")
                        .permitAll()
                        .requestMatchers("/authentication/recover")  // Add this line
                        .permitAll()
                        .requestMatchers("/products/add", "/dashboard/**", "/categories/add", "/brand/add")
                        .hasAnyAuthority("ADMIN")
                        .requestMatchers("/api/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/authentication/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/authentication/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
<<<<<<< HEAD
                        .failureUrl("/authentication/login?error")
=======
                        .failureUrl("/authentication/login?error") // Cập nhật URL lỗi cho đúng trang tùy chỉnh
>>>>>>> 30773b56cebec0a1c0a8481f7e309ffb08c20e61
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("vanlang")
                        .rememberMeCookieName("vanlang")
                        .tokenValiditySeconds(24 * 60 * 60)
                        .userDetailsService(userDetailsService())
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("text/html; charset=UTF-8");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("""
<<<<<<< HEAD
                                <script>
                                    alert("Bạn không có quyền truy cập vào tài nguyên này!");
                                    window.history.back();
                                </script>
                            """);
                        })

=======
                                        <script>
                                            alert("Bạn không có quyền truy cập vào tài nguyên này!");
                                            window.history.back();
                                        </script>
                                    """);
                        })
>>>>>>> 30773b56cebec0a1c0a8481f7e309ffb08c20e61
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1)
                        .expiredUrl("/authentication/login")
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName("vanlang")
                )
                .build();
    }
}

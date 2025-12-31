package com.example.homeway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@org.springframework.context.annotation.Configuration
@EnableWebSecurity
public class Configuration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/register/**","/api/v1/pay/confirm/**","/api/v1/payment/callBack","/api/v1/payment/status/*","/api/v1/payment/test/mark-paid/*").permitAll()
                        // all or testing
                        .requestMatchers("/api/v1/admin/companies/**","/api/v1/company/get","/api/v1/company/get/*","/api/v1/company/get-by-role/*",
                                "/api/v1/company/update/*","/api/v1/company/delete/*","/api/v1/vehicle/get",
                                "/api/v1/customer/get", "/api/v1/customer/update","/api/v1/customer/delete","/api/v1/notifications/admin/**","/api/v1/offer/admin/**", "/api/v1/request/get/*","/api/v1/request/get-by-customer/*",
                                "/api/v1/request/get-by-company/*","/api/v1/user/**","/api/v1/subscription/get","/api/v1/vehicle/get").hasAuthority("ADMIN")
                        // admin ^
                        .requestMatchers("/api/v1/customer/offer/accept/*","/api/v1/customer/offer/reject/*","/api/v1/customer/ai/ServiceEstimator/*","/api/v1/customer/review-assist/*","/api/v1/customer/customer/report-summary/*",
                                "/api/v1/customer/redesign-scope/*","/api/v1/customer/cost-estimation","/api/v1/customer/service-fit","/api/v1/customer/fix-vs-redesign","/api/v1/customer/ai/redesign-from-image/**","/api/v1/notifications/customer/**"
                                ,"/api/v1/offer/customer/get-my-offers","/api/v1/properties/**","/api/v1/report/read/*","/api/v1/report/request/*","/api/v1/request/inspection/*","/api/v1/request/moving/*",
                                "/api/v1/request/maintenance/*","/api/v1/request/redesign/*","/api/v1/payment/offer/*","/api/v1/review/create"
                                ,"/api/v1/review/customer/**").hasAuthority("CUSTOMER")
                        //customer ^
                        .requestMatchers("/api/v1/company/inspection/**").hasAuthority("INSPECTION_COMPANY")
                        .requestMatchers("/api/v1/company/moving/**","/api/v1/vehicle/**").hasAuthority("MOVING_COMPANY")
                        .requestMatchers("/api/v1/company/redesign/**").hasAuthority("REDESIGN_COMPANY")
                        //each company ^
                        .requestMatchers("/api/v1/subscription/**").hasAnyAuthority("INSPECTION_COMPANY","MOVING_COMPANY","MAINTENANCE_COMPANY","REDESIGN_COMPANY","WORKER")
                        .requestMatchers("/api/v1/company/ai/cost-estimation/*","/api/v1/company/worker/**",
                                "/api/v1/company/reportWriting","/api/v1/company/image-diagnosis/**","/api/v1/notifications/company/**","/api/v1/report/**","/api/v1/review/company/**","/api/v1/workers/**","/api/v1/company/maintenance/**").hasAnyAuthority("INSPECTION_COMPANY","MOVING_COMPANY","MAINTENANCE_COMPANY","REDESIGN_COMPANY","WORKER")
                        // worker and companies ^
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                )
                .httpBasic(basic -> {})
                .build();
    }

}

Osama Alahmadi
osama1123_
Online

Osama Alahmadi — Yesterday at 5:59 PM
الا
Leen Aljohani — Yesterday at 5:59 PM
It was just for testing
Osama Alahmadi — Yesterday at 5:59 PM
wait
Turki Alharbi — Yesterday at 6:00 PM
شرايكم ندخل سوا نسوي تست؟
Osama Alahmadi — Yesterday at 6:00 PM
تمام
public List<Vehicle> getMyVehicles(User user) {
        Company company = user.getCompany();
        if (company == null) {
            throw new ApiException("Company not found");
        }

        return vehicleRepository.findAllByCompany_Id(company.getId());
    }
Leen Aljohani — Yesterday at 6:01 PM
يمديكم تسوون تيست بدوني ؟ مشغولة شوية الان
Osama Alahmadi — Yesterday at 6:01 PM
هل كان اليوزر الي حطيتيه company
Osama Alahmadi — Yesterday at 6:01 PM
نسوي تست بعد العشاء عادي
Leen Aljohani — Yesterday at 6:01 PM
Osama Alahmadi — Yesterday at 6:02 PM
اتوقع لازم
بنشيك عليها
yeah you must enter a user in the system, you can't put anything it i will give the " unauthorized " message
Image
Leen Aljohani — Yesterday at 6:08 PM
Yes ofc I just wanted to check simply that the system works
Osama Alahmadi — Yesterday at 6:10 PM
yeah then the system works just fine, we wont need much testing, we will just test after adding the configuration settings
Leen Aljohani — Yesterday at 6:10 PM
Okayy
ان شاء الله كلو تمام
Leen Aljohani — Yesterday at 7:41 PM
http://homeway-application-env.eba-nr2pgrvr.eu-central-1.elasticbeanstalk.com/api/v1/
Turki Alharbi — Yesterday at 9:22 PM
public ResponseEntity<Map<String, String>> processPayment(
            SubscriptionPayment paymentRequest,
            Integer subscriptionId
    ) {

        UserSubscription subscription =
Expand
message.txt
4 KB
Turki Alharbi — Yesterday at 9:30 PM
public void updateAndConfirmPayment(
            Integer subscriptionId,
            String transactionId,
            Integer userId
    ) throws JsonProcessingException {
Expand
message.txt
3 KB
Osama Alahmadi — Yesterday at 9:41 PM
$2a$10$D9R9H4g0Y3Y4x7s4J3yP8OqZ7m3y9Yc0cJrZyYv0g7y3C5xkZQy9K
Leen Aljohani — Yesterday at 11:16 PM
الي يسوي تيست with real data يحفظها ب بوست مان بتتعدل باللينك يطلع منظرو احسن
I’m gonna check the ai
@Osama Alahmadi هو المشكلة بدت بعد ال authorization صح ؟
Osama Alahmadi — Yesterday at 11:17 PM
ايه لازم نحط ادمن
هذا قصدك؟
Leen Aljohani — Yesterday at 11:17 PM
مادري الصراحة بس بشوف ليه ما يشتغل
Osama Alahmadi — Yesterday at 11:18 PM
عشان في configration الحين
فا الadmin له دخل في ميثودز كثيرة
فا لازم يكون موجود بالسستم
Leen Aljohani — Yesterday at 11:18 PM
كيف
Turki Alharbi — Yesterday at 11:19 PM
الai  مافيه مشكله الا ال٥٠٠ وهي من الدبلويمنت
Leen Aljohani — Yesterday at 11:19 PM
لا حتى لمن جربها لوكال مازبط
Turki Alharbi — Yesterday at 11:19 PM
كل الاندبوينت؟
Leen Aljohani — Yesterday at 11:19 PM
الدبلويمنت في قروبات رفعو بدون حتى ما يخلو الكي سيكريت وزبط
Turki Alharbi — Yesterday at 11:20 PM
طيب 500 من وش يعني
Leen Aljohani — Yesterday at 11:20 PM
بشوف دحين
Osama Alahmadi — Yesterday at 11:21 PM
Turki Alharbi — Yesterday at 11:22 PM
لين تقول حتى اللوكل ماشتغل
Osama Alahmadi — Yesterday at 11:22 PM
قبل كان حتى اللوكل
بعدين طلع ما حطيت **
Turki Alharbi — Yesterday at 11:23 PM
عادي ترسل الكونفقريشن النهائي
لان ببدا اسوي تست
Osama Alahmadi — Yesterday at 11:23 PM
package com.example.homeway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
Expand
message.txt
5 KB
Leen Aljohani — Yesterday at 11:24 PM
هذا انا حطيتو؟
Osama Alahmadi — Yesterday at 11:24 PM
ايوا
ورفعتي الملف اقين
Turki Alharbi — Yesterday at 11:25 PM
الرجستر
ترا مافيه *
Osama Alahmadi — Yesterday at 11:25 PM
انت عدلته طيب ورسلته للين ولا
هي جا عندها التعديل
Turki Alharbi — Yesterday at 11:26 PM
لا مارسلته
Leen Aljohani — Yesterday at 11:26 PM
وين
Osama Alahmadi — Yesterday at 11:26 PM
@Leen Aljohani  ارسلي بالله الكود حق ال configration
Leen Aljohani — Yesterday at 11:26 PM
package com.example.homeway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
Expand
message.txt
5 KB
Osama Alahmadi — Yesterday at 11:26 PM
معدل عندها 
انت معدله عندك
بس ما سويت 
git add .
git commit -m "updated"
git push
Turki Alharbi — Yesterday at 11:27 PM
هو فيه مشاكل بالكونفقريشن؟
Osama Alahmadi — Yesterday at 11:27 PM
سوه الحين
Osama Alahmadi — Yesterday at 11:28 PM
,,
Turki Alharbi — Yesterday at 11:29 PM
او لا
Osama Alahmadi — Yesterday at 11:29 PM
لا
Turki Alharbi — Yesterday at 11:29 PM
طيب اهين الوسواس
Osama Alahmadi — Yesterday at 11:30 PM
سوي commit وpush  لأنك معدل ملف ال payment
ومعدل الكوفقريشن
git add .
git commit -m "updated"
git push
Turki Alharbi — Yesterday at 11:30 PM
الكونفقريشن ماعدلت الا النجمه ترا
بسوي الحين دقيقه
اسويه force?
Osama Alahmadi — Yesterday at 11:31 PM
لا سو عادي ما يجي
Turki Alharbi — Yesterday at 11:31 PM
لان ماضبط
نفس الكلام اللي قبل
Leen Aljohani — Yesterday at 11:53 PM
عدلتو
رفعت الكي زي ماهو وسويت كي جديد
صار يزبط
جربو من عندكم
Turki Alharbi — Yesterday at 11:53 PM
يعني مافيه 500
Leen Aljohani — Yesterday at 11:53 PM
Nope
Osama Alahmadi — Yesterday at 11:53 PM
ممتاز
Leen Aljohani — Yesterday at 11:54 PM
جربت بس اول ايندبوينت بال ai شوفو الباقي
Turki Alharbi — Yesterday at 11:54 PM
اوكي
لين سوي كلير لداتا بيز عشان ماتصير لخبطه
Leen Aljohani — Yesterday at 11:56 PM
okay how?
Turki Alharbi — Yesterday at 11:56 PM
كويري
Osama Alahmadi — Yesterday at 11:57 PM
in the console i think there is a command for clearing tables
SQL command
Leen Aljohani — 12:01 AM
okay done
البرزنتيشن يكون على فيقما؟
Osama Alahmadi — 12:06 AM
power point and then figma
Leen Aljohani — 12:07 AM
did you do the power point?
Osama Alahmadi — 12:07 AM
im currently making it
Leen Aljohani — 12:08 AM
okay share it نحط كلامنا او شي
Osama Alahmadi — 12:09 AM
بياخذ وقت شوي، بحاول اسوي واحد يكفي يوصف الي تشرحونه بشكل كويس وانتو فصلو
لأن بيكون قصير الباور بوينت
Leen Aljohani — 12:10 AM
okay
do you need any help?
Osama Alahmadi — 12:14 AM
no, i will get it done
Turki Alharbi — 3:01 AM
هذا الكونقريشن عدلت عليه وان لقيتو اخطاء عدلو برضو :
package com.example.homeway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
Expand
message.txt
5 KB
﻿
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
message.txt
5 KB

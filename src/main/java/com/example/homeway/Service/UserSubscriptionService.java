package com.example.homeway.Service;

import com.example.homeway.API.ApiException;
import com.example.homeway.DTO.Out.HistorySubscription;
import com.example.homeway.DTO.Out.UserSubscriptionDTOOut;
import com.example.homeway.EmailService.EmailService;
import com.example.homeway.Model.SubscriptionPayment;
import com.example.homeway.Model.User;
import com.example.homeway.Model.UserSubscription;
import com.example.homeway.Repository.SubscriptionPaymentRepository;
import com.example.homeway.Repository.UserRepository;
import com.example.homeway.Repository.UserSubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSubscriptionService {

    private final UserSubscriptionRepository UserSubscriptionRepository;
    private final SubscriptionPaymentRepository subscriptionPaymentRepository;
    private final UserRepository userRepository;
    private final SubscriptionPaymentService subscriptionPaymentService;
    private final EmailService emailService;

    private static final Double AI_PRICE =  100.00;

    public List<UserSubscriptionDTOOut> getAllSubscriptions() {
        return UserSubscriptionRepository.findAll()
                .stream()
                .map(this::mapToDTOOUT)
                .collect(Collectors.toList());
    }

    public ResponseEntity<Map<String, String>> addSubscription(
            Integer userId,
            String planType,
            SubscriptionPayment payment
    ) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found with id " + userId);
        }

        UserSubscription existingSub = UserSubscriptionRepository.findUserSubscriptionById(userId);

        if (existingSub != null &&
                "ACTIVE".equalsIgnoreCase(existingSub.getStatus())) {
            throw new ApiException("User already has an active subscription");
        }

        UserSubscription subscription =
                (existingSub != null) ? existingSub : new UserSubscription();

        subscription.setUser(user);
        subscription.setPlanType(planType);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(1));

        if ("FREE".equalsIgnoreCase(planType)) {

            subscription.setMonthlyPrice(0.0);
            subscription.setStatus("FREE_PLAN");
            subscription.setNextBillingDate(null);

            user.setIsSubscribed(false);
            userRepository.save(user);
            UserSubscriptionRepository.save(subscription);

            Map<String, String> result = new HashMap<>();
            result.put("message", "FREE plan activated");
            return ResponseEntity.ok(result);
        }

        if ("AI".equalsIgnoreCase(planType)) {

            subscription.setMonthlyPrice(AI_PRICE);
            subscription.setStatus("PENDING");
            subscription.setNextBillingDate(null);

            user.setIsSubscribed(false);

        } else {
            throw new ApiException("Invalid plan type");
        }

        if (payment == null ||
                payment.getAmount() == null ||
                payment.getAmount() < subscription.getMonthlyPrice()) {

            throw new ApiException("Insufficient amount");
        }

        UserSubscriptionRepository.save(subscription);
        userRepository.save(user);

        return subscriptionPaymentService.processPayment(payment, subscription.getId());
    }


    @Transactional
    public ResponseEntity<Map<String, String>> renewSubscription(Integer userId, SubscriptionPayment newPaymentData) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found with id " + userId);
        }

        UserSubscription subscription = UserSubscriptionRepository.findUserSubscriptionById(userId);
        if (subscription == null) {
            throw new ApiException("Subscription not found");
        }

        if (!subscription.getUser().getId().equals(userId)) {
            throw new ApiException("Subscription does not belong to this user");
        }

        if (!"EXPIRED".equalsIgnoreCase(subscription.getStatus())) {
            throw new ApiException("Subscription is still active and not eligible for renewal yet");
        }

        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(1));
        subscription.setStatus("PENDING");
        subscription.setMonthlyPrice(AI_PRICE);

        user.setIsSubscribed(true);

        SubscriptionPayment existingPayment = subscriptionPaymentRepository.findPaymentByUserSubscription(subscription);
        if (existingPayment != null) {
            existingPayment.setName(newPaymentData.getName());
            existingPayment.setNumber(newPaymentData.getNumber());
            existingPayment.setCvc(newPaymentData.getCvc());
            existingPayment.setMonth(newPaymentData.getMonth());
            existingPayment.setYear(newPaymentData.getYear());
            existingPayment.setAmount(AI_PRICE);
            existingPayment.setPaymentDate(LocalDateTime.now());
            existingPayment.setStatus("PENDING");

            subscriptionPaymentRepository.save(existingPayment);
        }

        userRepository.save(user);
        UserSubscriptionRepository.save(subscription);

        return subscriptionPaymentService.processPayment(existingPayment, subscription.getId());
    }


    @Transactional
    public void activateSubscription(Integer userId, Integer subscriptionId) {
        User user = userRepository.findUserById(userId);
        UserSubscription subscription = UserSubscriptionRepository.findUserSubscriptionById(subscriptionId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        if (subscription == null) {
            throw new ApiException("Subscription not found with id " + subscriptionId);
        }

        if (!subscription.getUser().equals(user)) {
            throw new ApiException("Not Authorized");
        }

        if (!"CANCELLED".equalsIgnoreCase(subscription.getStatus())) {
            throw new ApiException("Subscription is not cancelled. Only cancelled subscriptions can be activated.");
        }

        if (subscription.getEndDate() != null && subscription.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ApiException("Subscription has expired. Please renew instead of activating.");
        }

        if ("FREE".equalsIgnoreCase(subscription.getPlanType())) {
            subscription.setStatus("FREE_PLAN");
            subscription.getUser().setIsSubscribed(false);
        } else {
            subscription.setStatus("ACTIVE");
            subscription.getUser().setIsSubscribed(true);
        }

        UserSubscriptionRepository.save(subscription);
        userRepository.save(subscription.getUser());
    }

    public void updateSubscriptionStatus(Integer userId, Integer subscriptionId, String status) {
        User user = userRepository.findUserById(userId);
        UserSubscription subscription = UserSubscriptionRepository.findUserSubscriptionById(subscriptionId);

        if (subscription == null) {
            throw new ApiException("Subscription not found with id " + subscriptionId);
        }
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (!subscription.getUser().equals(user)) {
            throw new ApiException("Not Authorized");
        }

        subscription.setStatus(status.toUpperCase());
        UserSubscriptionRepository.save(subscription);
    }

    public void cancelSubscription(Integer userId, Integer subscriptionId) {
        User user = userRepository.findUserById(userId);
        UserSubscription subscription = UserSubscriptionRepository.findUserSubscriptionById(subscriptionId);

        if (subscription == null) {
            throw new ApiException("Subscription not found with id " + subscriptionId);
        }
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (!subscription.getUser().equals(user)) {
            throw new ApiException("Not Authorized");
        }

        subscription.setStatus("CANCELLED");
        subscription.getUser().setIsSubscribed(false);

        UserSubscriptionRepository.save(subscription);
        userRepository.save(subscription.getUser());
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkStatusSubscriptionExpired() {

        System.out.println("Checking for expired subscriptions...");

        List<UserSubscription> subscriptions = UserSubscriptionRepository.findAll();
        LocalDate today = LocalDate.now();

        for (UserSubscription subscription : subscriptions) {

            if (subscription.getNextBillingDate() == null ||
                    !"ACTIVE".equalsIgnoreCase(subscription.getStatus())) {
                continue;
            }

            if ("FREE".equalsIgnoreCase(subscription.getPlanType())) {
                continue;
            }

            LocalDate nextBillingDate = subscription.getNextBillingDate().toLocalDate();


            long daysUntilBilling =
                    java.time.temporal.ChronoUnit.DAYS.between(today, nextBillingDate);

            if (daysUntilBilling <= 3 && daysUntilBilling >= 0 &&
                    !Boolean.TRUE.equals(subscription.getRenewalEmailSent())) {

                emailService.sendEmail(
                        subscription.getUser().getEmail(),
                        "Subscription Renewal Reminder",
                        "Your AI subscription will renew in " + daysUntilBilling + " days."
                );

                subscription.setRenewalEmailSent(true);
                UserSubscriptionRepository.save(subscription);
            }


            if (!nextBillingDate.isAfter(today)) {

                subscription.setStatus("EXPIRED");
                subscription.getUser().setIsSubscribed(false);

                if (!Boolean.TRUE.equals(subscription.getExpiryEmailSent())) {
                    emailService.sendEmail(
                            subscription.getUser().getEmail(),
                            "Subscription Expired",
                            "Your AI subscription has expired. Please renew to continue using the service."
                    );
                    subscription.setExpiryEmailSent(true);
                }

                UserSubscriptionRepository.save(subscription);
                userRepository.save(subscription.getUser());
            }
        }
    }

    public List<HistorySubscription> historyOfSubscription(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("user not found");
        }


        List<UserSubscription> subscriptions = UserSubscriptionRepository.findUserSubscriptionsByUser(user);

        List<HistorySubscription> history = new ArrayList<>();
        for (UserSubscription s : subscriptions) {
            SubscriptionPayment payment = subscriptionPaymentRepository.findPaymentByUserSubscription(s);
            if (payment == null) {
                throw new ApiException("payment not found");
            }

            HistorySubscription h = new HistorySubscription();
            h.setPrice(s.getMonthlyPrice());
            h.setPaidAt(s.getStartDate());
            h.setIsPaid(payment.getStatus());
            history.add(h);
        }
        return history;
    }

    public Map<String, Object> getSubscriptionDashboard(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        Map<String, Object> dashboard = new HashMap<>();

        UserSubscription current = UserSubscriptionRepository.findUserSubscriptionById(userId);
        if (current != null) {
            Map<String, Object> currentPlan = new HashMap<>();
            currentPlan.put("planType", current.getPlanType());
            currentPlan.put("status", current.getStatus());
            currentPlan.put("monthlyPrice", current.getMonthlyPrice());
            currentPlan.put("startDate", current.getStartDate());
            currentPlan.put("endDate", current.getEndDate());
            currentPlan.put("nextBillingDate", current.getNextBillingDate());
            dashboard.put("currentSubscription", currentPlan);
        } else {
            dashboard.put("currentSubscription", null);
        }

        List<HistorySubscription> history = historyOfSubscription(userId);
        dashboard.put("subscriptionHistory", history);

        List<UserSubscription> allSubscriptions = UserSubscriptionRepository.findUserSubscriptionsByUser(user);

        Double totalSpent = allSubscriptions.stream()
                .filter(sub -> !"FREE".equalsIgnoreCase(sub.getPlanType()))
                .mapToDouble(sub -> sub.getMonthlyPrice() == null ? 0.0 : sub.getMonthlyPrice())
                .sum();

        Map<String, Object> paymentStats = new HashMap<>();
        paymentStats.put("totalAmountSpent", totalSpent);
        paymentStats.put("totalPayments", allSubscriptions.size());
        paymentStats.put("activePayments", allSubscriptions.stream()
                .filter(sub -> "ACTIVE".equalsIgnoreCase(sub.getStatus()))
                .count());

        dashboard.put("paymentStatistics", paymentStats);

        List<String> alerts = new ArrayList<>();
        if (current != null) {
            if ("EXPIRED".equalsIgnoreCase(current.getStatus())) {
                alerts.add("انتهت صلاحية اشتراكك. يرجى التجديد للاستمرار في استخدام الخدمات المتقدمة");
            } else if (current.getNextBillingDate() != null) {
                LocalDate nextBilling = current.getNextBillingDate().toLocalDate();
                LocalDate today = LocalDate.now();
                long daysUntilBilling = java.time.temporal.ChronoUnit.DAYS.between(today, nextBilling);

                if (daysUntilBilling <= 3 && daysUntilBilling >= 0) {
                    alerts.add("سيتم تجديد اشتراكك خلال " + daysUntilBilling + " أيام");
                }
            }
        }
        dashboard.put("alerts", alerts);

        return dashboard;
    }

    private UserSubscriptionDTOOut mapToDTOOUT(UserSubscription subscription) {
        return new UserSubscriptionDTOOut(
                subscription.getPlanType(),
                subscription.getStatus(),
                subscription.getMonthlyPrice(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getNextBillingDate()
        );
    }
}

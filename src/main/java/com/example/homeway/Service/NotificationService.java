package com.example.homeway.Service;

import com.example.homeway.API.ApiException;
import com.example.homeway.DTO.In.NotificationDTOIn;
import com.example.homeway.EmailService.EmailService;
import com.example.homeway.Model.Company;
import com.example.homeway.Model.Customer;
import com.example.homeway.Model.Notification;
import com.example.homeway.Model.User;
import com.example.homeway.Repository.CompanyRepository;
import com.example.homeway.Repository.CustomerRepository;
import com.example.homeway.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    // =====================================
    // ADMIN
    // Get ALL notifications
    // =====================================
    public List<Notification> getAllNotifications() {

        List<Notification> notifications = notificationRepository.getAllNotificationsOrdered();

        if (notifications.isEmpty()) {
            throw new ApiException("No notifications found");
        }

        return notifications;
    }

    // =====================================
    // ADMIN
    // Get ALL customer notifications
    // =====================================
    public List<Notification> getAllCustomerNotifications() {

        List<Notification> notifications =
                notificationRepository.getAllCustomerNotifications();

        if (notifications.isEmpty()) {
            throw new ApiException("No customer notifications found");
        }

        return notifications;
    }



    // =====================================
    // COMPANY
    // Get my notifications
    // =====================================
    public List<Notification> getMyNotificationsCompany(User user) {

        if (user == null) {
            throw new ApiException("Unauthorized");
        }

        Company company = companyRepository.findCompanyById(user.getId());
        if (company == null) {
            throw new ApiException("Company profile not found");
        }

        List<Notification> notifications =
                notificationRepository.getCompanyNotifications(company.getId());

        if (notifications.isEmpty()) {
            throw new ApiException("No notifications found for this company");
        }

        return notifications;
    }



    // =====================================
    // COMPANY
    // Send notification to customer
    // =====================================
    public void sendNotificationToCustomer(User user, Integer customerId, NotificationDTOIn dto) {

        if (user == null) {
            throw new ApiException("Unauthorized");
        }

        Company company = companyRepository.findCompanyById(user.getId());
        if (company == null) {
            throw new ApiException("Company profile not found");
        }

        Customer customer = customerRepository.findCustomerById(customerId);
        if (customer == null) {
            throw new ApiException("Customer not found");
        }

        Notification notification = new Notification();
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setCreated_at(LocalDateTime.now());

        notification.setCompany(company);
        notification.setCustomer(customer);

        notificationRepository.save(notification);
        emailService.sendEmail("osama.alahmadi90@gmail.com", "Notification received: "+ notification.getTitle(),  notification.getMessage());
    }

    // =====================================
    // COMPANY
    // Delete my notification
    // =====================================
    public void deleteCompanyNotification(User user, Integer notificationId) {

        if (user == null) {
            throw new ApiException("Unauthorized");
        }

        Company company = companyRepository.findCompanyById(user.getId());
        if (company == null) {
            throw new ApiException("Company profile not found");
        }

        Notification notification =
                notificationRepository.findNotificationById(notificationId);

        if (notification == null) {
            throw new ApiException("Notification not found");
        }

        // wnership check
        if (notification.getCompany() == null || !notification.getCompany().getId().equals(company.getId())) {
            throw new ApiException("You are not allowed to delete this notification");
        }

        notificationRepository.delete(notification);
    }



    // =====================================
    // CUSTOMER
    // Send notification to company
    // =====================================
    public void sendNotificationToCompany(User user, Integer companyId, NotificationDTOIn dto) {

        if (user == null) {
            throw new ApiException("Unauthorized");
        }

        Customer customer = customerRepository.findCustomerById(user.getId());
        if (customer == null) {
            throw new ApiException("Customer profile not found");
        }

        Company company = companyRepository.findCompanyById(companyId);
        if (company == null) {
            throw new ApiException("Company not found");
        }

        Notification notification = new Notification();
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setCreated_at(LocalDateTime.now());

        notification.setCustomer(customer);
        notification.setCompany(company);

        notificationRepository.save(notification);
        emailService.sendEmail("osama.alahmadi90@gmail.com", "Notification received: "+ notification.getTitle(),  notification.getMessage());
    }


    public void deleteCustomerNotification(User user, Integer notificationId) {

        if (user == null) {
            throw new ApiException("Unauthorized");
        }

        Customer customer = customerRepository.findCustomerById(user.getId());
        if (customer == null) {
            throw new ApiException("Customer profile not found");
        }

        Notification notification =
                notificationRepository.findNotificationById(notificationId);

        if (notification == null) {
            throw new ApiException("Notification not found");
        }

        if (notification.getCustomer() == null || !notification.getCustomer().getId().equals(customer.getId())) {
            throw new ApiException("You are not allowed to delete this notification");
        }

        notificationRepository.delete(notification);
    }
}

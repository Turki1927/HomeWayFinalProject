package com.example.homeway.Service;

import com.example.homeway.AIService.AIService;
import com.example.homeway.DTO.Ai.DescriptionDTOIn;
import com.example.homeway.DTO.Ai.RepairChecklistDTOIn;
import com.example.homeway.DTO.In.CompanyDTOIn;
import com.example.homeway.DTO.In.CompanyStatusDTOIn;
import com.example.homeway.DTO.Out.CompanyDTOOut;
import com.example.homeway.EmailService.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.homeway.API.ApiException;
import com.example.homeway.Model.*;
import com.example.homeway.Repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final RequestRepository requestRepository;
    private final WorkerRepository workerRepository;
    private final VehicleRepository vehicleRepository;
    private final OfferRepository offerRepository;
    private final NotificationRepository notificationRepository;
    private final AIService aiService;
    private final EmailService emailService;

    public List<CompanyDTOOut> getAllCompanies() {
        List<CompanyDTOOut> outs = new ArrayList<>();

        for (Company c : companyRepository.findAll()) {
            outs.add(convertToDTO(c));
        }
        return outs;
    }

    public CompanyDTOOut getCompanyById(Integer companyId) {
        Company company = companyRepository.findCompanyById(companyId);
        if (company == null) throw new ApiException("company not found with id: " + companyId);

        return convertToDTO(company);
    }

    public List<CompanyDTOOut> getCompaniesByRole(String role) {
        List<CompanyDTOOut> outs = new ArrayList<>();

        for (Company c : companyRepository.findAllByUser_Role(role)) {
            outs.add(convertToDTO(c));
        }

        return outs;
    }

    public void updateCompanyStatus(Integer companyId, CompanyStatusDTOIn dto) {
        Company company = companyRepository.findCompanyById(companyId);
        if (company == null) throw new ApiException("company not found with id: " + companyId);

        company.setStatus(dto.getStatus());
        companyRepository.save(company);
    }

    public void deleteCompany(Integer companyId) {
        Company company = companyRepository.findCompanyById(companyId);
        if (company == null) throw new ApiException("company not found with id: " + companyId);

        companyRepository.delete(company);
    }

    public void approveInspectionRequest(User user, Integer requestId, Double price) {

        if (user == null) throw new ApiException("unauthorized");
        Company company = user.getCompany();
        if (company == null) {
            throw new ApiException("company profile not found");
        }

        if (!"INSPECTION_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only INSPECTION_COMPANY can approve inspection requests");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) {
            throw new ApiException("request not found with id: " + requestId);
        }

        if (!"inspection".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not INSPECTION");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only pending requests can be approved");
        }

        if (request.getOffer() != null) {
            throw new ApiException("offer already exists for this request");
        }

        //create offer
        Offer offer = new Offer();
        offer.setPrice(price);
        offer.setStatus("pending");
        offer.setCreatedAt(LocalDateTime.now());
        offer.setRequest(request);
        offerRepository.save(offer);

        request.setStatus("approved");
        requestRepository.save(request);

        //notification
        createCustomerNotification(request.getCustomer(),"approved request","Your request was approved. Please accept/reject the offer.");
        emailService.sendEmail("osama.alahmadi90@gmail.com", "Request approved", ", your request with id: "+ requestId+ "has been approved, offer price: "+ offer.getPrice() + "Sar");
    }


    @Transactional
    public void startInspectionRequest(User user, Integer requestId) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }
        Company company = user.getCompany();
        if (company == null){
            throw new ApiException("company profile not found");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        if (!"INSPECTION_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only INSPECTION_COMPANY can start inspection requests");
        }
        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("request not found with id: " + requestId);

        if (!"inspection".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not INSPECTION");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"approved".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only approved requests can be started");
        }

        Offer offer = request.getOffer();
        if (offer == null) {
            throw new ApiException("offer does not exist for this request");
        }

        if (!"ACCEPTED".equalsIgnoreCase(request.getOffer().getStatus()))
            throw new ApiException("Offer is not accepted");

        if (!Boolean.TRUE.equals(request.getIsPaid()))
            throw new ApiException("Request is not paid yet");


        Worker worker = workerRepository.findAvailableInspectorWorker(company.getId());
        if (worker == null){
            throw new ApiException("no available inspector worker found");
        }

        worker.setIsAvailable(false);
        workerRepository.save(worker);

        request.setWorker(worker);

        request.setStatus("in_progress");
        request.setStartDate(LocalDate.now());

        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "Request started", "Your request is now in progress.");
    }

    @Transactional
    public void completeInspectionRequest(User user, Integer requestId) {

        if (user == null) throw new ApiException("unauthorized");

        Company company = user.getCompany();
        if (company == null) throw new ApiException("company profile not found");

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        if (!"INSPECTION_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only INSPECTION_COMPANY can complete inspection requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("request not found with id: " + requestId);

        if (!"inspection".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not INSPECTION");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"in_progress".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only in_progress requests can be completed");
        }

        if (request.getWorker() != null) {
            Worker w = request.getWorker();
            w.setIsAvailable(true);
            workerRepository.save(w);
            request.setWorker(null);
        }

        request.setStatus("completed");
        request.setEndDate(LocalDate.now());

        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "Request completed", "Your request has been completed. Report will be received soon.");
    }

    @Transactional
    public void rejectInspectionRequest(User user, Integer requestId) {

        if (user == null) throw new ApiException("unauthorized");
        Company company = user.getCompany();
        if (company == null) throw new ApiException("company profile not found");

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        if (!"INSPECTION_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only INSPECTION_COMPANY can reject inspection requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("request not found with id: " + requestId);

        if (!"inspection".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not INSPECTION");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only pending requests can be rejected");
        }

        if (request.getOffer() != null) {
            offerRepository.delete(request.getOffer());
            request.setOffer(null);
        }

        request.setStatus("rejected");
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "Request rejected", "Your request has been rejected.");
    }

    @Transactional
    public void approveMovingRequest(User user, Integer requestId, Double price) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        Company company = user.getCompany();
        if (company == null) {
            throw new ApiException("company profile not found");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        if (!"MOVING_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only MOVING_COMPANY can approve moving requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) {
            throw new ApiException("request not found with id: " + requestId);
        }

        if (!"moving".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not moving");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only pending requests can be approved");
        }

        if (request.getOffer() != null) {
            throw new ApiException("offer already exists for this request");
        }

        Offer offer = new Offer();
        offer.setPrice(price);
        offer.setStatus("pending");
        offer.setCreatedAt(LocalDateTime.now());
        offer.setRequest(request);
        offerRepository.save(offer);

        request.setStatus("approved");
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "approved request", "Your moving request was approved. Please accept/reject the offer.");
        emailService.sendEmail("osama.alahmadi90@gmail.com", "Request approved", ", your request with id: "+ requestId+ "has been approved, offer price: "+ offer.getPrice() + "Sar");
    }


    @Transactional
    public void startMovingRequest(User user, Integer requestId) {

        if (user == null){
            throw new ApiException("unauthorized");
        }

        Company company = user.getCompany();
        if (company == null) {
            throw new ApiException("company profile not found");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        if (!"MOVING_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only MOVING_COMPANY can start moving requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) {
            throw new ApiException("request not found with id: " + requestId);
        }

        if (!"moving".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not MOVING");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"approved".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only approved requests can be started");
        }

        Offer offer = request.getOffer();
        if (offer == null) {
            throw new ApiException("offer does not exist for this request");
        }

        if (!"ACCEPTED".equalsIgnoreCase(offer.getStatus())) {
            throw new ApiException("Offer is not accepted yet");
        }

        if (!Boolean.TRUE.equals(request.getIsPaid())) {
            throw new ApiException("Request is not paid yet");
        }




        //CHECK IF HE IS ACTIVE
        //assign worker and vehicle
        Worker worker = workerRepository.findAvailableMovingWorker(company.getId());
        if (worker == null) {
            throw new ApiException("no available moving worker found");
        }

        Vehicle vehicle = vehicleRepository.findAvailableMovingVehicle(company.getId());
        if (vehicle == null) {
            throw new ApiException("no available vehicle found");
        }

        worker.setIsAvailable(false);
        workerRepository.save(worker);

        vehicle.setAvailable(false);
        vehicleRepository.save(vehicle);

        request.setWorker(worker);
        request.setVehicle(vehicle);

        request.setStatus("in_progress");
        request.setStartDate(LocalDate.now());
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "request started", "Your moving request is now in progress.");
    }

    @Transactional
    public void completeMovingRequest(User user, Integer requestId) {

        if (user == null) throw new ApiException("unauthorized");

        Company company = user.getCompany();
        if (company == null) throw new ApiException("company profile not found");

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        if (!"MOVING_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only MOVING_COMPANY can complete moving requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("request not found with id: " + requestId);

        if (!"moving".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not MOVING");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"in_progress".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only in_progress requests can be completed");
        }

        //unassign worker
        if (request.getWorker() != null) {
            Worker w = request.getWorker();
            w.setIsAvailable(true);
            workerRepository.save(w);
            request.setWorker(null);
        }

        // unassign vehicle
        if (request.getVehicle() != null) {
            Vehicle v = request.getVehicle();
            v.setAvailable(true);
            vehicleRepository.save(v);
            request.setVehicle(null);
        }

        request.setStatus("completed");
        request.setEndDate(LocalDate.now());
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "request completed", "Your moving request has been completed.");
    }

    @Transactional
    public void rejectMovingRequest(User user, Integer requestId) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        Company company = user.getCompany();
        if (company == null) {
            throw new ApiException("company profile not found");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        if (!"MOVING_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only MOVING_COMPANY can reject moving requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null){
            throw new ApiException("request not found with id: " + requestId);
        }

        if (!"moving".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not MOVING");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only pending requests can be rejected");
        }

        if (request.getOffer() != null) {
            offerRepository.delete(request.getOffer());
            request.setOffer(null);
        }

        request.setStatus("rejected");
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(),"request rejected","Your moving request has been rejected.");
        emailService.sendEmail("osama.alahmadi90@gmail.com", "Request approved", ", your request with id: "+ requestId+ "has been rejected");
    }

    public void approveRedesignRequest(User user, Integer requestId, Double price) {

        if (user == null) throw new ApiException("unauthorized");
        Company company = user.getCompany();
        if (company == null) throw new ApiException("company profile not found");

        if (!"REDESIGN_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only REDESIGN_COMPANY can approve redesign requests");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("request not found with id: " + requestId);

        if (!"redesign".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not REDESIGN");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only pending requests can be approved");
        }

        if (request.getOffer() != null) {
            throw new ApiException("offer already exists for this request");
        }

        Offer offer = new Offer();
        offer.setPrice(price);
        offer.setStatus("pending");
        offer.setCreatedAt(LocalDateTime.now());
        offer.setRequest(request);
        offerRepository.save(offer);

        request.setStatus("approved");
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "Approved request", "Your redesign request was approved. Please accept/reject the offer.");
        emailService.sendEmail("osama.alahmadi90@gmail.com", "Request approved", ", your request with id: "+ requestId+ "has been approved, offer price: "+ offer.getPrice() + "Sar");
    }

    @Transactional
    public void startRedesignRequest(User user, Integer requestId) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }
        Company company = user.getCompany();
        if (company == null) {
            throw new ApiException("company profile not found");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        if (!"REDESIGN_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only REDESIGN_COMPANY can start redesign requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null){
            throw new ApiException("request not found with id: " + requestId);
        }

        if (!"redesign".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not REDESIGN");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"approved".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only approved requests can be started");
        }

        Offer offer = request.getOffer();
        if (offer == null) {
            throw new ApiException("offer does not exist for this request");
        }
        if (!"ACCEPTED".equalsIgnoreCase(offer.getStatus())) {
            throw new ApiException("Offer is not accepted yet");
        }

        if (!Boolean.TRUE.equals(request.getIsPaid())) {
            throw new ApiException("Request is not paid yet");
        }
        if (request.getWorker() != null) {
            throw new ApiException("request already has an assigned worker");
        }

        Worker worker = workerRepository.findAvailableRedesignWorker(company.getId());
        if (worker == null) throw new ApiException("no available redesign worker found");

        worker.setIsAvailable(false);
        workerRepository.save(worker);

        request.setWorker(worker);
        request.setStatus("in_progress");
        request.setStartDate(LocalDate.now());

        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "Request started", "Your redesign request is now in progress.");
    }

    @Transactional
    public void completeRedesignRequest(User user, Integer requestId) {

        if (user == null) throw new ApiException("unauthorized");
        Company company = user.getCompany();
        if (company == null) throw new ApiException("company profile not found");

        if (!"REDESIGN_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only REDESIGN_COMPANY can complete redesign requests");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("request not found with id: " + requestId);

        if (!"redesign".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not REDESIGN");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"in_progress".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only in_progress requests can be completed");
        }

        Worker worker = request.getWorker();
        if (worker != null) {
            worker.setIsAvailable(true);
            workerRepository.save(worker);
            request.setWorker(null);
        }

        request.setStatus("completed");
        request.setEndDate(LocalDate.now());
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "Request completed", "Your redesign request has been completed.");
    }

    @Transactional
    public void rejectRedesignRequest(User user, Integer requestId) {

        if (user == null) throw new ApiException("unauthorized");
        Company company = user.getCompany();
        if (company == null) throw new ApiException("company profile not found");

        if (!"REDESIGN_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only REDESIGN_COMPANY can reject redesign requests");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("request not found with id: " + requestId);

        if (!"redesign".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not REDESIGN");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only pending requests can be rejected");
        }

        if (request.getOffer() != null) {
            offerRepository.delete(request.getOffer());
            request.setOffer(null);
        }

        request.setStatus("rejected");
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "Request rejected", "Your redesign request has been rejected.");
        emailService.sendEmail("osama.alahmadi90@gmail.com", "Request approved", ", your request with id: "+ requestId+ "has been rejected");
    }

    public void approveMaintenanceRequest(User user, Integer requestId, Double price) {

        if (user == null) throw new ApiException("unauthorized");
        Company company = user.getCompany();
        if (company == null) throw new ApiException("company profile not found");

        if (!"MAINTENANCE_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only MAINTENANCE_COMPANY can approve maintenance requests");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("request not found with id: " + requestId);

        if (!"maintenance".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not MAINTENANCE");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only pending requests can be approved");
        }

        if (request.getOffer() != null) throw new ApiException("offer already exists for this request");

        Offer offer = new Offer();
        offer.setPrice(price);
        offer.setStatus("pending");
        offer.setCreatedAt(LocalDateTime.now());
        offer.setRequest(request);
        offerRepository.save(offer);

        request.setStatus("approved");
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "approved request", "Your maintenance request was approved. Please accept/reject the offer.");
        emailService.sendEmail("osama.alahmadi90@gmail.com", "Request approved", ", your request with id: "+ requestId+ "has been approved, offer price: "+ offer.getPrice() + "Sar");
    }

    @Transactional
    public void startMaintenanceRequest(User user, Integer requestId) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }
        Company company = user.getCompany();
        if (company == null){
            throw new ApiException("company profile not found");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        if (!"MAINTENANCE_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only MAINTENANCE_COMPANY can start maintenance requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) {
            throw new ApiException("request not found with id: " + requestId);
        }

        if (!"maintenance".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not MAINTENANCE");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"approved".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only approved requests can be started");
        }

        Offer offer = request.getOffer();
        if (offer == null) {
            throw new ApiException("offer does not exist for this request");
        }
        if (!"ACCEPTED".equalsIgnoreCase(offer.getStatus())) {
            throw new ApiException("Offer is not accepted yet");
        }

        if (!Boolean.TRUE.equals(request.getIsPaid())) {
            throw new ApiException("Request is not paid yet");
        }
        Worker worker = workerRepository.findAvailableMaintenanceWorker(company.getId());
        if (worker == null) throw new ApiException("no available maintenance worker found");

        worker.setIsAvailable(false);
        workerRepository.save(worker);

        request.setWorker(worker);
        request.setStatus("in_progress");
        request.setStartDate(LocalDate.now());
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(),"request started", "Your maintenance request is now in progress.");
    }

    @Transactional
    public void completeMaintenanceRequest(User user, Integer requestId) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }
        Company company = user.getCompany();
        if (company == null){
            throw new ApiException("company profile not found");
        }

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        if (!"MAINTENANCE_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only MAINTENANCE_COMPANY can complete maintenance requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null){
            throw new ApiException("request not found with id: " + requestId);
        }

        if (!"maintenance".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not MAINTENANCE");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"in_progress".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only in_progress requests can be completed");
        }

        if (request.getWorker() != null) {
            Worker w = request.getWorker();
            w.setIsAvailable(true);
            workerRepository.save(w);
            request.setWorker(null);
        }

        request.setStatus("completed");
        request.setEndDate(LocalDate.now());
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "request completed", "Your maintenance request has been completed. Report will be received soon.");
    }

    @Transactional
    public void rejectMaintenanceRequest(User user, Integer requestId) {

        if (user == null) throw new ApiException("unauthorized");
        Company company = user.getCompany();
        if (company == null) throw new ApiException("company profile not found");

        if(!"approved".equalsIgnoreCase(company.getStatus())){
            throw new ApiException("only approved companies can manage requests");
        }

        if (!"MAINTENANCE_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only MAINTENANCE_COMPANY can reject maintenance requests");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("request not found with id: " + requestId);

        if (!"maintenance".equalsIgnoreCase(request.getType())) {
            throw new ApiException("request type is not MAINTENANCE");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("only pending requests can be rejected");
        }

        if (request.getOffer() != null) {
            offerRepository.delete(request.getOffer());
            request.setOffer(null);
        }

        request.setStatus("rejected");
        requestRepository.save(request);

        createCustomerNotification(request.getCustomer(), "request rejected", "Your maintenance request has been rejected.");
        emailService.sendEmail("osama.alahmadi90@gmail.com", "Request approved", ", your request with id: "+ requestId+ "has been rejected");
    }

    private void createCustomerNotification(Customer customer, String title, String message) {
        if (customer == null) return;

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCreated_at(LocalDateTime.now());
        notification.setCustomer(customer);

        notificationRepository.save(notification);

    }

    //ai
    public String generateRepairChecklist(User user, RepairChecklistDTOIn dto) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        if (user.getWorker() == null) {
            throw new ApiException("worker profile not found");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        if (!user.getWorker().getIsActive()) {
            throw new ApiException("worker is not active");
        }

        return aiService.workerRepairChecklist(dto.getIssueDescription());
    }

    public String getWorkerSafetyRequirements(User user, DescriptionDTOIn dto) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        if (user.getWorker() == null) {
            throw new ApiException("worker profile not found");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        if (!user.getWorker().getIsActive()) {
            throw new ApiException("worker is not active");
        }

        return aiService.workerSafetyRequirements(dto.getDescription());

    }

    public String companyServiceEstimationCost(User user, Integer requestId) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) {
            throw new ApiException("request not found with id: " + requestId);
        }


         if (!request.getCompany().getId().equals(user.getCompany().getId())) {
             throw new ApiException("request does not belong to your company");
         }

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ApiException("request description is empty");
        }

        return aiService.companyServiceEstimationCost(request.getDescription());
    }

    //Spare Parts Estimator
    public String sparePartsEstimator(User user, String description) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        Worker worker = user.getWorker();
        if (worker == null) {
            throw new ApiException("worker profile not found");
        }

        if (!Boolean.TRUE.equals(worker.getIsActive())) {
            throw new ApiException("worker is not active");
        }

        Company company = worker.getCompany();
        if (company == null) {
            throw new ApiException("company profile not found");
        }

        if (!"MAINTENANCE_COMPANY".equalsIgnoreCase(company.getUser().getRole())) {
            throw new ApiException("only maintenance company can use this feature");
        }

        if (description == null || description.isBlank()) {
            throw new ApiException("description is empty");
        }

        return aiService.maintenanceCompanySparePartsCosts(description);
    }

    //Issue Diagnosis Assistant
    public String issueDiagnosisAssistant(User user, String description) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        Worker worker = user.getWorker();
        if (worker == null) {
            throw new ApiException("worker profile not found");
        }

        if (!Boolean.TRUE.equals(worker.getIsActive())) {
            throw new ApiException("worker is not active");
        }

        if (description == null || description.isBlank()) {
            throw new ApiException("description is empty");
        }

        return aiService.workerIssueDiagnosis(description);
    }

    //smart move planner
    public String smartMovePlanner(User user, Integer requestId) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        Worker worker = user.getWorker();
        if (worker == null) {
            throw new ApiException("worker profile not found");
        }

        if (!Boolean.TRUE.equals(worker.getIsActive())) {
            throw new ApiException("worker is not active");
        }

        Company company = worker.getCompany();
        if (company == null) {
            throw new ApiException("company profile not found");
        }

        if (!"MOVING_COMPANY".equalsIgnoreCase(company.getUser().getRole())) {
            throw new ApiException("only moving company can use this feature");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) {
            throw new ApiException("request not found with id: " + requestId);
        }

        if (!"moving".equalsIgnoreCase(request.getType())) {
            throw new ApiException("this request is not a moving request");
        }

        if (request.getCompany() == null ||
                !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request does not belong to your company");
        }

        String description = request.getDescription();
        if (description == null || description.isBlank()) {
            throw new ApiException("request description is empty");
        }

        return aiService.movingCompanyResourceMovingEstimation(description);
    }

    //time estimation helper
    public String timeEstimationHelper(User user, String description) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        Worker worker = user.getWorker();
        if (worker == null) {
            throw new ApiException("worker profile not found");
        }

        if (!Boolean.TRUE.equals(worker.getIsActive())) {
            throw new ApiException("worker is not active");
        }

        if (description == null || description.isBlank()) {
            throw new ApiException("description is empty");
        }

        return aiService.workerJobTimeEstimation(description);
    }

    //Maintenance plan generator
    public String maintenancePlan(User user, Integer requestId) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        Worker worker = user.getWorker();
        if (worker == null) {
            throw new ApiException("worker profile not found");
        }

        if (!Boolean.TRUE.equals(worker.getIsActive())) {
            throw new ApiException("worker is not active");
        }

        Company company = worker.getCompany();
        if (company == null) {
            throw new ApiException("company profile not found");
        }

        if (!"MAINTENANCE_COMPANY".equalsIgnoreCase(company.getUser().getRole())) {
            throw new ApiException("only maintenance company can use this feature");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) {
            throw new ApiException("request not found with id: " + requestId);
        }

        if (!"maintenance".equalsIgnoreCase(request.getType())) {
            throw new ApiException("this request is not a maintenance request");
        }

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request does not belong to your company");
        }

        String description = request.getDescription();
        if (description == null || description.isBlank()) {
            throw new ApiException("request description is empty");
        }

        return aiService.maintenanceCompanyMaintenancePlan(description);
    }

    public String workerReportCreationAssistant(User user, DescriptionDTOIn dto) {

        if (user == null) throw new ApiException("unauthorized");
        if (user.getWorker() == null) throw new ApiException("worker profile not found");

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        if (!user.getWorker().getIsActive()) {
            throw new ApiException("worker is not active");
        }

        return aiService.workerReportCreationAssistant(dto.getDescription());
    }

    public String companyInspectionPlanningAssistant(User user, DescriptionDTOIn dto) {

        if (user == null) throw new ApiException("unauthorized");
        if (user.getWorker() == null) throw new ApiException("worker profile not found");

        if (!"INSPECTION_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only inspection company can use this feature");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        if (!user.getWorker().getIsActive()) {
            throw new ApiException("worker is not active");
        }

        return aiService.companyInspectionPlanningAssistant(dto.getDescription());
    }

    public String movingCompanyTimeAdvice(User user, DescriptionDTOIn dto) {

        if (user == null) throw new ApiException("unauthorized");
        if (user.getWorker() == null) throw new ApiException("worker profile not found");

        if (!"MOVING_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only moving company can use this feature");
        }
        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        if (!user.getWorker().getIsActive()) {
            throw new ApiException("worker is not active");
        }

        return aiService.movingCompanyTimeAdvice(dto.getDescription());
    }

    public String maintenanceFixOrReplace(User user, DescriptionDTOIn dto) {

        if (user == null) throw new ApiException("unauthorized");
        if (user.getWorker() == null) throw new ApiException("worker profile not found");

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        if (!"MAINTENANCE_COMPANY".equalsIgnoreCase(user.getRole())) {
            throw new ApiException("only maintenance company can use this feature");
        }
        if (!user.getWorker().getIsActive()) {
            throw new ApiException("worker is not active");
        }

        return aiService.maintenanceFixOrReplace(dto.getDescription());
    }


    public String companyIssueImageDiagnosis(User user, String imageUrl, String language) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        if (user.getWorker() == null) {
            throw new ApiException("worker profile not found");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        if (!user.getWorker().getIsActive()) {
            throw new ApiException("worker is not active");
        }

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new ApiException("image url is required");
        }

        return aiService.companyIssueImageDiagnosis(imageUrl, language);
    }

    public CompanyDTOOut convertToDTO(Company company) {
        User u = company.getUser();
        return new CompanyDTOOut(
                company.getId(), company.getStatus(),
                (u != null ? u.getName() : null), (u != null ? u.getEmail() : null),
                (u != null ? u.getPhone() : null), (u != null ? u.getCountry() : null),
                (u != null ? u.getCity() : null), (u != null ? u.getRole() : null)
        );
    }

}

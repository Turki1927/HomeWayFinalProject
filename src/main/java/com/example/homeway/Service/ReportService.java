package com.example.homeway.Service;

import com.example.homeway.API.ApiException;
import com.example.homeway.DTO.In.ReportDTOIn;
import com.example.homeway.DTO.In.ReportUpdateDTOIn;
import com.example.homeway.Model.Report;
import com.example.homeway.Model.Request;
import com.example.homeway.Model.User;
import com.example.homeway.Model.Worker;
import com.example.homeway.Repository.ReportRepository;
import com.example.homeway.Repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final RequestRepository requestRepository;

    //helpers
    private Worker requireWorker(User user) {
        if (user == null) throw new ApiException("Unauthenticated");

        if (user.getWorker() == null) {
            throw new ApiException("Only workers can perform this action");
        }

        return user.getWorker();
    }

    private void requireRequestOwnedByWorker(Request request, Integer workerId) {
        if (request.getWorker() == null) {
            throw new ApiException("This request is not assigned to a worker");
        }
        if (!request.getWorker().getId().equals(workerId)) {
            throw new ApiException("You are not allowed to access this request");
        }
    }

    private void requireReportOwnedByWorker(Report report, Integer workerId) {
        if (report.getRequest() == null) {
            throw new ApiException("Report is not linked to a request");
        }
        requireRequestOwnedByWorker(report.getRequest(), workerId);
    }


    public Report getReport(User user, Integer reportId) {
        Worker worker = requireWorker(user);

        Report report = reportRepository.findReportById(reportId);
        if (report == null) throw new ApiException("Report not found");

        requireReportOwnedByWorker(report, worker.getId());

        return report;
    }


    public void addReport(User user, Integer requestId, ReportDTOIn dto) {
        Worker worker = requireWorker(user);

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("Request not found");

        //checks if request is in_progress
        if(!request.getStatus().equalsIgnoreCase("in_progress")){
            throw new ApiException("Report can only be created if request is completed");
        }

        requireRequestOwnedByWorker(request, worker.getId());

        Report report = new Report();
        report.setFindings(dto.getFindings());
        report.setRecommendations(dto.getRecommendations());
        report.setImageURL(dto.getImageURL());
        report.setCreatedAt(LocalDateTime.now());
        report.setRequest(request);

        reportRepository.save(report);
    }

    public void updateReport(User user, Integer reportId, ReportUpdateDTOIn dto) {
        Worker worker = requireWorker(user);

        Report report = reportRepository.findReportById(reportId);
        if (report == null) throw new ApiException("Report not found");

        requireReportOwnedByWorker(report, worker.getId());

        if (dto.getFindings() != null && !dto.getFindings().isBlank()) {
            report.setFindings(dto.getFindings());
        }

        if (dto.getRecommendations() != null && !dto.getRecommendations().isBlank()) {
            report.setRecommendations(dto.getRecommendations());
        }

        if (dto.getImageURL() != null && !dto.getImageURL().isBlank()) {
            report.setImageURL(dto.getImageURL());
        }

        reportRepository.save(report);
    }

    public void deleteReport(User user, Integer reportId) {
        Worker worker = requireWorker(user);

        Report report = reportRepository.findReportById(reportId);
        if (report == null) throw new ApiException("Report not found");

        requireReportOwnedByWorker(report, worker.getId());

        reportRepository.delete(report);
    }
}
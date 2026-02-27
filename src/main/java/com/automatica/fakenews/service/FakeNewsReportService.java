package com.automatica.fakenews.service;

import com.automatica.fakenews.kafka.dto.FactCheckResponseMessage;
import com.automatica.fakenews.model.FakeNewsReport;
import com.automatica.fakenews.repository.FakeNewsReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FakeNewsReportService {

    @Autowired
    private FakeNewsReportRepository reportRepository;

    public List<FakeNewsReport> getApprovedReports() {
        return reportRepository.findByApprovedTrueOrderByApprovedAtDesc();
    }

    public List<FakeNewsReport> getPendingReports() {
        return reportRepository.findByApprovedFalseAndRejectedAtIsNullOrderByReportedAtDesc();
    }

    public List<FakeNewsReport> getAllReports() {
        return reportRepository.findAllByOrderByReportedAtDesc();
    }

    public Optional<FakeNewsReport> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    @Transactional
    public FakeNewsReport saveReport(FakeNewsReport report) {
        return reportRepository.save(report);
    }


    @Transactional
    public void approveReport(Long id, String approvedBy) {
        Optional<FakeNewsReport> reportOpt = reportRepository.findById(id);
        if (reportOpt.isPresent()) {
            FakeNewsReport report = reportOpt.get();
            report.setApproved(true);
            report.setApprovedAt(LocalDateTime.now());
            report.setApprovedBy(approvedBy);
            report.setRejectedAt(null);
            report.setRejectedBy(null);
            reportRepository.save(report);
        }
    }

    @Transactional
    public void rejectReport(Long id, String rejectedBy, String rejectionReason) {
        Optional<FakeNewsReport> reportOpt = reportRepository.findById(id);
        if (reportOpt.isPresent()) {
            FakeNewsReport report = reportOpt.get();
            report.setApproved(false);
            report.setRejectedAt(LocalDateTime.now());
            report.setRejectedBy(rejectedBy);
            report.setRejectionReason(rejectionReason);
            reportRepository.save(report);
        }
    }

    public List<FakeNewsReport> getRejectedReports() {
        return reportRepository.findByRejectedAtIsNotNullOrderByRejectedAtDesc();
    }

    public List<FakeNewsReport> getPublicReports() {
        return reportRepository.findApprovedAndRejectedReportsOrderByProcessedAtDesc();
    }

    @Transactional
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    @Transactional
    public void applyFactCheckResult(FactCheckResponseMessage msg) {

        if (msg == null || msg.getReportId() == null) return;

        Optional<FakeNewsReport> optReport = reportRepository.findById(msg.getReportId());
        if (optReport.isEmpty()) return;

        FakeNewsReport report = optReport.get();


        if ("DONE".equalsIgnoreCase(report.getFactCheckStatus()) || "FAILED".equalsIgnoreCase(report.getFactCheckStatus())) {
            return;
        }


        report.setFactCheckStatus(msg.getStatus() != null ? msg.getStatus() : "DONE"); //daca nu e null -> done/failed, daca e null -> default done

        // rezultatele factcheck

        report.setFactCheckRequestId(msg.getRequestId());
        report.setFactCheckVerdict(msg.getVerdict());
        report.setFactCheckConfidence(msg.getConfidence());
        report.setFactCheckProvider(msg.getProvider());
        report.setFactCheckPublisher(msg.getPublisher());
        report.setFactCheckUrl(msg.getUrl());
        report.setFactCheckRationale(msg.getRationale());
        report.setFactCheckWhatToVerify(msg.getWhatToVerify());
        report.setFactCheckErrorMessage(msg.getErrorMessage());

        if ("DONE".equalsIgnoreCase(report.getFactCheckStatus())) {
            report.setFactCheckErrorMessage(null);
        }

        reportRepository.save(report);

    }
}



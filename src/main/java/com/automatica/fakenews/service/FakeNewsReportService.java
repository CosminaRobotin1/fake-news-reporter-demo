package com.automatica.fakenews.service;

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
        return reportRepository.findByApprovedFalseOrderByReportedAtDesc();
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
            reportRepository.save(report);
        }
    }

    @Transactional
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}

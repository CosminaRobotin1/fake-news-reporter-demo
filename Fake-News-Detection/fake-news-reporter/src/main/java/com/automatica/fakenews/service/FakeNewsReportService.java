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
// Service care contine logica pentru gestionarea raportarilor.
// Face legatura intre controllere/consumerul Kafka si repository-ul bazei de date.
@Service
public class FakeNewsReportService {

    // Repository folosit pentru operatiile cu tabela fake_news_reports
    @Autowired
    private FakeNewsReportRepository reportRepository;

    // Returneaza raportarile aprobate
    public List<FakeNewsReport> getApprovedReports() {
        return reportRepository.findByApprovedTrueOrderByApprovedAtDesc();
    }

    public List<FakeNewsReport> getPendingReports() {
        return reportRepository.findByApprovedFalseAndRejectedAtIsNullOrderByReportedAtDesc();
    }

    public List<FakeNewsReport> getAllReports() {
        return reportRepository.findAllByOrderByReportedAtDesc();
    }

    // Cauta o raportare dupa ID
    public Optional<FakeNewsReport> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    // Salveaza o raportare noua in baza de date
    @Transactional
    public FakeNewsReport saveReport(FakeNewsReport report) {
        return reportRepository.save(report);
    }

    // Aproba o raportare
    @Transactional
    public void approveReport(Long id, String approvedBy) {
        Optional<FakeNewsReport> reportOpt = reportRepository.findById(id);
        if (reportOpt.isPresent()) { // Daca raportarea exista, o modifica
            FakeNewsReport report = reportOpt.get();
            report.setApproved(true); // Marcheaza raportarea ca aprobata
            report.setApprovedAt(LocalDateTime.now());
            report.setApprovedBy(approvedBy);
            report.setRejectedAt(null);// Sterge datele de respingere, daca existau
            report.setRejectedBy(null);
            reportRepository.save(report);// Salveaza modificarile in baza de date
        }
    }

    // Respinge o raportare
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

    // Returneaza raportarile respinse
    public List<FakeNewsReport> getRejectedReports() {
        return reportRepository.findByRejectedAtIsNotNullOrderByRejectedAtDesc();
    }

    // Returneaza raportarile vizibile public:
    // aprobate sau respinse
    public List<FakeNewsReport> getPublicReports() {
        return reportRepository.findApprovedAndRejectedReportsOrderByProcessedAtDesc();
    }

    // Sterge o raportare dupa ID
    @Transactional
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    // Aplica rezultatul fact-check primit din Kafka
    @Transactional
    public void applyFactCheckResult(FactCheckResponseMessage msg) {

        // Daca mesajul este invalid, metoda se opreste
        if(msg == null || msg.getReportId() == null ) return;

        // Cauta raportarea in baza de date dupa reportId
        Optional<FakeNewsReport> optReport = reportRepository.findById(msg.getReportId());
        if (optReport.isEmpty()) return;// Daca raportarea nu exista, metoda se opreste

        FakeNewsReport report = optReport.get();// Scoate raportarea din Optional

        // Daca raportarea are deja rezultat final,
        // nu mai suprascrie datele
        if("DONE".equalsIgnoreCase(report.getFactCheckStatus()) || "FAILED".equalsIgnoreCase(report.getFactCheckStatus())){
            return;
        }

        // Seteaza statusul primit din mesaj;
        // daca statusul lipseste, pune DONE implicit
        report.setFactCheckStatus(msg.getStatus() != null ? msg.getStatus() : "DONE");

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

        // Daca procesarea s-a terminat cu succes,
        // sterge eventualul mesaj de eroare
        if("DONE".equalsIgnoreCase(report.getFactCheckStatus())){
            report.setFactCheckErrorMessage(null);
        }

        reportRepository.save(report); // Salveaza raportarea actualizata in baza de date

    }
}

//@Transactional înseamnă că operația pe baza de date este tratată ca un tot unitar: dacă ceva merge prost
// în timpul modificării, schimbările pot fi anulate.
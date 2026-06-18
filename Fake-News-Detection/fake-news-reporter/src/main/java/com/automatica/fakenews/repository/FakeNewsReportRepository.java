package com.automatica.fakenews.repository;

import com.automatica.fakenews.model.FakeNewsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
// Repository folosit pentru accesarea si interogarea raportarilor
// stocate in tabela fake_news_reports.
@Repository
public interface FakeNewsReportRepository extends JpaRepository<FakeNewsReport, Long> {

    // Returneaza toate raportarile aprobate,
    // ordonate descrescator dupa data aprobarii
    List<FakeNewsReport> findByApprovedTrueOrderByApprovedAtDesc();

    // Returneaza raportarile aflate in asteptare (pending),
    // adica neaprobate si nerespinse
    List<FakeNewsReport> findByApprovedFalseAndRejectedAtIsNullOrderByReportedAtDesc();

    // Returneaza toate raportarile respinse,
    // ordonate dupa data respingerii
    List<FakeNewsReport> findByRejectedAtIsNotNullOrderByRejectedAtDesc();

    // Returneaza raportarile aprobate si respinse
    // ordonate dupa data la care au fost procesate
    @Query("SELECT r FROM FakeNewsReport r WHERE r.approved = true OR r.rejectedAt IS NOT NULL ORDER BY CASE WHEN r.approved = true THEN r.approvedAt ELSE r.rejectedAt END DESC")
    List<FakeNewsReport> findApprovedAndRejectedReportsOrderByProcessedAtDesc();

    // Returneaza toate raportarile
    // ordonate descrescator dupa data raportarii
    List<FakeNewsReport> findAllByOrderByReportedAtDesc();
}

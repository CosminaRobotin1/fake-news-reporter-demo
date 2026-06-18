package com.automatica.fakenews.controller;

import com.automatica.fakenews.model.FakeNewsReport;
import com.automatica.fakenews.service.FakeNewsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
// Controller responsabil cu administrarea raportarilor.
// Permite vizualizarea, aprobarea, respingerea si stergerea raportarilor.
@Controller//gestionează cereri web și returnează pagini HTML
@RequestMapping("/admin")//toate url urile incep cu /admin
public class AdminController {

    @Autowired
    private FakeNewsReportService reportService;

    // Incarca dashboard-ul administratorului cu toate raportarile.
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<FakeNewsReport> pendingReports = reportService.getPendingReports(); // Obtine raportarile aflate in asteptare
        List<FakeNewsReport> approvedReports = reportService.getApprovedReports();
        List<FakeNewsReport> rejectedReports = reportService.getRejectedReports();

        model.addAttribute("pendingReports", pendingReports); // Trimite listele catre pagina HTML
        model.addAttribute("approvedReports", approvedReports);
        model.addAttribute("rejectedReports", rejectedReports);

        return "admin/dashboard";
    }

    @PostMapping("/approve/{id}")
    public String approveReport(@PathVariable Long id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes){
        String username = authentication.getName(); // Obtine username-ul administratorului logat
        reportService.approveReport(id, username); // Marcheaza raportarea ca aprobata
        redirectAttributes.addFlashAttribute("successMessage", "Report approved successfully!");
        return "redirect:/admin/dashboard";//Reîncarcă dashboard-ul.
    }

    @PostMapping("/reject/{id}")
    public String rejectReport(@PathVariable Long id, @RequestParam("reason") String rejectionReason,
                               Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        reportService.rejectReport(id, username, rejectionReason);
        redirectAttributes.addFlashAttribute("successMessage", "Report rejected successfully!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String deleteReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reportService.deleteReport(id);// Sterge raportarea cu ID-ul primit
        redirectAttributes.addFlashAttribute("successMessage", "Report deleted successfully!");
        return "redirect:/admin/dashboard";
    }
}

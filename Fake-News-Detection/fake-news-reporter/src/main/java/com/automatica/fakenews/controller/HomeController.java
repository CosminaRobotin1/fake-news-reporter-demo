package com.automatica.fakenews.controller;

import com.automatica.fakenews.dto.ReportForm;
import com.automatica.fakenews.kafka.dto.FactCheckRequestMessage;
import com.automatica.fakenews.model.FakeNewsReport;
import com.automatica.fakenews.producer.FactCheckKafkaProducer;
import com.automatica.fakenews.service.FakeNewsReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;
import java.util.List;
//gestionează partea publică a aplicației, adică ce vede utilizatorul normal în browser.
@Controller
public class HomeController {

    // Service folosit pentru citirea si salvarea raportarilor in baza de date
    @Autowired
    private FakeNewsReportService reportService;

    // Producer Kafka folosit pentru trimiterea cererilor de fact-check
    @Autowired
    private FactCheckKafkaProducer factCheckKafkaProducer;

    //Afișează pagina principală
    @GetMapping("/")
    public String home(Model model) {//Ia raportările publice din baza de date și le trimite către pagina index.html
        List<FakeNewsReport> reports = reportService.getPublicReports();
        model.addAttribute("reports", reports);
        return "index";// Returneaza pagina templates/index.html
    }

    //Afișează pagina cu raportările publice
    @GetMapping("/reports")
    public String reports(Model model) {//Ia aceleași raportări publice și le trimite către reports.html
        List<FakeNewsReport> reports = reportService.getPublicReports();
        model.addAttribute("reports", reports);
        return "reports";
    }

    //Afișează formularul prin care utilizatorul poate raporta o știre
    @GetMapping("/report")
    public String showReportForm(Model model) {
        model.addAttribute("reportForm", new ReportForm());// Creeaza un obiect gol pentru formular
        return "report-form"; // Returneaza pagina templates/report-form.html
    }

    @PostMapping("/report")//se execută când utilizatorul apasă Submit Report.
    public String submitReport(@Valid @ModelAttribute("reportForm") ReportForm reportForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) { // Daca formularul are erori de validare, ramane pe pagina formularului
            return "report-form";
        }

        //kafka
        // Creeaza o raportare noua pe baza datelor introduse in formular
        FakeNewsReport report = new FakeNewsReport();
        // Copiaza datele din formular in obiectul care va fi salvat in baza de date
        report.setNewsSource(reportForm.getNewsSource());
        report.setUrl(reportForm.getUrl());
        report.setCategory(reportForm.getCategory());
        report.setDescription(reportForm.getDescription());
        report.setFactCheckStatus("PENDING");//

        // Salveaza raportarea in baza de date H2
        FakeNewsReport savedReport = reportService.saveReport(report);

        // Alege textul care va fi verificat:
        // daca exista descriere, verifica descrierea;
        // altfel verifica URL-ul
        String textToCheck = (savedReport.getDescription() != null && !savedReport.getDescription().isBlank())
                ? savedReport.getDescription()
                : savedReport.getUrl();

        // Creeaza mesajul Kafka care va fi trimis catre FactCheckStandalone
        FactCheckRequestMessage msg = new FactCheckRequestMessage(UUID.randomUUID().toString(), savedReport.getId(), textToCheck);

        factCheckKafkaProducer.send(msg); // Trimite cererea de fact-check prin Kafka
        redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your report has been submitted and is pending approval.");

        // Redirectioneaza utilizatorul inapoi la pagina principala
        return "redirect:/";
    }

    //Afișează pagina de login pentru administrator.
    @GetMapping("/login")//returneaza login.html
    public String login() {
        return "login";
    }
}
//Când utilizatorul trimite o știre suspectă, controllerul salvează raportarea în baza de date și trimite automat o cerere de verificare prin Kafka către serviciul FactCheckStandalone.
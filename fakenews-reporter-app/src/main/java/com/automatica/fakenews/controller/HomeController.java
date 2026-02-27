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

@Controller
public class HomeController {

    @Autowired
    private FakeNewsReportService reportService;

    @Autowired
    private FactCheckKafkaProducer factCheckKafkaProducer;

    @GetMapping("/")
    public String home(Model model) {
        List<FakeNewsReport> reports = reportService.getPublicReports();
        model.addAttribute("reports", reports);
        return "index";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        List<FakeNewsReport> reports = reportService.getPublicReports();
        model.addAttribute("reports", reports);
        return "reports";
    }

    @GetMapping("/report")
    public String showReportForm(Model model) {
        model.addAttribute("reportForm", new ReportForm());
        return "report-form";
    }

    @PostMapping("/report")
    public String submitReport(@Valid @ModelAttribute("reportForm") ReportForm reportForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return "report-form";
        }

        //kafka
        FakeNewsReport report = new FakeNewsReport();
        report.setNewsSource(reportForm.getNewsSource());
        report.setUrl(reportForm.getUrl());
        report.setCategory(reportForm.getCategory());
        report.setDescription(reportForm.getDescription());
        report.setFactCheckStatus("PENDING");

        FakeNewsReport savedReport = reportService.saveReport(report);


        String textToCheck = (savedReport.getDescription() != null && !savedReport.getDescription().isBlank()) ? savedReport.getDescription() : savedReport.getUrl();

        FactCheckRequestMessage msg = new FactCheckRequestMessage(UUID.randomUUID().toString(), savedReport.getId(), textToCheck);

        factCheckKafkaProducer.send(msg);


        redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your report has been submitted and is pending approval.");

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

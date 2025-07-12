package com.myproject.expense_tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
@Service
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    @Autowired
    public EmailTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String buildHtmlReport(String fullName, String month, Map<String, Double> monthlySummary) {
        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("month", month);
        context.setVariable("income", monthlySummary.getOrDefault("totalIncome", 0.0));
        context.setVariable("expenses", monthlySummary.getOrDefault("totalExpense", 0.0));
        context.setVariable("savings", monthlySummary.getOrDefault("savings", 0.0));

        return templateEngine.process("email-report", context); // email-report.html in templates/
    }
}
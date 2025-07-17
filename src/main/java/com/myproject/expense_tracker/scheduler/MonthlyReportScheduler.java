package com.myproject.expense_tracker.scheduler;

import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.repository.ExpenseRepository;
import com.myproject.expense_tracker.service.*;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class MonthlyReportScheduler {

    private final UserService userService;
    private final EmailService emailService;
    private final ReportService reportService;
    private final ChartService chartService;
    private final EmailTemplateService emailTemplateService;


    public MonthlyReportScheduler(UserService userService, EmailService emailService, ReportService reportService, ChartService chartService, EmailTemplateService emailTemplateService) {
        this.userService = userService;
        this.emailService = emailService;
        this.reportService = reportService;
        this.chartService = chartService;
        this.emailTemplateService = emailTemplateService;
    }

//    @Scheduled(cron = "0 0 10 1 * ?") // Every 1st of month at 10:00 AM
//    @Scheduled(cron = "0 */2 * * * *") // Every 2 minutes
    public void sendMonthlyReports() throws IOException, MessagingException {
        String month = LocalDate.now().minusMonths(1).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        List<User> users = userService.findAllUsers();
        for (User user : users) {
            Map<String, Double> monthlySummary = reportService.getLastMonthSummary(user.getUsername());
            Map<String, Double> categorySummary = reportService.getMonthlyCategorySummary(user.getUsername());

//            Plain text email
//            String report = generatePlainTextReport(user.getFullName(),
//                    month,
//                    monthlySummary,
//                    categorySummary
//            );

//            emailService.sendSimpleEmail(
//                    user.getEmail(),
//                    "Your " + month + " Expense Report",
//                    report
//            );


//            HTML email report with chart
            File chartFile = chartService.generateCategoryPieChart(categorySummary);
            String htmlBody = emailTemplateService.buildHtmlReport(user.getFullName(),
                    month,
                    monthlySummary);
            emailService.sendHtmlEmailWithChart(
                    user.getEmail(),
                    "Your " + month + " Expense Report",
                    htmlBody,
                    chartFile
            );
            break;
        }
    }
    private String generatePlainTextReport(
            String fullName,
            String month,
            Map<String, Double> monthlySummary,
            Map<String, Double> categorySummary
    ) {
        double income = monthlySummary.getOrDefault("totalIncome", 0.0);
        double expense = monthlySummary.getOrDefault("totalExpense", 0.0);
        double savings = monthlySummary.getOrDefault("savings", 0.0);

        StringBuilder report = new StringBuilder(String.format("""
            Hello %s,

            📊 Here is your summary for %s:

            ▪ Total Income: ₹%.2f
            ▪ Total Expenses: ₹%.2f
            ▪ Savings: ₹%.2f

            🔍 Expense Breakdown by Category:
            """, fullName, month, income, expense, savings));

        for (Map.Entry<String, Double> entry : categorySummary.entrySet()) {
            report.append(String.format("   ▪ %s: ₹%.2f\n", entry.getKey(), entry.getValue()));
        }
        report.append("\nThank you for using Expense Tracker!");
        return report.toString();
    }
}
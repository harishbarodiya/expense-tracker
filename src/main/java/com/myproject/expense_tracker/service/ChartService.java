package com.myproject.expense_tracker.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class ChartService {

    public File generateCategoryPieChart(Map<String, Double> categorySummary) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        categorySummary.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Expense by Category",
                dataset,
                true, true, false
        );

        File chartFile = new File("category-chart.png"); // You can use a temp folder if preferred
        ChartUtils.saveChartAsPNG(chartFile, chart, 600, 400);
        return chartFile;
    }
}

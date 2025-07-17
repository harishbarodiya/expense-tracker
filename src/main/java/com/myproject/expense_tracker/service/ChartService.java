package com.myproject.expense_tracker.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class ChartService {
    private static Logger logger = LoggerFactory.getLogger(ChartService.class);

    public File generateCategoryPieChart(Map<String, Double> categorySummary) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        categorySummary.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Expense by Category",
                dataset,
                true, true, false
        );

        File chartFile = new File("category-chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 600, 400);
        logger.info("Chart generated successfully!");

        return chartFile;
    }
}

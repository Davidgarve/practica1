package org.ulpgc.dacd.view;

import javax.swing.*;
import java.awt.*;

public class TripPlannerWebUI extends JFrame {

    private final TripPlannerController tripPlannerController;

    public TripPlannerWebUI(TripPlannerController tripPlannerController) {
        this.tripPlannerController = tripPlannerController;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Trip Planner Web UI");

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel predictionPanel = createPredictionPanel();
        JPanel recommendationPanel = createRecommendationPanel();
        JPanel hotelPanel = createHotelPanel();

        tabbedPane.addTab("Weather Forecast", predictionPanel);
        tabbedPane.addTab("Travel Recommendations", recommendationPanel);
        tabbedPane.addTab("Available Hotels", hotelPanel);

        getContentPane().add(tabbedPane);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createPredictionPanel() {
        JPanel predictionPanel = new JPanel();
        predictionPanel.setLayout(new BorderLayout());
        JPanel predictionContent = tripPlannerController.createPredictionTabContent();
        predictionPanel.add(predictionContent, BorderLayout.CENTER);
        return predictionPanel;
    }

    private JPanel createRecommendationPanel() {
        JPanel recommendationPanel = new JPanel();
        recommendationPanel.setLayout(new BorderLayout());
        JPanel recommendationContent = tripPlannerController.createRecommendationTabContent();
        recommendationPanel.add(recommendationContent, BorderLayout.CENTER);
        return recommendationPanel;
    }

    private JPanel createHotelPanel() {
        JPanel hotelPanel = new JPanel();
        hotelPanel.setLayout(new BorderLayout());
        JPanel hotelContent = tripPlannerController.createHotelTabContent();
        hotelPanel.add(hotelContent, BorderLayout.CENTER);
        return hotelPanel;
    }
}
package org.ulpgc.dacd.view;

import org.ulpgc.dacd.control.SQLiteEventStore;

import javax.swing.*;
import java.awt.*;

public class TripPlannerWebUI extends JFrame {

    private final TripPlannerController tripPlannerController;

    public TripPlannerWebUI() {
        this.tripPlannerController = new TripPlannerController(new SQLiteEventStore());
        initComponents();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TripPlannerWebUI tripPlannerWebUI = new TripPlannerWebUI();
            tripPlannerWebUI.setVisible(true);
        });
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Trip Planner Web UI");

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel predictionPanel = createPredictionPanel();
        JPanel recommendationPanel = createRecommendationPanel();
        JPanel hotelPanel = createHotelPanel();

        tabbedPane.addTab("Predicción del Tiempo", predictionPanel);
        tabbedPane.addTab("Recomendaciones de Viaje", recommendationPanel);
        tabbedPane.addTab("Hoteles Disponibles", hotelPanel);

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

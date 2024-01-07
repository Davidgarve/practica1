package org.ulpgc.dacd.view;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.ulpgc.dacd.control.HotelBusinessLogic;
import org.ulpgc.dacd.control.SQLiteEventStore;
import org.ulpgc.dacd.control.WeatherBusinessLogic;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class TripPlannerController {

    private final WeatherBusinessLogic weatherBusinessLogic;
    private final HotelBusinessLogic hotelBusinessLogic;

    public TripPlannerController(SQLiteEventStore eventStore) {
        this.weatherBusinessLogic = new WeatherBusinessLogic(eventStore);
        this.hotelBusinessLogic = new HotelBusinessLogic(eventStore);
    }

    public JPanel createPredictionTabContent() {
        JPanel predictionContent = createStyledJPanel();
        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        JDatePickerImpl predictionDatePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        JComboBox<String> predictionLocationChoiceBox = new JComboBox<>();
        JButton predictButton = createStyledButton("Predict Conditions");
        JTextPane predictionTextPane = new JTextPane();

        updateAvailableLocations(predictionLocationChoiceBox);

        predictButton.addActionListener(e -> {
            Object selectedDateObject = predictionDatePicker.getModel().getValue();

            if (selectedDateObject instanceof Date) {
                handlePredictionButtonClick(
                        predictionLocationChoiceBox.getSelectedItem().toString(),
                        ((Date) selectedDateObject).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        predictionTextPane.getDocument()
                );
            } else if (selectedDateObject instanceof GregorianCalendar) {
                handlePredictionButtonClick(
                        predictionLocationChoiceBox.getSelectedItem().toString(),
                        ((GregorianCalendar) selectedDateObject).toZonedDateTime().toLocalDate(),
                        predictionTextPane.getDocument()
                );
            } else {
                System.err.println("Unsupported date type: " + selectedDateObject.getClass());
            }
        });


        predictionContent.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(10, 0, 5, 5);

        predictionContent.add(new JLabel("Attention: This service only provides responses for a maximum range of 5 days after today"));
        gbc.gridy++;
        predictionContent.add(new JLabel("Location"), gbc);

        gbc.gridy++;
        predictionContent.add(predictionDatePicker, gbc);

        gbc.gridy++;
        predictionContent.add(new JLabel("Location"), gbc);

        gbc.gridy++;
        predictionContent.add(predictionLocationChoiceBox, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2; // Occupies two columns
        predictionContent.add(predictButton, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1; // Reverts to occupying one column
        predictionContent.add(new JLabel("Solution"), gbc);

        gbc.gridx = 1;
        predictionContent.add(predictionTextPane, gbc);

        return predictionContent;
    }

    public JPanel createRecommendationTabContent() {
        JPanel recommendationContent = createStyledJPanel();
        UtilDateModel checkInModel = new UtilDateModel();
        UtilDateModel checkOutModel = new UtilDateModel();
        Properties properties = new Properties();
        JDatePanelImpl checkInDatePanel = new JDatePanelImpl(checkInModel, properties);
        JDatePanelImpl checkOutDatePanel = new JDatePanelImpl(checkOutModel, properties);
        JDatePickerImpl checkInDatePicker = new JDatePickerImpl(checkInDatePanel, new DateLabelFormatter());
        JDatePickerImpl checkOutDatePicker = new JDatePickerImpl(checkOutDatePanel, new DateLabelFormatter());
        JComboBox<String> recommendationLocationChoiceBox = new JComboBox<>();
        JButton recommendButton = createStyledButton("Get Recommendations");
        JTextPane recommendationTextPane = new JTextPane();

        updateAvailableLocations(recommendationLocationChoiceBox);

        recommendButton.addActionListener(e -> {
            Object checkInDateObject = checkInDatePicker.getModel().getValue();
            Object checkOutDateObject = checkOutDatePicker.getModel().getValue();

            if (checkInDateObject instanceof Date && checkOutDateObject instanceof Date) {
                handleRecommendationButtonClick(
                        (String) recommendationLocationChoiceBox.getSelectedItem(),
                        ((Date) checkInDateObject).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        ((Date) checkOutDateObject).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        recommendationTextPane.getDocument()
                );
            } else {
                System.err.println("Unsupported date type");
            }
        });

        // Changes in the layout of components
        recommendationContent.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        recommendationContent.add(new JLabel("Attention: This service only provides responses for a maximum range of 5 days after today"));
        gbc.gridy++;
        recommendationContent.add(new JLabel("Location"), gbc);

        gbc.gridy++;
        recommendationContent.add(checkInDatePicker, gbc);

        gbc.gridy++;
        recommendationContent.add(new JLabel("Check-Out Date"), gbc);

        gbc.gridy++;
        recommendationContent.add(checkOutDatePicker, gbc);

        gbc.gridy++;
        recommendationContent.add(new JLabel("Location"), gbc);

        gbc.gridy++;
        recommendationContent.add(recommendationLocationChoiceBox, gbc);

        gbc.gridy++;
        recommendationContent.add(recommendButton, gbc);

        gbc.gridy++;
        recommendationContent.add(recommendationTextPane, gbc);

        return recommendationContent;
    }

    public JPanel createHotelTabContent() {
        JPanel hotelContent = createStyledJPanel();
        JComboBox<String> hotelLocationChoiceBox = new JComboBox<>();
        UtilDateModel checkInModel = new UtilDateModel();
        UtilDateModel checkOutModel = new UtilDateModel();
        Properties properties = new Properties();
        JDatePanelImpl checkInDatePanel = new JDatePanelImpl(checkInModel, properties);
        JDatePanelImpl checkOutDatePanel = new JDatePanelImpl(checkOutModel, properties);
        JDatePickerImpl checkInDatePicker = new JDatePickerImpl(checkInDatePanel, new DateLabelFormatter());
        JDatePickerImpl checkOutDatePicker = new JDatePickerImpl(checkOutDatePanel, new DateLabelFormatter());
        JButton getHotelsButton = createStyledButton("Get Available Hotels");

        // Utilizar un JTextPane con formato HTML
        JTextPane hotelTextPane = createStyledHtmlTextPane();

        updateAvailableLocations(hotelLocationChoiceBox);

        getHotelsButton.addActionListener(e -> {
            Object checkInDateObject = checkInDatePicker.getModel().getValue();
            Object checkOutDateObject = checkOutDatePicker.getModel().getValue();

            if (checkInDateObject instanceof Date && checkOutDateObject instanceof Date) {
                // Pasar el JTextPane al método handleGetHotelsButtonClick
                handleGetHotelsButtonClick(
                        (String) hotelLocationChoiceBox.getSelectedItem(),
                        ((Date) checkInDateObject).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        ((Date) checkOutDateObject).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        hotelTextPane
                );
            } else {
                System.err.println("Unsupported date type");
            }
        });

        hotelContent.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        hotelContent.add(new JLabel("Attention: This service only provides responses for a maximum range of 5 days after today"));
        gbc.gridy++;
        hotelContent.add(new JLabel("Location"), gbc);

        gbc.gridy++;
        hotelContent.add(hotelLocationChoiceBox, gbc);

        gbc.gridy++;
        hotelContent.add(new JLabel("Check-In Date"), gbc);

        gbc.gridy++;
        hotelContent.add(checkInDatePicker, gbc);

        gbc.gridy++;
        hotelContent.add(new JLabel("Check-Out Date"), gbc);

        gbc.gridy++;
        hotelContent.add(checkOutDatePicker, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2; // Ocupa dos columnas
        hotelContent.add(getHotelsButton, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1; // Revierte a ocupar una columna

        // Utilizar JTextPane con formato HTML para mostrar el contenido de los hoteles
        gbc.gridy++;
        hotelContent.add(hotelTextPane, gbc);

        return hotelContent;
    }

    private JTextPane createStyledHtmlTextPane() {
        JTextPane textPane = new JTextPane();
        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        HTMLDocument htmlDocument = (HTMLDocument) htmlEditorKit.createDefaultDocument();
        textPane.setEditorKit(htmlEditorKit);
        textPane.setDocument(htmlDocument);
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setBackground(new Color(240, 240, 240));
        textPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return textPane;
    }


    public static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private String pattern = "yyyy-MM-dd";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                if (value instanceof Date) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime((Date) value);
                    return dateFormatter.format(cal.getTime());
                } else if (value instanceof GregorianCalendar) {
                    return dateFormatter.format(((GregorianCalendar) value).getTime());
                }
            }
            return "";
        }
    }

    private void updateAvailableLocations(JComboBox<String> locationChoiceBox) {
        List<String> availableLocations = hotelBusinessLogic.getAvailableHotelLocations();
        locationChoiceBox.removeAllItems();
        for (String location : availableLocations) {
            locationChoiceBox.addItem(location);
        }
    }

    private void handlePredictionButtonClick(String selectedLocation, LocalDate selectedDate, Document document) {
        String formattedDate = selectedDate != null ? selectedDate.toString() : null;
        String predictionResult = weatherBusinessLogic.compareWeatherConditions(selectedLocation, formattedDate);

        if (predictionResult.isEmpty()) {
            showAlert("No Data", "No data available for the selected date.");
        } else {
            document.putProperty(Document.StreamDescriptionProperty, null);
            try {
                document.remove(0, document.getLength());
                document.insertString(0, predictionResult, null);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleRecommendationButtonClick(String selectedLocation, LocalDate checkInDate, LocalDate checkOutDate, Document document) {
        if (checkInDate == null || checkOutDate == null || checkInDate.isAfter(checkOutDate)) {
            showAlert("Date Error", "Select valid check-in and check-out dates.");
            return;
        }

        // Check if there is data available for at least one of the dates
        if (!weatherBusinessLogic.isDataAvailableForDates(checkInDate, checkOutDate)) {
            showAlert("Insufficient Data", "No data available for the selected date(s).");
            return;
        }

        try {
            List<Map<String, Object>> weatherData = weatherBusinessLogic.getWeatherInfoInRange(selectedLocation, checkInDate, checkOutDate);
            List<Map<String, String>> weatherStatusList = Collections.singletonList(weatherBusinessLogic.getWeatherStatusForDate(weatherData));
            String recommendation = weatherBusinessLogic.generateWeatherRecommendation(weatherStatusList);
            document.putProperty(Document.StreamDescriptionProperty, null);
            document.remove(0, document.getLength());
            document.insertString(0, recommendation, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "An error occurred. Please try again.");
        }
    }

    private void handleGetHotelsButtonClick(String selectedLocation, LocalDate checkInDate, LocalDate checkOutDate, JTextPane textPane) {
        if (checkInDate == null || checkOutDate == null || checkInDate.isAfter(checkOutDate)) {
            showAlert("Date Error", "Select valid check-in and check-out dates.");
            return;
        }

        try {
            List<Map<String, Object>> hotelData = hotelBusinessLogic.getHotelInfoForDates(selectedLocation, checkInDate, checkOutDate);

            // Obtener contenido en formato HTML
            String htmlContent = buildHotelInfoHTML(hotelData);

            // Insertar contenido HTML en el JTextPane
            setTextPaneContent(textPane, htmlContent);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "An error occurred while obtaining hotel information. Please try again.");
        }
    }

    private void setTextPaneContent(JTextPane textPane, String content) {
        textPane.setContentType("text/html");
        textPane.setText(content);
    }

    public static String buildHotelInfoHTML(List<Map<String, Object>> hotelData) {
        StringBuilder htmlContent = new StringBuilder();

        if (hotelData.isEmpty()) {
            htmlContent.append("No data available for the selected location and dates.");
        } else {
            // Utilizar una tabla HTML para organizar los hoteles en columnas
            htmlContent.append("<table border='1'><tr>");

            for (Map<String, Object> hotelInfo : hotelData) {
                String hotelName = hotelInfo.get("hotelName").toString();
                String location = hotelInfo.get("location").toString();
                String checkIn = hotelInfo.get("checkIn").toString();
                String checkOut = hotelInfo.get("checkOut").toString();

                htmlContent.append("<td width='200'>")  // Ancho ajustado a 200 píxeles, ajusta según tus necesidades
                        .append("<b>").append(hotelName).append("</b>").append("<br>")
                        .append("Location: ").append(location).append("<br>")
                        .append("Check-In: ").append(checkIn).append("<br>")
                        .append("Check-Out: ").append(checkOut).append("<br>");

                List<Map<String, Object>> ratesList = (List<Map<String, Object>>) hotelInfo.get("rates");
                if (ratesList != null && !ratesList.isEmpty()) {
                    htmlContent.append("<b>Rates:</b>").append("<br>");
                    for (Map<String, Object> rateInfo : ratesList) {
                        String rateName = rateInfo.get("name").toString();
                        Double rateValue = (Double) rateInfo.get("rate");
                        Double taxValue = (Double) rateInfo.get("tax");

                        htmlContent.append(rateName).append(": ").append(rateValue + taxValue).append("<br>");
                    }
                } else {
                    htmlContent.append("No rate data available for this hotel on the selected dates.");
                }

                htmlContent.append("</td>");
            }

            htmlContent.append("</tr></table>");
        }

        return htmlContent.toString();
    }

    private JPanel createStyledJPanel() {
        JPanel styledPanel = new JPanel();
        styledPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return styledPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void showAlert(String title, String content) {
        JOptionPane.showMessageDialog(null, content, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
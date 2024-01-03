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
        JButton predictButton = createStyledButton("Predecir Condiciones");
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

        predictionContent.add(new JLabel("Atención: Este servicio solo ofrece respuestas para un rango máximo de 5 días después de hoy"));
        gbc.gridy++;
        predictionContent.add(new JLabel("Ubicación"), gbc);

        gbc.gridy++;
        predictionContent.add(predictionDatePicker, gbc);

        gbc.gridy++;
        predictionContent.add(new JLabel("Ubicación"), gbc);

        gbc.gridy++;
        predictionContent.add(predictionLocationChoiceBox, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2; // Ocupa dos columnas
        predictionContent.add(predictButton, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1; // Vuelve a ocupar una columna
        predictionContent.add(new JLabel("Solución"), gbc);

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
        JButton recommendButton = createStyledButton("Obtener Recomendaciones");
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

        // Cambios en la disposición de los componentes
        recommendationContent.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        recommendationContent.add(new JLabel("Atención: Este servicio solo ofrece respuestas para un rango máximo de 5 días después de hoy"));
        gbc.gridy++;
        recommendationContent.add(new JLabel("Ubicación"), gbc);

        gbc.gridy++;
        recommendationContent.add(checkInDatePicker, gbc);

        gbc.gridy++;
        recommendationContent.add(new JLabel("Fecha de Check-Out"), gbc);

        gbc.gridy++;
        recommendationContent.add(checkOutDatePicker, gbc);

        gbc.gridy++;
        recommendationContent.add(new JLabel("Ubicación"), gbc);

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
        JButton getHotelsButton = createStyledButton("Obtener Hoteles Disponibles");
        JTextPane hotelTextPane = new JTextPane();

        updateAvailableLocations(hotelLocationChoiceBox);

        getHotelsButton.addActionListener(e -> {
            Object checkInDateObject = checkInDatePicker.getModel().getValue();
            Object checkOutDateObject = checkOutDatePicker.getModel().getValue();

            if (checkInDateObject instanceof Date && checkOutDateObject instanceof Date) {
                handleGetHotelsButtonClick(
                        (String) hotelLocationChoiceBox.getSelectedItem(),
                        ((Date) checkInDateObject).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        ((Date) checkOutDateObject).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        hotelTextPane.getDocument()
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
        hotelContent.add(new JLabel("Atención: Este servicio solo ofrece respuestas para un rango máximo de 5 días después de hoy"));
        gbc.gridy++;
        hotelContent.add(new JLabel("Ubicación"), gbc);

        gbc.gridy++;
        hotelContent.add(hotelLocationChoiceBox, gbc);

        gbc.gridy++;
        hotelContent.add(new JLabel("Fecha de Check-In"), gbc);

        gbc.gridy++;
        hotelContent.add(checkInDatePicker, gbc);

        gbc.gridy++;
        hotelContent.add(new JLabel("Fecha de Check-Out"), gbc);

        gbc.gridy++;
        hotelContent.add(checkOutDatePicker, gbc);

        gbc.gridy++;
        hotelContent.add(getHotelsButton, gbc);

        gbc.gridy++;
        hotelContent.add(hotelTextPane, gbc);

        return hotelContent;
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
            showAlert("Sin datos", "No hay datos disponibles para la fecha seleccionada.");
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
            showAlert("Error de fechas", "Selecciona fechas de check-in y check-out válidas.");
            return;
        }

        // Verifica si hay datos disponibles para al menos una de las fechas
        if (!weatherBusinessLogic.isDataAvailableForDates(selectedLocation, checkInDate, checkOutDate)) {
            showAlert("Datos insuficientes", "No hay datos disponibles para la(s) fecha(s) seleccionada(s).");
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
            showAlert("Error", "Se produjo un error. Por favor, inténtalo de nuevo.");
        }
    }

    private void handleGetHotelsButtonClick(String selectedLocation, LocalDate checkInDate, LocalDate checkOutDate, Document document) {
        if (checkInDate == null || checkOutDate == null || checkInDate.isAfter(checkOutDate)) {
            showAlert("Error de fechas", "Selecciona fechas de check-in y check-out válidas.");
            return;
        }

        try {
            List<Map<String, Object>> hotelData = hotelBusinessLogic.getHotelInfoForDates(selectedLocation, checkInDate, checkOutDate);
            String htmlContent = buildHotelInfoHTML(hotelData);
            document.putProperty(Document.StreamDescriptionProperty, null);
            document.remove(0, document.getLength());
            document.insertString(0, htmlContent, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Se produjo un error al obtener la información de hoteles. Por favor, inténtalo de nuevo.");
        }
    }

    public static String buildHotelInfoHTML(List<Map<String, Object>> hotelData) {
        StringBuilder htmlContent = new StringBuilder();

        if (hotelData.isEmpty()) {
            htmlContent.append("No hay datos disponibles para la ubicación y fechas seleccionadas.");
        } else {
            for (Map<String, Object> hotelInfo : hotelData) {
                String hotelName = hotelInfo.get("hotelName").toString();
                String location = hotelInfo.get("location").toString();
                String checkIn = hotelInfo.get("checkIn").toString();
                String checkOut = hotelInfo.get("checkOut").toString();

                htmlContent.append(hotelName).append("\n")
                        .append("Ubicación: ").append(location).append("\n")
                        .append("Check-In: ").append(checkIn).append("\n")
                        .append("Check-Out: ").append(checkOut).append("\n");

                List<Map<String, Object>> ratesList = (List<Map<String, Object>>) hotelInfo.get("rates");
                if (ratesList != null && !ratesList.isEmpty()) {
                    htmlContent.append("Rates: ").append("\n");
                    for (Map<String, Object> rateInfo : ratesList) {
                        String rateName = rateInfo.get("name").toString();
                        Double rateValue = (Double) rateInfo.get("rate");
                        Double taxValue = (Double) rateInfo.get("tax");

                        htmlContent.append(rateName).append(": ").append(rateValue + taxValue).append("\n");
                    }
                    htmlContent.append("\n");
                } else {
                    htmlContent.append("No hay datos de tarifas disponibles para este hotel en las fechas seleccionadas.");
                }
            }
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

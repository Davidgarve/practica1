package org.ulpgc.dacd.view;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

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

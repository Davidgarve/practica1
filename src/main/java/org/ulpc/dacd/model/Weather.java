package org.ulpc.dacd.model;
import java.time.Instant;

public class Weather {
    private Instant ts;
    private double pop;
    private double speed;
    private double temp;
    private int humidity;
    private Location location;
    private String DtTxt;
    private int CloudsAll;

    public double getPop() {
        return pop;
    }

    public void setPop(double pop) {
        this.pop = pop;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getDtTxt() {
        return DtTxt;
    }

    public void setDtTxt(String dtTxt) {
        DtTxt = dtTxt;
    }

    public int getCloudsAll() {
        return CloudsAll;
    }

    public void setCloudsAll(int clouds) {
        CloudsAll = clouds;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "pop=" + pop +
                ", speed=" + speed +
                ", temp=" + temp +
                ", humidity=" + humidity +
                '}';
    }
}

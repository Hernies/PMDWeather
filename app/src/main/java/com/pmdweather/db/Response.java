package com.pmdweather.db;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    private Daily daily;

    // Getters and Setters
    public Daily getDaily() {
        return daily;
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }

    public static class Daily implements Serializable {
        private List<String> time;
        private List<Integer> weatherCode;
        private List<Double> temperature2mMax;
        private List<Double> temperature2mMin;
        private List<Double> apparentTemperatureMax;
        private List<Double> apparentTemperatureMin;

        // Getters and Setters
        public List<String> getTime() {
            return time;
        }

        public void setTime(List<String> time) {
            this.time = time;
        }

        public List<Integer> getWeatherCode() {
            return weatherCode;
        }

        public void setWeatherCode(List<Integer> weatherCode) {
            this.weatherCode = weatherCode;
        }

        public List<Double> getTemperature2mMax() {
            return temperature2mMax;
        }

        public void setTemperature2mMax(List<Double> temperature2mMax) {
            this.temperature2mMax = temperature2mMax;
        }

        public List<Double> getTemperature2mMin() {
            return temperature2mMin;
        }

        public void setTemperature2mMin(List<Double> temperature2mMin) {
            this.temperature2mMin = temperature2mMin;
        }

        public List<Double> getApparentTemperatureMax() {
            return apparentTemperatureMax;
        }

        public void setApparentTemperatureMax(List<Double> apparentTemperatureMax) {
            this.apparentTemperatureMax = apparentTemperatureMax;
        }

        public List<Double> getApparentTemperatureMin() {
            return apparentTemperatureMin;
        }

        public void setApparentTemperatureMin(List<Double> apparentTemperatureMin) {
            this.apparentTemperatureMin = apparentTemperatureMin;
        }
    }
}

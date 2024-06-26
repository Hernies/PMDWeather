package com.pmdweather.db;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    private Hourly hourly;
    private Daily daily;

    // Getters and Setters
    public Hourly getHourly() {
        return hourly;
    }

    public void setHourly(Hourly hourly) {
        this.hourly = hourly;
    }

    public Daily getDaily() {
        return daily;
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }

    public static class Hourly implements Serializable {
        private List<String> time;
        private List<Double> temperature2m;
        private List<Integer> relativeHumidity2m;
        private List<Double> apparentTemperature;
        private List<Integer> weatherCode;

        // Getters and Setters
        public List<String> getTime() {
            return time;
        }

        public void setTime(List<String> time) {
            this.time = time;
        }

        public List<Double> getTemperature2m() {
            return temperature2m;
        }

        public void setTemperature2m(List<Double> temperature2m) {
            this.temperature2m = temperature2m;
        }

        public List<Integer> getRelativeHumidity2m() {
            return relativeHumidity2m;
        }

        public void setRelativeHumidity2m(List<Integer> relativeHumidity2m) {
            this.relativeHumidity2m = relativeHumidity2m;
        }

        public List<Double> getApparentTemperature() {
            return apparentTemperature;
        }

        public void setApparentTemperature(List<Double> apparentTemperature) {
            this.apparentTemperature = apparentTemperature;
        }

        public List<Integer> getWeatherCode() {
            return weatherCode;
        }

        public void setWeatherCode(List<Integer> weatherCode) {
            this.weatherCode = weatherCode;
        }
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

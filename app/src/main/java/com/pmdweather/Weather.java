package com.pmdweather;

import com.google.gson.annotations.SerializedName;
import java.util.List;



public class Weather {

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("elevation")
    private Integer elevation;

    @SerializedName("generationtime_ms")
    private Double generationtimeMs;

    @SerializedName("utc_offset_seconds")
    private Integer utcOffsetSeconds;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("timezone_abbreviation")
    private String timezoneAbbreviation;

    @SerializedName("current_units")
    private CurrentUnits currentUnits;

    @SerializedName("current")
    private Current current;

    // Getters and Setters
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getElevation() {
        return elevation;
    }

    public void setElevation(Integer elevation) {
        this.elevation = elevation;
    }

    public Double getGenerationtimeMs() {
        return generationtimeMs;
    }

    public void setGenerationtimeMs(Double generationtimeMs) {
        this.generationtimeMs = generationtimeMs;
    }

    public Integer getUtcOffsetSeconds() {
        return utcOffsetSeconds;
    }

    public void setUtcOffsetSeconds(Integer utcOffsetSeconds) {
        this.utcOffsetSeconds = utcOffsetSeconds;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezoneAbbreviation() {
        return timezoneAbbreviation;
    }

    public void setTimezoneAbbreviation(String timezoneAbbreviation) {
        this.timezoneAbbreviation = timezoneAbbreviation;
    }

    public CurrentUnits getCurrentUnits() {
        return currentUnits;
    }

    public void setCurrentUnits(CurrentUnits currentUnits) {
        this.currentUnits = currentUnits;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public static class CurrentUnits {

        @SerializedName("time")
        private String time;

        @SerializedName("interval")
        private String interval;

        @SerializedName("temperature_2m")
        private String temperature2m;

        @SerializedName("relative_humidity_2m")
        private String relativeHumidity2m;

        @SerializedName("apparent_temperature")
        private String apparentTemperature;

        @SerializedName("precipitation")
        private String precipitation;

        @SerializedName("weather_code")
        private String weatherCode;

        @SerializedName("cloud_cover")
        private String cloudCover;

        @SerializedName("wind_speed_10m")
        private String windSpeed10m;

        // Getters and Setters
        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public String getTemperature2m() {
            return temperature2m;
        }

        public void setTemperature2m(String temperature2m) {
            this.temperature2m = temperature2m;
        }

        public String getRelativeHumidity2m() {
            return relativeHumidity2m;
        }

        public void setRelativeHumidity2m(String relativeHumidity2m) {
            this.relativeHumidity2m = relativeHumidity2m;
        }

        public String getApparentTemperature() {
            return apparentTemperature;
        }

        public void setApparentTemperature(String apparentTemperature) {
            this.apparentTemperature = apparentTemperature;
        }

        public String getPrecipitation() {
            return precipitation;
        }

        public void setPrecipitation(String precipitation) {
            this.precipitation = precipitation;
        }

        public String getWeatherCode() {
            return weatherCode;
        }

        public void setWeatherCode(String weatherCode) {
            this.weatherCode = weatherCode;
        }

        public String getCloudCover() {
            return cloudCover;
        }

        public void setCloudCover(String cloudCover) {
            this.cloudCover = cloudCover;
        }

        public String getWindSpeed10m() {
            return windSpeed10m;
        }

        public void setWindSpeed10m(String windSpeed10m) {
            this.windSpeed10m = windSpeed10m;
        }
    }

    public static class Hourly {

        @SerializedName("time")
        private List<String> time;

        @SerializedName("temperature_2m")
        private List<Double> temperature2m;

        @SerializedName("relative_humidity_2m")
        private List<Integer> relativeHumidity2m;

        @SerializedName("apparent_temperature")
        private List<Double> apparentTemperature;

        @SerializedName("weather_code")
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

    public static class HourlyUnits {

        @SerializedName("time")
        private String time;

        @SerializedName("temperature_2m")
        private String temperature2m;

        @SerializedName("relative_humidity_2m")
        private String relativeHumidity2m;

        @SerializedName("apparent_temperature")
        private String apparentTemperature;

        @SerializedName("weather_code")
        private String weatherCode;

        // Getters and Setters
        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTemperature2m() {
            return temperature2m;
        }

        public void setTemperature2m(String temperature2m) {
            this.temperature2m = temperature2m;
        }

        public String getRelativeHumidity2m() {
            return relativeHumidity2m;
        }

        public void setRelativeHumidity2m(String relativeHumidity2m) {
            this.relativeHumidity2m = relativeHumidity2m;
        }

        public String getApparentTemperature() {
            return apparentTemperature;
        }

        public void setApparentTemperature(String apparentTemperature) {
            this.apparentTemperature = apparentTemperature;
        }

        public String getWeatherCode() {
            return weatherCode;
        }

        public void setWeatherCode(String weatherCode) {
            this.weatherCode = weatherCode;
        }
    }

    public static class Current {

        @SerializedName("time")
        private String time;

        @SerializedName("interval")
        private Integer interval;

        @SerializedName("temperature_2m")
        private Double temperature2m;

        @SerializedName("relative_humidity_2m")
        private Integer relativeHumidity2m;

        @SerializedName("apparent_temperature")
        private Double apparentTemperature;

        @SerializedName("precipitation")
        private Double precipitation;

        @SerializedName("weather_code")
        private Integer weatherCode;

        @SerializedName("cloud_cover")
        private Integer cloudCover;

        @SerializedName("wind_speed_10m")
        private Double windSpeed10m;

        @SerializedName("imagecode")
        private int imagecode;
        // Getters and Setters
        public Integer getImagecode(){return imagecode;}
        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public Integer getInterval() {
            return interval;
        }

        public void setInterval(Integer interval) {
            this.interval = interval;
        }

        public Double getTemperature2m() {
            return temperature2m;
        }

        public void setTemperature2m(Double temperature2m) {
            this.temperature2m = temperature2m;
        }

        public Integer getRelativeHumidity2m() {
            return relativeHumidity2m;
        }

        public void setRelativeHumidity2m(Integer relativeHumidity2m) {
            this.relativeHumidity2m = relativeHumidity2m;
        }

        public Double getApparentTemperature() {
            return apparentTemperature;
        }

        public void setApparentTemperature(Double apparentTemperature) {
            this.apparentTemperature = apparentTemperature;
        }

        public Double getPrecipitation() {
            return precipitation;
        }

        public void setPrecipitation(Double precipitation) {
            this.precipitation = precipitation;
        }

        public Integer getWeatherCode() {
            return weatherCode;
        }

        public void setWeatherCode(Integer weatherCode) {
            this.weatherCode = weatherCode;
        }

        public Integer getCloudCover() {
            return cloudCover;
        }

        public void setCloudCover(Integer cloudCover) {
            this.cloudCover = cloudCover;
        }

        public Double getWindSpeed10m() {
            return windSpeed10m;
        }

        public void setWindSpeed10m(Double windSpeed10m) {
            this.windSpeed10m = windSpeed10m;
        }
    }
}

package com.adinga.trigger_engine_service.api.dto;

import jakarta.validation.constraints.*;

public class GeoRuleDtos {

    public static class CreateReq {
        @NotNull public Long todoId;
        @NotBlank public String deviceId;
        @NotNull public Double lat;
        @NotNull public Double lng;
        @NotNull @Min(10) public Integer radiusM; // 최소 10m
        @Pattern(regexp = "ENTER|EXIT") public String when;
        public Boolean enabled = Boolean.TRUE;

        public Long getTodoId() { return todoId; }
        public void setTodoId(Long todoId) { this.todoId = todoId; }
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }
        public Double getLng() { return lng; }
        public void setLng(Double lng) { this.lng = lng; }
        public Integer getRadiusM() { return radiusM; }
        public void setRadiusM(Integer radiusM) { this.radiusM = radiusM; }
        public String getWhen() { return when; }
        public void setWhen(String when) { this.when = when; }
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    }

    public static class UpdateReq {
        public Double lat;
        public Double lng;
        public Integer radiusM;
        @Pattern(regexp = "ENTER|EXIT") public String when;
        public Boolean enabled;

        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }
        public Double getLng() { return lng; }
        public void setLng(Double lng) { this.lng = lng; }
        public Integer getRadiusM() { return radiusM; }
        public void setRadiusM(Integer radiusM) { this.radiusM = radiusM; }
        public String getWhen() { return when; }
        public void setWhen(String when) { this.when = when; }
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    }

    public static class Res {
        public Long id;
        public Long todoId;
        public String deviceId;
        public Double lat;
        public Double lng;
        public Integer radiusM;
        public String when;
        public Boolean enabled;

        public Long getId() { return id; }         public void setId(Long id) { this.id = id; }
        public Long getTodoId() { return todoId; } public void setTodoId(Long todoId) { this.todoId = todoId; }
        public String getDeviceId() { return deviceId; } public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public Double getLat() { return lat; }     public void setLat(Double lat) { this.lat = lat; }
        public Double getLng() { return lng; }     public void setLng(Double lng) { this.lng = lng; }
        public Integer getRadiusM() { return radiusM; } public void setRadiusM(Integer radiusM) { this.radiusM = radiusM; }
        public String getWhen() { return when; }   public void setWhen(String when) { this.when = when; }
        public Boolean getEnabled() { return enabled; } public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    }
}

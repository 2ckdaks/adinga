package com.adinga.notification_service.repository;

import com.adinga.notification_service.model.DevicePushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DevicePushTokenRepository extends JpaRepository<DevicePushToken, Long> {
    Optional<DevicePushToken> findByDeviceId(String deviceId);
    List<DevicePushToken> findAllByDeviceIdIn(Collection<String> deviceIds);
}

package com.github.juliusd.ueberboeseapi.service;

import com.github.juliusd.ueberboeseapi.generated.dtos.DeviceEventApiDto;
import com.github.juliusd.ueberboeseapi.generated.dtos.DeviceEventsRequestApiDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventStorageService {

  private final EventsProperties properties;

  private final Map<String, List<DeviceEventApiDto>> eventsByDevice = new ConcurrentHashMap<>();

  public void storeEvent(String deviceId, DeviceEventsRequestApiDto event) {
    List<DeviceEventApiDto> events =
        eventsByDevice.computeIfAbsent(deviceId, k -> new ArrayList<>());

    synchronized (events) {
      events.addAll(event.getPayload().getEvents());

      while (events.size() > properties.maxEventsPerDevice()) {
        events.removeFirst();
      }
    }
  }

  public List<DeviceEventApiDto> getEventsForDevice(String deviceId) {
    var events = eventsByDevice.get(deviceId);
    if (events == null) {
      return new ArrayList<>();
    }

    synchronized (events) {
      return new ArrayList<>(events);
    }
  }

  public int getEventCount(String deviceId) {
    var events = eventsByDevice.get(deviceId);
    if (events == null) {
      return 0;
    }

    synchronized (events) {
      return events.size();
    }
  }

  public void clearAllEvents() {
    eventsByDevice.clear();
  }
}

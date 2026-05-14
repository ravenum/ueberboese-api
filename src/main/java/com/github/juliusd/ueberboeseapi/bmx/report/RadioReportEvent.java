package com.github.juliusd.ueberboeseapi.bmx.report;

public record RadioReportEvent(
    String timeStamp,
    EventType eventType,
    String reason,
    String reasonSubCode,
    Integer timeIntoTrack,
    Integer playbackDelay) {

  public enum EventType {
    START,
    STOP,
    PAUSE,
    RESUME,
    TIMED
  }
}

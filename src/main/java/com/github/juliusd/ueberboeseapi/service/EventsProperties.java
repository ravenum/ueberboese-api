package com.github.juliusd.ueberboeseapi.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ueberboese.events")
public record EventsProperties(int maxEventsPerDevice) {}

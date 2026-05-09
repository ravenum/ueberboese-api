package com.github.juliusd.ueberboeseapi;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NoiseController {

  @GetMapping("/")
  public ResponseEntity<Void> index() {
    return ResponseEntity.ok().build();
  }

  @GetMapping("/favicon.ico")
  public ResponseEntity<byte[]> favicon() throws IOException {
    ClassPathResource resource = new ClassPathResource("static/icons/favicon.ico");
    byte[] content;
    try (InputStream inputStream = resource.getInputStream()) {
      content = inputStream.readAllBytes();
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("image/x-icon"));
    return ResponseEntity.ok().headers(headers).body(content);
  }
}

package com.github.juliusd.ueberboeseapi;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class NoiseControllerTest extends TestBase {

  @Autowired private MockMvc mockMvc;

  private WireMockServer wireMockServer;

  @BeforeEach
  void setUpWireMock() {
    wireMockServer = new WireMockServer(options().port(8089));
    wireMockServer.start();
  }

  @AfterEach
  void tearDown() {
    if (wireMockServer != null) {
      wireMockServer.stop();
    }
  }

  @Test
  void rootShouldReturn200WithoutProxy() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().isOk());

    wireMockServer.verify(0, getRequestedFor(urlEqualTo("/")));
  }

  @Test
  void faviconShouldReturnIcoWithoutProxy() throws Exception {
    byte[] body =
        mockMvc
            .perform(get("/favicon.ico"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

    assertThat(body).isNotEmpty();
    // ICO magic: first 4 bytes are 00 00 01 00
    assertThat(body[0]).isEqualTo((byte) 0x00);
    assertThat(body[1]).isEqualTo((byte) 0x00);
    assertThat(body[2]).isEqualTo((byte) 0x01);
    assertThat(body[3]).isEqualTo((byte) 0x00);

    wireMockServer.verify(0, getRequestedFor(urlEqualTo("/favicon.ico")));
  }
}

package eu.europa.ec.dgc.verifier.restapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"dgc.context={\"testContext\": true}"})
class ContextControllerWithEnvironmentIntegrationTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void requestContext() throws Exception {
        mockMvc.perform(get("/context"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("{\"testContext\": true}"));
    }




}


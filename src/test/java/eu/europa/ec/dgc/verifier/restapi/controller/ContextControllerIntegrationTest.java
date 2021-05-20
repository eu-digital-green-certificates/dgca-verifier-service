package eu.europa.ec.dgc.verifier.restapi.controller;

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ContextControllerIntegrationTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void requestContext() throws Exception {
        mockMvc.perform(get("/context"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(result -> assertContextStrEqualFile(result));
    }

    private void assertContextStrEqualFile(MvcResult result) throws UnsupportedEncodingException {
        String resultContext = result.getResponse().getContentAsString();
        Resource resource = new ClassPathResource("/static/context.json");
        String fileContext = null;
        try {
            fileContext = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Assertions.fail(e);
        }
        Assertions.assertEquals(resultContext, fileContext);
    }

}


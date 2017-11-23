package com.brave;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.RequestDispatcher;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Created by junzhang on 23/11/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiDocumentation {
    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation)).build();
    }


    @Test
    public void errorExample() throws Exception {
        this.mockMvc
            .perform(get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI,
                    "/notes")
                .requestAttr(RequestDispatcher.ERROR_MESSAGE,
                    "The tag 'http://localhost:8080/tags/123' does not exist"))
            .andDo(print()).andExpect(status().isBadRequest())
            .andExpect(jsonPath("error", is("Bad Request")))
            .andExpect(jsonPath("timestamp", is(notNullValue())))
            .andExpect(jsonPath("status", is(400)))
            .andExpect(jsonPath("path", is(notNullValue())))
            .andDo(document("error-example",
                responseFields(
                    fieldWithPath("error").description("The HTTP error that occurred, e.g. `Bad Request`"),
                    fieldWithPath("message").description("A description of the cause of the error"),
                    fieldWithPath("path").description("The path to which the request was made"),
                    fieldWithPath("status").description("The HTTP status code, e.g. `400`"),
                    fieldWithPath("timestamp").description("The time, in milliseconds, at which the error occurred"))));
    }

    @Test
    public void indexExample() throws Exception {
        this.mockMvc.perform(get("/").accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("hello")))
            .andDo(document("index-example",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                )
            );

    }

    @Test
    public void demoTest() throws  Exception {
        this.mockMvc.perform(get("/demo"))
            .andExpect(status().isOk())
            .andDo(document("demo-example",
                    responseFields(
                        subsectionWithPath("name").description("姓名")
                    )
                )
            );
    }
}

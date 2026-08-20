package com.sharib.nttuc1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
@AutoConfigureMockMvc
class Nttuc1ApplicationTests {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MockMvc mockMvc;
	String fileUploadEndPoint = "/api/docs/upload";

	@Test
	void contextLoads() {
	}

	@Test
	void shouldUploadPdfSuccessfully() throws Exception {
		log.info("shouldUploadPdfSuccessfully");
		MockMultipartFile mockPdfFile = new MockMultipartFile(
				"file", // parameter name in controller @RequestParam("file")
				"test-document.pdf", // file name
				MediaType.APPLICATION_PDF_VALUE, // Content type
				"%PDF-1.5 dummy pdf content".getBytes() // Dummy PDF content bytes
		);

		String responseBody = mockMvc.perform(multipart(fileUploadEndPoint)
						.file(mockPdfFile))
				.andExpect(status().isOk())
				.andReturn() // Completes execution and returns MvcResult
				.getResponse()
				.getContentAsString();
		log.info("responseBody = " + responseBody);
	}

	@Test
	void shouldRejectNonPdfNonTextFile() throws Exception {
		log.info("shouldRejectNonPdfNonTextFile");
		// Create a mock PNG file instead of a PDF
		MockMultipartFile mockPNGFile = new MockMultipartFile(
				"file",
				"pngfile.png",
				MediaType.IMAGE_PNG_VALUE,
				"fake-png-bytes".getBytes());

		String responseBody = mockMvc.perform(multipart(fileUploadEndPoint)
						.file(mockPNGFile))
				.andExpect(status().isUnsupportedMediaType())
				.andReturn() // Completes execution and returns MvcResult
				.getResponse()
				.getContentAsString();
		log.info("responseBody = " + responseBody);
	}
}

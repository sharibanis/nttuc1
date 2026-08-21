package com.sharib.nttuc1;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Nttuc1ApplicationTests {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MockMvc mockMvc;
	String fileUploadEndPoint = "/api/docs/upload";
	String queryEndPoint = "/api/docs/query/{text}";

	@Order(1)
	@Test
	void contextLoads() {
	}

	@Order(2)
	@Test
	void shouldUploadPdfSuccessfully() throws Exception {
		log.info("shouldUploadPdfSuccessfully");
		PDDocument document = null;
		File targetFile = null;
		FileInputStream fis = null;
		String fileText = "Please honour my insurance claim for MediShield Life poilcy.";
		// 1. Create a blank PDF document
		try  {
			document = new PDDocument();
			// 2. Create and add a blank page
			PDPage page = new PDPage();
			document.addPage(page);

			// 3. Initialize a content stream to write to the page
			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

				// 4. Set up the text block
				contentStream.beginText();

				// Set font and font size using modern Standard14Fonts API
				contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);

				// Set text position (X, Y coordinates from bottom-left corner)
				//contentStream.newLineAtOffset(100, 700);

				// Show the text string
				contentStream.showText(fileText);

				// End the text block
				contentStream.endText();
			}

			// 5. Save the final document to a file
			targetFile = new File("output.pdf");
			if(targetFile.exists()) {
				targetFile.delete();
			}
			document.save(targetFile);
			log.info("PDF created successfully. {}", targetFile.getAbsolutePath());
			if (targetFile != null) {
				fis = new FileInputStream(targetFile);
				MockMultipartFile mockPdfFile = new MockMultipartFile(
						"file", // parameter name in controller @RequestParam("file")
						"TestDocument.pdf", // file name
						MediaType.APPLICATION_PDF_VALUE, // Content type
						fis.readAllBytes() // Dummy PDF content bytes
				);

				String responseBody = mockMvc.perform(multipart(fileUploadEndPoint)
								.file(mockPdfFile))
						.andExpect(status().isOk())
						.andReturn() // Completes execution and returns MvcResult
						.getResponse()
						.getContentAsString();
				log.info("responseBody = " + responseBody);
			}
		} catch (IOException e) {
			log.info("Error creating PDF. ", e);
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (document != null) {
				document.close();
			}
		}
	}

	@Order(3)
	@Test
	void shouldUploadTextSuccessfully() throws Exception {
		log.info("shouldUploadTextSuccessfully");
		String targetFileName = "output.txt";
		File targetFile = new File(targetFileName);
		if(targetFile.exists()) {
			targetFile.delete();
		}
		String fileText = "Please honour my insurance claim for MediShield Life poilcy.";
		Files.writeString(Path.of(targetFileName), fileText);
		FileInputStream fis = new FileInputStream(targetFile);
		MockMultipartFile mockTextFile = new MockMultipartFile(
				"file", // parameter name in controller @RequestParam("file")
				targetFileName, // file name
				MediaType.TEXT_PLAIN_VALUE, // Content type
				fis.readAllBytes() // Dummy text content bytes
		);

		String responseBody = mockMvc.perform(multipart(fileUploadEndPoint)
						.file(mockTextFile))
				.andExpect(status().isOk())
				.andReturn() // Completes execution and returns MvcResult
				.getResponse()
				.getContentAsString();
		log.info("responseBody = " + responseBody);
	}

	@Order(4)
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

	@Order(5)
	@Test
	void shouldRejectEmptyFile() throws Exception {
		log.info("shouldRejectEmptyFile");
		// Create a mock empty file
		MockMultipartFile mockEmptyFile = new MockMultipartFile(
				"file",
				"output.txt",
				MediaType.TEXT_PLAIN_VALUE,
				new byte[0]);

		String responseBody = mockMvc.perform(multipart(fileUploadEndPoint)
						.file(mockEmptyFile))
				.andExpect(status().isBadRequest())
				.andReturn() // Completes execution and returns MvcResult
				.getResponse()
				.getContentAsString();
		log.info("responseBody = " + responseBody);
	}

	@Order(6)
	@Test
	void shouldRejectLargeFile() throws Exception {
		log.info("shouldRejectLargeFile");
		// Create a mock large file
		byte[] largeFileContent = new byte[1024 * 1024];
		MockMultipartFile mockLargeFile = new MockMultipartFile(
				"file",
				"output.txt",
				MediaType.TEXT_PLAIN_VALUE,
				largeFileContent);

		String responseBody = mockMvc.perform(multipart(fileUploadEndPoint)
						.file(mockLargeFile))
				.andExpect(status().isBadRequest())
				.andReturn() // Completes execution and returns MvcResult
				.getResponse()
				.getContentAsString();
		log.info("responseBody = " + responseBody);
	}

	@Order(7)
	@Test
	void shouldReturnQueryString() throws Exception {
		String springAIQuery = System.getProperty("spring.ai.query");
		if (springAIQuery == null) {
			springAIQuery = "Spring AI Query";
		}
		log.info("shouldReturnQueryString: springAIQuery: " + springAIQuery);
		// Create a mock PNG file instead of a PDF
		String responseBody = mockMvc.perform(get(queryEndPoint, springAIQuery))
				.andExpect(status().isOk())
				.andReturn() // Completes execution and returns MvcResult
				.getResponse()
				.getContentAsString();
		log.info("responseBody = " + responseBody);
	}
}

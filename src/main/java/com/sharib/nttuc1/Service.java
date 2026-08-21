package com.sharib.nttuc1;

import org.apache.tika.Tika;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.stereotype.Service
public class Service {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private SimpleVectorStore vectorStore;
	private ChatModel chatModel;

	//private Repository repository;

	public Service(SimpleVectorStore vectorStore, ChatModel chatModel) {
		this.vectorStore = vectorStore;
		this.chatModel = chatModel;
	}

	void uploadDocument(MultipartFile file) throws Exception {
		log.info("uploadDocument : " + file.getOriginalFilename());
		Document document = null;
		String contentType = file.getContentType();
		String extractedText = null;
		PDDocument pdfDocument = new PDDocument();
		Tika tika = new Tika();
		boolean isPdf = Objects.equals(tika.detect(file.getBytes()), "application/pdf");
		boolean isText = Objects.equals(tika.detect(file.getBytes()), "text/plain");
		if (isPdf) {
			log.info("uploadDocument : isPdf : " + isPdf);
			try {
				if (contentType.equals(MediaType.APPLICATION_PDF_VALUE)) {
					pdfDocument = Loader.loadPDF(file.getBytes());
					PDFTextStripper pdfStripper = new PDFTextStripper();
					extractedText = pdfStripper.getText(pdfDocument);
					log.info("extractedText  : " + extractedText);
					document = new Document(extractedText);
				}
				List<Document> documents = List.of(document);
				log.info("Saving {} on SimpleVectorStore...", file.getOriginalFilename());
				vectorStore.add(documents);
			} finally {
				if (pdfDocument != null) {
					pdfDocument.close();
				}
			}
		} else if (isText) {
			log.info("uploadDocument : isText : " + isText);
			extractedText = new String(file.getBytes());
			document = new Document(extractedText);
			List<Document> documents = List.of(document);
			log.info("Saving {} on SimpleVectorStore...", file.getOriginalFilename());
			vectorStore.add(documents);
		} else {
			String msg = "Not a valid PDF or text file: " + file.getOriginalFilename();
			log.error(msg);
			throw new Exception(msg);
		}
	}

	public String queryDocuments(String text) {
		log.info("queryDocuments : " + text);
		//check for empty library
		String responseString = null;
		SearchRequest request = SearchRequest.builder()
				.query("Spring AI")
				.topK(1)
				.build();
		List<Document> docs = vectorStore.similaritySearch(request);
		if (docs.isEmpty()) {
			responseString = "The library is empty. Please add documents.";
			log.info("queryDocuments : " + responseString);
			return responseString;
		} else {
			log.info("queryDocuments : Library size = " + docs.size());
		}
		ChatResponse response = ChatClient.builder(chatModel)
				.build().prompt()
				.advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
				.user(text)
				.call()
				.chatResponse();
		responseString = response.getResult().getOutput().getText();
		log.info("queryDocuments : responseString : " + responseString);
		return responseString;
	}
}

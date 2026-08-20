package com.sharib.nttuc1;

import com.openai.models.vectorstores.VectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ai.document.Document;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.stereotype.Service
public class Service {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private SimpleVectorStore vectorStore;

	//private Repository repository;

	public Service(SimpleVectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}

	void uploadDocument(MultipartFile file) throws Exception {
		log.info("uploadDocument : " + file.getName());
		Document document = null;
		String contentType = file.getContentType();
		if (contentType.equals(MediaType.APPLICATION_PDF_VALUE)) {
			Resource pdfResource  = new InputStreamResource(file.getInputStream());
			Media pdfMedia = new Media(MimeType.valueOf(MediaType.APPLICATION_PDF_VALUE),
					pdfResource);
			document = new Document(pdfMedia, Map.of("type", "pdf"));
		}
		List<Document> documents = new ArrayList<Document>();
		documents.add(document);
		vectorStore.add(documents);
	}
}

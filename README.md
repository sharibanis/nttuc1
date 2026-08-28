# Use Case 1 — Document Ingestion & Retrieval Service

###  Design and user guide

* `SpringBootApplication` with logging 
* Using local Ollama and embedded SimpleVectorStore for faster setup and startup
* `@PostMapping uploadDocument(@RequestParam("file") MultipartFile file)` to upload files
* Checking for text and PDF files. Only text and PDF files allowed to be uploaded.
* Extracted text from PDF files using Apache PDFBox and stored in SimpleVectorStore
* Text files are much smaller in size and faster to RAG process than PDF 
* In Retrieval-Augmented Generation (RAG), using plain text files (.txt) is fast and 
* clean because the data is already flat and ready to chunk.
* `@Test shouldUploadPdfSuccessfully()` to test for PDF file upload
* `@Test shouldUploadTextSuccessfully()` to test for text file upload
* `@Test shouldRejectNonPdfNonTextFile()` to test for non PDF and non text file upload
* `@Test shouldRejectEmptyFile()` to test for empty file upload
* `@Test shouldRejectLargeFile()` to test for large file upload (> 1MB)
* Created PDF files using Apache PDFBox
* Decided to run local ollama mistral LLM due to API keys (not free and models not easily accessible)
* Running ollama mistral in Docker using docker-compose (this setup took some time due docker-compose issues)
* `docker exec -it ollama ollama pull mistral` to pull ollama mistral model
* `@GetMapping queryDocuments(@PathVariable String queryText)` to query documents
* `@Test shouldReturnQueryString()` tests the RAG LLM output
* The query string can be passed using `mvn test -Dspring.ai.query="Spring AI Query"`
* File upload end point is `/api/docs/upload`
* AI query end point is `/api/docs/query/{queryText}`;
* Using Java 21/SpringA1 2.0.0

## Instructions
1. Install and run Docker Desktop
2. Run the `nttuc1` app using `mvn clean spring-boot:run`. This will create ollama docker image using docker compose.
3. May need to pull ollama mistral model using `docker exec -it ollama ollama pull mistral`
4. Test using query string `mvn test -Dspring.ai.query="Spring AI Query"`

## Testing results
<img width="1920" height="1128" alt="image" src="https://github.com/user-attachments/assets/e606714c-62ab-424f-b59f-4e6545a2a88d" />

## DESIGN
How to keep answer quality from degrading as the document library grows to tens of thousands of files.

**Key Strategies for Scaling**

**Smart Chunking**: Break large files into small, logical sections of 300 to 500 tokens so search tools find exact passages fast.

**Hybrid Search**: Combine keyword search (BM25) with vector search to match exact product codes and catch semantic meaning.

**Metadata Tagging**: Add tags like date, author, and category to filter out irrelevant files before searching.

**Cross-Encoder Re-ranking**: Use a second-stage re-ranker to score and sort the top 50 search results by true relevance before sending them to the language model.

**Regular Evaluation:** Run automated test sets every week to track retrieval score (Hit Rate) and answer correctness as data grows.


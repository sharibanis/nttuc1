# Use Case 1 — Document Ingestion & Retrieval Service

###  Design and user guide

* `SpringBootApplication` with logging 
* Using local Ollama and embedded SimpleVectorStore for faster setup and startup
* `@PostMapping uploadDocument(@RequestParam("file") MultipartFile file)` to upload files
* Checking for text and PDF files
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
* Decided to run local LLM due to API keys (not free and models not easily accessible)
* Running ollama mistral in Docker using docker-compose (this setup took some time due docker-compose issues)
* `docker exec -it ollama ollama pull mistral` to pull ollama mistral model
* Using Java 21
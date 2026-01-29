package ma.youhad.aiagent.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Component;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Component
public class DocumentIndexer {
    @Value("classpath:/pdfs/ticket.pdf")
    private Resource pdfResource;
   //we suppose the file is static
    @Value("store.json")
    private String fileStore;
    // we use here a file instead of vector database like postgres
    private SimpleVectorStore simpleVectorStore;
    /*private EmbeddingModel embeddingModel;*/

    public DocumentIndexer(SimpleVectorStore simpleVectorStore) {
        this.simpleVectorStore = simpleVectorStore;
        /*this.embeddingModel = embeddingModel;*/
    }

     // by default,  the embedding model of open AI  to do the embedding is injected
    /*public SimpleVectorStore getVectorStore(){
       //SimpleVectorStore store the vector in a specific file
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        // Create an empty vector store, ready to store vectors.
        Path path = Path.of("src","main","resources","store");
        File file = new File(path.toFile(), fileStore);
        if(!file.exists()){
            PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(pdfResource);
            //  Read the pdf file
            List<Document> documents =pagePdfDocumentReader.get();
            // Get enable us to get a list of documents  and each document is a page that contain just text
            // The get() method is what actually performs the reading and returns that list.
            TextSplitter textSplitter = new TokenTextSplitter();
            // Splits each document's text into smaller chunks based on token count
            List<Document> chunks =textSplitter.apply(documents);
            simpleVectorStore.add(chunks);
           //  Pass each chunk’s text to our EmbeddingModel (e.g., OpenAI embeddings).
            // Store the resulting vector representation along with the chunk’s original
            // text and metadata inside the vector store.
            simpleVectorStore.save(file);
            // Before saving --> All the chunks (from simpleVectorStore.add(chunks)) live only in memory.
            // When we call .save(file) -->  The store’s data (embeddings + original text + metadata) is written to the file we specify (store.json in our case).
        }else {
            simpleVectorStore.load(file);
        }
        return simpleVectorStore;
    }*/

    // ✅ Add this method to load the vector store at startup
    // runs automatically after Spring creates the bean.
    @PostConstruct
    public void init() {
        Path path = Path.of("src", "main", "resources", "store");
        File file = new File(path.toFile(), fileStore);
        if (file.exists()) {
            simpleVectorStore.load(file); // Load existing vectors into memory
        }
    }

    public void loadFile(MultipartFile pdfFile){
        Path path = Path.of("src","main","resources","store");
        File file = new File(path.toFile(), fileStore);
        if(file.exists()){
            this.simpleVectorStore.load(file);
        }
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(pdfFile.getResource());
        List<Document> documents =pagePdfDocumentReader.get();
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> chunks =textSplitter.apply(documents);
        simpleVectorStore.add(chunks);
        simpleVectorStore.save(file);
    }


}

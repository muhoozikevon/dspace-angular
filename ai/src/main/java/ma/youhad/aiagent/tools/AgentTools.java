package ma.youhad.aiagent.tools;
import org.springframework.ai.vectorstore.VectorStore;
// Import Spring AI's VectorStore interface, which provides methods like similaritySearch() to search vectors.

import ma.youhad.aiagent.records.Employee;
import org.springframework.ai.tool.annotation.Tool;
// Import Spring AI's @Tool annotation to mark methods as AI tools that can be called by the agent.

import org.springframework.ai.tool.annotation.ToolParam;
// Import the @ToolParam annotation to describe parameters of your AI tool methods.

import org.springframework.ai.vectorstore.SearchRequest;
// Import SearchRequest class used to build a search query for the VectorStore.

import org.springframework.stereotype.Component;
// Import Spring's @Component annotation to make this class a Spring bean, so it can be injected automatically.

import org.springframework.ai.document.Document;
// Import Spring AI's Document class, which represents text documents returned from the vector store search.

import java.util.List;
// Import List interface to hold collections of elements.

import java.util.stream.Collectors;
// Import Collectors to transform streams into collections (like List).

@Component
// Marks this class as a Spring-managed component so it can be automatically detected and instantiated.
public class AgentTools {

    private VectorStore vectorStore;
    // A field to hold the vector store instance, used for performing similarity searches.

    public AgentTools(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        // Constructor that receives a VectorStore instance (injected by Spring) and assigns it to the field.
    }

    @Tool(description = "Get info about an employee (name, salary, seniority)")
    // Marks this method as an AI tool with a description that will show up in the agent.
    public Employee getEmployee(@ToolParam(description = "The employee's name") String employeeName) {
        // Method parameter is annotated with @ToolParam to describe it in AI tool metadata.
        return new Employee(
                employeeName, 12345, 5
                // Returns a new Employee object. Here salary=12345 and seniority=5 are hardcoded for example purposes.
        );
    }

    @Tool(description = "Search Information in a file ")
    // Marks this method as an AI tool that can search documents using the vector store.
    public List<String> searchContext(String query) {
        // Accepts a query string to search for.
        List<Document> documents = vectorStore.similaritySearch(
                // Calls similaritySearch() on the VectorStore to find relevant documents.
                SearchRequest.builder()
                        // Creates a SearchRequest object using builder pattern.
                        .query(query)
                        // Sets the search query.
                        .topK(4)
                        // Limits the search to the top 4 most similar documents.
                        .build()
                // Builds the SearchRequest object.
        );
        return documents.stream()
                // Converts the List<Document> into a stream for processing.
                .map(Document::getText)
                // Extracts the text content from each Document object.
                .collect(Collectors.toList());
        // Collects the text into a List<String> to return.
    }
}

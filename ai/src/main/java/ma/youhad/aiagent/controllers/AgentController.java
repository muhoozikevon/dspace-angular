package ma.youhad.aiagent.controllers;


import ma.youhad.aiagent.agents.AIAgent;
import ma.youhad.aiagent.rag.DocumentIndexer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin("*")
public class AgentController {
    private AIAgent agent;
    private DocumentIndexer documentIndexer;

    public AgentController(AIAgent agent, DocumentIndexer documentIndexer) {
        this.agent = agent;
        this.documentIndexer = documentIndexer;
    }


    @GetMapping(value = "/askAgent", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> askAgent(@RequestParam(defaultValue = "Hello") String query) {
        return agent.onQuery(query);
    }

    @PostMapping(value = "/loadFile" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void loadFile(@RequestPart("file") MultipartFile file) {
        documentIndexer.loadFile(file);

    }
}

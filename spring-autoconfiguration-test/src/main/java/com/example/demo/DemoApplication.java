package com.example.demo;

import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@SpringBootApplication
@RestController
public class DemoApplication {

    @Value( "${compute-project}" )
    private String project;

    @Value( "${compute-zone}" )
    private String zone;

    @Autowired
    private LanguageServiceClient languageAutoClient;

    @Autowired
    private InstancesClient instancesAutoClient;

    @GetMapping("/compute")
    void compute() throws IOException {

        System.out.printf("Listing instances from %s in %s:", project, zone);
        for (Instance zoneInstance : instancesAutoClient.list(project, zone).iterateAll()) {
            System.out.println(zoneInstance.getName());
        }
        System.out.println("####### Listing instances complete #######");
    }

    @GetMapping("/language")
    String language() throws IOException {

        // The text to analyze
        String text = "Hello, world!";
        Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        // Detects the sentiment of the text
        Sentiment sentiment = languageAutoClient.analyzeSentiment(doc).getDocumentSentiment();
        String result = String.format("Text: %s%n Sentiment: %s, %s%n",
                text, sentiment.getScore(), sentiment.getMagnitude());
        System.out.println(result);
        return result;
    }

    @GetMapping("/")
    String hello() throws IOException {
        return "Hello!";
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

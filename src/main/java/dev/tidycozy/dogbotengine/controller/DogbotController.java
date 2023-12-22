package dev.tidycozy.dogbotengine.controller;

import dev.tidycozy.dogbotengine.model.DogbotRequest;
import dev.tidycozy.dogbotengine.service.NLPService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/speak")
public class DogbotController {

    NLPService nlpService;

    public DogbotController(NLPService nlpService) {
        this.nlpService = nlpService;
    }

    @PostMapping
    public String getResponse(@RequestBody DogbotRequest dogbotRequest) {
        return nlpService.getDogbotAnswer(dogbotRequest.getPhrase());
    }

}

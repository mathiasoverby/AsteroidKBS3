package dk.sdu.cbse.scoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController

public class ScoringService {
    private int score = 0;

    public static void main(String[] args) {
        SpringApplication.run(ScoringService.class, args);
    }


    @GetMapping("/score")
    public int getScore() {
        return score;
    }


    @PostMapping("/score")
    public int updateScore(@RequestParam(defaultValue = "0") int increment) {
        score += increment;
        return score;
    }


    @DeleteMapping("/score")
    public int resetScore() {
        score = 0;
        return score;
    }
}
package agilisys.conge;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableProcessApplication
@EntityScan(basePackages = "agilisys.conge.entity")
public class CongeBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(CongeBackApplication.class, args);
    }

}

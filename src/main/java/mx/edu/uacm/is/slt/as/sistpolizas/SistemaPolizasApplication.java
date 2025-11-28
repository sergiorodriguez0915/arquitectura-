package mx.edu.uacm.is.slt.as.sistpolizas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "mx.edu.uacm.is.slt.as.sistpolizas.repository")
@EntityScan("mx.edu.uacm.is.slt.as.sistpolizas.model")
public class SistemaPolizasApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaPolizasApplication.class, args);
	}

}

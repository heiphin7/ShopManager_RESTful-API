package api.shopmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class ShopmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopmanagerApplication.class, args);
	}

}

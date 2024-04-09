package com.msme.bank;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "Kariuki and Sons Bank Pvt Ltd",
        description = "Backend Rest APIs for KnS Bank",
        version = "v1.O",
        contact = @Contact(
                name = "Kariuki Bonface",
                email = "bonface.karau@programmer.net",
                url = "https://github.com/kariukiB/knsbank"
        ),
        license = @License(
                name = "Kariuki and Sons Bank Pvt Ltd",
                url = "https://github.com/kariukiB/knsbank"
        )
))
public class KariukiBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(KariukiBankApplication.class, args);
    }

}

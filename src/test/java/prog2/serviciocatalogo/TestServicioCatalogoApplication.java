package prog2.serviciocatalogo;

import org.springframework.boot.SpringApplication;

public class TestServicioCatalogoApplication {

    public static void main(String[] args) {
        SpringApplication.from(ServicioCatalogoApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			Controller controller = ctx.getBean(Controller.class);
			Scanner scanner = new Scanner(System.in);

			while (true) {
				System.out.println("Seleccione una opción:");
				System.out.println("1. Buscar un libro");
				System.out.println("2. Ver historial de búsquedas");
				System.out.println("3. Lista de autores registrados");
				System.out.println("4. Filtrar libros por idioma");
				System.out.println("5. Salir");

				int option = scanner.nextInt();
				scanner.nextLine(); // Consume newline

				if (option == 1) {
					controller.searchBookFromConsole();
				} else if (option == 2) {
					List<String> history = controller.getSearchHistory();
					System.out.println("Historial de búsquedas:");
					history.forEach(System.out::println);
				} else if (option == 3) {
					List<String> authors = controller.getAuthors();
					System.out.println("Autores registrados:");
					authors.forEach(System.out::println);
				} else if (option == 4) {
					System.out.print("Ingrese el idioma (eng para inglés, spa para español): ");
					String language = scanner.nextLine();
					List<GutendexService.Book> books = controller.getBooksByLanguage(language);
					System.out.println("Libros en " + language + ":");
					books.forEach(System.out::println);
				} else if (option == 5) {
					break;
				} else {
					System.out.println("Opción no válida. Intente de nuevo.");
				}
			}
		};
	}
}

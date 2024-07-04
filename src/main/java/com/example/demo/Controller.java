package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping("/gutendex")
public class Controller {
    private final GutendexService gutendexService;

    @Autowired
    public Controller(GutendexService gutendexService) {
        this.gutendexService = gutendexService;
    }

    @GetMapping("/books")
    public String getBooks(@RequestParam String query) {
        return gutendexService.fetchBooks(query);
    }

    @GetMapping("/search-history")
    public List<String> getSearchHistory() {
        return gutendexService.getSearchHistory();
    }

    @GetMapping("/authors")
    public List<String> getAuthors() {
        return gutendexService.getAuthorsList();
    }

    @GetMapping("/books-by-language")
    public List<GutendexService.Book> getBooksByLanguage(@RequestParam String language) {
        return gutendexService.filterBooksByLanguage(language);
    }

    // Método para leer desde la consola
    public void searchBookFromConsole() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el nombre del libro: ");
        String bookName = scanner.nextLine();
        String result = getBooks(bookName);
        System.out.println("Resultado de la búsqueda: ");
        System.out.println(result);
    }
}


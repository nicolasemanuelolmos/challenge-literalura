package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GutendexService {
    private final HttpClient httpClient;
    private final List<String> searchHistory;
    private final Set<String> currentAuthors; // Conjunto de autores para evitar duplicados
    private final List<Book> books; // Lista de libros buscados

    public GutendexService() {
        this.httpClient = HttpClient.newHttpClient();
        this.searchHistory = new ArrayList<>();
        this.currentAuthors = new HashSet<>();
        this.books = new ArrayList<>();
    }

    public String fetchBooks(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String uri = "https://gutendex.com/books/?search=" + encodedQuery;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        // Limpiar la lista de autores y libros para la búsqueda actual
        currentAuthors.clear();
        books.clear();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response: " + response.body());  // Logging the response

            // Procesar la respuesta y extraer autores y libros
            processResponse(response.body());

            // Almacenar la búsqueda en la lista
            searchHistory.add(query);

            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void processResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode booksNode = rootNode.path("results");

            for (JsonNode bookNode : booksNode) {
                String title = bookNode.path("title").asText();
                JsonNode authorsNode = bookNode.path("authors");
                JsonNode languagesNode = bookNode.path("languages");
                String language = languagesNode.size() > 0 ? languagesNode.get(0).asText() : "unknown"; // Asume que el primer idioma es el relevante
                List<String> authors = new ArrayList<>();

                for (JsonNode authorNode : authorsNode) {
                    String authorName = authorNode.path("name").asText();
                    authors.add(authorName);
                    // Agregar autor al conjunto de autores de la búsqueda actual
                    currentAuthors.add(authorName);
                }

                books.add(new Book(title, authors, language));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getSearchHistory() {
        return searchHistory;
    }

    public List<String> getAuthorsList() {
        return new ArrayList<>(currentAuthors);
    }

    public List<Book> filterBooksByLanguage(String language) {
        return books.stream()
                .filter(book -> book.getLanguage().equalsIgnoreCase(language))
                .collect(Collectors.toList());
    }

    // Clase interna para representar un libro
    public static class Book {
        private String title;
        private List<String> authors;
        private String language;

        public Book(String title, List<String> authors, String language) {
            this.title = title;
            this.authors = authors;
            this.language = language;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getAuthors() {
            return authors;
        }

        public String getLanguage() {
            return language;
        }

        @Override
        public String toString() {
            return "Title: " + title + ", Authors: " + authors + ", Language: " + language;
        }
    }
}
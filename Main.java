import java.io.*;
import java.util.*;

class PageReader {
    public static String readPage(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public static Set<String> readExcludeWords(String fileName) throws IOException {
        Set<String> excludeWords = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String word;
            while ((word = br.readLine()) != null) {
                excludeWords.add(word.trim().toLowerCase());
            }
        }
        return excludeWords;
    }
}

class WordIndex {
    private String word;
    private Set<Integer> pages;

    public WordIndex(String word) {
        this.word = word;
        this.pages = new TreeSet<>();
    }

    public String getWord() {
        return word;
    }

    public void addPage(int pageNumber) {
        pages.add(pageNumber);
    }

    public Set<Integer> getPages() {
        return pages;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(word).append(" : ");
        for (int page : pages) {
            sb.append(page).append(", ");
        }
        // Remove the trailing comma and space
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}

class IndexGenerator {
    private Map<String, WordIndex> index;

    public IndexGenerator() {
        this.index = new TreeMap<>();
    }

    public void generateIndex(String[] pageFiles, String excludeFile) throws IOException {
        Set<String> excludeWords = PageReader.readExcludeWords(excludeFile);

        for (int i = 0; i < pageFiles.length; i++) {
            String pageContent = PageReader.readPage(pageFiles[i]);
            String[] words = pageContent.split("\\W+"); // Split by non-word characters

            for (String word : words) {
                word = word.toLowerCase();
                if (!excludeWords.contains(word)) {
                    if (!index.containsKey(word)) {
                        index.put(word, new WordIndex(word));
                    }
                    index.get(word).addPage(i + 1); // Page numbers are 1-based
                }
            }
        }
    }

    public void writeIndex(String outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (WordIndex wordIndex : index.values()) {
                writer.write(wordIndex.toString());
                writer.newLine();
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        String[] pageFiles = {"Page1.txt", "Page2.txt", "Page3.txt"};
        String excludeFile = "exclude-words.txt";
        String outputFile = "index.txt";

        try {
            IndexGenerator indexGenerator = new IndexGenerator();
            indexGenerator.generateIndex(pageFiles, excludeFile);
            indexGenerator.writeIndex(outputFile);
            System.out.println("Index generated successfully.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

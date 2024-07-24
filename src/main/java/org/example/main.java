package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class main {
    public static void main(String[] args) {
        PDFReader reader = new PDFReader();
        String pdfFilePath = "C:\\Users\\David\\Downloads\\E-book-Portugues-PC-RJ-FGV.pdf";
        reader.extractText(pdfFilePath);

        String treatedText = reader.getTratar().getTextoTratado();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.html"));
            writer.write(generateHTML(treatedText));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nTexto Tratado:");
        System.out.println(treatedText);
    }

    private static String generateHTML(String treatedText) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Texto Tratado</title>\n" +
                "    <link href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1 class=\"my-4\">Texto Tratado</h1>\n" +
                "        " + treatedText + "\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}

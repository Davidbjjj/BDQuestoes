package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class main {
    public static void main(String[] args) {
        PDFReader reader = new PDFReader();
        String pdfFilePath = "C:\\Users\\David\\Downloads\\E-book-Portugues-PC-RJ-FGV.pdf";
        reader.extractText(pdfFilePath);

        String treatedText = reader.getTratar().getTextoTratado();
        List<String> gabarito = reader.getTratar().getGabarito();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.html"));
            writer.write(generateHTML(treatedText, gabarito));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nTexto Tratado:");
        System.out.println(treatedText);
    }

    private static String generateHTML(String treatedText, List<String> gabarito) {
        StringBuilder gabaritoJSArray = new StringBuilder("[");
        for (int i = 0; i < gabarito.size(); i++) {
            gabaritoJSArray.append("\"").append(gabarito.get(i)).append("\"");
            if (i < gabarito.size() - 1) {
                gabaritoJSArray.append(", ");
            }
        }
        gabaritoJSArray.append("]");

        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Texto Tratado</title>\n" +
                "    <link href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                "    <script>\n" +
                "        const gabarito = " + gabaritoJSArray.toString() + ";\n" +
                "        function verificarResposta(questionNumber) {\n" +
                "            const form = document.getElementById('formQuestion' + questionNumber);\n" +
                "            const radios = form.querySelectorAll('input[type=\"radio\"]');\n" +
                "            let selectedAnswer = null;\n" +
                "            radios.forEach(radio => {\n" +
                "                if (radio.checked) {\n" +
                "                    selectedAnswer = radio.value;\n" +
                "                }\n" +
                "            });\n" +
                "            const resultElement = document.getElementById('resultado' + questionNumber);\n" +
                "            if (selectedAnswer) {\n" +
                "                const correctAnswer = gabarito[questionNumber - 1];\n" +
                "                if (selectedAnswer.trim().toUpperCase() === correctAnswer) {\n" +
                "                    resultElement.textContent = 'Resposta correta!';\n" +
                "                    resultElement.className = 'text-success';\n" +
                "                } else {\n" +
                "                    resultElement.textContent = 'Resposta incorreta.';\n" +
                "                    resultElement.className = 'text-danger';\n" +
                "                }\n" +
                "            } else {\n" +
                "                resultElement.textContent = 'Por favor, selecione uma resposta.';\n" +
                "                resultElement.className = 'text-warning';\n" +
                "            }\n" +
                "        }\n" +
                "    </script>\n" +
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

package org.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tratartexto {
    private String texto;
    private StringBuilder textoTratado = new StringBuilder();

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public String getTextoTratado() {
        return textoTratado.toString();
    }

    public void tratarTexto() {
        if (texto != null) {
            // Padrão para capturar "1. (EEAR/2002)" e "4. (Fuzileiro Naval - 2018)"
            Pattern patternEEAR = Pattern.compile("(\\d+\\.\\s\\([^\\)]+\\))(.*?)(?=\\d+\\.\\s\\([^\\)]+\\)|$)", Pattern.DOTALL);
            Matcher matcherEEAR = patternEEAR.matcher(texto);

            while (matcherEEAR.find()) {
                // Adiciona a questão atual ao texto tratado
                textoTratado.append(matcherEEAR.group(1)).append("\n");

                // Adiciona o texto da questão e as alternativas
                String questionText = matcherEEAR.group(2).trim();
                String[] alternativas = {"a)", "b)", "c)", "d)", "e)"};

                // Adiciona o texto entre a questão e as alternativas ao texto tratado
                for (String alternativa : alternativas) {
                    int altIndex = questionText.indexOf(alternativa);
                    if (altIndex != -1) {
                        int endIndex = findEndIndex(questionText, altIndex + alternativa.length(), alternativas);
                        textoTratado.append(questionText, 0, altIndex).append("\n");
                        textoTratado.append(alternativa).append(" ").append(questionText.substring(altIndex + alternativa.length(), endIndex).trim()).append("\n");
                        questionText = questionText.substring(endIndex).trim();
                    }
                }

                // Adiciona o texto restante após a última alternativa
                if (!questionText.isEmpty()) {
                    textoTratado.append(questionText).append("\n");
                }
            }
        }
    }

    // Encontra o índice de término da alternativa atual até a próxima alternativa ou o final do texto
    private int findEndIndex(String text, int start, String[] alternativas) {
        int minIndex = text.length();
        for (String alternativa : alternativas) {
            int index = text.indexOf(alternativa, start);
            if (index != -1 && index < minIndex) {
                minIndex = index;
            }
        }
        return minIndex;
    }
}

package com.example.BancoQuestao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class tratartexto {
    private String texto;
    private StringBuilder textoTratado = new StringBuilder();
    private List<String> gabarito = new ArrayList<>();

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public String getTextoTratado() {
        return textoTratado.toString();
    }

    public List<String> getGabarito() {
        return gabarito;
    }

    public void tratarTexto() {
        if (texto != null) {
            // Padrão para capturar o gabarito
            Pattern patternGabarito = Pattern.compile("(?i)GABARITO\\s+((?:\\d+\\.\\s+[A-E]\\s*)+)", Pattern.DOTALL);
            Matcher matcherGabarito = patternGabarito.matcher(texto);

            if (matcherGabarito.find()) {
                String gabaritoText = matcherGabarito.group(1);
                Pattern patternRespostas = Pattern.compile("(\\d+)\\.\\s+([A-E])");
                Matcher matcherRespostas = patternRespostas.matcher(gabaritoText);

                while (matcherRespostas.find()) {
                    gabarito.add(matcherRespostas.group(2));
                }
            }

            // Padrão para capturar as questões
            Pattern patternEEAR = Pattern.compile("(\\d+\\.\\s\\([^\\)]+\\))(.*?)(?=\\d+\\.\\s\\([^\\)]+\\)|$)", Pattern.DOTALL);
            Matcher matcherEEAR = patternEEAR.matcher(texto);

            int questionCounter = 0;

            while (matcherEEAR.find()) {
                questionCounter++; // Incrementa o contador de questões

                // Adiciona a questão atual ao texto tratado em HTML
                textoTratado.append("<div class=\"card my-2\">\n<div class=\"card-body\">\n");

                // Adiciona o título da questão
                textoTratado.append("<h5 class=\"card-title\">").append(matcherEEAR.group(1)).append("</h5>\n");

                // Adiciona o texto da questão e as alternativas
                String questionText = matcherEEAR.group(2).trim();
                String[] alternativas = {"A)", "B)", "C)", "D)", "E)", "a)", "b)", "c)", "d)", "e)", "(A)", "(B)", "(C)", "(D)", "(E)", "(a)", "(b)", "(c)", "(d)", "(e)"};

                // Encontra o índice inicial das alternativas
                int startIndex = questionText.length();
                for (String alternativa : alternativas) {
                    int altIndex = questionText.indexOf(alternativa);
                    if (altIndex != -1 && altIndex < startIndex) {
                        startIndex = altIndex;
                    }
                }

                // Adiciona o texto da questão antes das alternativas
                textoTratado.append("<p class=\"card-text\">").append(questionText, 0, startIndex).append("</p>\n<form id=\"formQuestion").append(questionCounter).append("\">\n");

                // Adiciona as alternativas ao texto tratado como botões de rádio
                questionText = questionText.substring(startIndex);
                for (String alternativa : alternativas) {
                    int altIndex = questionText.indexOf(alternativa);
                    if (altIndex != -1) {
                        int endIndex = findEndIndex(questionText, altIndex + alternativa.length(), alternativas);
                        textoTratado.append("<div class=\"form-check\">\n")
                                .append("<input class=\"form-check-input\" type=\"radio\" name=\"question").append(questionCounter)
                                .append("\" value=\"").append(alternativa.charAt(0)).append("\" id=\"question").append(questionCounter).append(alternativa).append("\">\n")
                                .append("<label class=\"form-check-label\" for=\"question").append(questionCounter).append(alternativa).append("\">\n")
                                .append(alternativa).append(" ").append(questionText, altIndex + alternativa.length(), endIndex).append("\n")
                                .append("</label>\n</div>\n");
                        questionText = questionText.substring(endIndex).trim();
                    }
                }

                textoTratado.append("</form>\n")
                        .append("<button class=\"btn btn-primary\" onclick=\"verificarResposta(").append(questionCounter).append(")\">Verificar Resposta</button>\n")
                        .append("<p id=\"resultado").append(questionCounter).append("\" class=\"mt-2\"></p>\n")
                        .append("</div>\n</div>\n");
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

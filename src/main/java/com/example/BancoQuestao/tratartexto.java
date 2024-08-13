package com.example.BancoQuestao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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

    private void salvarGabaritoJson() {
        Gson gson = new Gson();
        JsonObject jsonGabarito = new JsonObject();

        // Preenche o JSON com o gabarito
        for (int i = 0; i < gabarito.size(); i++) {
            String resposta = gabarito.get(i);
            if (resposta != null) {
                jsonGabarito.addProperty(String.valueOf(i + 1), resposta);
            }
        }

        // Salva o arquivo em um diretório acessível para leitura posterior
        // Ajuste o caminho conforme necessário para o seu ambiente
        String caminho = "src/main/resources/static/gabarito.json";

        try (FileWriter writer = new FileWriter(caminho)) {
            gson.toJson(jsonGabarito, writer);
            System.out.println("Gabarito salvo com sucesso em " + caminho);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao salvar o gabarito em " + caminho);
        }
    }

    public void tratarTexto() {
        if (texto != null) {
            // Padrão para capturar o gabarito (insensível a maiúsculas/minúsculas)
            Pattern patternGabarito = Pattern.compile("(?i)(GABARITO\\s+((?:\\d+\\.\\s*[A-E]\\s*)+))", Pattern.DOTALL);
            Matcher matcherGabarito = patternGabarito.matcher(texto);

            String gabaritoText = null;

            if (matcherGabarito.find()) {
                gabaritoText = matcherGabarito.group(2);  // Captura apenas a parte das respostas
                texto = texto.replace(matcherGabarito.group(1), "");  // Remove o gabarito do texto original
            }

            if (gabaritoText != null) {
                Pattern patternRespostas = Pattern.compile("(\\d+)\\.\\s*([A-E])");
                Matcher matcherRespostas = patternRespostas.matcher(gabaritoText);

                while (matcherRespostas.find()) {
                    int questaoNumero = Integer.parseInt(matcherRespostas.group(1));
                    String resposta = matcherRespostas.group(2);

                    // Garante que a lista gabarito tenha espaço suficiente para armazenar a resposta na posição correta
                    while (gabarito.size() < questaoNumero) {
                        gabarito.add(null);  // Preenche com null para manter o índice correto
                    }
                    gabarito.set(questaoNumero - 1, resposta);  // Substitui o null pela resposta correta
                }

                // Exibe o gabarito mapeado no console
                System.out.println("Gabarito mapeado: " + gabarito);
                salvarGabaritoJson();
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

                // Renderiza expressões matemáticas delimitadas por $$
                questionText = renderMathExpressions(questionText);

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
                                .append("\" value=\"").append(alternativa).append("\" id=\"question").append(questionCounter).append(alternativa).append("\">\n")
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

            // Adiciona o gabarito ao HTML gerado
            textoTratado.append("<script>\n");
            textoTratado.append("const gabarito = ").append(new Gson().toJson(gabarito)).append(";\n");
            textoTratado.append("function verificarResposta(questionNumber) {\n");
            textoTratado.append("  const radios = document.getElementsByName('question' + questionNumber);\n");
            textoTratado.append("  let userAnswer = null;\n");
            textoTratado.append("  for (const radio of radios) {\n");
            textoTratado.append("    if (radio.checked) {\n");
            textoTratado.append("      userAnswer = radio.value.trim().charAt(0).toUpperCase();\n");
            textoTratado.append("      break;\n");
            textoTratado.append("    }\n");
            textoTratado.append("  }\n");
            textoTratado.append("  const resultado = document.getElementById('resultado' + questionNumber);\n");
            textoTratado.append("  if (userAnswer === gabarito[questionNumber - 1]) {\n"); // Corrigido para usar o índice correto
            textoTratado.append("    resultado.textContent = 'Correto!';\n");
            textoTratado.append("    resultado.style.color = 'green';\n");
            textoTratado.append("  } else if(userAnswer) {\n"); // Verifica se userAnswer não é null
            textoTratado.append("    resultado.textContent = 'Incorreto. A resposta correta é ' + gabarito[questionNumber - 1];\n"); // Corrigido para usar o índice correto
            textoTratado.append("    resultado.style.color = 'red';\n");
            textoTratado.append("  } else {\n"); // Se userAnswer for null, mostra uma mensagem para selecionar uma resposta
            textoTratado.append("    resultado.textContent = 'Por favor, selecione uma alternativa.';\n");
            textoTratado.append("    resultado.style.color = 'orange';\n");
            textoTratado.append("  }\n");
            textoTratado.append("}\n");
            textoTratado.append("</script>\n");
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

    // Função para renderizar expressões matemáticas delimitadas por $$
    private String renderMathExpressions(String text) {
        return text.replaceAll("\\$\\$(.*?)\\$\\$", "<span class=\"math\">$1</span>");
    }
}

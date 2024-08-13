package com.example.BancoQuestao;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class ArquivoController {

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Por favor, selecione um arquivo para upload");
            return "index";
        }

        try {
            // Salva o arquivo PDF no sistema de arquivos
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(convFile);

            // Processa o PDF e gera o HTML
            PDFReader reader = new PDFReader();
            reader.extractText(convFile.getAbsolutePath());
            String treatedText = reader.getTratar().getTextoTratado();
            List<String> gabarito = reader.getTratar().getGabarito();

            model.addAttribute("treatedText", treatedText);
            model.addAttribute("gabarito", gabarito);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Erro ao processar o arquivo: " + e.getMessage());
            return "index";
        }

        return "output";
    }
}
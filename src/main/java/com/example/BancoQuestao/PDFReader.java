package com.example.BancoQuestao;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PDFReader {
    private tratartexto tratar = new tratartexto();

    public String extractText(String pdfFilePath) {
        String extractedText = "";
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            extractedText = pdfStripper.getText(document);
            // Passando o texto extraído para a instância de tratartexto
            tratar.setTexto(extractedText);
            tratar.tratarTexto();  // Chama o método para tratar o texto
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractedText;
    }

    public tratartexto getTratar() {
        return tratar;
    }
}
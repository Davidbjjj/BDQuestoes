package org.example;

public class main {
    public static void main(String[] args) {
        PDFReader reader = new PDFReader();
        String pdfFilePath = "C:\\Users\\David\\Downloads\\E-book-Portugues-PC-RJ-FGV.pdf";
        reader.extractText(pdfFilePath);
        // Exibir o texto tratado
        //System.out.println("Texto Original:");
       // System.out.println(reader.getTratar().getTexto());
        System.out.println("\nTexto Tratado:");
        System.out.println(reader.getTratar().getTextoTratado());
    }
}

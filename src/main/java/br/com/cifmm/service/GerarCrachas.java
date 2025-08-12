package br.com.cifmm.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cifmm.model.FuncionarioModel;
import br.com.cifmm.repository.FuncionarioRepository;

@Service
public class GerarCrachas {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private GerarQRCode gerarQRCodeService;

    private static final String IMAGES_PATH = "C:/Users/Relogio.ponto/eclipse-workspace/CIFMM2/resources/images/";
    private static final int PHOTO_X = 41; // Posição X da foto no template frente
    private static final int PHOTO_Y = 71; // Posição Y da foto no template frente
    private static final int PHOTO_WIDTH = 129; // Largura da foto
    private static final int PHOTO_HEIGHT = 179; // Altura da foto
    private static final int QR_X = 274; // Posição X do QR code no template verso
    private static final int QR_Y = 245; // Posição Y do QR code no template verso
    private static final int QR_WIDTH = 50; // Largura do QR code
    private static final int QR_HEIGHT = 50; // Altura do QR code

    public void gerarTodosCrachas() {
        new File("output").mkdirs();

        List<FuncionarioModel> funcionarios = funcionarioRepository.findAll();

        for (FuncionarioModel func : funcionarios) {
            String nome = func.getNome();
            String matricula = func.getRe(); // 're' como matrícula
            String cargo = func.getCargo();
            String secretaria = func.getSecretaria();

            if (nome == null || matricula == null || cargo == null || secretaria == null) {
                System.out.println("Dados incompletos para o funcionário com RE: " + matricula + ". Pulando.");
                continue;
            }

            gerarCrachaComQR(nome, matricula, cargo, secretaria);
        }
    }

    // Método para gerar crachá individual (usa o helper também)
    public void gerarCracha(FuncionarioModel func) {
        gerarCrachaComQR(func.getNome(), func.getRe(), func.getCargo(), func.getSecretaria());
    }

    private void gerarCrachaComQR(String nome, String matricula, String cargo, String secretaria) {
        new File("output").mkdirs();

        // Verifica e baixa o QR code se necessário
        File qrFile = new File(IMAGES_PATH + matricula + ".png");
        if (!qrFile.exists()) {
            System.out.println("QR Code não encontrado, iniciando download para RE: " + matricula);
            gerarQRCodeService.baixarQRCode(matricula);
            
            // Aguarda até que o arquivo seja criado (máximo 30 segundos)
            int maxAttempts = 30;
            int attempts = 0;
            while (!qrFile.exists() && attempts < maxAttempts) {
                try {
                    Thread.sleep(1000); // Espera 1 segundo
                    attempts++;
                    System.out.println("Aguardando QR Code ser baixado... (" + attempts + "/" + maxAttempts + ")");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Espera interrompida: " + e.getMessage());
                    break;
                }
            }
            
            if (!qrFile.exists()) {
                System.err.println("Falha: QR Code não foi baixado após " + maxAttempts + " segundos para RE: " + matricula);
                return; // Aborta a geração do crachá se o QR Code não foi baixado
            }
        }

        try {
            BufferedImage templateFrente = ImageIO.read(new File(IMAGES_PATH + "Cracha_Frente.jpg"));
            BufferedImage templateVerso = ImageIO.read(new File(IMAGES_PATH + "Cracha_Verso.jpg"));

            // Carrega a foto e o QR code
            BufferedImage photo = carregarFotoFuncionario(matricula);
            BufferedImage qrCode = carregarQRCode(matricula);

            String primeiroNome = getPrimeiroNome(nome);

            // Processa a frente
            Graphics2D gFrente = templateFrente.createGraphics();
            gFrente.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gFrente.setFont(new Font("Arial", Font.BOLD, 24));
            gFrente.setColor(Color.BLACK);

            // Desenha a foto se encontrada
            if (photo != null) {
                gFrente.drawImage(photo, PHOTO_X, PHOTO_Y, PHOTO_WIDTH, PHOTO_HEIGHT, null);
            }

            drawStringFit(gFrente, primeiroNome, 274, 210, 220);

            Font fonteCustomizada = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Users\\Relogio.ponto\\eclipse-workspace\\CIFMM2\\resources\\fonts\\Museo500-Regular.otf")).deriveFont(18f);
            gFrente.setFont(fonteCustomizada);
            gFrente.drawString("RE: " + matricula, 274, 245);
            gFrente.dispose();

            // Processa o verso
            Graphics2D gVerso = templateVerso.createGraphics();
            gVerso.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Font fonteCustomizada2 = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Users\\Relogio.ponto\\eclipse-workspace\\CIFMM2\\resources\\fonts\\Museo300-Regular.otf")).deriveFont(12f);
            gVerso.setFont(fonteCustomizada2);
            gVerso.setColor(Color.BLACK);
            gVerso.drawString(nome, 23, 45);
            gVerso.drawString(cargo, 25, 96);
            gVerso.drawString(secretaria, 25, 150);

            // Desenha o QR code se encontrado
            if (qrCode != null) {
                gVerso.drawImage(qrCode, QR_X, QR_Y, QR_WIDTH, QR_HEIGHT, null);
            }

            gVerso.dispose();

            String frentePath = "output/cracha_frente_" + matricula + ".png";
            String versoPath = "output/cracha_verso_" + matricula + ".png";
            ImageIO.write(templateFrente, "png", new File(frentePath));
            ImageIO.write(templateVerso, "png", new File(versoPath));

            System.out.println("Crachá gerado para " + nome + ": " + frentePath + " e " + versoPath);
        } catch (Exception e) {
            System.err.println("Erro ao gerar crachá: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper para carregar a foto baseada no RE
    private BufferedImage carregarFotoFuncionario(String matricula) {
        BufferedImage photo = null;
        try {
            File photoFile = new File(IMAGES_PATH + matricula + ".jpg");
            if (photoFile.exists()) {
                photo = ImageIO.read(photoFile);
            } else {
                photoFile = new File(IMAGES_PATH + matricula + ".png");
                if (photoFile.exists()) {
                    photo = ImageIO.read(photoFile);
                }
            }
            if (photo == null) {
                System.err.println("Foto não encontrada para RE: " + matricula + " na pasta " + IMAGES_PATH);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar foto para RE: " + matricula + ": " + e.getMessage());
        }
        return photo;
    }

    // Helper para carregar o QR code baseado no RE
    private BufferedImage carregarQRCode(String matricula) {
        BufferedImage qrCode = null;
        try {
            File qrFile = new File(IMAGES_PATH + matricula + ".png");
            System.out.println("Tentando carregar QR Code de: " + qrFile.getAbsolutePath());
            
            if (qrFile.exists()) {
                qrCode = ImageIO.read(qrFile);
                System.out.println("QR Code carregado com sucesso para RE: " + matricula);
            } else {
                System.err.println("QR Code não encontrado para RE: " + matricula + " em: " + qrFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar QR Code para RE: " + matricula + ": " + e.getMessage());
        }
        return qrCode;
    }

    // Helper para pegar o primeiro nome
    private String getPrimeiroNome(String nome) {
        if (nome == null) return "";
        nome = nome.trim();
        if (nome.isEmpty()) return "";
        String[] partes = nome.split("\\s+");
        return partes.length > 0 ? partes[0] : nome;
    }

    // Helper para reduzir a fonte até caber em uma largura máxima
    private void drawStringFit(Graphics2D g, String text, int x, int y, int maxWidth) {
        if (text == null) text = "";
        Font font = g.getFont();
        FontMetrics fm = g.getFontMetrics(font);
        int width = fm.stringWidth(text);

        while (width > maxWidth && font.getSize() > 8) {
            font = font.deriveFont((float) font.getSize() - 1f);
            g.setFont(font);
            fm = g.getFontMetrics(font);
            width = fm.stringWidth(text);
        }
        g.drawString(text, x, y);
    }
}
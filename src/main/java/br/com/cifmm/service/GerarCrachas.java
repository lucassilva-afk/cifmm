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

    public void gerarTodosCrachas() {
        // Cria a pasta output se não existir
        new File("output").mkdirs();

        // Busca todos os funcionários do banco de dados
        List<FuncionarioModel> funcionarios = funcionarioRepository.findAll();

        for (FuncionarioModel func : funcionarios) {
            String nome = func.getNome();
            String matricula = func.getRe(); // Assumindo que 're' é a matrícula
            String cargo = func.getCargo();
            String secretaria = func.getSecretaria();

            if (nome == null || matricula == null || cargo == null || secretaria == null) {
                System.out.println("Dados incompletos para o funcionário com RE: " + matricula + ". Pulando.");
                continue;
            }

            try {
                // Lê as imagens de template
                BufferedImage templateFrente = ImageIO.read(new File("C:/Users/Relogio.ponto/eclipse-workspace/CIFMM2/resources/images/Cracha_Frente.jpg"));
                BufferedImage templateVerso = ImageIO.read(new File("C:/Users/Relogio.ponto/eclipse-workspace/CIFMM2/resources/images/Cracha_Verso.jpg"));

                // Processa a frente
                Graphics2D gFrente = templateFrente.createGraphics();
                gFrente.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gFrente.setFont(new Font("Arial", Font.BOLD, 14));
                gFrente.setColor(Color.BLACK);
                gFrente.drawString(nome, 274, 210); // Ajuste posições conforme template
                Font fonteCustomizada = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Users\\Relogio.ponto\\eclipse-workspace\\CIFMM2\\resources\\fonts\\Museo500-Regular.otf")).deriveFont(14);
                gFrente.setColor(Color.BLACK);
                gFrente.setFont(new Font("Arial", Font.PLAIN, 14));
                gFrente.drawString("RE: " + matricula, 274, 229);                
                gFrente.dispose();

                // Processa o verso (exemplo: adicionando secretaria)
                Graphics2D gVerso = templateVerso.createGraphics();
                gVerso.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);                               
                gVerso.setFont(fonteCustomizada);
                gVerso.setColor(Color.BLACK);
                gVerso.drawString(nome, 33, 36); // Ajuste posições conforme template
                gVerso.drawString(cargo, 31, 92); // Ajuste posições conforme template
                gVerso.drawString(secretaria, 29, 144); // Ajuste posições conforme template
                gVerso.dispose();

                // Salva as imagens na pasta output
                String frentePath = "output/cracha_frente_" + matricula + ".png";
                String versoPath = "output/cracha_verso_" + matricula + ".png";
                ImageIO.write(templateFrente, "png", new File(frentePath));
                ImageIO.write(templateVerso, "png", new File(versoPath));

                System.out.println("Crachá gerado para " + nome + ": " + frentePath + " e " + versoPath);
            } catch (Exception e) {
                System.err.println("Erro ao gerar crachá para " + nome + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Método para gerar crachá individual (opcional, atualizado para frente e verso)
    public void gerarCracha(FuncionarioModel func) {
        gerarCracha(func.getNome(), func.getRe(), func.getCargo(), func.getSecretaria());
    }

    private void gerarCracha(String nome, String matricula, String cargo, String secretaria) {
        new File("output").mkdirs();

        try {
            BufferedImage templateFrente = ImageIO.read(new File("C:/Users/Relogio.ponto/eclipse-workspace/CIFMM2/resources/images/Cracha_Frente.jpg"));
            BufferedImage templateVerso = ImageIO.read(new File("C:/Users/Relogio.ponto/eclipse-workspace/CIFMM2/resources/images/Cracha_Verso.jpg"));

         // Processa a frente
            Graphics2D gFrente = templateFrente.createGraphics();
            gFrente.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gFrente.setFont(new Font("Arial", Font.BOLD, 14));
            gFrente.setColor(Color.BLACK);
            gFrente.drawString(nome, 274, 210); // Ajuste posições conforme template
            Font fonteCustomizada = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Users\\Relogio.ponto\\eclipse-workspace\\CIFMM2\\resources\\fonts\\Museo500-Regular.otf")).deriveFont(14);
            gFrente.setColor(Color.BLACK);
            gFrente.setFont(new Font("Arial", Font.PLAIN, 14));
            gFrente.drawString("RE: " + matricula, 274, 229);                
            gFrente.dispose();

            Graphics2D gVerso = templateVerso.createGraphics();
            gVerso.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);                               
            gVerso.setFont(fonteCustomizada);
            gVerso.setColor(Color.BLACK);
            gVerso.drawString(nome, 33, 36); // Ajuste posições conforme template
            gVerso.drawString(cargo, 31, 92); // Ajuste posições conforme template
            gVerso.drawString(secretaria, 29, 144); // Ajuste posições conforme template
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
}
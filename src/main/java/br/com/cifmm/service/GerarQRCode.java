package br.com.cifmm.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.github.bonigarcia.wdm.WebDriverManager;

@Service
public class GerarQRCode {

    private static final String IMAGES_PATH = "C:/Users/Relogio.ponto/eclipse-workspace/CIFMM2/resources/images/";
    private static final String DOWNLOAD_PATH = "C:/Users/Relogio.ponto/Downloads/"; // Diretório temporário de downloads
    private static final String QR_URL = "https://validar.mogimirim.sp.gov.br/admin/qr/fR8V0M839kpVXT1gXv8SSf5M0wsXEcAC0fyCEnMC6FOa9XC57F1X0qU0K5RM2Lpk";

    public void baixarQRCode(String re) {
        // Configura o caminho correto do diretório de downloads        
        
        System.out.println("Iniciando download do QR Code para RE: " + re);
        System.out.println("Diretório de download: " + DOWNLOAD_PATH);

        // Verifica e cria o diretório se necessário
        File downloadDir = new File(DOWNLOAD_PATH);
        if (!downloadDir.exists()) {
            System.out.println("Criando diretório de download...");
            if (!downloadDir.mkdirs()) {
                System.err.println("Falha ao criar diretório de download!");
                return;
            }
        }

        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        // Removido o headless para debug
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");

        // Configuração explícita do diretório de download
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("download.default_directory", DOWNLOAD_PATH);
        chromePrefs.put("download.prompt_for_download", false);
        options.setExperimentalOption("prefs", chromePrefs);

        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

            // Acessa a página
            System.out.println("Acessando URL do QR Code...");
            driver.get(QR_URL);
            Thread.sleep(2000); // Pausa para visualização

            // Preenche o formulário
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement textField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Re")));
            textField.clear();
            textField.sendKeys(re);
            Thread.sleep(2000); // Pausa para visualização

            // Submete o formulário
            System.out.println("Submetendo formulário...");
            WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
            submitButton.click();

            // Monitora o diretório de downloads
            System.out.println("Monitorando downloads em: " + DOWNLOAD_PATH);
            boolean fileDownloaded = false;
            File downloadedFile = null;
            
            for (int i = 0; i < 30; i++) {
                // Lista TODOS os arquivos no diretório de downloads para debug
                File[] allFiles = downloadDir.listFiles();
                if (allFiles != null) {
                    System.out.println("Arquivos no diretório (" + allFiles.length + "):");
                    for (File f : allFiles) {
                        System.out.println("- " + f.getName() + " (" + f.length() + " bytes)");
                    }
                }

                // Filtra apenas imagens
                File[] files = downloadDir.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));
                
                if (files != null && files.length > 0) {
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                    downloadedFile = files[0];
                    System.out.println("Arquivo candidato: " + downloadedFile.getName());
                    
                    long initialSize = downloadedFile.length();
                    Thread.sleep(1000);
                    if (initialSize > 0 && initialSize == downloadedFile.length()) {
                        fileDownloaded = true;
                        System.out.println("Download completo! Tamanho: " + initialSize + " bytes");
                        break;
                    }
                }
                System.out.println("Aguardando download... (" + (i+1) + "/30)");
                Thread.sleep(1000);
            }

         // ... código anterior ...

            if (fileDownloaded) {
                Path targetPath = Paths.get(IMAGES_PATH + re + ".png");
                Files.move(downloadedFile.toPath(), targetPath);
                System.out.println("QR Code salvo em: " + targetPath);
            } else {
                System.err.println("Nenhum arquivo de QR Code foi baixado!");
                // Tira screenshot para debug
                File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                Files.copy(screenshot.toPath(), Paths.get(IMAGES_PATH + "download_error_" + re + ".png"));
                System.err.println("Screenshot do erro salvo em: " + IMAGES_PATH + "download_error_" + re + ".png");
            }

            // ... código posterior ...

        } catch (Exception e) {
            System.err.println("Erro durante o processo:");
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
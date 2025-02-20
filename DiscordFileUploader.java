import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DiscordFileUploader {
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1340302953533407242/mCQqk6Pm8NgJolLPqF57Bv9QJRjFa-fABJnfa8pdE6OBYSsnuwlW3swKRZsBLr2GaHtf";

    public static void main(String[] args) {
        // Kullanıcıdan dosya seçmesini iste
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Göndermek için bir dosya seç");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Tüm Dosyalar", "*.*"));

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Seçilen dosya: " + selectedFile.getAbsolutePath());

            // Dosyayı Discord'a yükle
            boolean success = uploadFileToDiscord(selectedFile);

            if (success) {
                JOptionPane.showMessageDialog(null, "Dosya başarıyla yüklendi!");
            } else {
                JOptionPane.showMessageDialog(null, "Dosya yüklenirken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static boolean uploadFileToDiscord(File file) {
        try {
            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----JavaBoundary");

            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

            // Dosya içeriğini hazırla
            writer.append("------JavaBoundary\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n");
            writer.append("Content-Type: application/octet-stream\r\n\r\n");
            writer.flush();

            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append("\r\n------JavaBoundary--\r\n");
            writer.flush();
            writer.close();

            // Yanıtı al
            int responseCode = connection.getResponseCode();
            System.out.println("HTTP Yanıt Kodu: " + responseCode);

            return responseCode == 200 || responseCode == 204;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

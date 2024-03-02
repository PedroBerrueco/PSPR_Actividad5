import org.apache.commons.net.ftp.FTPClient;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FTPServidor {

    FTPClient ftp = new FTPClient();

    private static final String ENCRYPTION_KEY = "supercalifragilisticoespialidoso";

    public static void main(String[] args) {

        String server = "demo.wftpserver.com";
        int port = 21;
        String user = "demo-user";
        String pass = "demo-user";

        String localFile = "src/filePedro.txt";

        try {

            // Conectar al servidor FTP
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);

            // Encriptar el archivo antes de subirlo al servidor FTP

            String key = ENCRYPTION_KEY;
            byte[] keyBytes = key.getBytes("UTF-8");
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(Files.readAllBytes(Paths.get(localFile)));
            Files.write(Paths.get(localFile), encryptedBytes);

            // Subir el archivo encriptado al servidor FTP
            InputStream inputStream = new FileInputStream(localFile);
            boolean uploadSuccess = ftpClient.storeFile("/remote/path/to/filePedro.txt", inputStream);
            inputStream.close();

            // Verificar si la subida fue exitosa
            if (uploadSuccess) {
                System.out.println("Archivo encriptado subido correctamente.");
            } else {
                System.out.println("Error al subir el archivo encriptado.");
            }

            // Obtener la respuesta del servidor FTP
            int replyCode = ftpClient.getReplyCode();
            String replyString = ftpClient.getReplyString();
            System.out.println("Respuesta del servidor FTP: " + replyCode + " " + replyString);


            // Cerrar la conexión FTP
            ftpClient.logout();
            ftpClient.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

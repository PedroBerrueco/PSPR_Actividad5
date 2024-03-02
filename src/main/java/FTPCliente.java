import org.apache.commons.net.ftp.FTPClient;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class FTPCliente {

    public static void main(String[] args) {
        String server = "demo.wftpserver.com";
        int port = 21;
        String user = "demo-user";
        String pass = "demo-user";

        String remoteFile = "/remote/path/to/filePedro.txt";
        String localFile = "src/filePedro.txt";
        String downloadFile = "src/downloadFile.txt";

        try {
            // Conectar al servidor FTP
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);


            // Descargar el archivo desde el servidor FTP
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
            boolean downloadSuccess = ftpClient.retrieveFile(remoteFile, outputStream);
            outputStream.close();

            if (!downloadSuccess) {
                System.out.println("No se pudo descargar el archivo desde el servidor FTP. Se utilizará el archivo local existente.");

            }


            // Solicitar la clave de desencriptado al usuario
            Scanner scanner = new Scanner(System.in);
            System.out.println("Introduce la clave de desencriptado:");
            String key = scanner.nextLine();
            scanner.close();
            System.out.println("Clave de desencriptación: " + key);

            // Desencriptar el archivo descargado o local
            byte[] keyBytes = key.getBytes("UTF-8");

            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] decryptedBytes = cipher.doFinal(Files.readAllBytes(Paths.get(localFile)));

            System.out.println("Datos desencriptados: " + new String(decryptedBytes));

            // Escribir los datos desencriptados en un nuevo archivo
            String newFile = "src/pedroFile.txt"; // Definir el nuevo nombre del archivo
            FileOutputStream outS = new FileOutputStream(newFile);
            outS.write(decryptedBytes);
            outS.close();

            System.out.println("Archivo descargado y desencriptado correctamente.");

            // Cerrar la conexión FTP
            ftpClient.logout();
            ftpClient.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

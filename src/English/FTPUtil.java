import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPUtil {

  public static void uploadFile(String server, int port, String user, String password,
      String localFilePath, String remoteFilePath) {
    
    FTPClient ftpClient = new FTPClient();
    LoggerUtil.i("File upload started.");
    FileInputStream inputStream = null;

    try {
      // Connect to the FTP server
      ftpClient.connect(server, port);
      boolean login = ftpClient.login(user, password);

      if (!login) {
        LoggerUtil.i(remoteFilePath);
        return;
      }

      // Enter passive mode and set file type to binary
      ftpClient.enterLocalPassiveMode();
      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

      // Open the local file as an input stream
      File localFile = new File(localFilePath);
      inputStream = new FileInputStream(localFile);

      // Upload the file to the server
      boolean success = ftpClient.storeFile(remoteFilePath, inputStream);

      if (success) {
        LoggerUtil.i("File uploaded successfully.");
      } else {
        LoggerUtil.i("Failed to upload the file.");
      }

    } catch (IOException ex) {
      LoggerUtil.i("Error occurred: " + ex.getMessage());
      ex.printStackTrace();
    } finally {
      try {
        if (inputStream != null) {
          inputStream.close();
        }
        ftpClient.logout();
        ftpClient.disconnect();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
  
  public static void main(String[] args) {
    // FTP server details
    String server = "pc1511sq";
    int port = 21;
    String user = "anonymous";
    String password = "";

    // Local file path
    String localFilePath = "D:/tenant-service-54f7f85cf8-29jc9.txt";

    // Remote file path where the file will be uploaded
    String remoteFilePath = "file.txt";

    // Upload the file to the FTP server
    uploadFile(server, port, user, password, localFilePath, remoteFilePath);
}

}

import java.io.ByteArrayOutputStream;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class EmailUtil {

  public static void sendEmailWithAttachment(String recipientEmail, String subject,
      String messageBody, ByteArrayOutputStream outputStream) {
    // Sender's Gmail credentials
    String senderEmail = "hiepdev18@gmail.com";
    String senderPassword = "123456Aa@"; // Use app-specific password if necessary

    // Set up properties for Gmail SMTP server
    Properties properties = new Properties();
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", "true");
    properties.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.port", "587");

    // Create a session with authenticator
    Session session = Session.getInstance(properties, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(senderEmail, senderPassword);
      }
    });

    // Create the email message
    Message message = new MimeMessage(session);
    try {
      message.setFrom(new InternetAddress(senderEmail));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
      message.setSubject(subject);
   // Create a multipart message for the body and the attachment
      Multipart multipart = new MimeMultipart();

      // Add the email body part
      MimeBodyPart textBodyPart = new MimeBodyPart();
      textBodyPart.setText(messageBody);
      multipart.addBodyPart(textBodyPart);

      // Create the attachment part with the output stream content
      MimeBodyPart attachmentBodyPart = new MimeBodyPart();
      DataSource dataSource =
          new ByteArrayDataSource(outputStream.toByteArray(), "application/octet-stream");
      attachmentBodyPart.setDataHandler(new DataHandler(dataSource));
      attachmentBodyPart.setFileName("output.txt"); // Set the desired attachment filename
      multipart.addBodyPart(attachmentBodyPart);

      // Set the multipart message to the email
      message.setContent(multipart);

      // Send the email
      Transport.send(message);
      System.out.println("Email sent successfully with attachment to " + recipientEmail);
    } catch (AddressException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (MessagingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}

package util;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil
{
    private static final String CORREO_ORIGEN = "developeruarm@gmail.com";
    private static final String CONTRASENA_APP = "bwmd ziwo vunp iujb";
    
    public static void enviarCorreoVerificacion(String correoDestino, String nombre, String codigo)
    {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
 
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(CORREO_ORIGEN, CONTRASENA_APP);
            }
        });
 
        try
        {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(CORREO_ORIGEN));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
            mensaje.setSubject("Tu código de verificación");
            mensaje.setText(
                "Hola " + nombre + ",\n\n" +
                "Gracias por registrarte. Usa el siguiente código para verificar tu correo electrónico:\n\n" +
                "        " + codigo + "\n\n" +
                "Este código es válido por " + TokenUtil.MINUTOS_EXPIRACION + " minutos.\n\n" +
                "Si tú no solicitaste este registro, puedes ignorar este correo."
            );
 
            Transport.send(mensaje);
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
            throw new RuntimeException("No se pudo enviar el correo de verificación", e);
        }
    }

}
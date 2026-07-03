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
    private static final String CONTRASENA_APP = "bddm bscp zkhw etjt";

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

            String html = construirHtmlVerificacion(nombre, codigo, TokenUtil.MINUTOS_EXPIRACION);
            mensaje.setContent(html, "text/html; charset=UTF-8");

            Transport.send(mensaje);
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
            throw new RuntimeException("No se pudo enviar el correo de verificación", e);
        }
    }

    private static String construirHtmlVerificacion(String nombre, String codigo, int minutosExpiracion)
    {
        return "<!DOCTYPE html>"
            + "<html lang=\"es\">"
            + "<body style=\"margin:0;padding:0;background-color:#eef1f5;font-family:Arial,Helvetica,sans-serif;\">"
            + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#eef1f5;padding:32px 16px;\">"
            + "<tr><td align=\"center\">"
            + "<table role=\"presentation\" width=\"480\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#ffffff;border-radius:10px;overflow:hidden;box-shadow:0 2px 6px rgba(0,0,0,0.08);\">"

            + "<tr><td style=\"background:linear-gradient(90deg,#0f2a4a,#1d6fa5,#e0952f);height:6px;line-height:6px;font-size:0;\">&nbsp;</td></tr>"

            + "<tr><td style=\"padding:32px 32px 0 32px;\">"
            + "<h1 style=\"margin:0 0 8px 0;color:#12243d;font-size:22px;\">Verifica tu correo</h1>"
            + "<div style=\"width:48px;height:3px;background-color:#e0952f;margin:0 0 24px 0;\"></div>"
            + "</td></tr>"

            + "<tr><td style=\"padding:0 32px 32px 32px;\">"
            + "<p style=\"margin:0 0 16px 0;color:#12243d;font-size:16px;\">Hola " + escapar(nombre) + ",</p>"
            + "<p style=\"margin:0 0 24px 0;color:#4b5c72;font-size:15px;line-height:1.5;\">"
            + "Gracias por registrarte. Usa el siguiente código para verificar tu correo electrónico:</p>"

            + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">"
            + "<tr><td align=\"center\" style=\"padding:8px 0 24px 0;\">"
            + "<span style=\"display:inline-block;background-color:#fdf6d8;border:1px solid #f0e2a3;color:#12243d;font-size:28px;font-weight:bold;letter-spacing:6px;padding:14px 28px;border-radius:6px;\">"
            + escapar(codigo)
            + "</span>"
            + "</td></tr>"
            + "</table>"

            + "<p style=\"margin:0 0 8px 0;color:#6b7c8f;font-size:14px;\">"
            + "Este código es válido por " + minutosExpiracion + " minutos.</p>"
            + "<p style=\"margin:0;color:#9aa7b5;font-size:13px;\">"
            + "Si tú no solicitaste este registro, puedes ignorar este correo.</p>"
            + "</td></tr>"

            + "<tr><td style=\"background-color:#12243d;padding:16px 32px;\">"
            + "<p style=\"margin:0;color:#a9b7c8;font-size:12px;text-align:center;\">Este es un mensaje automático, por favor no respondas a este correo.</p>"
            + "</td></tr>"

            + "</table>"
            + "</td></tr>"
            + "</table>"
            + "</body></html>";
    }

    private static String escapar(String valor)
    {
        if (valor == null) return "";
        return valor
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }
}
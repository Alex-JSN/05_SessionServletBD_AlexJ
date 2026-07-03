package util;

import java.security.SecureRandom;
import java.util.UUID;

public class TokenUtil
{
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Genera un token único para el enlace de verificación de correo (si decides usar enlace). */
    public static String generarToken()
    {
        return UUID.randomUUID().toString();
    }

    /** Genera un código numérico de 6 dígitos (con ceros a la izquierda si hace falta), como String. */
    public static String generarCodigo()
    {
        int numero = RANDOM.nextInt(1_000_000);
        return String.format("%06d", numero);
    }

    /** Minutos de validez del código/token antes de que expire. */
    public static final int MINUTOS_EXPIRACION = 15;
}
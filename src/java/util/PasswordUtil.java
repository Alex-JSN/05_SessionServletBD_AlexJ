package util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil
{
    private static final int ITERACIONES = 65536;
    private static final int LONGITUD_LLAVE = 128;

    public static String hashear(String contrasenaPlana)
    {
        try
        {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(contrasenaPlana.toCharArray(), salt, ITERACIONES, LONGITUD_LLAVE);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e)
        {
            throw new RuntimeException("Error al generar hash de contraseña", e);
        }
    }

    public static boolean verificar(String contrasenaPlana, String hashGuardado)
    {
        try
        {
            String[] partes = hashGuardado.split(":");
            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] hashOriginal = Base64.getDecoder().decode(partes[1]);

            PBEKeySpec spec = new PBEKeySpec(contrasenaPlana.toCharArray(), salt, ITERACIONES, LONGITUD_LLAVE);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashIntento = skf.generateSecret(spec).getEncoded();

            if (hashIntento.length != hashOriginal.length) return false;
            int diff = 0;
            for (int i = 0; i < hashIntento.length; i++)
            {
                diff |= hashIntento[i] ^ hashOriginal[i];
            }
            return diff == 0;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
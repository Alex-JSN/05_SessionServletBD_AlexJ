package modelo;

public class Alumno
{
    private String matricula;
    private String nombre;
    private String paterno;
    private String materno;

    private String p1, p2, p3;
    
    public Alumno() {}
    
    public String getMatricula() { return matricula; }
    
    public void setMatricula(String matricula) { this.matricula = matricula; }
    
    public String getNombre() { return nombre; }
    
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getPaterno() { return paterno; }
    
    public void setPaterno(String paterno) { this.paterno = paterno; }
    
    public String getMaterno() { return materno; }
    
    public void setMaterno(String materno) { this.materno = materno; }
    
    public String getP1() { return p1; }
    
    public void setP1(String p1) { this.p1 = p1; }
    
    public String getP2() { return p2; }
    
    public void setP2(String p2) { this.p2 = p2; }    
    
    public String getP3() { return p3; }
    
    public void setP3(String p3) { this.p3 = p3; }
    
    public double getPromedio()
    {
        // Si al alumno aún no se le ha registrado ninguna calificación,
        // p1/p2/p3 llegan como null (por el LEFT JOIN con Calificaciones).
        if (p1 == null || p2 == null || p3 == null)
        {
            return 0.0;
        }

        try
        {
            double P1 = Double.parseDouble(p1);
            double P2 = Double.parseDouble(p2);
            double P3 = Double.parseDouble(p3);
            return (P1 + P2 + P3) / 3.0;
        }
        catch (NumberFormatException e)
        {
            System.out.println("Error: valores no numéricos en calificaciones");
            return 0.0; 
        }
    }
}
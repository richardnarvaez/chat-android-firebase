package ec.richardnarvaez.chatf.chat.models;


public class Friends {
    private String nombre;
    private String foto;
    private String numero;
    private String key;

    public Friends(String nombre1, String foto1, String numero1,String key) {
        this.nombre=nombre1;
        this.foto=foto1;
        this.numero=numero1;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
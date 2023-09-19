package cl.jdcsolutions.p_bike.Objetos;

public class Bicicleta {

    private String marca;
    private String color;
    private String id;

    private int numero;


    public Bicicleta(String marca, String color, String id, int numero) {
        this.marca = marca;
        this.color = color;
        this.id = id;
        this.numero = numero;
    }

    public String getMarca() {
        return marca;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

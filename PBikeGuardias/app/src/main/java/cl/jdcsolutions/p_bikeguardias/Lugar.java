package cl.jdcsolutions.p_bikeguardias;

public class Lugar {

    private String id;
    private boolean estado;


    public Lugar() {
    }

    public Lugar(String id, boolean estado) {
        this.id = id;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}

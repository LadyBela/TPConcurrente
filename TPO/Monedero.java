package TPO;

public class Monedero {
    private int fichaMR; // Ficha de montaña rusa
    private int fichaAC; // Ficha de autitos chocadores
    private int fichaRV; // Ficha de realidad virtual
    private int fichaCG; // Ficha de carrera gomones

    public Monedero() {
        this.fichaRV = 0;
        this.fichaAC = 0;
        this.fichaMR = 0;
        this.fichaCG = 0;
    }

    public void agregarFichas(String tipo, int cantidad) {
        switch (tipo) {
            case "AC":
                this.fichaAC += cantidad;
                break;
            case "MR":
                this.fichaMR += cantidad;
                break;
            case "RV":
                this.fichaRV += cantidad;
                break;
            case "CG":
                this.fichaCG += cantidad;
                break;
        }
    }

    public void quitarFichas(String tipo, int cantidad) {
        switch (tipo) {
            case "AC":
                this.fichaAC -= cantidad;
                break;
            case "MR":
                this.fichaMR -= cantidad;
                break;
            case "RV":
                this.fichaRV -= cantidad;
                break;
            case "CG":
                this.fichaCG -= cantidad;
                break;
        }
    }

    public int getFichasActuales(String tipo) {
        switch (tipo) {
            case "MR":
                return this.fichaMR;
            case "AC":
                return this.fichaAC;
            case "RV":
                return this.fichaRV;
            case "CG":
                return this.fichaCG;
            default:
                return -1;
        }
    }

}

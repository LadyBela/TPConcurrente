package TPO;
public class Monedero {
    private int fichaRV;    //Ficha de realidad virtual
    private int fichaAC;    //Ficha de autitos chocadores
    private int fichaMR;    //ficha de montaña rusa
    private int fichaCG;    //ficha de carrera gomones


    public Monedero(){
        
    }


    public void agregarFichas(String tipo, int cantidad){
        switch(tipo){
            case "RV":
                this.fichaRV += cantidad;
                break;
            case "AC":
                this.fichaAC += cantidad;
                break;
            case "MR":
                this.fichaMR += cantidad;
                break;
            case "CG":
                this.fichaCG += cantidad;
                break;
        }
    }

    public void quitarFichas(String tipo, int cantidad){
        switch(tipo){
            case "RV":
                this.fichaRV -= cantidad;
                break;
            case "AC":
                this.fichaAC -= cantidad;
                break;
            case "MR":
                this.fichaMR -= cantidad;
                break;
            case "CG":
                this.fichaCG -= cantidad;
                break;
        }
    }

    public int getFichasActuales(String tipo){
        switch(tipo){
            case "RV":
                return this.fichaRV;
            case "AC":
                return this.fichaAC;
            case "MR":
                return this.fichaMR;
            case "CG":
                return this.fichaCG;
            default:
                return -1;
        }
    }
    
}

package TPO;

public class AreaPremios {

    public synchronized void canjearFichas(Persona p) {

        System.out.println("AP | Persona " + p.getNombre() + " va a canjear fichas en el Área de Premios.");

        String premioMR = "";
        String premioAC = "";
        String premioRV = "";
        String premioCG = "";

        int fichasMR = p.cantidadFichas("MR");
        int fichasAC = p.cantidadFichas("AC");
        int fichasRV = p.cantidadFichas("RV");
        int fichasCG = p.cantidadFichas("CG");

        int total = fichasMR + fichasAC + fichasRV + fichasCG;

        if (total != 0) {
            if (fichasMR > 0) {
                // System.out.println("AP | - " + fichasMR + " fichas de Montaña Rusa");
                premioMR = generarPremio(fichasMR, "MR");
                p.quitarFichas("MR", fichasMR);
            }
            if (fichasAC > 0) {
                // System.out.println("AP | - " + fichasAC + " fichas de Autitos Chocadores");
                premioAC = generarPremio(fichasAC, "AC");
                p.quitarFichas("AC", fichasAC);
            }
            if (fichasRV > 0) {
                // System.out.println("AP | - " + fichasRV + " fichas de Realidad Virtual");
                premioRV = generarPremio(fichasRV, "RV");
                p.quitarFichas("RV", fichasRV);
            }
            if (fichasCG > 0) {
                // System.out.println("AP | - " + fichasCG + " fichas de Carrera de Gomones");
                premioCG = generarPremio(fichasCG, "CG");
                p.quitarFichas("CG", fichasCG);
            }

            String premioFinal = "Premios MR: " + premioMR + " , premios AC: " + premioAC + " , premios RV: " + premioRV
                    + " , premios CG: " + premioCG;

            System.out.println(
                    "AP | Persona " + p.getNombre() + " canjeó " + total + " fichas y recibió: " + premioFinal);
        }

    }

    private String generarPremio(int totalFichas, String tipo) {
        String premio = "";
        while (totalFichas > 0) {
            if (totalFichas >= 80) {
                premio += "Peluche Grande | ";
                totalFichas -= 80;
            }
            if (totalFichas >= 40) {
                premio += "Peluche Mediano | ";
                totalFichas -= 40;
            }
            if (totalFichas >= 20) {
                premio += "Pelota | ";
                totalFichas -= 20;
            }
            if (totalFichas >= 10) {
                premio += "Juguete Pequeño | ";
                totalFichas -= 10;
            }
            if (totalFichas >= 5) {
                premio += "Chupetin | ";
                totalFichas -= 5;
            }
            if (totalFichas >= 1) {
                premio += "Caramelo | ";
                totalFichas -= 1;
            }
        }
        return premio;
    }

}

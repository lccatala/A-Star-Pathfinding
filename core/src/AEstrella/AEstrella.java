/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AEstrella;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *
 * @author mirse
 */
public class AEstrella {
 
    //Mundo sobre el que se debe calcular A*
    Mundo mundo;
    
    //Camino
    public char camino[][];
    
    //Casillas expandidas
    int camino_expandido[][];
    
    //Número de nodos expandidos
    int expandidos;
    
    //Coste del camino
    float coste_total;
    
    // Método de estimación de la distancia desde el nodo actual al dragón
    public static int heuristica = 0;
    
    public AEstrella(){
        expandidos = 0;
        mundo = new Mundo();
    }
    
    public AEstrella(Mundo m){
        //Copia el mundo que le llega por parámetro
        mundo = new Mundo(m);
        camino = new char[m.tamanyo_y][m.tamanyo_x];
        camino_expandido = new int[m.tamanyo_y][m.tamanyo_x];
        expandidos = 0;
        
        //Inicializa las variables camino y camino_expandidos donde el A* debe incluir el resultado
        for(int i=0;i<m.tamanyo_x;i++)
            for(int j=0;j<m.tamanyo_y;j++){
                camino[j][i] = '.';
                camino_expandido[j][i] = -1;
            }
    }
    
    public int CalcularAEstrella() {
        expandidos = 0;
        
        List<Nodo> listaInterior = new ArrayList<Nodo>();
        List<Nodo> listaFrontera = new ArrayList<Nodo>();
        int x1 = mundo.getCaballero().getX();
        int y1 = mundo.getCaballero().getY();
        int x2 = mundo.getDragon().getX();
        int y2 = mundo.getDragon().getY();
        
        double distancia = distancia(x1, y1, x2, y2, heuristica);
        
        Nodo inicio = new Nodo(mundo.getCaballero(), distancia, 0);
        inicio.guardarHijos(mundo.getDragon().getX(), mundo.getDragon().getY(), mundo);
                
        listaFrontera.add(inicio);
        
        while (!listaFrontera.isEmpty()) {
            Nodo n = listaFrontera.get(indiceMenorF(listaFrontera));
            //System.out.println("El nodo (" + n.posicion.getX() + ", " + n.posicion.getY() + ") es seleccionado por tener la menor f de toda la listaFrontera y pasado a la listaInterior.");
            
            // Si n es meta
            if (n.posicion.getX() == mundo.getDragon().getX() && n.posicion.getY() == mundo.getDragon().getY()) {
                //System.out.println("El nodo (" + n.posicion.getX() + ", " + n.posicion.getY() + ") es la meta. El algoritmo termina su ejecución.");
                reconstruirCamino(n);
                System.out.println("Camino");
                mostrarCamino();

                System.out.println("Camino explorado");
                mostrarCaminoExpandido();

                System.out.println("Nodos expandidos: "+expandidos);
                return 1;
            }
            
            listaFrontera.remove(n);
            listaInterior.add(n);
            
            if (camino_expandido[n.posicion.getY()][n.posicion.getX()] == -1)
                camino_expandido[n.posicion.getY()][n.posicion.getX()] = expandidos;
            expandidos++;
            
            for (Nodo m : n.hijos) {
                if (!listaInterior.contains(m)) {
                    double gPrima = n.g + coste(m);
                    m.padre = n;
                    if (!listaFrontera.contains(m)) {
                        //System.out.println("Su hijo, el nodo (" + m.posicion.getX() + ", " + m.posicion.getY() + ") pasa a la listaFrontera.");
                        m.g = n.g + coste(m);
                        m.h = distancia(m.posicion.getX(), m.posicion.getY(), mundo.getDragon().getX(), mundo.getDragon().getY(), heuristica);
                        m.f = m.g + m.h;
                        m.guardarHijos(mundo.getDragon().getX(), mundo.getDragon().getY(), mundo);
                        listaFrontera.add(m);
                    }
                    else if (gPrima < m.g) {
                        m.g = gPrima;
                        m.f = m.g + m.h;
                    }
                }
            }
        }
        return -1;
    }
    
    // 0 -> Manhattan con celdas cuadradas
    // 1 -> Manhattan con celdas hexagonales
    // 2 -> Euclidea con celdas cuadradas
    // 3 -> Euclídea con celdas hexagonales
    public static double distancia(int x1, int y1, int x2, int y2, int heuristica) {
        switch (heuristica) {
            case 0:
                return (float)(Math.abs(x2 - x1) + Math.abs(y2 - y1));
            case 1:
                return (float)((Math.abs(x1 - x2) + Math.abs(x1 + y1 - x2 - y2) + Math.abs(y1 - y2)) / 2);
            case 2:
                return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            case 3:
                float[] valores = evenRToCube(x1, y1);
                float x = valores[0];
                float y = valores[1];
                float z = valores[2];
                return (float)(Math.sqrt((Math.pow(x - x, 2) + Math.pow(y - y, 2) + Math.pow(z - z, 2)) / 2));
            default:
                return Float.MAX_VALUE;
        }
    }
    
    private static float[] evenRToCube(int x, int y) {
        float xr = x - (y + (y&1)) / 2;
        float zr = y;
        float yr = -xr - zr;
        float resultado[] = {xr, yr, zr};
        return resultado;
    }
    
    private int coste(Nodo m) {
        if (m.posicion.getX() > -1 && m.posicion.getY() > -1 && m.posicion.getX() < mundo.tamanyo_x && m.posicion.getY() < mundo.tamanyo_y) {
            char tipo = mundo.getCelda(m.posicion.getX(), m.posicion.getY());
        
            switch (tipo) {
                case 'b':
                    return Integer.MAX_VALUE;
                case 'c':
                    return 1;
                case 'h':
                    return 2;
                case 'a':
                    return 3;         
            }
        }
        return Integer.MAX_VALUE;
    }
    
    private int indiceMenorF(List<Nodo> lista) {
        int resultado = -1;
        double fMenor = Double.MAX_VALUE;
        
        for (Nodo n : lista) {
            if (fMenor > n.f) {
                fMenor = n.f;
                resultado = lista.indexOf(n);
            }
        }
        
        return resultado;
    }
    
    public void reconstruirCamino(Nodo n) {
        while (n.padre != null) {
            camino[n.posicion.getY()][n.posicion.getX()] = 'X';
            coste_total += n.f;
            n = n.padre;
        }
    }
    
    //Muestra la matriz que contendrá el camino después de calcular A*
    public void mostrarCamino(){
        for (int i=0; i<mundo.tamanyo_y; i++){
            if(i%2==0)
                System.out.print(" ");
            for(int j=0;j<mundo.tamanyo_x; j++){
                System.out.print(camino[i][j]+" ");
            }
            System.out.println();   
        }
    }
    
    //Muestra la matriz que contendrá el orden de los nodos expandidos después de calcular A*
    public void mostrarCaminoExpandido(){
        for (int i=0; i<mundo.tamanyo_y; i++){
            if(i%2==0)
                    System.out.print(" ");
            for(int j=0;j<mundo.tamanyo_x; j++){
                if(camino_expandido[i][j]>-1 && camino_expandido[i][j]<10)
                    System.out.print(" ");
                System.out.print(camino_expandido[i][j]+" ");
            }
            System.out.println();   
        }
    }
    
    public void reiniciarAEstrella(Mundo m){
        //Copia el mundo que le llega por parámetro
        mundo = new Mundo(m);
        camino = new char[m.tamanyo_y][m.tamanyo_x];
        camino_expandido = new int[m.tamanyo_y][m.tamanyo_x];
        expandidos = 0;
        
        //Inicializa las variables camino y camino_expandidos donde el A* debe incluir el resultado
        for(int i=0;i<m.tamanyo_x;i++)
            for(int j=0;j<m.tamanyo_y;j++){
                camino[j][i] = '.';
                camino_expandido[j][i] = -1;
            }
    }
    
    public float getCosteTotal(){
        return coste_total;
    }
}

class Nodo {
    public Nodo(Coordenada posicion, double h, double g) {
        this.h = h;
        this.g = g;
        this.f = g + h;
        this.posicion = posicion;
    }
    
    public Nodo(Coordenada posicion, double h, double g, Nodo padre) {
        this.h = h;
        this.g = g;
        this.padre = padre;
        this.f = g + h;
        this.posicion = posicion;
    }
    
    public double f, g, h;
    public Set<Nodo> hijos = new HashSet<Nodo>();
    public Nodo padre;
    
    public Coordenada posicion;
    
    public void guardarHijos(int x2, int y2, Mundo m) {
        List<Coordenada> posiciones = new ArrayList<Coordenada>();
        
        if (posicion.getY() % 2 != 0) {
            posiciones.add(new Coordenada(posicion.getX() - 1, posicion.getY() - 1));
            posiciones.add(new Coordenada(posicion.getX(), posicion.getY() - 1));
            posiciones.add(new Coordenada(posicion.getX() + 1, posicion.getY()));
            posiciones.add(new Coordenada(posicion.getX(), posicion.getY() + 1));
            posiciones.add(new Coordenada(posicion.getX() - 1, posicion.getY() + 1));
            posiciones.add(new Coordenada(posicion.getX() - 1, posicion.getY()));
        } else {
            posiciones.add(new Coordenada(posicion.getX() - 1, posicion.getY()));
            posiciones.add(new Coordenada(posicion.getX(), posicion.getY() - 1));
            posiciones.add(new Coordenada(posicion.getX() + 1, posicion.getY() - 1));
            posiciones.add(new Coordenada(posicion.getX() + 1, posicion.getY()));
            posiciones.add(new Coordenada(posicion.getX() + 1, posicion.getY() + 1));
            posiciones.add(new Coordenada(posicion.getX(), posicion.getY() + 1));
        }
        
        for (Coordenada posicion : posiciones)
            if (posicion.getX() > -1 && posicion.getY() > -1 && posicion.getX() < m.tamanyo_x && posicion.getY() < m.tamanyo_y)
                hijos.add(new Nodo(posicion, AEstrella.distancia(posicion.getX(), posicion.getY(), x2, y2, AEstrella.heuristica), g, this));
    }
    
    // Sobrecargamos .equals() para que funcione .contains() en las listas interior y frontera
    @Override
    public boolean equals(Object that) {
        Nodo n = (Nodo)that;
        return (posicion.getX() == n.posicion.getX() && posicion.getY() == n.posicion.getY());
    }
}


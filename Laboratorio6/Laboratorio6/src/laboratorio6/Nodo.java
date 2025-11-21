/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package laboratorio6;

/**
 *
 * @author COMPAQCQ45
 */
public class Nodo {
    Object dato;
    Nodo izq, der;
    
    public Nodo(Object dato, Nodo izq, Nodo der)
    {
        this.dato = dato;
        this.izq = izq;
        this.der = der;
    }
    
    public Object getDato()
    {
        return dato;
    }
    
    public void setDato(Object dato)
    {
        this.dato = dato;
    }
    
    public Nodo getIzq()
    {
        return izq;
    }
    
    public void setIzq(Nodo izq)
    {
        this.izq = izq;
    }
    
    public Nodo getDer()
    {
        return der;
    }
    
    public void setDer(Nodo der)
    {
        this.der = der;
    }
}

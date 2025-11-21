 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import laboratorio6.Nodo;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author COMPAQCQ45
 */
public class ArbolBinario extends javax.swing.JFrame {

    /**
     * Creates new form ArbolBinario
     */
    
    public Nodo raiz;
    private Color background = new Color(255,255,255);
    private Color secundary = new Color(5, 255, 13);
    private Color text = new Color(78,88,97);
    private Color hover = new Color(85, 161, 88);
    private Color resaltar = new Color(255, 255, 255);
    
    public ArbolBinario() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        inicializarPanelDibujo();
    }
    
    
    private void inicializarPanelDibujo() 
    {
       arbol = new javax.swing.JPanel() {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g; // Convertimos a Graphics2D
        
        // Activamos antialiasing para que al hacer zoom no se vea feo
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (raiz != null) {
            // 1. CALCULAMOS EL TAMAÑO REAL QUE OCUPA EL ÁRBOL
            // Usamos tu lógica: 2^(profundidad-1) * un factor de ancho base
            int profundidad = calcularAlturaArbol(raiz);
            // Estimamos que cada nodo necesita unos 50-60px de ancho mínimo en el nivel más bajo
            int anchoVirtualDelArbol = (int) (Math.pow(2, profundidad - 1) * 50); 
            
            
            // Aseguramos un ancho mínimo para árboles pequeños
            if (anchoVirtualDelArbol < getWidth()) {
                anchoVirtualDelArbol = getWidth();
            }

            // 2. CALCULAMOS EL FACTOR DE ZOOM
            // Obtenemos el ancho visible del scroll (o del panel padre)
            double anchoVisible = (double) getParent().getWidth();
            double escala = 1.0;
            

            // Si el árbol es más grande que la vista, calculamos cuánto reducirlo
            if (anchoVirtualDelArbol > anchoVisible) {
                escala = anchoVisible / anchoVirtualDelArbol;
                
                // Opcional: Dejar un margen del 10% (multiplicar por 0.9) para que no toque los bordes
                escala = escala * 0.9; 
                
                
                // Opcional: Poner un límite mínimo para que no se haga microscópico
                // Por ejemplo, que nunca baje del 30% (0.3)
                if (escala < 0.7) escala = 0.7;
            }

            // 3. APLICAMOS LA ESCALA
            // Todo lo que se dibuje después de esto se verá afectado por el zoom
            AffineTransform transformacionOriginal = g2d.getTransform(); // Guardamos estado original
            g2d.scale(escala, escala);

            // 4. DIBUJAMOS
            // IMPORTANTE: Como hemos escalado el "mundo", ahora el centro X no es getWidth()/2
            // El centro es la mitad del ancho virtual que calculamos arriba.
            int centroX = anchoVirtualDelArbol / 2;
            
            // Ajustamos la separación inicial basada en el ancho virtual
            int separacionInicial = anchoVirtualDelArbol / 7;

            dibujarArbol(g, raiz, centroX, 50, separacionInicial);

            // Restauramos la transformación original (buena práctica)
            g2d.setTransform(transformacionOriginal);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // Mantenemos esto igual o ajustamos según necesidad.
        // Con el auto-zoom, el scroll horizontal ya no es tan necesario,
        // pero el vertical sí.
        return calcularDimensionesDeDibujo();
    }
};
    
    // 2. CONFIGURACIÓN DE COLORES Y TAMAÑO INICIAL
    arbol.setBackground(resaltar); // Tu color beige
    
    // Cálculo inicial de altura (opcional, ya que getPreferredSize lo maneja, pero está bien dejarlo)
    int alturaNecesaria = (raiz == null ? 1 : calcularAlturaArbol(raiz)) * 70 + 50; 
    arbol.setPreferredSize(new Dimension(1000, alturaNecesaria));

    // 3. CONFIGURACIÓN DEL SCROLL (Aquí va lo del borde)
    jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder()); // <--- SIN BORDE
    jScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane2.setViewportView(arbol);
}
    
    private Dimension calcularDimensionesDeDibujo() 
    {
        if (raiz == null) {
            return new Dimension(0, 0);
        }
    
        // 1. Calcular Altura
        int profundidad = calcularAlturaArbol(raiz);
        int alturaNecesaria = profundidad * 70 + 50; // 70px por nivel + margen

        // 2. Calcular Anchura
        int anchoMinimo = (int) (Math.pow(2, profundidad - 1)); 

        // Aseguramos que el ancho no sea menor que el ancho del JScrollPane para el scroll horizontal
        int anchoFinal = Math.max(anchoMinimo, jScrollPane2.getWidth()); 


        if (anchoFinal < 1200) 
        { // Un valor grande para empezar
             anchoFinal = 1200;
        }

        return new Dimension(anchoFinal, alturaNecesaria);
    }
    
    public int calcularAlturaArbol(Nodo nodo) 
    {
        if (nodo == null) 
            return 0;
        return 1 + Math.max(calcularAlturaArbol(nodo.getIzq()), calcularAlturaArbol(nodo.getDer()));
    }

    private void dibujarArbol(Graphics g, Nodo nodo, int x, int y, int separacion) {
        if (nodo != null) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int nodeRadius = 20; // Radio del círculo del nodo (40/2)

        // --- Dibujar líneas primero (para que los nodos queden encima) ---
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setColor(text);

        // Línea izquierda
        if (nodo.getIzq() != null) {
            // Calcular ángulo y puntos de inicio/fin ajustados
            double angle = Math.atan2((y + 70) - y, (x - separacion) - x);
            int startX = (int) (x + nodeRadius * Math.cos(angle - Math.PI)); // Desde borde del padre
            int startY = (int) (y + nodeRadius * Math.sin(angle - Math.PI));
            int endX = (int) ((x - separacion) - nodeRadius * Math.cos(angle)); // Hasta borde del hijo
            int endY = (int) ((y + 70) - nodeRadius * Math.sin(angle));
            g2d.drawLine(startX, startY, endX, endY);
        }

        // Línea derecha
        if (nodo.getDer() != null) {
            // Calcular ángulo y puntos de inicio/fin ajustados
            double angle = Math.atan2((y + 70) - y, (x + separacion) - x);
            int startX = (int) (x + nodeRadius * Math.cos(angle - Math.PI));
            int startY = (int) (y + nodeRadius * Math.sin(angle - Math.PI));
            int endX = (int) ((x + separacion) - nodeRadius * Math.cos(angle));
            int endY = (int) ((y + 70) - nodeRadius * Math.sin(angle));
            g2d.drawLine(startX, startY, endX, endY);
        }
        
        g2d.setColor(new Color(0, 0, 0, 30)); // Sombra semitransparente
        g2d.fillOval(x - 17, y - 17, 40, 40); // Ligeramente desplazada a la derecha y abajo

        // --- Dibujar nodo ---
        g2d.setFont(new Font("Tahoma", Font.BOLD, 14));
        g2d.setColor(new Color(132, 237, 109)); // Color de fondo del nodo
        g2d.fillOval(x - nodeRadius, y - nodeRadius, nodeRadius * 2, nodeRadius * 2);

        g2d.setColor(new Color(179, 242, 165)); // Color del borde del nodo
        g2d.setStroke(new BasicStroke(1)); // Borde más delgado que la línea de conexión
        g2d.drawOval(x - nodeRadius, y - nodeRadius, nodeRadius * 2, nodeRadius * 2);

        g2d.setColor(text); // Color del texto del nodo
        // Ajustar posición del texto para que esté centrado
        String nodeText = nodo.getDato().toString();
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(nodeText);
        int textHeight = fm.getHeight();
        int textX = x - textWidth / 2;
        int textY = y + (textHeight / 4); // Ajuste vertical para centrar mejor
        g2d.drawString(nodeText, textX, textY);

        // --- Llamadas recursivas para los hijos ---
        if (nodo.getIzq() != null) {
            dibujarArbol(g2d, nodo.getIzq(), x - separacion, y + 70, separacion / 2);
        }
        if (nodo.getDer() != null) {
            dibujarArbol(g2d, nodo.getDer(), x + separacion, y + 70, separacion / 2);
        }
    }
}

    // ======= CREAR ÁRBOL =======
    public void crearArbol() {
        String dato = pedirInputValido("Ingrese el dato de la raíz:");
        if (dato == null) return;
        raiz = new Nodo(dato, null, null);
        arbol.repaint();
        crearSubarbol(raiz);
        arbol.repaint();
        jScrollPane2.revalidate();
    }

    private void crearSubarbol(Nodo nodo) {
        // hijo izquierdo
        int respIzq = JOptionPane.showConfirmDialog(
                null,
                "¿Existe nodo por izquierda de " + nodo.getDato() + "?",
                "Insertar Nodo",
                JOptionPane.YES_NO_OPTION
        );

        if (respIzq == JOptionPane.YES_OPTION) {
            String datoIzq = pedirInputValido(
                    "Ingrese el dato del hijo izquierdo de " + nodo.getDato() + ":"
            );
            if (datoIzq != null) {
                nodo.setIzq(new Nodo(datoIzq, null, null));
                arbol.repaint();
                crearSubarbol(nodo.getIzq());
            }
        }

        // hijo derecho
        int respDer = JOptionPane.showConfirmDialog(
                null,
                "¿Existe nodo por derecha de " + nodo.getDato() + "?",
                "Insertar Nodo",
                JOptionPane.YES_NO_OPTION
        );

        if (respDer == JOptionPane.YES_OPTION) {
            String datoDer = pedirInputValido(
                    "Ingrese el dato del hijo derecho de " + nodo.getDato() + ":"
            );
            if (datoDer != null) {
                nodo.setDer(new Nodo(datoDer, null, null));
                arbol.repaint();
                crearSubarbol(nodo.getDer());
            }
        }
        arbol.repaint();
        jScrollPane2.revalidate();
    }

    private String pedirInputValido(String mensaje) {
        while (true) {
            String dato = JOptionPane.showInputDialog(mensaje);
            if (dato == null) return null; // Cancelado
            if (dato.matches("^[A-Z]+$")) {
                return dato;
            } else {
                JOptionPane.showMessageDialog(null, "Error: Solo se permiten letras mayúsculas (A-Z).", "Entrada inválida", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ======= RECORRIDOS =======
    public String preOrden(Nodo nodo) {
        if (nodo == null) return "";
        return nodo.getDato() + "||" + preOrden(nodo.getIzq()) + preOrden(nodo.getDer());
    }

    public String inOrden(Nodo nodo) {
        if (nodo == null) return "";
        return inOrden(nodo.getIzq()) + "||" + nodo.getDato() + "||" + inOrden(nodo.getDer());
    }

    public String postOrden(Nodo nodo) {
        if (nodo == null) return "";
        return postOrden(nodo.getIzq()) + postOrden(nodo.getDer())+ nodo.getDato() + "||";
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelArbol = new javax.swing.JPanel();
        containerButtons = new Clases.PanelRound();
        buttonCreate = new Clases.PanelRound();
        labelCreate = new javax.swing.JLabel();
        buttonReiniciar = new Clases.PanelRound();
        labelReiniciar = new javax.swing.JLabel();
        buttonRecorridos = new Clases.PanelRound();
        labelRecorridos = new javax.swing.JLabel();
        buttonBack = new Clases.PanelRound();
        labelBack = new javax.swing.JLabel();
        btnGuardar = new Clases.PanelRound();
        labelBack1 = new javax.swing.JLabel();
        containerRecorridos = new Clases.PanelRound();
        preordenContainer = new Clases.PanelRound();
        preorden = new javax.swing.JLabel();
        inorderContainer = new Clases.PanelRound();
        inorden = new javax.swing.JLabel();
        postordenContainer = new Clases.PanelRound();
        postorden = new javax.swing.JLabel();
        titlePre = new javax.swing.JLabel();
        titleIn = new javax.swing.JLabel();
        titlePost = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        arbol = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        panelArbol.setBackground(background);

        containerButtons.setBackground(new java.awt.Color(0, 153, 0));
        containerButtons.setRoundBottomRight(50);
        containerButtons.setRoundTopRight(50);

        buttonCreate.setBackground(new java.awt.Color(5, 255, 13));
        buttonCreate.setPreferredSize(new java.awt.Dimension(170, 60));
        buttonCreate.setRoundBottomLeft(60);
        buttonCreate.setRoundBottomRight(60);
        buttonCreate.setRoundTopLeft(60);
        buttonCreate.setRoundTopRight(60);
        buttonCreate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonCreateMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buttonCreateMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buttonCreateMouseExited(evt);
            }
        });

        labelCreate.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        labelCreate.setForeground(new java.awt.Color(78, 88, 97));
        labelCreate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCreate.setText("Crear ");
        labelCreate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelCreateMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelCreateMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout buttonCreateLayout = new javax.swing.GroupLayout(buttonCreate);
        buttonCreate.setLayout(buttonCreateLayout);
        buttonCreateLayout.setHorizontalGroup(
            buttonCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonCreateLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(labelCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        buttonCreateLayout.setVerticalGroup(
            buttonCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelCreate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        buttonReiniciar.setBackground(new java.awt.Color(5, 255, 13));
        buttonReiniciar.setPreferredSize(new java.awt.Dimension(170, 60));
        buttonReiniciar.setRoundBottomLeft(60);
        buttonReiniciar.setRoundBottomRight(60);
        buttonReiniciar.setRoundTopLeft(60);
        buttonReiniciar.setRoundTopRight(60);
        buttonReiniciar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonReiniciarMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buttonReiniciarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buttonReiniciarMouseExited(evt);
            }
        });
        buttonReiniciar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelReiniciar.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        labelReiniciar.setForeground(new java.awt.Color(78, 88, 97));
        labelReiniciar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelReiniciar.setText("Reiniciar");
        labelReiniciar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelReiniciarMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelReiniciarMouseEntered(evt);
            }
        });
        buttonReiniciar.add(labelReiniciar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, 40));

        buttonRecorridos.setBackground(new java.awt.Color(5, 255, 13));
        buttonRecorridos.setPreferredSize(new java.awt.Dimension(170, 60));
        buttonRecorridos.setRoundBottomLeft(60);
        buttonRecorridos.setRoundBottomRight(60);
        buttonRecorridos.setRoundTopLeft(60);
        buttonRecorridos.setRoundTopRight(60);
        buttonRecorridos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonRecorridosMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buttonRecorridosMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buttonRecorridosMouseExited(evt);
            }
        });
        buttonRecorridos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelRecorridos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        labelRecorridos.setForeground(new java.awt.Color(78, 88, 97));
        labelRecorridos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelRecorridos.setText("Resultados");
        labelRecorridos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRecorridosMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRecorridosMouseEntered(evt);
            }
        });
        buttonRecorridos.add(labelRecorridos, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 9, -1, 40));

        buttonBack.setBackground(new java.awt.Color(5, 255, 13));
        buttonBack.setPreferredSize(new java.awt.Dimension(170, 60));
        buttonBack.setRoundBottomLeft(60);
        buttonBack.setRoundBottomRight(60);
        buttonBack.setRoundTopLeft(60);
        buttonBack.setRoundTopRight(60);
        buttonBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonBackMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buttonBackMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buttonBackMouseExited(evt);
            }
        });

        labelBack.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        labelBack.setForeground(new java.awt.Color(78, 88, 97));
        labelBack.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelBack.setText("Atrás");
        labelBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelBackMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelBackMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout buttonBackLayout = new javax.swing.GroupLayout(buttonBack);
        buttonBack.setLayout(buttonBackLayout);
        buttonBackLayout.setHorizontalGroup(
            buttonBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonBackLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(labelBack, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        buttonBackLayout.setVerticalGroup(
            buttonBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonBackLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(labelBack, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        btnGuardar.setBackground(new java.awt.Color(5, 255, 13));
        btnGuardar.setPreferredSize(new java.awt.Dimension(170, 60));
        btnGuardar.setRoundBottomLeft(60);
        btnGuardar.setRoundBottomRight(60);
        btnGuardar.setRoundTopLeft(60);
        btnGuardar.setRoundTopRight(60);
        btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGuardarMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnGuardarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnGuardarMouseExited(evt);
            }
        });

        labelBack1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        labelBack1.setForeground(new java.awt.Color(78, 88, 97));
        labelBack1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelBack1.setText("Guardar");
        labelBack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelBack1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelBack1MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout btnGuardarLayout = new javax.swing.GroupLayout(btnGuardar);
        btnGuardar.setLayout(btnGuardarLayout);
        btnGuardarLayout.setHorizontalGroup(
            btnGuardarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnGuardarLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(labelBack1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        btnGuardarLayout.setVerticalGroup(
            btnGuardarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnGuardarLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(labelBack1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout containerButtonsLayout = new javax.swing.GroupLayout(containerButtons);
        containerButtons.setLayout(containerButtonsLayout);
        containerButtonsLayout.setHorizontalGroup(
            containerButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(containerButtonsLayout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(buttonCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(buttonReiniciar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69)
                .addComponent(buttonRecorridos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addComponent(buttonBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        containerButtonsLayout.setVerticalGroup(
            containerButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, containerButtonsLayout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addGroup(containerButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonRecorridos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonReiniciar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );

        containerRecorridos.setBackground(new java.awt.Color(0, 153, 0));
        containerRecorridos.setRoundBottomLeft(70);
        containerRecorridos.setRoundBottomRight(70);
        containerRecorridos.setRoundTopLeft(70);
        containerRecorridos.setRoundTopRight(70);

        preordenContainer.setBackground(hover);
        preordenContainer.setPreferredSize(new java.awt.Dimension(65, 22));
        preordenContainer.setRoundBottomLeft(50);
        preordenContainer.setRoundBottomRight(50);
        preordenContainer.setRoundTopLeft(50);
        preordenContainer.setRoundTopRight(50);

        preorden.setBackground(text);
        preorden.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        preorden.setForeground(text);
        preorden.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        preorden.setText("  ");

        javax.swing.GroupLayout preordenContainerLayout = new javax.swing.GroupLayout(preordenContainer);
        preordenContainer.setLayout(preordenContainerLayout);
        preordenContainerLayout.setHorizontalGroup(
            preordenContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preordenContainerLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(preorden, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        preordenContainerLayout.setVerticalGroup(
            preordenContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preordenContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(preorden, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        inorderContainer.setBackground(hover);
        inorderContainer.setPreferredSize(new java.awt.Dimension(65, 22));
        inorderContainer.setRoundBottomLeft(50);
        inorderContainer.setRoundBottomRight(50);
        inorderContainer.setRoundTopLeft(50);
        inorderContainer.setRoundTopRight(50);

        inorden.setBackground(text);
        inorden.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        inorden.setForeground(text);
        inorden.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inorden.setText("  ");

        javax.swing.GroupLayout inorderContainerLayout = new javax.swing.GroupLayout(inorderContainer);
        inorderContainer.setLayout(inorderContainerLayout);
        inorderContainerLayout.setHorizontalGroup(
            inorderContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inorderContainerLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(inorden, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                .addContainerGap())
        );
        inorderContainerLayout.setVerticalGroup(
            inorderContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inorderContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inorden, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        postordenContainer.setBackground(hover);
        postordenContainer.setPreferredSize(new java.awt.Dimension(65, 22));
        postordenContainer.setRoundBottomLeft(50);
        postordenContainer.setRoundBottomRight(50);
        postordenContainer.setRoundTopLeft(50);
        postordenContainer.setRoundTopRight(50);

        postorden.setBackground(text);
        postorden.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        postorden.setForeground(text);
        postorden.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        postorden.setText("  ");

        javax.swing.GroupLayout postordenContainerLayout = new javax.swing.GroupLayout(postordenContainer);
        postordenContainer.setLayout(postordenContainerLayout);
        postordenContainerLayout.setHorizontalGroup(
            postordenContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, postordenContainerLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(postorden, javax.swing.GroupLayout.PREFERRED_SIZE, 669, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        postordenContainerLayout.setVerticalGroup(
            postordenContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, postordenContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(postorden, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        titlePre.setBackground(text);
        titlePre.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        titlePre.setForeground(text);
        titlePre.setText("Pre-Orden");

        titleIn.setBackground(text);
        titleIn.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        titleIn.setForeground(text);
        titleIn.setText("In-Orden");

        titlePost.setBackground(text);
        titlePost.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        titlePost.setForeground(text);
        titlePost.setText("Pos-Orden");

        javax.swing.GroupLayout containerRecorridosLayout = new javax.swing.GroupLayout(containerRecorridos);
        containerRecorridos.setLayout(containerRecorridosLayout);
        containerRecorridosLayout.setHorizontalGroup(
            containerRecorridosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(containerRecorridosLayout.createSequentialGroup()
                .addGroup(containerRecorridosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(containerRecorridosLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(containerRecorridosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(containerRecorridosLayout.createSequentialGroup()
                                .addComponent(titlePost)
                                .addGap(27, 27, 27)
                                .addComponent(postordenContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 701, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(containerRecorridosLayout.createSequentialGroup()
                                .addComponent(preordenContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(52, 52, 52)
                                .addComponent(inorderContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(containerRecorridosLayout.createSequentialGroup()
                        .addGap(184, 184, 184)
                        .addComponent(titlePre)
                        .addGap(332, 332, 332)
                        .addComponent(titleIn)))
                .addContainerGap(77, Short.MAX_VALUE))
        );
        containerRecorridosLayout.setVerticalGroup(
            containerRecorridosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(containerRecorridosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(containerRecorridosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titlePre)
                    .addComponent(titleIn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(containerRecorridosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(preordenContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inorderContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(containerRecorridosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(containerRecorridosLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(titlePost)
                        .addContainerGap(50, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, containerRecorridosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(postordenContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        arbol.setPreferredSize(new java.awt.Dimension(1000, 1000));

        javax.swing.GroupLayout arbolLayout = new javax.swing.GroupLayout(arbol);
        arbol.setLayout(arbolLayout);
        arbolLayout.setHorizontalGroup(
            arbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1328, Short.MAX_VALUE)
        );
        arbolLayout.setVerticalGroup(
            arbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(arbol);

        javax.swing.GroupLayout panelArbolLayout = new javax.swing.GroupLayout(panelArbol);
        panelArbol.setLayout(panelArbolLayout);
        panelArbolLayout.setHorizontalGroup(
            panelArbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArbolLayout.createSequentialGroup()
                .addGroup(panelArbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(containerButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(panelArbolLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(containerRecorridos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(7, 7, 7))
        );
        panelArbolLayout.setVerticalGroup(
            panelArbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArbolLayout.createSequentialGroup()
                .addComponent(containerButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 571, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(containerRecorridos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69))
        );

        getContentPane().add(panelArbol, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonBackMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonBackMouseExited
        // TODO add your handling code here:
        buttonBack.setBackground(secundary);
    }//GEN-LAST:event_buttonBackMouseExited

    private void buttonBackMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonBackMouseEntered
        // TODO add your handling code here:
        buttonBack.setBackground(hover);
    }//GEN-LAST:event_buttonBackMouseEntered

    private void buttonBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonBackMouseClicked
        // TODO add your handling code here:
        Presentacion frame = new Presentacion();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_buttonBackMouseClicked

    private void labelBackMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBackMouseEntered
        // TODO add your handling code here:
        buttonBack.setBackground(hover);
    }//GEN-LAST:event_labelBackMouseEntered

    private void labelBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBackMouseClicked
        // TODO add your handling code here:
        Presentacion frame = new Presentacion();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_labelBackMouseClicked

    private void buttonRecorridosMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonRecorridosMouseExited
        // TODO add your handling code here:
        buttonRecorridos.setBackground(secundary);
    }//GEN-LAST:event_buttonRecorridosMouseExited

    private void buttonRecorridosMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonRecorridosMouseEntered
        // TODO add your handling code here:
        buttonRecorridos.setBackground(hover);
    }//GEN-LAST:event_buttonRecorridosMouseEntered

    private void buttonRecorridosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonRecorridosMouseClicked
        // TODO add your handling code here:
        String pre = preOrden(raiz);
        String in = inOrden(raiz);
        String post = postOrden(raiz);

        preorden.setText(pre);
        inorden.setText(in);
        postorden.setText(post);
    }//GEN-LAST:event_buttonRecorridosMouseClicked

    private void labelRecorridosMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRecorridosMouseEntered
        // TODO add your handling code here:
        buttonRecorridos.setBackground(hover);
    }//GEN-LAST:event_labelRecorridosMouseEntered

    private void labelRecorridosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRecorridosMouseClicked
        // TODO add your handling code here:
        String pre = preOrden(raiz);
        String in = inOrden(raiz);
        String post = postOrden(raiz);

        preorden.setText(pre);
        inorden.setText(in);
        postorden.setText(post);
    }//GEN-LAST:event_labelRecorridosMouseClicked

    private void buttonReiniciarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonReiniciarMouseExited
        // TODO add your handling code here:
        buttonReiniciar.setBackground(secundary);
    }//GEN-LAST:event_buttonReiniciarMouseExited

    private void buttonReiniciarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonReiniciarMouseEntered
        // TODO add your handling code here:
        buttonReiniciar.setBackground(hover);
    }//GEN-LAST:event_buttonReiniciarMouseEntered

    private void buttonReiniciarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonReiniciarMouseClicked
        // TODO add your handling code here:
        raiz = null;
        panelArbol.repaint();
    }//GEN-LAST:event_buttonReiniciarMouseClicked

    private void labelReiniciarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelReiniciarMouseEntered
        // TODO add your handling code here:
        buttonReiniciar.setBackground(hover);
    }//GEN-LAST:event_labelReiniciarMouseEntered

    private void labelReiniciarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelReiniciarMouseClicked
        // TODO add your handling code here:
        raiz = null;
        panelArbol.repaint();
    }//GEN-LAST:event_labelReiniciarMouseClicked

    private void buttonCreateMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonCreateMouseExited
        // TODO add your handling code here:
        buttonCreate.setBackground(secundary);
    }//GEN-LAST:event_buttonCreateMouseExited

    private void buttonCreateMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonCreateMouseEntered
        // TODO add your handling code here:
        buttonCreate.setBackground(hover);
    }//GEN-LAST:event_buttonCreateMouseEntered

    private void buttonCreateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonCreateMouseClicked
        // TODO add your handling code here:
        crearArbol();
    }//GEN-LAST:event_buttonCreateMouseClicked

    private void labelCreateMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelCreateMouseEntered
        // TODO add your handling code here:
        buttonCreate.setBackground(hover);
    }//GEN-LAST:event_labelCreateMouseEntered

    private void labelCreateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelCreateMouseClicked
        // TODO add your handling code here:
        crearArbol();
    }//GEN-LAST:event_labelCreateMouseClicked

    private void labelBack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBack1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_labelBack1MouseClicked

    private void labelBack1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBack1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelBack1MouseEntered

    private void btnGuardarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarMouseClicked
        // TODO add your handling code here:
        try {
    // 1. Crear una imagen vacía del tamaño del panel 'arbol'
    BufferedImage imagen = new BufferedImage(
        arbol.getWidth(), 
        arbol.getHeight(), 
        BufferedImage.TYPE_INT_RGB
    );
    
    // 2. "Pintar" el panel dentro de esa imagen
    Graphics2D g2 = imagen.createGraphics();
    arbol.paint(g2); // Llama al método paint que ya programaste
    g2.dispose();

    // 3. Guardar el archivo
    javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
    if (fileChooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
        File archivo = fileChooser.getSelectedFile();
        // Asegurar extensión .png
        if (!archivo.getName().toLowerCase().endsWith(".png")) {
            archivo = new File(archivo.getAbsolutePath() + ".png");
        }
        javax.imageio.ImageIO.write(imagen, "png", archivo);
        JOptionPane.showMessageDialog(this, "Imagen guardada exitosamente.");
    }
} catch (Exception ex) {
    ex.printStackTrace();
}
       
        
        
    }//GEN-LAST:event_btnGuardarMouseClicked

    private void btnGuardarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuardarMouseEntered

    private void btnGuardarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuardarMouseExited
  
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ArbolBinario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ArbolBinario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ArbolBinario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ArbolBinario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ArbolBinario().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel arbol;
    private Clases.PanelRound btnGuardar;
    private Clases.PanelRound buttonBack;
    private Clases.PanelRound buttonCreate;
    private Clases.PanelRound buttonRecorridos;
    private Clases.PanelRound buttonReiniciar;
    private Clases.PanelRound containerButtons;
    private Clases.PanelRound containerRecorridos;
    private javax.swing.JLabel inorden;
    private Clases.PanelRound inorderContainer;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelBack;
    private javax.swing.JLabel labelBack1;
    private javax.swing.JLabel labelCreate;
    private javax.swing.JLabel labelRecorridos;
    private javax.swing.JLabel labelReiniciar;
    private javax.swing.JPanel panelArbol;
    private javax.swing.JLabel postorden;
    private Clases.PanelRound postordenContainer;
    private javax.swing.JLabel preorden;
    private Clases.PanelRound preordenContainer;
    private javax.swing.JLabel titleIn;
    private javax.swing.JLabel titlePost;
    private javax.swing.JLabel titlePre;
    // End of variables declaration//GEN-END:variables
}

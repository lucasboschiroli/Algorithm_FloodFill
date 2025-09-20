import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public class FloodFillApp extends JFrame {

    private CanvasPanel canvasOriginal;
    private CanvasPanel canvasPilha;
    private CanvasPanel canvasFila;

    private int[][] matrizOriginal;
    private int[][] matrizPilha;
    private int[][] matrizFila;

    private static final int CANVAS_SIZE = 400;
    private static final int GRID_SIZE = 20;
    private static final int PIXEL_SIZE = CANVAS_SIZE / GRID_SIZE;

    private JLabel statusLabel;
    private JLabel coordenadasLabel;
    private JButton btnCarregarImagem;
    private JButton btnIniciarFloodFill;
    private JButton btnResetar;
    private JButton btnSalvarResultado;
    private JButton btnGerarNovaImagem;
    private JCheckBox chkAnimacao;
    private JSlider sliderVelocidade;

    private int clickX = -1, clickY = -1;
    private boolean floodFillEmAndamento = false;
    private ProcessadorImagem processador;

    public FloodFillApp() {
        // Inicializa o processador para salvar animações
        this.processador = new ProcessadorImagem("flood_fill_gui_output");

        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setTitle("Flood Fill Visualizer - Pilha (Vermelho) vs Fila (Verde)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Criar matriz inicial
        criarMatrizExemplo();
        atualizarCanvas();

        pack();
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // Canvas panels
        canvasOriginal = new CanvasPanel("Original - Clique para escolher ponto");
        canvasPilha = new CanvasPanel("Pilha (DFS) - Vermelho");
        canvasFila = new CanvasPanel("Fila (BFS) - Verde");

        // Botões
        btnCarregarImagem = new JButton("Carregar Imagem");
        btnIniciarFloodFill = new JButton("Iniciar Flood Fill");
        btnResetar = new JButton("Resetar");
        btnSalvarResultado = new JButton("Salvar Resultado");
        btnGerarNovaImagem = new JButton("Nova Imagem Aleatória");

        // Controles
        chkAnimacao = new JCheckBox("Animação", true);
        sliderVelocidade = new JSlider(1, 100, 30);
        sliderVelocidade.setPaintTicks(true);
        sliderVelocidade.setPaintLabels(true);
        sliderVelocidade.setMajorTickSpacing(25);

        // Labels
        statusLabel = new JLabel("Clique no canvas original para escolher o ponto inicial");
        coordenadasLabel = new JLabel("Coordenadas: -");

        // Estilizar componentes
        estilizarComponentes();
    }

    private void estilizarComponentes() {
        btnCarregarImagem.setBackground(new Color(76, 175, 80));
        btnCarregarImagem.setForeground(Color.WHITE);
        btnCarregarImagem.setFocusPainted(false);

        btnGerarNovaImagem.setBackground(new Color(156, 39, 176));
        btnGerarNovaImagem.setForeground(Color.WHITE);
        btnGerarNovaImagem.setFocusPainted(false);

        btnIniciarFloodFill.setBackground(new Color(255, 152, 0));
        btnIniciarFloodFill.setForeground(Color.WHITE);
        btnIniciarFloodFill.setFocusPainted(false);
        btnIniciarFloodFill.setFont(btnIniciarFloodFill.getFont().deriveFont(Font.BOLD, 14f));

        btnResetar.setBackground(new Color(244, 67, 54));
        btnResetar.setForeground(Color.WHITE);
        btnResetar.setFocusPainted(false);

        btnSalvarResultado.setBackground(new Color(33, 150, 243));
        btnSalvarResultado.setForeground(Color.WHITE);
        btnSalvarResultado.setFocusPainted(false);

        statusLabel.setFont(statusLabel.getFont().deriveFont(14f));
        coordenadasLabel.setFont(coordenadasLabel.getFont().deriveFont(12f));
        coordenadasLabel.setForeground(new Color(25, 118, 210));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("Flood Fill: Pilha (DFS-Vermelho) vs Fila (BFS-Verde)", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        titulo.setForeground(new Color(25, 118, 210));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // Canvas container
        JPanel canvasContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        canvasContainer.add(canvasOriginal);
        canvasContainer.add(canvasPilha);
        canvasContainer.add(canvasFila);

        // Controles
        JPanel controles1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        controles1.add(btnCarregarImagem);
        controles1.add(btnGerarNovaImagem);
        controles1.add(btnIniciarFloodFill);
        controles1.add(btnResetar);
        controles1.add(btnSalvarResultado);

        JPanel controles2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        controles2.add(chkAnimacao);
        controles2.add(new JLabel("Velocidade:"));
        controles2.add(sliderVelocidade);

        JPanel controlesContainer = new JPanel(new GridLayout(2, 1, 0, 10));
        controlesContainer.add(controles1);
        controlesContainer.add(controles2);

        // Status
        JPanel statusContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statusContainer.add(statusLabel);
        statusContainer.add(coordenadasLabel);

        // Layout principal
        add(titulo, BorderLayout.NORTH);
        add(canvasContainer, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.add(controlesContainer);
        bottomPanel.add(statusContainer);
        add(bottomPanel, BorderLayout.SOUTH);

        // Cor de fundo
        Color bgColor = new Color(240, 248, 255);
        getContentPane().setBackground(bgColor);
        canvasContainer.setBackground(bgColor);
        bottomPanel.setBackground(bgColor);
        controlesContainer.setBackground(bgColor);
        statusContainer.setBackground(bgColor);
        controles1.setBackground(bgColor);
        controles2.setBackground(bgColor);
    }

    private void setupEventHandlers() {
        btnCarregarImagem.addActionListener(e -> carregarImagem());
        btnIniciarFloodFill.addActionListener(e -> iniciarFloodFill());
        btnResetar.addActionListener(e -> resetarVisualizacao());
        btnSalvarResultado.addActionListener(e -> salvarResultado());
        btnGerarNovaImagem.addActionListener(e -> gerarNovaImagemAleatoria());

        canvasOriginal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleCanvasClick(e);
            }
        });

        canvasOriginal.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleCanvasMouseMove(e);
            }
        });
    }

    private void handleCanvasClick(MouseEvent e) {
        if (floodFillEmAndamento) return;

        clickX = e.getX() / PIXEL_SIZE;
        clickY = e.getY() / PIXEL_SIZE;

        if (clickX >= 0 && clickX < GRID_SIZE && clickY >= 0 && clickY < GRID_SIZE) {
            atualizarCanvas();
            statusLabel.setText(String.format("Ponto selecionado: (%d, %d) - Pronto para Flood Fill!", clickX, clickY));
            coordenadasLabel.setText(String.format("Coordenadas: (%d, %d)", clickX, clickY));
        }
    }

    private void handleCanvasMouseMove(MouseEvent e) {
        if (floodFillEmAndamento) return;

        int mouseX = e.getX() / PIXEL_SIZE;
        int mouseY = e.getY() / PIXEL_SIZE;

        if (mouseX >= 0 && mouseX < GRID_SIZE && mouseY >= 0 && mouseY < GRID_SIZE) {
            coordenadasLabel.setText(String.format("Coordenadas: (%d, %d)", mouseX, mouseY));
        }
    }

    // Canvas customizado
    class CanvasPanel extends JPanel {
        private String titulo;

        public CanvasPanel(String titulo) {
            this.titulo = titulo;
            setPreferredSize(new Dimension(CANVAS_SIZE + 30, CANVAS_SIZE + 50));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                    BorderFactory.createEmptyBorder(10, 15, 15, 15)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Título
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 12f));
            g2d.setColor(new Color(47, 79, 79));
            FontMetrics fm = g2d.getFontMetrics();
            int titleX = (getWidth() - fm.stringWidth(titulo)) / 2;
            g2d.drawString(titulo, titleX, 20);

            // Desenhar grid
            if (this == canvasOriginal && matrizOriginal != null) {
                desenharMatriz(g2d, matrizOriginal, 15, 30);
                if (clickX != -1 && clickY != -1) {
                    g2d.setColor(Color.RED);
                    g2d.fillOval(15 + clickX * PIXEL_SIZE + PIXEL_SIZE/4,
                            30 + clickY * PIXEL_SIZE + PIXEL_SIZE/4,
                            PIXEL_SIZE/2, PIXEL_SIZE/2);
                }
            } else if (this == canvasPilha && matrizPilha != null) {
                desenharMatriz(g2d, matrizPilha, 15, 30);
            } else if (this == canvasFila && matrizFila != null) {
                desenharMatriz(g2d, matrizFila, 15, 30);
            }

            g2d.dispose();
        }

        private void desenharMatriz(Graphics2D g2d, int[][] matriz, int offsetX, int offsetY) {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    Color cor = intToColor(matriz[i][j]);
                    g2d.setColor(cor);
                    g2d.fillRect(offsetX + j * PIXEL_SIZE, offsetY + i * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
                }
            }
        }
    }

    private Color intToColor(int colorInt) {
        if (colorInt == GerenciarCores.obterCorBranca()) return Color.WHITE;
        if (colorInt == GerenciarCores.obterCorPreta()) return Color.BLACK;
        if (colorInt == GerenciarCores.obterCorVermelha()) return Color.RED;
        if (colorInt == GerenciarCores.obterCorVerde()) return Color.GREEN;
        if (colorInt == GerenciarCores.obterCorAzul()) return Color.BLUE;
        return Color.GRAY;
    }

    private void criarMatrizExemplo() {
        matrizOriginal = new int[GRID_SIZE][GRID_SIZE];

        // Preencher com branco
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                matrizOriginal[i][j] = GerenciarCores.obterCorBranca();
            }
        }

        criarFormasExemplo();
        copiarMatrizes();
    }

    private void criarFormasExemplo() {
        // Bordas
        for (int i = 0; i < GRID_SIZE; i++) {
            matrizOriginal[0][i] = GerenciarCores.obterCorPreta();
            matrizOriginal[GRID_SIZE-1][i] = GerenciarCores.obterCorPreta();
            matrizOriginal[i][0] = GerenciarCores.obterCorPreta();
            matrizOriginal[i][GRID_SIZE-1] = GerenciarCores.obterCorPreta();
        }

        // Retângulo central
        for (int i = 5; i < 15; i++) {
            for (int j = 5; j < 15; j++) {
                if (i == 5 || i == 14 || j == 5 || j == 14) {
                    matrizOriginal[i][j] = GerenciarCores.obterCorPreta();
                }
            }
        }

        // Círculo
        int centerX = 10, centerY = 10, radius = 3;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                double distance = Math.sqrt((i - centerY) * (i - centerY) + (j - centerX) * (j - centerX));
                if (Math.abs(distance - radius) < 0.5) {
                    matrizOriginal[i][j] = GerenciarCores.obterCorPreta();
                }
            }
        }
    }

    private void gerarNovaImagemAleatoria() {
        if (floodFillEmAndamento) {
            JOptionPane.showMessageDialog(this, "Aguarde a conclusão do Flood Fill atual.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        matrizOriginal = new int[GRID_SIZE][GRID_SIZE];

        // Preencher com branco
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                matrizOriginal[i][j] = GerenciarCores.obterCorBranca();
            }
        }

        // Gerar formas aleatórias
        java.util.Random rand = new java.util.Random();

        // Bordas sempre presentes
        for (int i = 0; i < GRID_SIZE; i++) {
            matrizOriginal[0][i] = GerenciarCores.obterCorPreta();
            matrizOriginal[GRID_SIZE-1][i] = GerenciarCores.obterCorPreta();
            matrizOriginal[i][0] = GerenciarCores.obterCorPreta();
            matrizOriginal[i][GRID_SIZE-1] = GerenciarCores.obterCorPreta();
        }

        // Linhas aleatórias
        for (int k = 0; k < rand.nextInt(3) + 2; k++) {
            int linha = rand.nextInt(GRID_SIZE - 4) + 2;
            int inicio = rand.nextInt(GRID_SIZE/2) + 2;
            int fim = rand.nextInt(GRID_SIZE/2) + GRID_SIZE/2;

            for (int j = inicio; j < fim; j++) {
                matrizOriginal[linha][j] = GerenciarCores.obterCorPreta();
            }
        }

        copiarMatrizes();
        atualizarCanvas();
        clickX = -1;
        clickY = -1;
        statusLabel.setText("Nova imagem aleatória gerada! Clique para escolher ponto inicial.");
        coordenadasLabel.setText("Coordenadas: -");
    }

    private void copiarMatrizes() {
        matrizPilha = new int[GRID_SIZE][GRID_SIZE];
        matrizFila = new int[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                matrizPilha[i][j] = matrizOriginal[i][j];
                matrizFila[i][j] = matrizOriginal[i][j];
            }
        }
    }

    private void atualizarCanvas() {
        SwingUtilities.invokeLater(() -> {
            canvasOriginal.repaint();
            canvasPilha.repaint();
            canvasFila.repaint();
        });
    }

    private void carregarImagem() {
        if (floodFillEmAndamento) {
            JOptionPane.showMessageDialog(this, "Aguarde a conclusão do Flood Fill atual.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imagens", "png", "jpg", "jpeg", "bmp", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            try {
                int[][] novaMatriz = processador.carregarImagem(arquivo.getAbsolutePath());
                matrizOriginal = redimensionarMatriz(novaMatriz, GRID_SIZE, GRID_SIZE);
                copiarMatrizes();
                atualizarCanvas();

                clickX = -1;
                clickY = -1;
                statusLabel.setText("Imagem carregada com sucesso! Clique para escolher ponto inicial.");
                coordenadasLabel.setText("Coordenadas: -");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar imagem: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int[][] redimensionarMatriz(int[][] original, int novaLargura, int novaAltura) {
        int alturaOriginal = original.length;
        int larguraOriginal = original[0].length;
        int[][] nova = new int[novaAltura][novaLargura];

        for (int i = 0; i < novaAltura; i++) {
            for (int j = 0; j < novaLargura; j++) {
                int origI = (i * alturaOriginal) / novaAltura;
                int origJ = (j * larguraOriginal) / novaLargura;
                nova[i][j] = original[origI][origJ];
            }
        }

        return nova;
    }

    private void iniciarFloodFill() {
        if (clickX == -1 || clickY == -1) {
            JOptionPane.showMessageDialog(this, "Clique no canvas original para escolher um ponto inicial.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (floodFillEmAndamento) {
            JOptionPane.showMessageDialog(this, "Flood Fill já está em andamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        executarFloodFillComClassesOriginais();
    }

    private void executarFloodFillComClassesOriginais() {
        floodFillEmAndamento = true;
        btnIniciarFloodFill.setEnabled(false);
        statusLabel.setText("Executando Flood Fill... Aguarde!");

        copiarMatrizes();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Usar as classes originais FloodFillAlgoritmo
                ProcessadorImagem processadorPilha = new ProcessadorImagem("flood_fill_gui_output/pilha");
                ProcessadorImagem processadorFila = new ProcessadorImagem("flood_fill_gui_output/fila");

                // Executar Pilha com animação (Vermelho)
                CompletableFuture<Void> futurePilha = CompletableFuture.runAsync(() -> {
                    SwingFloodFillAnimado floodFillPilha = new SwingFloodFillAnimado(
                            matrizPilha, canvasPilha, processadorPilha, "pilha");
                    floodFillPilha.preencherComPilhaAnimado(clickX, clickY,
                            GerenciarCores.obterCorVermelha(),
                            chkAnimacao.isSelected() ? sliderVelocidade.getValue() : 0);
                });

                // Executar Fila com animação (Verde)
                CompletableFuture<Void> futureFila = CompletableFuture.runAsync(() -> {
                    SwingFloodFillAnimado floodFillFila = new SwingFloodFillAnimado(
                            matrizFila, canvasFila, processadorFila, "fila");
                    floodFillFila.preencherComFilaAnimado(clickX, clickY,
                            GerenciarCores.obterCorVerde(),
                            chkAnimacao.isSelected() ? sliderVelocidade.getValue() : 0);
                });

                CompletableFuture.allOf(futurePilha, futureFila).join();
                return null;
            }

            @Override
            protected void done() {
                floodFillEmAndamento = false;
                btnIniciarFloodFill.setEnabled(true);
                statusLabel.setText("Flood Fill concluído! Pilha=Vermelho, Fila=Verde. Animações salvas!");
            }
        };

        worker.execute();
    }

    private void resetarVisualizacao() {
        if (floodFillEmAndamento) {
            JOptionPane.showMessageDialog(this, "Aguarde a conclusão do Flood Fill atual.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        criarMatrizExemplo();
        atualizarCanvas();
        clickX = -1;
        clickY = -1;
        statusLabel.setText("Visualização resetada. Clique no canvas original para escolher um novo ponto inicial.");
        coordenadasLabel.setText("Coordenadas: -");
    }

    private void salvarResultado() {
        if (matrizPilha == null || matrizFila == null) {
            JOptionPane.showMessageDialog(this, "Nenhum resultado para salvar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            processador.salvarImagemFinal(matrizPilha, "gui_pilha_resultado");
            processador.salvarImagemFinal(matrizFila, "gui_fila_resultado");

            statusLabel.setText("Resultados salvos na pasta 'flood_fill_gui_output'!");
            JOptionPane.showMessageDialog(this, "Resultados salvos com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar resultados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

// Classe auxiliar para animação usando as classes originais
class SwingFloodFillAnimado {
    private int[][] matriz;
    private Component canvas;
    private int largura;
    private int altura;
    private ProcessadorImagem processador;
    private String tipo;
    private int frameCount = 0;

    public SwingFloodFillAnimado(int[][] matriz, Component canvas, ProcessadorImagem processador, String tipo) {
        this.matriz = matriz;
        this.canvas = canvas;
        this.altura = matriz.length;
        this.largura = matriz[0].length;
        this.processador = processador;
        this.tipo = tipo;
    }

    public void preencherComPilhaAnimado(int x, int y, int novaCor, int velocidade) {
        PilhaPixel pilha = new PilhaPixel();
        executarFloodFillAnimado(pilha, x, y, novaCor, velocidade);
    }

    public void preencherComFilaAnimado(int x, int y, int novaCor, int velocidade) {
        FilaPixel fila = new FilaPixel();
        executarFloodFillAnimado(fila, x, y, novaCor, velocidade);
    }

    private void executarFloodFillAnimado(EstruturaPixel estrutura, int x, int y, int novaCor, int velocidade) {
        if (!coordenadaValida(x, y)) return;

        int corOriginal = matriz[y][x];
        if (GerenciarCores.coresSaoIguais(corOriginal, novaCor)) return;

        estrutura.adicionar(new Pixel(x, y));
        int pixelsProcessados = 0;

        while (!estrutura.estaVazia()) {
            Pixel pixelAtual = estrutura.remover();
            if (pixelAtual == null) continue;

            int px = pixelAtual.getX();
            int py = pixelAtual.getY();

            if (coordenadaValida(px, py) && GerenciarCores.coresSaoIguais(matriz[py][px], corOriginal)) {
                matriz[py][px] = novaCor;
                pixelsProcessados++;

                // Salvar frame de animação a cada 10 pixels para GUI (mais frequente que os 50 do console)
                if (pixelsProcessados % 10 == 0) {
                    frameCount++;
                    processador.salvarImagemAnimacao(matriz, frameCount, tipo);
                }

                // Atualizar canvas na thread da UI
                SwingUtilities.invokeLater(() -> {
                    canvas.repaint();
                });

                // Pausa para animação
                if (velocidade > 0) {
                    try {
                        Thread.sleep(Math.max(1, 101 - velocidade));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // Adicionar vizinhos
                adicionarVizinhos(estrutura, px, py);
            }
        }

        // Salvar imagem final
        processador.salvarImagemFinal(matriz, tipo + "_final");
    }

    private void adicionarVizinhos(EstruturaPixel estrutura, int x, int y) {
        if (coordenadaValida(x, y - 1)) estrutura.adicionar(new Pixel(x, y - 1));
        if (coordenadaValida(x, y + 1)) estrutura.adicionar(new Pixel(x, y + 1));
        if (coordenadaValida(x - 1, y)) estrutura.adicionar(new Pixel(x - 1, y));
        if (coordenadaValida(x + 1, y)) estrutura.adicionar(new Pixel(x + 1, y));
    }

    private boolean coordenadaValida(int x, int y) {
        return x >= 0 && x < largura && y >= 0 && y < altura;
    }
}
}
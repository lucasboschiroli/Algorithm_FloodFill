class FloodFillAlgoritmo {
    private int[][] matriz;
    private int largura;
    private int altura;
    private int corOriginal;
    private int novaCor;
    private EstruturaPixel estrutura;
    private ProcessadorImagem processador;

    public FloodFillAlgoritmo(int[][] matriz, ProcessadorImagem processador) {
        this.matriz = matriz;
        this.altura = matriz.length;
        this.largura = matriz[0].length;
        this.processador = processador;
    }

    public void preencherComPilha(int x, int y, int novaCor) {
        this.estrutura = new PilhaPixel();
        executarFloodFill(x, y, novaCor, "Pilha");
    }

    public void preencherComFila(int x, int y, int novaCor) {
        this.estrutura = new FilaPixel();
        executarFloodFill(x, y, novaCor, "Fila");
    }

    private void executarFloodFill(int x, int y, int novaCor, String tipoEstrutura) {
        if (!coordenadaValida(x, y)) {
            System.out.println("Coordenada inicial inválida!");
            return;
        }

        this.corOriginal = matriz[y][x];
        this.novaCor = novaCor;

        // Se a cor já for a mesma, não há necessidade de preencher
        if (GerenciarCores.coresSaoIguais(corOriginal, novaCor)) {
            System.out.println("A cor original já é igual à nova cor!");
            return;
        }

        System.out.println("Iniciando Flood Fill com " + tipoEstrutura);
        System.out.println("Posição inicial: (" + x + ", " + y + ")");
        System.out.println("Cor original: " + Integer.toHexString(corOriginal));
        System.out.println("Nova cor: " + Integer.toHexString(novaCor));

        // Adiciona o pixel inicial à estrutura
        estrutura.adicionar(new Pixel(x, y));

        int pixelsProcessados = 0;
        int frameCount = 0;

        // Loop principal do algoritmo
        while (!estrutura.estaVazia()) {
            Pixel pixelAtual = estrutura.remover();

            if (pixelAtual == null) {
                continue;
            }

            int px = pixelAtual.getX();
            int py = pixelAtual.getY();

            // Verifica se o pixel é válido e tem a cor original
            if (coordenadaValida(px, py) &&
                    GerenciarCores.coresSaoIguais(matriz[py][px], corOriginal)) {

                // Pinta o pixel atual
                matriz[py][px] = novaCor;
                pixelsProcessados++;

                // ATUALIZAÇÃO: Salva imagem a cada pixel modificado (conforme requisito)
                frameCount++;
                processador.salvarImagemAnimacao(matriz, frameCount, tipoEstrutura);

                // Log a cada 50 pixels para não poluir o console
                if (pixelsProcessados % 50 == 0) {
                    System.out.println("Progresso: " + pixelsProcessados + " pixels processados");
                }

                // Adiciona os 4 vizinhos à estrutura
                adicionarVizinhos(px, py);
            }
        }

        System.out.println("Flood Fill concluído! Pixels processados: " + pixelsProcessados);
        System.out.println("Total de frames de animação salvos: " + frameCount);

        // Salva a imagem final
        processador.salvarImagemFinal(matriz, tipoEstrutura.toLowerCase());
    }

    private void adicionarVizinhos(int x, int y) {
        // Vizinho superior
        if (coordenadaValida(x, y - 1)) {
            estrutura.adicionar(new Pixel(x, y - 1));
        }

        // Vizinho inferior
        if (coordenadaValida(x, y + 1)) {
            estrutura.adicionar(new Pixel(x, y + 1));
        }

        // Vizinho esquerdo
        if (coordenadaValida(x - 1, y)) {
            estrutura.adicionar(new Pixel(x - 1, y));
        }

        // Vizinho direito
        if (coordenadaValida(x + 1, y)) {
            estrutura.adicionar(new Pixel(x + 1, y));
        }
    }

    private boolean coordenadaValida(int x, int y) {
        return x >= 0 && x < largura && y >= 0 && y < altura;
    }

    public void imprimirMatriz() {
        System.out.println("Estado atual da matriz:");

        // Para matrizes grandes, imprimir apenas uma amostra
        int alturaParaImprimir = Math.min(altura, 15);
        int larguraParaImprimir = Math.min(largura, 30);

        if (altura > 15 || largura > 30) {
            System.out.println("(Mostrando amostra " + alturaParaImprimir + "x" + larguraParaImprimir + " de " + altura + "x" + largura + ")");
        }

        for (int i = 0; i < alturaParaImprimir; i++) {
            for (int j = 0; j < larguraParaImprimir; j++) {
                if (matriz[i][j] == GerenciarCores.obterCorBranca()) {
                    System.out.print("B ");
                } else if (matriz[i][j] == GerenciarCores.obterCorPreta()) {
                    System.out.print("P ");
                } else if (matriz[i][j] == GerenciarCores.obterCorVermelha()) {
                    System.out.print("V ");
                } else if (matriz[i][j] == GerenciarCores.obterCorAzul()) {
                    System.out.print("A ");
                } else if (matriz[i][j] == GerenciarCores.obterCorVerde()) {
                    System.out.print("G ");
                } else {
                    System.out.print("? ");
                }
            }
            System.out.println();
        }

        if (altura > 15 || largura > 30) {
            System.out.println("... (matriz completa salva nas imagens)");
        }

        System.out.println();
    }

    // Método adicional para verificar se o preenchimento é possível
    public boolean podePreencherPonto(int x, int y, int novaCor) {
        if (!coordenadaValida(x, y)) {
            return false;
        }

        int corAtual = matriz[y][x];
        return !GerenciarCores.coresSaoIguais(corAtual, novaCor);
    }

    // Método para obter informações sobre a matriz
    public String obterInformacoesMatriz() {
        int pixelsBrancos = 0;
        int pixelsPretos = 0;
        int pixelsColoridos = 0;

        for (int i = 0; i < altura; i++) {
            for (int j = 0; j < largura; j++) {
                if (matriz[i][j] == GerenciarCores.obterCorBranca()) {
                    pixelsBrancos++;
                } else if (matriz[i][j] == GerenciarCores.obterCorPreta()) {
                    pixelsPretos++;
                } else {
                    pixelsColoridos++;
                }
            }
        }

        return String.format("Matriz %dx%d - Brancos: %d, Pretos: %d, Coloridos: %d",
                largura, altura, pixelsBrancos, pixelsPretos, pixelsColoridos);
    }
}
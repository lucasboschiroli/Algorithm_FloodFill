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

                // Salva imagem a cada 50 pixels processados (para animação)
                if (pixelsProcessados % 50 == 0) {
                    processador.salvarImagemAnimacao(matriz, pixelsProcessados, tipoEstrutura);
                }

                // Adiciona os 4 vizinhos à estrutura
                adicionarVizinhos(px, py);
            }
        }

        System.out.println("Flood Fill concluído! Pixels processados: " + pixelsProcessados);

        // Salva a imagem final
        processador.salvarImagemFinal(matriz, tipoEstrutura);
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
        for (int i = 0; i < altura; i++) {
            for (int j = 0; j < largura; j++) {
                if (matriz[i][j] == GerenciarCores.obterCorBranca()) {
                    System.out.print("B ");
                } else if (matriz[i][j] == GerenciarCores.obterCorPreta()) {
                    System.out.print("P ");
                } else if (matriz[i][j] == GerenciarCores.obterCorVermelha()) {
                    System.out.print("V ");
                } else if (matriz[i][j] == GerenciarCores.obterCorAzul()) {
                    System.out.print("A ");
                } else {
                    System.out.print("? ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
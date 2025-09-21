import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("TDE01 - FLOOD FILL");
        System.out.println("==========================================");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Escolha o modo de execução:");
        System.out.println("1 - Interface gráfica interativa (comparação visual Pilha vs Fila)");
        System.out.println("2 - Exemplo automático com imagem input/input.png (demonstração completa)");
        System.out.print("Opção: ");

        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpa buffer

        switch (opcao) {
            case 1:
                executarInterfaceGrafica();
                break;
            case 2:
                executarExemploAutomatico();
                break;
            default:
                System.out.println("Opção inválida. Executando exemplo automático...");
                executarExemploAutomatico();
        }

        scanner.close();
    }

    /* MODO 1: Interface gráfica (mantém funcionalidade existente) */
    private static void executarInterfaceGrafica() {
        System.out.println("\n=== MODO 1: INTERFACE GRÁFICA ===");
        System.out.println("Iniciando interface gráfica interativa...");
        System.out.println("Comparação visual: Pilha (Vermelho) vs Fila (Verde)");

        javax.swing.SwingUtilities.invokeLater(() -> {
            FloodFillApp app = new FloodFillApp();
            app.setVisible(true);
            System.out.println("Interface gráfica iniciada com sucesso!");
        });
    }

    /* MODO 2: Exemplo automático carregando input/input.png */
    private static void executarExemploAutomatico() {
        System.out.println("\n=== MODO 2: EXEMPLO AUTOMÁTICO COM IMAGEM ===");
        System.out.println("Executando demonstração completa com input/input.png...");

        ProcessadorImagem processador = new ProcessadorImagem("flood_fill_example_output");

        int[][] matriz = carregarImagemInput(processador);

        int[] ponto = processador.encontrarPixelBrancoEmAreaFechada(matriz);

        System.out.println("Ponto escolhido para demonstração: (" + ponto[0] + ", " + ponto[1] + ")");

        executarFloodFillCompleto(matriz, ponto[0], ponto[1], processador);
    }

    private static int[][] carregarImagemInput(ProcessadorImagem processador) {
        System.out.println("=== CARREGANDO IMAGEM INPUT ===");
        System.out.println(" Tentando carregar: input/input.png");

        // Tenta carregar a imagem do diretório input
        int[][] matriz = processador.carregarImagemDaPasta("input", "input.png");

        if (matriz != null) {
            System.out.println(" Imagem input/input.png carregada com sucesso!");
            System.out.println(" Dimensões: " + matriz[0].length + "x" + matriz.length);
        } else {
            System.out.println(" ERRO: Não foi possível carregar input/input.png");
            System.out.println(" Verifique se o arquivo existe no diretório 'input'");
            System.out.println(" Gerando matriz exemplo como fallback...");
            matriz = criarMatrizExemploCompleta();
        }

        return matriz;
    }

    private static void executarFloodFillCompleto(int[][] matrizOriginal, int x, int y, ProcessadorImagem processador) {
        System.out.println("\n=== EXECUTANDO FLOOD FILL COM ESTRUTURAS PRÓPRIAS ===");
        System.out.println("Ponto inicial: (" + x + ", " + y + ")");

        int[][] matrizPilha = copiarMatriz(matrizOriginal);
        int[][] matrizFila = copiarMatriz(matrizOriginal);

        // Cria processadores específicos para cada estrutura
        ProcessadorImagem processadorPilha = new ProcessadorImagem(processador.getDiretorioSaida() + "/pilha");
        ProcessadorImagem processadorFila = new ProcessadorImagem(processador.getDiretorioSaida() + "/fila");

        processador.salvarImagemFinal(matrizOriginal, "original");

        System.out.println("\n--- EXECUTANDO COM PILHA (DFS) ---");
        FloodFillAlgoritmo algoritmoPilha = new FloodFillAlgoritmo(matrizPilha, processadorPilha);

        long inicioPilha = System.currentTimeMillis();
        algoritmoPilha.preencherComPilha(x, y, GerenciarCores.obterCorVermelha());
        long fimPilha = System.currentTimeMillis();

        System.out.println("\n--- EXECUTANDO COM FILA (BFS) ---");
        FloodFillAlgoritmo algoritmoFila = new FloodFillAlgoritmo(matrizFila, processadorFila);

        long inicioFila = System.currentTimeMillis();
        algoritmoFila.preencherComFila(x, y, GerenciarCores.obterCorVerde());
        long fimFila = System.currentTimeMillis();

        // Relatório final comparativo
        gerarRelatorioFinalCompleto(algoritmoPilha, algoritmoFila, fimPilha - inicioPilha, fimFila - inicioFila);
    }

    private static void gerarRelatorioFinalCompleto(FloodFillAlgoritmo pilha, FloodFillAlgoritmo fila, long tempoPilha, long tempoFila) {
        System.out.println("\n=== RELATÓRIO FINAL COMPLETO ===");
        System.out.println("PERFORMANCE:");
        System.out.println("- Tempo Pilha (DFS): " + tempoPilha + "ms");
        System.out.println("- Tempo Fila (BFS): " + tempoFila + "ms");
        System.out.println("- Diferença: " + Math.abs(tempoPilha - tempoFila) + "ms");

        System.out.println("\nESTATÍSTICAS:");
        System.out.println("- Resultado Pilha: " + pilha.obterInformacoesMatriz());
        System.out.println("- Resultado Fila: " + fila.obterInformacoesMatriz());

        System.out.println("\nDIFERENÇAS ESTRUTURAIS DEMONSTRADAS:");
        System.out.println(" PILHA (DFS): Explora em profundidade, padrão mais irregular");
        System.out.println("  - Últimos vizinhos adicionados são processados primeiro");
        System.out.println("  - Cria padrões de preenchimento mais 'serpenteantes'");
        System.out.println(" FILA (BFS): Explora em largura, padrão mais uniforme/circular");
        System.out.println("  - Primeiros vizinhos adicionados são processados primeiro");
        System.out.println("  - Cria padrões de preenchimento mais 'concêntricos'");

        System.out.println("\nARQUIVOS GERADOS (FORMATO PNG OBRIGATÓRIO):");
        System.out.println(" Imagem original salva");
        System.out.println(" Resultado Pilha (vermelho) com animação completa");
        System.out.println(" Resultado Fila (verde) com animação completa");
        System.out.println(" Frames de animação PNG para cada pixel modificado");

    }

    /* Cria matriz exemplo otimizada conforme requisitos (fallback) */
    private static int[][] criarMatrizExemploCompleta() {
        int tamanho = 30;
        int[][] matriz = new int[tamanho][tamanho];

        // Fundo branco
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                matriz[i][j] = GerenciarCores.obterCorBranca();
            }
        }

        // Bordas pretas
        for (int i = 0; i < tamanho; i++) {
            matriz[0][i] = GerenciarCores.obterCorPreta();
            matriz[tamanho-1][i] = GerenciarCores.obterCorPreta();
            matriz[i][0] = GerenciarCores.obterCorPreta();
            matriz[i][tamanho-1] = GerenciarCores.obterCorPreta();
        }

        // Formas internas para demonstrar diferenças entre Pilha e Fila
        // Retângulo central
        for (int i = 12; i <= 18; i++) {
            for (int j = 12; j <= 18; j++) {
                if (i == 12 || i == 18 || j == 12 || j == 18) {
                    matriz[i][j] = GerenciarCores.obterCorPreta();
                }
            }
        }

        // Formas adicionais para demonstrar padrões diferentes
        for (int i = 5; i < 10; i++) {
            matriz[5][i] = GerenciarCores.obterCorPreta();
            matriz[i][5] = GerenciarCores.obterCorPreta();
        }

        // Linha diagonal parcial
        for (int i = 22; i < 28; i++) {
            if (i < tamanho && i < tamanho) {
                matriz[i][i] = GerenciarCores.obterCorPreta();
            }
        }

        System.out.println("Matriz exemplo criada: " + tamanho + "x" + tamanho);
        return matriz;
    }

    private static int[][] copiarMatriz(int[][] original) {
        int altura = original.length;
        int largura = original[0].length;
        int[][] copia = new int[altura][largura];

        for (int i = 0; i < altura; i++) {
            System.arraycopy(original[i], 0, copia[i], 0, largura);
        }

        return copia;
    }
}
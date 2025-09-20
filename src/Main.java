import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== FLOOD FILL - IMPLEMENTAÇÃO ===");
        System.out.println("Trabalho de Estruturas de Dados - Pilha vs Fila");
        System.out.println();

        // Opção de execução
        System.out.println("Escolha o modo de execução:");
        System.out.println("1 - Interface gráfica interativa");
        System.out.println("2 - Exemplo automático com matriz padrão");
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

        javax.swing.SwingUtilities.invokeLater(() -> {
            new FloodFillApp().setVisible(true);
        });
    }

    /* MODO 3: Exemplo automático com matriz padrão */
    private static void executarExemploAutomatico() {
        System.out.println("\n=== MODO 2: EXEMPLO AUTOMÁTICO ===");
        System.out.println("Executando demonstração com matriz exemplo...");

        // Cria processador
        ProcessadorImagem processador = new ProcessadorImagem("flood_fill_example_output");

        // Cria matriz exemplo conforme especificações
        int[][] matriz = criarMatrizExemploCompleta();

        // Encontra ponto ideal
        int[] ponto = processador.encontrarPixelBrancoEmAreaFechada(matriz);

        System.out.println("Usando ponto: (" + ponto[0] + ", " + ponto[1] + ")");

        // Executa flood fill
        executarFloodFillCompleto(matriz, ponto[0], ponto[1], processador);
    }

    private static void executarFloodFillCompleto(int[][] matrizOriginal, int x, int y, ProcessadorImagem processador) {
        System.out.println("\n=== EXECUTANDO FLOOD FILL ===");
        System.out.println("Ponto inicial: (" + x + ", " + y + ")");

        // Cria duas cópias da matriz original
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

        // Relatório final
        gerarRelatorioFinal(algoritmoPilha, algoritmoFila, fimPilha - inicioPilha, fimFila - inicioFila);
    }

    /* Gera relatório comparativo final*/

    private static void gerarRelatorioFinal(FloodFillAlgoritmo pilha, FloodFillAlgoritmo fila, long tempoPilha, long tempoFila) {
        System.out.println("\n=== RELATÓRIO FINAL ===");
        System.out.println("Tempo Pilha (DFS): " + tempoPilha + "ms");
        System.out.println("Tempo Fila (BFS): " + tempoFila + "ms");
        System.out.println();
        System.out.println("Informações Pilha: " + pilha.obterInformacoesMatriz());
        System.out.println("Informações Fila: " + fila.obterInformacoesMatriz());
        System.out.println();
        System.out.println("DIFERENÇAS ESTRUTURAIS:");
        System.out.println("- PILHA (DFS): Explora em profundidade, padrão mais irregular");
        System.out.println("- FILA (BFS): Explora em largura, padrão mais uniforme/circular");
        System.out.println();
        System.out.println("SAÍDAS GERADAS:");
        System.out.println("- Imagem original salva");
        System.out.println("- Resultado Pilha (vermelho) com animação");
        System.out.println("- Resultado Fila (verde) com animação");
        System.out.println("- Frames de animação para cada estrutura");
        System.out.println("\nProcessamento concluído com sucesso!");
        System.out.println("==========================================");
    }

    /* Cria matriz exemplo otimizada para demonstração */
    private static int[][] criarMatrizExemploCompleta() {
        int tamanho = 30;
        int[][] matriz = new int[tamanho][tamanho];

        // Preenche com branco
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

        // Formas internas para demonstrar diferenças entre estruturas

        // Retângulo
        for (int i = 10; i <= 20; i++) {
            for (int j = 10; j <= 20; j++) {
                if (i == 10 || i == 20 || j == 10 || j == 20) {
                    matriz[i][j] = GerenciarCores.obterCorPreta();
                }
            }
        }

        for (int i = 5; i < 10; i++) {
            matriz[5][i] = GerenciarCores.obterCorPreta();
            matriz[i][5] = GerenciarCores.obterCorPreta();
        }

        // Diagonal parcial
        for (int i = 22; i < 28; i++) {
            matriz[i][i] = GerenciarCores.obterCorPreta();
        }

        System.out.println("Matriz exemplo criada: " + tamanho + "x" + tamanho);
        return matriz;
    }

    private static int[][] copiarMatriz(int[][] original) {
        int altura = original.length;
        int largura = original[0].length;
        int[][] copia = new int[altura][largura];

        for (int i = 0; i < altura; i++) {
            for (int j = 0; j < largura; j++) {
                copia[i][j] = original[i][j];
            }
        }

        return copia;
    }

}
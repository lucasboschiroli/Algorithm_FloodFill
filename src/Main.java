//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA FLOOD FILL ===");
        System.out.println("Implementação com Pilha e Fila próprias");
        System.out.println();

        // Inicializa o processador de imagens
        ProcessadorImagem processador = new ProcessadorImagem("output");

        // Carrega a imagem da pasta input com nome input
        // Tenta diferentes extensões de arquivo comuns para imagens P&B
        int[][] matriz = processador.carregarImagemDaPasta("input", "input");

        // Cria uma cópia da matriz para testar com fila
        int[][] matrizFila = copiarMatriz(matriz);

        // Cria o algoritmo
        FloodFillAlgoritmo algoritmo = new FloodFillAlgoritmo(matriz, processador);

        System.out.println("Matriz inicial:");
        algoritmo.imprimirMatriz();

        // Opção 1: Preencher a partir do centro (mais provável de pegar áreas internas)
        int[] coordenadaInicial = processador.encontrarPixelBrancoNoCentro(matriz);

        // Opção 2: Se você quiser preencher áreas fechadas específicas, descomente a linha abaixo:
        // int[] coordenadaInicial = processador.encontrarPixelBrancoEmAreaFechada(matriz);

        // Opção 3: Se você souber as coordenadas exatas, pode definir manualmente:
        // int[] coordenadaInicial = {100, 200}; // x, y específicos

        int x = coordenadaInicial[0];
        int y = coordenadaInicial[1];

        System.out.println("Coordenada inicial encontrada: (" + x + ", " + y + ")");

        // Testa com Pilha
        System.out.println("=== TESTANDO COM PILHA ===");
        algoritmo.preencherComPilha(y, x, GerenciarCores.obterCorVermelha());
        algoritmo.imprimirMatriz();

        // Testa com Fila (usando a cópia da matriz original)
        System.out.println("=== TESTANDO COM FILA ===");
        FloodFillAlgoritmo algoritmoFila = new FloodFillAlgoritmo(matrizFila, processador);
        algoritmoFila.preencherComFila(y, x, GerenciarCores.obterCorAzul());
        algoritmoFila.imprimirMatriz();

        System.out.println("Execução concluída! Verifique as imagens na pasta 'output'.");
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
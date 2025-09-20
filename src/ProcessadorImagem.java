import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class ProcessadorImagem {
    private String diretorioSaida;

    public ProcessadorImagem(String diretorioSaida) {
        this.diretorioSaida = diretorioSaida;
        criarDiretorio();
    }

    private void criarDiretorio() {
        File dir = new File(diretorioSaida);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ATUALIZAÇÃO: Validação rigorosa de formato PNG
    public int[][] carregarImagemDaPasta(String pasta, String nomeArquivo) {
        // Primeiro tenta carregar PNG (formato obrigatório conforme requisitos)
        String caminhoCompleto = pasta + File.separator + nomeArquivo + ".png";
        File arquivo = new File(caminhoCompleto);

        if (arquivo.exists()) {
            System.out.println("Carregando imagem PNG: " + caminhoCompleto);
            int[][] matriz = carregarImagem(caminhoCompleto);
            if (validarImagemParaFloodFill(matriz)) {
                return matriz;
            } else {
                System.out.println("AVISO: A imagem não atende aos critérios ideais (cores sólidas/fundo branco com divisões pretas)");
                System.out.println("Continuando com conversão automática para preto e branco...");
                return matriz;
            }
        }

        System.err.println("Arquivo PNG não encontrado: " + caminhoCompleto);
        System.err.println("REQUISITO: O trabalho especifica uso de imagens PNG com cores sólidas");
        System.out.println("Criando matriz de exemplo conforme especificações...");
        return criarMatrizExemplo();
    }

    // ATUALIZAÇÃO: Validação específica de formato de entrada
    private boolean validarImagemParaFloodFill(int[][] matriz) {
        int totalPixels = matriz.length * matriz[0].length;
        int pixelsBrancos = 0;
        int pixelsPretos = 0;
        int outrasCore = 0;

        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                if (matriz[i][j] == 0xFFFFFFFF) {
                    pixelsBrancos++;
                } else if (matriz[i][j] == 0xFF000000) {
                    pixelsPretos++;
                } else {
                    outrasCore++;
                }
            }
        }

        // Verifica se a imagem tem predominantemente branco com divisões pretas
        double percentualBranco = (double) pixelsBrancos / totalPixels;
        double percentualOutras = (double) outrasCore / totalPixels;

        System.out.println("Análise da imagem:");
        System.out.println("- Pixels brancos: " + pixelsBrancos + " (" + String.format("%.1f", percentualBranco * 100) + "%)");
        System.out.println("- Pixels pretos: " + pixelsPretos + " (" + String.format("%.1f", (double) pixelsPretos / totalPixels * 100) + "%)");
        System.out.println("- Outras cores: " + outrasCore + " (" + String.format("%.1f", percentualOutras * 100) + "%)");

        // Considera adequada se tem pelo menos 60% de branco e poucas outras cores
        return percentualBranco >= 0.6 && percentualOutras <= 0.1;
    }

    public int[][] carregarImagem(String caminhoArquivo) {
        try {
            // ATUALIZAÇÃO: Validação de formato PNG
            if (!caminhoArquivo.toLowerCase().endsWith(".png")) {
                System.err.println("AVISO: O formato recomendado é PNG conforme especificações do trabalho");
            }

            BufferedImage imagem = ImageIO.read(new File(caminhoArquivo));
            if (imagem == null) {
                throw new IOException("Não foi possível carregar a imagem");
            }

            int largura = imagem.getWidth();
            int altura = imagem.getHeight();
            int[][] matriz = new int[altura][largura];

            System.out.println("Processando imagem: " + largura + "x" + altura + " pixels");

            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    int rgb = imagem.getRGB(x, y);
                    // Converte para o formato esperado (preto e branco com algumas cores)
                    matriz[y][x] = processarPixelParaFloodFill(rgb);
                }
            }

            System.out.println("Imagem carregada e processada com sucesso");
            return matriz;

        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem: " + e.getMessage());
            System.out.println("Gerando matriz de exemplo...");
            return criarMatrizExemplo();
        }
    }

    // ATUALIZAÇÃO: Processamento melhorado para manter algumas cores além de P&B
    private int processarPixelParaFloodFill(int rgb) {
        // Extrai os componentes RGB
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        // Detecta cores puras primeiro
        if (r > 200 && g < 50 && b < 50) return 0xFFFF0000; // Vermelho puro
        if (r < 50 && g > 200 && b < 50) return 0xFF00FF00; // Verde puro
        if (r < 50 && g < 50 && b > 200) return 0xFF0000FF; // Azul puro

        // Calcula luminosidade para preto/branco
        int luminosidade = (int) (0.299 * r + 0.587 * g + 0.114 * b);

        // Converte para preto ou branco baseado em threshold
        if (luminosidade > 127) {
            return 0xFFFFFFFF; // Branco
        } else {
            return 0xFF000000; // Preto
        }
    }

    // Método para encontrar um pixel branco no centro da imagem
    public int[] encontrarPixelBrancoNoCentro(int[][] matriz) {
        int altura = matriz.length;
        int largura = matriz[0].length;

        // Começa do centro da imagem e expande em espiral
        int centroY = altura / 2;
        int centroX = largura / 2;

        System.out.println("Procurando pixel branco ideal para Flood Fill...");
        System.out.println("Centro da imagem: (" + centroX + ", " + centroY + ")");

        // Verifica o centro primeiro
        if (matriz[centroY][centroX] == 0xFFFFFFFF) {
            System.out.println("Pixel branco encontrado no centro!");
            return new int[]{centroX, centroY};
        }

        // Expande em círculos concêntricos do centro
        for (int raio = 1; raio < Math.max(largura, altura) / 2; raio++) {
            for (int dy = -raio; dy <= raio; dy++) {
                for (int dx = -raio; dx <= raio; dx++) {
                    // Verifica se está na borda do círculo atual
                    if (Math.abs(dx) == raio || Math.abs(dy) == raio) {
                        int y = centroY + dy;
                        int x = centroX + dx;

                        // Verifica limites
                        if (y >= 0 && y < altura && x >= 0 && x < largura) {
                            if (matriz[y][x] == 0xFFFFFFFF) {
                                System.out.println("Pixel branco encontrado em: (" + x + ", " + y + ")");
                                return new int[]{x, y};
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Usando centro da imagem como ponto padrão");
        return new int[]{centroX, centroY};
    }

    // ATUALIZAÇÃO: Método melhorado para encontrar área adequada para flood fill
    public int[] encontrarPixelBrancoEmAreaFechada(int[][] matriz) {
        int altura = matriz.length;
        int largura = matriz[0].length;

        System.out.println("Procurando área ideal para demonstração do Flood Fill...");

        // Procura por regiões brancas cercadas (boas para demonstração)
        for (int y = 2; y < altura - 2; y++) {
            for (int x = 2; x < largura - 2; x++) {
                if (matriz[y][x] == 0xFFFFFFFF) {
                    // Verifica se tem uma boa área branca ao redor
                    boolean areaPromissora = verificarAreaParaFloodFill(matriz, x, y, 3);

                    if (areaPromissora) {
                        System.out.println("Área ideal para Flood Fill encontrada: (" + x + ", " + y + ")");
                        return new int[]{x, y};
                    }
                }
            }
        }

        // Se não encontrar área ideal, usa o método do centro
        return encontrarPixelBrancoNoCentro(matriz);
    }

    private boolean verificarAreaParaFloodFill(int[][] matriz, int centroX, int centroY, int raio) {
        int pixelsBrancosNaArea = 0;
        int totalPixelsVerificados = 0;

        for (int dy = -raio; dy <= raio; dy++) {
            for (int dx = -raio; dx <= raio; dx++) {
                int y = centroY + dy;
                int x = centroX + dx;

                if (y >= 0 && y < matriz.length && x >= 0 && x < matriz[0].length) {
                    totalPixelsVerificados++;
                    if (matriz[y][x] == 0xFFFFFFFF) {
                        pixelsBrancosNaArea++;
                    }
                }
            }
        }

        // Retorna true se pelo menos 70% da área for branca
        double percentualBranco = (double) pixelsBrancosNaArea / totalPixelsVerificados;
        return percentualBranco >= 0.7;
    }

    // ATUALIZAÇÃO: Matriz exemplo otimizada para demonstrar flood fill
    private int[][] criarMatrizExemplo() {
        System.out.println("Criando matriz exemplo otimizada para Flood Fill...");
        int[][] matriz = new int[20][20];

        // Preenche com branco
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                matriz[i][j] = 0xFFFFFFFF; // Branco
            }
        }

        // Cria bordas pretas
        for (int i = 0; i < 20; i++) {
            matriz[0][i] = 0xFF000000; // Borda superior
            matriz[19][i] = 0xFF000000; // Borda inferior
            matriz[i][0] = 0xFF000000; // Borda esquerda
            matriz[i][19] = 0xFF000000; // Borda direita
        }

        // Cria algumas formas internas para demonstrar o algoritmo
        // Retângulo no centro
        for (int i = 8; i <= 12; i++) {
            for (int j = 8; j <= 12; j++) {
                if (i == 8 || i == 12 || j == 8 || j == 12) {
                    matriz[i][j] = 0xFF000000; // Borda do retângulo
                }
            }
        }

        // Linha diagonal
        for (int i = 2; i < 8; i++) {
            matriz[i][i] = 0xFF000000;
        }

        System.out.println("Matriz exemplo criada: 20x20 com áreas ideais para Flood Fill");
        return matriz;
    }

    // ATUALIZAÇÃO: Método otimizado para salvar animação (cada pixel)
    public void salvarImagemAnimacao(int[][] matriz, int frame, String tipo) {
        // Para evitar criar muitos arquivos, só salva a cada 5 frames para imagens grandes
        int altura = matriz.length;
        int largura = matriz[0].length;
        int totalPixels = altura * largura;

        boolean salvarFrame = true;

        // Para imagens muito grandes, reduz a frequência de salvamento
        if (totalPixels > 10000) { // Maior que 100x100
            salvarFrame = (frame % 10 == 0); // Salva a cada 10 frames
        } else if (totalPixels > 1000) { // Maior que ~30x30
            salvarFrame = (frame % 5 == 0); // Salva a cada 5 frames
        }
        // Para imagens pequenas, salva todos os frames

        if (salvarFrame) {
            try {
                BufferedImage imagem = criarBufferedImage(matriz);
                String nomeArquivo = String.format("%s/animacao_%s_frame_%05d.png",
                        diretorioSaida, tipo.toLowerCase(), frame);
                ImageIO.write(imagem, "PNG", new File(nomeArquivo));

                // Log menos frequente para não poluir console
                if (frame % 50 == 0) {
                    System.out.println("Frame " + frame + " salvo");
                }
            } catch (IOException e) {
                System.err.println("Erro ao salvar frame " + frame + ": " + e.getMessage());
            }
        }
    }

    public void salvarImagemFinal(int[][] matriz, String tipo) {
        try {
            BufferedImage imagem = criarBufferedImage(matriz);
            String nomeArquivo = String.format("%s/resultado_final_%s.png",
                    diretorioSaida, tipo.toLowerCase());
            ImageIO.write(imagem, "PNG", new File(nomeArquivo));
            System.out.println("Imagem final salva: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar imagem final: " + e.getMessage());
        }
    }

    private BufferedImage criarBufferedImage(int[][] matriz) {
        int altura = matriz.length;
        int largura = matriz[0].length;
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                imagem.setRGB(x, y, matriz[y][x]);
            }
        }

        return imagem;
    }

    // ATUALIZAÇÃO: Método para validar se arquivo é PNG
    public static boolean validarFormatoPNG(String caminhoArquivo) {
        return caminhoArquivo.toLowerCase().endsWith(".png");
    }

    // ATUALIZAÇÃO: Relatório de adequação da imagem
    public void gerarRelatorioImagem(int[][] matriz, String nomeArquivo) {
        System.out.println("\n=== RELATÓRIO DE ADEQUAÇÃO DA IMAGEM ===");
        System.out.println("Arquivo: " + nomeArquivo);
        System.out.println("Dimensões: " + matriz[0].length + "x" + matriz.length);

        validarImagemParaFloodFill(matriz);

        int[] melhorPonto = encontrarPixelBrancoEmAreaFechada(matriz);
        System.out.println("Ponto sugerido para Flood Fill: (" + melhorPonto[0] + ", " + melhorPonto[1] + ")");
        System.out.println("=====================================\n");
    }

    // Getter para o diretório de saída
    public String getDiretorioSaida() {
        return diretorioSaida;
    }
}
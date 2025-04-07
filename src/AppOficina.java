
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * MIT License
 *
 * Copyright(c) 2022-25 João Caram <caram@pucminas.br>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class AppOficina {

    static final int MAX_PEDIDOS = 100;
    static Produto[] produtos;
    static Produto[] produtosCod;
    static Produto[] produtosDesc;
    static int quantProdutos = 0;
    static String nomeArquivoDados = "produtos.txt";
    static IOrdenador<Produto> ordenador;

    // #region utilidades
    static Scanner teclado;


    static <T extends Number> T lerNumero(String mensagem, Class<T> classe) {
        System.out.print(mensagem + ": ");
        T valor;
        try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa() {
        System.out.println("Tecle Enter para continuar.");
        teclado.nextLine();
    }

    static void cabecalho() {
        limparTela();
        System.out.println("XULAMBS COMÉRCIO DE COISINHAS v0.2\n================");
    }

    static int exibirMenuPrincipal() {
        cabecalho();
        System.out.println("1 - Procurar produto por código");
        System.out.println("2 - Procurar produto por descrição");
        System.out.println("3 - Filtrar produtos por preço máximo");
        System.out.println("4 - Ordenar produtos");
        System.out.println("5 - Embaralhar produtos");
        System.out.println("6 - Listar produtos");
        System.out.println("7 - Listar produtos por código");
        System.out.println("0 - Finalizar");

        return lerNumero("Digite sua opção", Integer.class);
    }

    static int exibirMenuOrdenadores() {
        cabecalho();
        System.out.println("1 - Bolha");
        System.out.println("2 - Inserção");
        System.out.println("3 - Mergesort");
        System.out.println("0 - Finalizar");

        return lerNumero("Digite sua opção", Integer.class);
    }

    static int exibirMenuCriterioOrdenacao() {
        cabecalho();
        System.out.println("1 - Padrão");
        System.out.println("2 - Ordenar por valor");
        System.out.println("3 - Ordenar por código");
        System.out.println("0 - Finalizar");

        return lerNumero("Digite sua opção", Integer.class);
    }

    // #endregion
    static Produto[] carregarProdutos(String nomeArquivo){
        Scanner dados;
        Produto[] dadosCarregados;
        try {
            dados = new Scanner(new File(nomeArquivo));
            int tamanho = Integer.parseInt(dados.nextLine());

            dadosCarregados = new Produto[tamanho];
            while (dados.hasNextLine()) {
                Produto novoProduto = Produto.criarDoTexto(dados.nextLine());
                dadosCarregados[quantProdutos] = novoProduto;
                quantProdutos++;
            }

            dados.close();
        } catch (FileNotFoundException fex){
            System.out.println("Arquivo não encontrado. Produtos não carregados");
            dadosCarregados = null;
        }
        return dadosCarregados;
    }

    static Produto localizarProdutoPorCod() {
        cabecalho();
        System.out.println("Localizando um produto por código");
        int numero = lerNumero("Digite o identificador do produto", Integer.class);
        Produto localizado = null;
        
        for (int i = 0; i < quantProdutos && localizado == null; i++) {
            if (produtosCod[i].hashCode() == numero)
                localizado = produtosCod[i];
        }
        return localizado;
    }

    static Produto localizarProdutoPorDesc() {
        cabecalho();
        System.out.println("Localizando um produto");
        String desc = teclado.nextLine();
        Produto localizado = null;
        
        for (int i = 0; i < quantProdutos && localizado == null; i++) {
            if (produtosDesc[i].descricao.equals(desc))
                localizado = produtosDesc[i];
        }
        return localizado;
    }

    private static void mostrarProduto(Produto produto) {
        cabecalho();
        String mensagem = "Dados inválidos";
        
        if (produto!=null){
            mensagem = String.format("Dados do produto:\n%s", produto);            
        }
        
        System.out.println(mensagem);
    }

    private static void filtrarPorPrecoMaximo(){
        cabecalho();
        System.out.println("Filtrando por valor máximo:");
        double valor = lerNumero("valor", Double.class);
        StringBuilder relatorio = new StringBuilder();

        for (int i = 0; i < quantProdutos; i++) {
            if (produtos[i].valorDeVenda() < valor)
                relatorio.append(produtos[i]+"\n");
        }

        System.out.println(relatorio.toString());
    }

    static void ordenarProdutos(){
        cabecalho();
        Comparator<Produto> comp = null;

        int opcao = exibirMenuOrdenadores();
        int ordenacao = exibirMenuCriterioOrdenacao();

        switch (opcao) {
            case 1 -> ordenador = new Bubblesort<>();
            case 2 -> ordenador = new InsertSort<>();
            case 3 -> ordenador = new MergeSort<>();
        }

        switch (ordenacao) {
            case 1 -> comp = Produto::compareTo;
            case 2 -> comp = new ComparadorPorValor();
            case 3 -> comp = new ComparadorPorCod();
        }

        if (ordenador!=null) {
            produtos = ordenador.ordenar(produtos, comp);        
            System.out.println("Tempo gasto: " +ordenador.getTempoOrdenacao() + " ms.");
        }
        ordenador = null;
    }

    static void embaralharProdutos(){
        Collections.shuffle(Arrays.asList(produtos));
    }

    static void verificarSubstituicao(Produto[] dadosOriginais, Produto[] copiaDados){
        cabecalho();
        System.out.print("Deseja sobrescrever os dados originais pelos ordenados (S/N)?");
        String resposta = teclado.nextLine().toUpperCase();
        if(resposta.equals("S"))
            dadosOriginais = Arrays.copyOf(copiaDados, copiaDados.length);
    }

    static void listarProdutos(){
        cabecalho();
        for (int i = 0; i < quantProdutos; i++) {
            System.out.println(produtos[i]);
        }
    }

    private static void listarProdutosCod() {
        cabecalho();
        for (int i = 0; i < quantProdutos; i++) {
            System.out.println(produtosCod[i]);
        };
    }

    static void saveProdutos(String fileName, Produto[] produtosOrdenados) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Adicionar a quantidade de produtos na primeira linha do arquivo
            writer.write(String.valueOf(produtosOrdenados.length));
            writer.newLine();

            // Pra cada produto, adicionar uma nova linha no arquivo com o produto
            for (Produto produto : produtosOrdenados) {
                writer.write(produto.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Houve o seguinte erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    static void copiarProdutos(){
        ordenador = new MergeSort<>();

        produtosCod = new Produto[quantProdutos];
        produtosDesc = new Produto[quantProdutos];

        // Ordenar pelo código do produto
        Comparator<Produto> comparator = new ComparadorPorCod(); // Ordenar pelo código do produto
        Produto[] dadosOrdenadosPorCod = ordenador.ordenar(Arrays.copyOf(produtos, quantProdutos), comparator);

        saveProdutos("produtos_por_cod.txt", dadosOrdenadosPorCod);
        produtosCod = Arrays.copyOf(dadosOrdenadosPorCod, quantProdutos);


        // Ordenar pela descrição do produto
        comparator = Produto::compareTo;
        Produto[] dadosOrdenadosPorDesc = ordenador.ordenar(Arrays.copyOf(produtos, quantProdutos), comparator);

        saveProdutos("produtos_por_desc.txt", dadosOrdenadosPorDesc);
        produtosDesc = Arrays.copyOf(dadosOrdenadosPorDesc, quantProdutos);
    }

    public static void main(String[] args) {
        teclado = new Scanner(System.in);
        
        produtos = carregarProdutos(nomeArquivoDados);
        copiarProdutos();
        embaralharProdutos();

        int opcao = -1;
        
        do {
            opcao = exibirMenuPrincipal();
            switch (opcao) {
                case 1 -> mostrarProduto(localizarProdutoPorCod());
                case 2 -> mostrarProduto(localizarProdutoPorDesc());
                case 3 -> filtrarPorPrecoMaximo();
                case 4 -> ordenarProdutos();
                case 5 -> embaralharProdutos();
                case 6 -> listarProdutos(); // Listar produtos normalmente por descrição
                case 7 -> listarProdutosCod(); // Listar produtos por código
                case 0 -> System.out.println("FLW VLW OBG VLT SMP.");
            }
            pausa();
        } while (opcao != 0);

        teclado.close();
    }                        
}

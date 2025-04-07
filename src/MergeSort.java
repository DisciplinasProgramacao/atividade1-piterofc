import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;

public class MergeSort<T extends Comparable<T>> implements IOrdenador<T>{

    private long comparacoes;
    private long movimentacoes;
    private LocalDateTime inicio;
    private LocalDateTime termino;
    private T[] dadosOrdenados;

    public MergeSort() {
        comparacoes = 0;
        movimentacoes = 0;
    }


    @Override
    public T[] ordenar(T[] dados) {
        return ordenar(dados, T::compareTo);
    }

    @Override
    public T[] ordenar(T[] dados, Comparator<T> comparator) {    
        int length = dados.length;

        dadosOrdenados = Arrays.copyOf(dados, length);

        inicio = LocalDateTime.now();
        mergesort(0, dados.length-1, comparator);
        termino = LocalDateTime.now();

        return dadosOrdenados;
    }

    private T[] merge(int start, int end, T[] dados, Comparator<T> comparator) {
        T[] novosDados = Arrays.copyOf(dados, dados.length);

        int metade = (start + end) / 2;
        int i1 = start;
        int i2 = metade + 1;
        int posicao = start;

        while (i1 <= metade && i2 <= end) {
            comparacoes++;

            if (comparator.compare(dados[i1], dados[i2]) <= 0) {
                novosDados[posicao++] = dados[i1++];
            } else {
                novosDados[posicao++] = dados[i2++];
            }

            movimentacoes++;
        }

        int de = i1;
        int para = metade;
        if (i1 > metade) {
            de = i2;
            para = end;
        }
    
        for (int i = de; i <= para; i++) {
            novosDados[posicao++] = dados[i];
            movimentacoes++;
        }
    
        return novosDados;
    }

    private void mergesort(int start, int end, Comparator<T> comparator) {
        if (start < end){
            int metade = (end + start) / 2;

            mergesort(start, metade, comparator);
            mergesort(metade+1, end, comparator);

            dadosOrdenados = merge(start, end, dadosOrdenados, comparator); 
        }
    }


    public long getComparacoes() {
        return comparacoes;
    }

    public long getMovimentacoes() {
        return movimentacoes;
    }

    public double getTempoOrdenacao() {
        return Duration.between(inicio, termino).toMillis();
    }

}

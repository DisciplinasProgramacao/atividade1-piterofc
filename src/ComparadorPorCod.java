import java.util.Comparator;

public class ComparadorPorCod implements Comparator<Produto> {
    
    @Override
    public int compare(Produto o1, Produto o2) {
        int resposta = 0;

        if (o1.hashCode() != o2.hashCode()) {
            resposta = (o1.hashCode() > o2.hashCode() ? 1 : -1);
        }

        return resposta;
    }

}
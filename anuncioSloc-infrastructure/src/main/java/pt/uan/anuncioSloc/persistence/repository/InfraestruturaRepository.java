package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Repository para operações com Infraestrutura usando armazenamento em memória
 */
public class InfraestruturaRepository extends BaseRepository<Infraestrutura, Long> {

    public InfraestruturaRepository() {
        super(Infraestrutura.class);
    }

    /**
     * Encontra uma infraestrutura pelo nome
     */
    public Infraestrutura findByNome(String nome) {
        return firstOrNull(filter(infraestrutura -> Objects.equals(infraestrutura.getNome(), nome)));
    }

    /**
     * Encontra uma infraestrutura pela URL
     */
    public Infraestrutura findByUrlServidor(String url) {
        return firstOrNull(filter(infraestrutura -> Objects.equals(infraestrutura.getUrlServidor(), url)));
    }

    /**
     * Lista todas as infraestruturas com paginação
     */
    public List<Infraestrutura> findAllPaginated(int offset, int limit) {
        return page(sort(findAll(), Comparator.comparing(Infraestrutura::getId)), offset, limit);
    }

    /**
     * Retorna infraestruturas ativas
     */
    public List<Infraestrutura> findAllAtivas() {
        return sort(filter(infraestrutura -> infraestrutura.getCapacidadeMaxima() != null
                        && infraestrutura.getCapacidadeMaxima() > 0),
                Comparator.comparing(Infraestrutura::getNome));
    }

    /**
     * Encontra infraestruturas por capacidade mínima
     */
    public List<Infraestrutura> findByCapacidadeMinima(Integer capacidade) {
        return sort(filter(infraestrutura -> infraestrutura.getCapacidadeMaxima() != null
                        && infraestrutura.getCapacidadeMaxima() >= capacidade),
                Comparator.comparing(Infraestrutura::getNome));
    }
}

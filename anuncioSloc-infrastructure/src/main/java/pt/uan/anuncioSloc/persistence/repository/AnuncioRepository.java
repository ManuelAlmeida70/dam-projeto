package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.Anuncio;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Repository para operações com Anuncio usando armazenamento em memória
 */
public class AnuncioRepository extends BaseRepository<Anuncio, Long> {

    public AnuncioRepository() {
        super(Anuncio.class);
    }

    /**
     * Encontra todos os anúncios de um utilizador
     */
    public List<Anuncio> findByEmail(String email) {
        return sort(filter(anuncio -> Objects.equals(anuncio.getEmail(), email)),
                Comparator.comparing(Anuncio::getDataPublicacao).reversed());
    }

    /**
     * Encontra todos os anúncios de uma infraestrutura
     */
    public List<Anuncio> findByInfraId(Long infraId) {
        return sort(filter(anuncio -> anuncio.getInfraestrutura() != null
                        && Objects.equals(anuncio.getInfraestrutura().getId(), infraId)),
                Comparator.comparing(Anuncio::getDataPublicacao).reversed());
    }

    /**
     * Encontra todos os anúncios de um local
     */
    public List<Anuncio> findByLocalId(Long localId) {
        return sort(filter(anuncio -> anuncio.getLocal() != null
                        && Objects.equals(anuncio.getLocal().getId(), localId)),
                Comparator.comparing(Anuncio::getDataPublicacao).reversed());
    }

    /**
     * Encontra anúncios ativos de uma infraestrutura
     */
    public List<Anuncio> findAtivesByInfraId(Long infraId) {
        return sort(filter(anuncio -> anuncio.getInfraestrutura() != null
                        && Objects.equals(anuncio.getInfraestrutura().getId(), infraId)
                        && Boolean.TRUE.equals(anuncio.getAtivo())),
                Comparator.comparing(Anuncio::getDataPublicacao).reversed());
    }

    /**
     * Encontra anúncios ativos de um local
     */
    public List<Anuncio> findAtivesByLocalId(Long localId) {
        return sort(filter(anuncio -> anuncio.getLocal() != null
                        && Objects.equals(anuncio.getLocal().getId(), localId)
                        && Boolean.TRUE.equals(anuncio.getAtivo())),
                Comparator.comparing(Anuncio::getDataPublicacao).reversed());
    }

    /**
     * Encontra anúncios expirados
     */
    public List<Anuncio> findExpired() {
        long now = System.currentTimeMillis();
        return filter(anuncio -> anuncio.getDataExpiracao() != null
                && anuncio.getDataExpiracao() < now
                && Boolean.TRUE.equals(anuncio.getAtivo()));
    }

    /**
     * Conta anúncios por utilizador
     */
    public long countByEmail(String email) {
        return findByEmail(email).size();
    }

    /**
     * Conta anúncios por infraestrutura
     */
    public long countByInfraId(Long infraId) {
        return findByInfraId(infraId).size();
    }
}

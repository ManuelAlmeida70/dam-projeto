package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.Coordenada;
import java.util.Objects;
import java.util.List;

/**
 * Repository para operações com Coordenada usando armazenamento em memória
 */
public class CoordenadaRepository extends BaseRepository<Coordenada, Long> {

    public CoordenadaRepository() {
        super(Coordenada.class);
    }

    /**
     * Encontra todas as coordenadas de um local
     */
    public List<Coordenada> findByLocalId(Long localId) {
        return filter(coordenada -> coordenada.getLocal() != null
                && Objects.equals(coordenada.getLocal().getId(), localId));
    }

    /**
     * Encontra coordenadas GPS de um local
     */
    public List<Coordenada> findGPSByLocalId(Long localId) {
        return filter(coordenada -> coordenada.getLocal() != null
                && Objects.equals(coordenada.getLocal().getId(), localId)
                && coordenada.getTipo() == Coordenada.TipoCoordenada.GPS);
    }

    /**
     * Encontra coordenadas WiFi de um local
     */
    public List<Coordenada> findWiFiByLocalId(Long localId) {
        return filter(coordenada -> coordenada.getLocal() != null
                && Objects.equals(coordenada.getLocal().getId(), localId)
                && coordenada.getTipo() == Coordenada.TipoCoordenada.WIFI);
    }

    /**
     * Conta coordenadas por local
     */
    public long countByLocalId(Long localId) {
        return findByLocalId(localId).size();
    }
}

package pt.uan.anuncioSloc.persistence.repository;

import pt.uan.anuncioSloc.persistence.entity.UtilizadorInfra;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Repository para operações com UtilizadorInfra usando armazenamento em memória
 */
public class UtilizadorInfraRepository extends BaseRepository<UtilizadorInfra, Long> {

    public UtilizadorInfraRepository() {
        super(UtilizadorInfra.class);
    }

    /**
     * Encontra um utilizador por email e infraestrutura
     */
    public UtilizadorInfra findByEmailAndInfraId(String email, Long infraId) {
        return firstOrNull(filter(utilizador -> Objects.equals(utilizador.getEmail(), email)
                && utilizador.getInfraestrutura() != null
                && Objects.equals(utilizador.getInfraestrutura().getId(), infraId)));
    }

    /**
     * Encontra todos os utilizadores de uma infraestrutura
     */
    public List<UtilizadorInfra> findByInfraId(Long infraId) {
        return sort(filter(utilizador -> utilizador.getInfraestrutura() != null
                        && Objects.equals(utilizador.getInfraestrutura().getId(), infraId)),
                Comparator.comparing(UtilizadorInfra::getEmail));
    }

    /**
     * Encontra todos os utilizadores por email
     */
    public List<UtilizadorInfra> findByEmail(String email) {
        return filter(utilizador -> Objects.equals(utilizador.getEmail(), email));
    }

    /**
     * Conta utilizadores por infraestrutura
     */
    public long countByInfraId(Long infraId) {
        return findByInfraId(infraId).size();
    }

    /**
     * Retorna utilizadores com inatividade acima de X ms
     */
    public List<UtilizadorInfra> findInactiveUsers(Long inactivityThresholdMs) {
        long thresholdTime = System.currentTimeMillis() - inactivityThresholdMs;
        return sort(filter(utilizador -> utilizador.getUltimaAtividade() != null
                        && utilizador.getUltimaAtividade() < thresholdTime),
                Comparator.comparing(UtilizadorInfra::getEmail));
    }
}

package pt.uan.anuncioSloc.service;

import pt.uan.anuncioSloc.persistence.entity.UtilizadorInfra;
import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import pt.uan.anuncioSloc.persistence.repository.UtilizadorInfraRepository;
import pt.uan.anuncioSloc.persistence.repository.InfraestruturaRepository;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Serviço de lógica de negócio para Utilizador
 */
public class UtilizadorService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Integer SALDO_INICIAL = 10;
    private static final Integer DECREMENTO_INATIVIDADE = 1; // 1 ponto por período de inatividade

    private final UtilizadorInfraRepository utilizadorRepo = new UtilizadorInfraRepository();
    private final InfraestruturaRepository infraRepo = new InfraestruturaRepository();

    /**
     * Valida o formato de email
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Cria um novo utilizador em uma infraestrutura
     */
    public UtilizadorInfra criarUtilizador(String email, Long infraId) {
        // Validações
        if (!validarEmail(email)) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }

        Infraestrutura infra = infraRepo.findById(infraId);
        if (infra == null) {
            throw new IllegalArgumentException("Infraestrutura não encontrada");
        }

        // Verifica se utilizador já existe nesta infraestrutura
        UtilizadorInfra existing = utilizadorRepo.findByEmailAndInfraId(email, infraId);
        if (existing != null) {
            throw new IllegalArgumentException("Utilizador " + email + " já existe nesta infraestrutura");
        }

        UtilizadorInfra utilizador = new UtilizadorInfra(email, infra, SALDO_INICIAL);
        return utilizadorRepo.save(utilizador);
    }

    /**
     * Obtém um utilizador por ID
     */
    public UtilizadorInfra obterUtilizador(Long id) {
        return utilizadorRepo.findById(id);
    }

    /**
     * Obtém saldo de um utilizador em uma infraestrutura
     */
    public Integer obterSaldo(String email, Long infraId) {
        UtilizadorInfra util = utilizadorRepo.findByEmailAndInfraId(email, infraId);
        return util != null ? util.getSaldo() : null;
    }

    /**
     * Obtém saldo total do utilizador em todas as infraestruturas
     */
    public Integer obterSaldoTotal(String email) {
        if (!validarEmail(email)) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }

        List<UtilizadorInfra> utilizadores = utilizadorRepo.findByEmail(email);
        return utilizadores.stream()
                .mapToInt(UtilizadorInfra::getSaldo)
                .sum();
    }

    /**
     * Adiciona pontos ao saldo do utilizador
     */
    public boolean adicionarPontos(String email, Long infraId, Integer pontos) {
        UtilizadorInfra util = utilizadorRepo.findByEmailAndInfraId(email, infraId);
        if (util == null) {
            return false;
        }

        util.adicionarPontos(pontos);
        utilizadorRepo.update(util);
        return true;
    }

    /**
     * Remove pontos do saldo do utilizador
     */
    public boolean removerPontos(String email, Long infraId, Integer pontos) {
        UtilizadorInfra util = utilizadorRepo.findByEmailAndInfraId(email, infraId);
        if (util == null) {
            return false;
        }

        boolean sucesso = util.removerPontos(pontos);
        if (sucesso) {
            utilizadorRepo.update(util);
        }
        return sucesso;
    }

    /**
     * Lista todos os utilizadores de uma infraestrutura
     */
    public List<UtilizadorInfra> listarUtilizadoresDaInfraestrutura(Long infraId) {
        return utilizadorRepo.findByInfraId(infraId);
    }

    /**
     * Lista todas as infraestruturas de um utilizador
     */
    public List<UtilizadorInfra> listarInfraestruturasDoUtilizador(String email) {
        if (!validarEmail(email)) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }
        return utilizadorRepo.findByEmail(email);
    }

    /**
     * Atualiza a atividade de um utilizador
     */
    public void atualizarUltimaAtividade(String email, Long infraId) {
        UtilizadorInfra util = utilizadorRepo.findByEmailAndInfraId(email, infraId);
        if (util != null) {
            util.setUltimaAtividade(System.currentTimeMillis());
            utilizadorRepo.update(util);
        }
    }

    /**
     * Incrementa contador de anúncios publicados
     */
    public void incrementarAnunsiosPublicados(String email, Long infraId) {
        UtilizadorInfra util = utilizadorRepo.findByEmailAndInfraId(email, infraId);
        if (util != null) {
            util.incrementarAnunsiosPublicados();
            utilizadorRepo.update(util);
        }
    }

    /**
     * Remove pontos de utilizadores inativos
     */
    public void decrementarInatividade(Long inactivityThresholdMs) {
        List<UtilizadorInfra> inativos = utilizadorRepo.findInactiveUsers(inactivityThresholdMs);
        for (UtilizadorInfra util : inativos) {
            util.removerPontos(DECREMENTO_INATIVIDADE);
            utilizadorRepo.update(util);
        }
    }

    /**
     * Verifica se utilizador tem saldo suficiente
     */
    public boolean temSaldoSuficiente(String email, Long infraId, Integer pontosNecessarios) {
        Integer saldo = obterSaldo(email, infraId);
        return saldo != null && saldo >= pontosNecessarios;
    }

    /**
     * Retorna informações do utilizador
     */
    public String obterInfoUtilizador(String email, Long infraId) {
        UtilizadorInfra util = utilizadorRepo.findByEmailAndInfraId(email, infraId);
        if (util == null) {
            return null;
        }

        StringBuilder info = new StringBuilder();
        info.append("Email: ").append(util.getEmail()).append("\n");
        info.append("Saldo: ").append(util.getSaldo()).append(" pontos\n");
        info.append("Anúncios publicados: ").append(util.getTotalAunsioPublicados()).append("\n");
        info.append("Total de pontos ganhos: ").append(util.getTotalPontosGanhos()).append("\n");

        return info.toString();
    }
}

package pt.uan.anuncioSloc.persistence.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa o saldo de um utilizador em uma infraestrutura específica
 */
@Entity
@Table(name = "utilizador_infra", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "infraestrutura_id"}))
public class UtilizadorInfra implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email; // Email do utilizador

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infraestrutura_id", nullable = false)
    private Infraestrutura infraestrutura;

    @Column(nullable = false)
    private Integer saldo; // Saldo em pontos

    @Column(nullable = false)
    private Long dataCriacao;

    @Column
    private Long ultimaAtividade; // Timestamp da última atividade

    // Contadores
    @Column
    private Long totalAunsioPublicados = 0L;

    @Column
    private Long totalPontosGanhos = 0L;

    // Constructors
    public UtilizadorInfra() {
        this.dataCriacao = System.currentTimeMillis();
        this.ultimaAtividade = System.currentTimeMillis();
    }

    public UtilizadorInfra(String email, Infraestrutura infraestrutura, Integer saldoInicial) {
        this();
        this.email = email;
        this.infraestrutura = infraestrutura;
        this.saldo = saldoInicial;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Infraestrutura getInfraestrutura() {
        return infraestrutura;
    }

    public void setInfraestrutura(Infraestrutura infraestrutura) {
        this.infraestrutura = infraestrutura;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void setSaldo(Integer saldo) {
        this.saldo = saldo;
    }

    public Long getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Long getUltimaAtividade() {
        return ultimaAtividade;
    }

    public void setUltimaAtividade(Long ultimaAtividade) {
        this.ultimaAtividade = ultimaAtividade;
    }

    public Long getTotalAunsioPublicados() {
        return totalAunsioPublicados;
    }

    public void setTotalAunsioPublicados(Long totalAunsioPublicados) {
        this.totalAunsioPublicados = totalAunsioPublicados;
    }

    public Long getTotalPontosGanhos() {
        return totalPontosGanhos;
    }

    public void setTotalPontosGanhos(Long totalPontosGanhos) {
        this.totalPontosGanhos = totalPontosGanhos;
    }

    /**
     * Adiciona pontos ao saldo
     */
    public void adicionarPontos(Integer pontos) {
        if (pontos > 0) {
            this.saldo += pontos;
            this.totalPontosGanhos += pontos;
            this.ultimaAtividade = System.currentTimeMillis();
        }
    }

    /**
     * Remove pontos do saldo
     */
    public boolean removerPontos(Integer pontos) {
        if (this.saldo >= pontos) {
            this.saldo -= pontos;
            this.ultimaAtividade = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /**
     * Incrementa o contador de anúncios publicados
     */
    public void incrementarAnunsiosPublicados() {
        this.totalAunsioPublicados++;
        this.ultimaAtividade = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UtilizadorInfra that = (UtilizadorInfra) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UtilizadorInfra{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", saldo=" + saldo +
                ", totalAunsioPublicados=" + totalAunsioPublicados +
                ", totalPontosGanhos=" + totalPontosGanhos +
                '}';
    }
}

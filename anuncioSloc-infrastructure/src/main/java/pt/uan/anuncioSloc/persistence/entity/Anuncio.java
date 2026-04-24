package pt.uan.anuncioSloc.persistence.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa um anúncio postado por um utilizador numa infraestrutura específica
 */
@Entity
@Table(name = "anuncios")
public class Anuncio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email; // Email do autor

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infraestrutura_id", nullable = false)
    private Infraestrutura infraestrutura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    private Local local;

    @Column(nullable = false, length = 2000)
    private String conteudo; // Conteúdo do anúncio

    @Column(nullable = false)
    private Long dataPublicacao;

    @Column
    private Long dataExpiracao; // Quando o anúncio expira (null = nunca expira)

    @Column(nullable = false)
    private Long totalEntregas = 0L; // Número de utilizadores que receberam

    @Column(nullable = false)
    private Integer pontosGerados = 0; // Pontos gerados até agora

    @Column
    private Boolean ativo = true;

    // Constructors
    public Anuncio() {
        this.dataPublicacao = System.currentTimeMillis();
    }

    public Anuncio(String email, Infraestrutura infraestrutura, Local local, String conteudo) {
        this();
        this.email = email;
        this.infraestrutura = infraestrutura;
        this.local = local;
        this.conteudo = conteudo;
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

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Long getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Long dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Long getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(Long dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public Long getTotalEntregas() {
        return totalEntregas;
    }

    public void setTotalEntregas(Long totalEntregas) {
        this.totalEntregas = totalEntregas;
    }

    public Integer getPontosGerados() {
        return pontosGerados;
    }

    public void setPontosGerados(Integer pontosGerados) {
        this.pontosGerados = pontosGerados;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    /**
     * Incrementa o contador de entregas
     */
    public void incrementarTotalEntregas() {
        this.totalEntregas++;
    }

    /**
     * Adiciona pontos gerados
     */
    public void adicionarPontosGerados(Integer pontos) {
        if (pontos > 0) {
            this.pontosGerados += pontos;
        }
    }

    /**
     * Verifica se o anúncio está expirado
     */
    public boolean estaExpirado() {
        if (dataExpiracao == null) {
            return false;
        }
        return System.currentTimeMillis() > dataExpiracao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anuncio anuncio = (Anuncio) o;
        return Objects.equals(id, anuncio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Anuncio{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", conteudo='" + conteudo + '\'' +
                ", totalEntregas=" + totalEntregas +
                ", pontosGerados=" + pontosGerados +
                ", ativo=" + ativo +
                '}';
    }
}

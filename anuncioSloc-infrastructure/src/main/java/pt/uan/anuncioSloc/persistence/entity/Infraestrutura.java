package pt.uan.anuncioSloc.persistence.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Infraestrutura representa um servidor de infraestrutura localizado numa área geográfica
 */
@Entity
@Table(name = "infraestruturas")
public class Infraestrutura implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome; // Ex: "Largo da Independência"

    @Column(nullable = false)
    private String urlServidor; // URL do Web Service

    @Column(nullable = false)
    private Integer capacidadeMaxima; // Número máximo de utilizadores

    @Column(nullable = false)
    private Integer premioEntrega; // Bónus de pontos por entrega

    @Column(nullable = false)
    private Long dataCriacao;

    @OneToMany(mappedBy = "infraestrutura", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Local> locais = new HashSet<>();

    @OneToMany(mappedBy = "infraestrutura", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Anuncio> anuncios = new HashSet<>();

    @OneToMany(mappedBy = "infraestrutura", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UtilizadorInfra> utilizadores = new HashSet<>();

    @OneToMany(mappedBy = "infraestrutura", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Restricao> restricoes = new HashSet<>();

    // Estatísticas
    @Column
    private Long totalAnuncios = 0L;

    @Column
    private Long totalEntregas = 0L;

    // Constructors
    public Infraestrutura() {
        this.dataCriacao = System.currentTimeMillis();
    }

    public Infraestrutura(String nome, String urlServidor, Integer capacidadeMaxima, Integer premioEntrega) {
        this();
        this.nome = nome;
        this.urlServidor = urlServidor;
        this.capacidadeMaxima = capacidadeMaxima;
        this.premioEntrega = premioEntrega;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrlServidor() {
        return urlServidor;
    }

    public void setUrlServidor(String urlServidor) {
        this.urlServidor = urlServidor;
    }

    public Integer getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(Integer capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public Integer getPremioEntrega() {
        return premioEntrega;
    }

    public void setPremioEntrega(Integer premioEntrega) {
        this.premioEntrega = premioEntrega;
    }

    public Long getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Set<Local> getLocais() {
        return locais;
    }

    public void setLocais(Set<Local> locais) {
        this.locais = locais;
    }

    public Set<Anuncio> getAnuncios() {
        return anuncios;
    }

    public void setAnuncios(Set<Anuncio> anuncios) {
        this.anuncios = anuncios;
    }

    public Set<UtilizadorInfra> getUtilizadores() {
        return utilizadores;
    }

    public void setUtilizadores(Set<UtilizadorInfra> utilizadores) {
        this.utilizadores = utilizadores;
    }

    public Set<Restricao> getRestricoes() {
        return restricoes;
    }

    public void setRestricoes(Set<Restricao> restricoes) {
        this.restricoes = restricoes;
    }

    public Long getTotalAnuncios() {
        return totalAnuncios;
    }

    public void setTotalAnuncios(Long totalAnuncios) {
        this.totalAnuncios = totalAnuncios;
    }

    public Long getTotalEntregas() {
        return totalEntregas;
    }

    public void setTotalEntregas(Long totalEntregas) {
        this.totalEntregas = totalEntregas;
    }

    /**
     * Incrementa o contador de anúncios
     */
    public void incrementarTotalAnuncios() {
        this.totalAnuncios++;
    }

    /**
     * Incrementa o contador de entregas
     */
    public void incrementarTotalEntregas() {
        this.totalEntregas++;
    }

    /**
     * Retorna o número de conexões disponíveis
     */
    public Integer getConexoesDisponiveis() {
        return Math.max(0, this.capacidadeMaxima - this.utilizadores.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Infraestrutura that = (Infraestrutura) o;
        return Objects.equals(id, that.id) && Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    @Override
    public String toString() {
        return "Infraestrutura{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", capacidadeMaxima=" + capacidadeMaxima +
                ", premioEntrega=" + premioEntrega +
                ", totalAnuncios=" + totalAnuncios +
                ", totalEntregas=" + totalEntregas +
                '}';
    }
}

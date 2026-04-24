package pt.uan.anuncioSloc.persistence.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Local representa um ponto específico de uma Infraestrutura
 * Ex: "Largo da Independência", "Biblioteca da Zona X", etc.
 */
@Entity
@Table(name = "locais")
public class Local implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome; // Ex: "Largo da Independência"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infraestrutura_id", nullable = false)
    private Infraestrutura infraestrutura;

    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Coordenada> coordenadas = new HashSet<>();

    @Column(nullable = false)
    private Long dataCriacao;

    // Estatísticas
    @Column
    private Long totalAnuncios = 0L;

    @Column
    private Long totalEntregas = 0L;

    // Constructors
    public Local() {
        this.dataCriacao = System.currentTimeMillis();
    }

    public Local(String nome, Infraestrutura infraestrutura) {
        this();
        this.nome = nome;
        this.infraestrutura = infraestrutura;
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

    public Infraestrutura getInfraestrutura() {
        return infraestrutura;
    }

    public void setInfraestrutura(Infraestrutura infraestrutura) {
        this.infraestrutura = infraestrutura;
    }

    public Set<Coordenada> getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(Set<Coordenada> coordenadas) {
        this.coordenadas = coordenadas;
    }

    public Long getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Long dataCriacao) {
        this.dataCriacao = dataCriacao;
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
     * Adiciona uma coordenada GPS
     */
    public void adicionarCoordenadaGPS(Double latitude, Double longitude, Integer raioMetros) {
        Coordenada coord = new Coordenada(Coordenada.TipoCoordenada.GPS, latitude, longitude, raioMetros);
        coord.setLocal(this);
        this.coordenadas.add(coord);
    }

    /**
     * Adiciona uma coordenada WiFi
     */
    public void adicionarCoordenadaWiFi(String wifiSSIDs) {
        Coordenada coord = new Coordenada(Coordenada.TipoCoordenada.WIFI, wifiSSIDs);
        coord.setLocal(this);
        this.coordenadas.add(coord);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Local local = (Local) o;
        return Objects.equals(id, local.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Local{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", totalAnuncios=" + totalAnuncios +
                ", totalEntregas=" + totalEntregas +
                '}';
    }
}

package pt.uan.anuncioSloc.persistence.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Coordenada pode ser GPS ou WiFi
 * GPS: latitude, longitude, raio
 * WiFi: lista de SSIDs
 */
@Entity
@Table(name = "coordenadas")
public class Coordenada implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoCoordenada tipo; // GPS ou WIFI

    // GPS coordinates
    @Column(precision = 10, scale = 8)
    private Double latitude;

    @Column(precision = 11, scale = 8)
    private Double longitude;

    @Column
    private Integer raioMetros; // raio em metros para GPS

    // WiFi coordinates
    @Column(length = 500)
    private String wifiSSIDs; // Lista de SSIDs separados por vírgula

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    private Local local;

    // Constructors
    public Coordenada() {
    }

    public Coordenada(TipoCoordenada tipo, Double latitude, Double longitude, Integer raioMetros) {
        this.tipo = tipo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.raioMetros = raioMetros;
    }

    public Coordenada(TipoCoordenada tipo, String wifiSSIDs) {
        this.tipo = tipo;
        this.wifiSSIDs = wifiSSIDs;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoCoordenada getTipo() {
        return tipo;
    }

    public void setTipo(TipoCoordenada tipo) {
        this.tipo = tipo;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRaioMetros() {
        return raioMetros;
    }

    public void setRaioMetros(Integer raioMetros) {
        this.raioMetros = raioMetros;
    }

    public String getWifiSSIDs() {
        return wifiSSIDs;
    }

    public void setWifiSSIDs(String wifiSSIDs) {
        this.wifiSSIDs = wifiSSIDs;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordenada that = (Coordenada) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Coordenada{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", raioMetros=" + raioMetros +
                ", wifiSSIDs='" + wifiSSIDs + '\'' +
                '}';
    }

    public enum TipoCoordenada {
        GPS,
        WIFI
    }
}

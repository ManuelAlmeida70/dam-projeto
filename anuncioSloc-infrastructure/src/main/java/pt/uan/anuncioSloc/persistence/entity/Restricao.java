package pt.uan.anuncioSloc.persistence.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa restrições de entrega de anúncios em uma infraestrutura
 * Ex: excluir determinadas redes televisivas
 */
@Entity
@Table(name = "restricoes")
public class Restricao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infraestrutura_id", nullable = false)
    private Infraestrutura infraestrutura;

    @Column(nullable = false, length = 500)
    private String descricao; // Descrição da restrição

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoRestricao tipo;

    @Column(nullable = false)
    private Long dataCriacao;

    // Constructors
    public Restricao() {
        this.dataCriacao = System.currentTimeMillis();
    }

    public Restricao(Infraestrutura infraestrutura, String descricao, TipoRestricao tipo) {
        this();
        this.infraestrutura = infraestrutura;
        this.descricao = descricao;
        this.tipo = tipo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Infraestrutura getInfraestrutura() {
        return infraestrutura;
    }

    public void setInfraestrutura(Infraestrutura infraestrutura) {
        this.infraestrutura = infraestrutura;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoRestricao getTipo() {
        return tipo;
    }

    public void setTipo(TipoRestricao tipo) {
        this.tipo = tipo;
    }

    public Long getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restricao that = (Restricao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Restricao{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", tipo=" + tipo +
                '}';
    }

    public enum TipoRestricao {
        EXCLUIR_REDE,     // Excluir determinada rede
        INCLUIR_APENAS,   // Incluir apenas determinada rede
        FILTRO_CONTEUDO   // Filtro de conteúdo
    }
}

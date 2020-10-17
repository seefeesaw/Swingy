package com.ashongwe.swingy.model;

import javax.persistence.*;

@Entity
@Table(name = "ARTIFACTS", schema = "swingy")
public class ArtifactsEntity {
    private int id;
    private Artifact artifact;
    private HeroEntity heroEntity;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "int default 1")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "TYPE")
    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArtifactsEntity that = (ArtifactsEntity) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name = "HERO_ID")
    public HeroEntity getHeroEntity() {
        return heroEntity;
    }

    public void setHeroEntity(HeroEntity heroEntity) {
        this.heroEntity = heroEntity;
    }
}

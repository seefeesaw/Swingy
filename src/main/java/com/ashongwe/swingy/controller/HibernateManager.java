package com.ashongwe.swingy.controller;

import com.ashongwe.swingy.model.*;
import com.ashongwe.swingy.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.validation.*;
import java.io.*;
import java.util.List;
import java.util.Set;

public class HibernateManager {
    private static SessionFactory sessionFactory;
    private static HibernateManager hibernateManager;

    private HibernateManager() {
    }

    public static HibernateManager getHibernateManager() {
        if (hibernateManager == null) {
            hibernateManager = new HibernateManager();
            hibernateManager.setUp();
        }
        return hibernateManager;
    }

    public void setUp() {
        Configuration configuration = new Configuration();
        sessionFactory = configuration.configure().buildSessionFactory();
    }

    public void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public boolean saveHero(String name, HeroClass heroClass, Artifact artifact, int attack, int defense, int hitPoints, int y, int x)
    {
        Session session = null;

        try {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();

            HeroEntity hero = new HeroEntity();
            hero.setName(name);
            hero.setHeroClass(heroClass);
            hero.setLevel(1);
            hero.setExperience(0);
            hero.setAttack(attack);
            hero.setDefense(defense);
            hero.setHitPoints(hitPoints);
            hero.setY(y);
            hero.setX(x);

            Set<ConstraintViolation<HeroEntity>> constraintViolations = validator.validate(hero);
            if (constraintViolations.size() != 0) {
                System.out.println("\nList of constraint violations:");
                for (ConstraintViolation<HeroEntity> constraintViolation : constraintViolations) {
                    System.out.println("Constraint Violation: " + constraintViolation.getMessage());
                }
                return false;
            }

            session.save(hero);

            ArtifactsEntity artifactsEntity = new ArtifactsEntity();
            artifactsEntity.setHeroEntity(hero);
            artifactsEntity.setArtifact(artifact);
            session.save(artifactsEntity);

            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            assert session != null;
            session.close();
        }
        return true;
    }

    public HeroEntity getNewHero() {
        Session session = null;

        try {
            session = sessionFactory.openSession();
            HeroEntity heroEntity = session.createQuery("FROM HeroEntity order by id DESC", HeroEntity.class).setMaxResults(1).uniqueResult();

            return heroEntity;
        } finally {
            assert session != null;
            session.close();
        }
    }

    public List<HeroEntity> getListHeroes() {
        Session session = null;

        try {
            session = sessionFactory.openSession();
            List<HeroEntity> heroEntities = session.createQuery(
                    "FROM HeroEntity where hitPoints > 0", HeroEntity.class).list();

            return heroEntities;
        } finally {
            assert session != null;
            session.close();
        }
    }

    public List<HeroEntity> getLeaderboard() {
        Session session = null;

        try {
            session = sessionFactory.openSession();
            List<HeroEntity> heroEntities = session.createQuery(
                    "FROM HeroEntity where hitPoints = 0", HeroEntity.class).list();

            return heroEntities;
        } finally {
            assert session != null;
            session.close();
        }
    }

    public void updateHero(GameEngine game) {
        Session session = null;

        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();

            HeroEntity heroEntity = session.get(HeroEntity.class, game.getHero().getId());
            heroEntity.setLevel(game.getHero().getLevel());
            heroEntity.setExperience(game.getHero().getExperience());
            heroEntity.setAttack(game.getHero().getAttack());
            heroEntity.setDefense(game.getHero().getDefense());
            heroEntity.setHitPoints(game.getHero().getHitPoints());
            heroEntity.setY(game.getHero().getY());
            heroEntity.setX(game.getHero().getX());

            session.update(heroEntity);
            transaction.commit();
        } finally {
            assert session != null;
            session.close();
        }
    }

    public void saveGame(GameEngine game, List<String> gameAction) {
        Session session = null;

        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(game.getGameEntities());
            objectOutputStream.writeObject(game.getVillains());
            objectOutputStream.writeObject(game.getObstacles());
            objectOutputStream.writeObject(gameAction);
            objectOutputStream.close();

            HeroEntity heroEntity = session.get(HeroEntity.class, game.getHero().getId());
            heroEntity.setSave(byteArrayOutputStream.toByteArray());

            session.saveOrUpdate(heroEntity);

            transaction.commit();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert session != null;
            session.close();
        }
    }

    public InputStream loadGame(int heroId) {
        Session session = null;

        try {
            session = sessionFactory.openSession();

            HeroEntity heroEntity = session.get(HeroEntity.class, heroId);
            if (heroEntity.getSave() == null) {
                return null;
            }

            return new ByteArrayInputStream(heroEntity.getSave());

        } finally {
            assert session != null;
            session.close();
        }
    }

    public void updateArtifacts(Hero hero, Artifact artifact) {
        Session session = null;

        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();

            HeroEntity heroEntity = session.get(HeroEntity.class, hero.getId());

            ArtifactsEntity artifactsEntity = new ArtifactsEntity();
            artifactsEntity.setHeroEntity(heroEntity);
            artifactsEntity.setArtifact(artifact);
            
            session.save(artifactsEntity);
            transaction.commit();

        } finally {
            assert session != null;
            session.close();
        }
    }
}

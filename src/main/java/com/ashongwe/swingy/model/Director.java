package com.ashongwe.swingy.model;

public class Director {

    private static Director director;

    private Director() {

    }

    public static Director getDirector() {
        if (director == null) {
            director = new Director();
        }

        return director;
    }

    public static void constructElf(Builder builder, HeroEntity heroEntity) {
        builder.setId(heroEntity.getId());
        builder.setName(heroEntity.getName());
        builder.setHeroClass(HeroClass.Elf);
        builder.setLevel(heroEntity.getLevel());
        builder.setExperience(heroEntity.getExperience());
        builder.setAttack(heroEntity.getAttack());
        builder.setDefense(heroEntity.getDefense());
        builder.setHitPoints(heroEntity.getHitPoints());
        builder.setY(heroEntity.getY());
        builder.setX(heroEntity.getX());
    }

    public static void constructDwarf(Builder builder, HeroEntity heroEntity) {
        builder.setId(heroEntity.getId());
        builder.setName(heroEntity.getName());
        builder.setHeroClass(HeroClass.Dwarf);
        builder.setLevel(heroEntity.getLevel());
        builder.setExperience(heroEntity.getExperience());
        builder.setAttack(heroEntity.getAttack());
        builder.setDefense(heroEntity.getDefense());
        builder.setHitPoints(heroEntity.getHitPoints());
        builder.setY(heroEntity.getY());
        builder.setX(heroEntity.getX());
    }

    public static void constructWizard(Builder builder, HeroEntity heroEntity) {
        builder.setId(heroEntity.getId());
        builder.setName(heroEntity.getName());
        builder.setHeroClass(HeroClass.Wizard);
        builder.setLevel(heroEntity.getLevel());
        builder.setExperience(heroEntity.getExperience());
        builder.setAttack(heroEntity.getAttack());
        builder.setDefense(heroEntity.getDefense());
        builder.setHitPoints(heroEntity.getHitPoints());
        builder.setY(heroEntity.getY());
        builder.setX(heroEntity.getX());
    }
}

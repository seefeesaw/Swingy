package com.ashongwe.swingy.model;

public final class HeroFactory {
    private static final HeroFactory factory = new HeroFactory();

    public static HeroFactory getInstance() {
        return factory;
    }

    public Hero buildHero(HeroEntity heroEntity) {
        Hero hero = null;

        switch (heroEntity.getHeroClass()) {
            case Elf:
                ElfBuilder eb = new ElfBuilder();
                Director.constructElf(eb, heroEntity);
                hero = eb.getResult();
                break;
            case Dwarf:
                DwarfBuilder db = new DwarfBuilder();
                Director.constructDwarf(db, heroEntity);
                hero = db.getResult();
                break;
            case Wizard:
                WizardBuilder hb = new WizardBuilder();
                Director.constructWizard(hb, heroEntity);
                hero = hb.getResult();
                break;
        }

        return hero;
    }
}

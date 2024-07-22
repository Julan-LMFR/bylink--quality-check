package fr.leroymerlin.bylink.qualitycheck.basa.lifecycle;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LifeCycleEnum {

    PRE_INIT("IMPOSSIBLE", "Pre-initialisé", 7),
    INIT("INI", "Initialisé", 3),
    INTERNAL_SERVICE_ON("ASI", "Actif Service Interne", 2),
    TRADE_ON("AC", "Actif Commerce", 1),
    SCHEDULED_FOR_DELETE("SUPP", "Suppression Programmée", 4),
    DELETE_IN_PROGRESS("ESUP", "En Suppression", 5),
    DELETED("SUP", "Supprimée", 6);

    private String name;
    private String desc;
    private int level;

    public static String getDescByName(String name) {
        for (LifeCycleEnum item : LifeCycleEnum.values()) {
            if (item.getName().equals(name)) {
                return item.getDesc();
            }
        }
        return null;
    }

    public static int getLevelByName(String name) {
        for (LifeCycleEnum item : LifeCycleEnum.values()) {
            if (item.getName().equals(name)) {
                return item.getLevel();
            }
        }
        return 0;
    }
}

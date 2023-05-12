package net.xenyria.eem;

public enum EXenyriaServerType {

    HUB("hub-"),
    PAINTSQUAD("ps-"),
    RUSH("rush-"),
    UNKNOWN("");

    EXenyriaServerType(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    private final String idPrefix;
    public static EXenyriaServerType determineServerType(String serverInstanceId) {
        for(EXenyriaServerType type : values()) {
            if(serverInstanceId.startsWith(type.idPrefix)) {
                return type;
            }
        }
        return UNKNOWN;
    }

}

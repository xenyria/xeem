package net.xenyria.eem;

public class PlayingSessionInformation {

    private static boolean onNetwork;
    public static boolean isOnNetwork() { return onNetwork; }
    public static void setOnNetwork(boolean onNetwork) { PlayingSessionInformation.onNetwork = onNetwork; }

    private static EXenyriaServerType currentServerType = EXenyriaServerType.UNKNOWN;
    public static EXenyriaServerType getCurrentServerType() { return currentServerType; }
    public static void setCurrentServerType(EXenyriaServerType currentServerType) {
        PlayingSessionInformation.currentServerType = currentServerType;
    }

    private static String serverInstanceId = "";
    public static String getServerInstanceId() { return serverInstanceId; }
    public static void setServerInstanceId(String serverInstanceId) {
        PlayingSessionInformation.serverInstanceId = serverInstanceId;
    }
}

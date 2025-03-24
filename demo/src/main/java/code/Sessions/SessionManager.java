package code.Sessions;

import java.util.HashMap;
import java.util.UUID;

public class SessionManager {

    public static final SessionManager SESSIONMANAGER = new SessionManager();

    private HashMap<UUID, UserSession> sessions = new HashMap<>();

    private HashMap<UUID, String> users = new HashMap<>();

    public void openSession(UserSession session, String username) {
        sessions.put(session.sessionID, session);
        users.put(session.sessionID, username);
    }

    public UserSession getSession(UUID sessionID) {
        return sessions.get(sessionID);
    }

    public String getUsername(UUID sessionID) {
        return users.get(sessionID);
    }

    public void endSession(UUID sessionID) {
        sessions.remove(sessionID);
    }

}

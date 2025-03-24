package code.Sessions;

import java.util.UUID;

public class UserSession {

	public UUID sessionID;

	private long creationTime;

	public UserSession() {
		sessionID = UUID.randomUUID();
		creationTime = System.currentTimeMillis();
	}

	public long getAgeMillis() {
		return System.currentTimeMillis() - creationTime;
	}

	public long getAgeSec() {
		return (System.currentTimeMillis() - creationTime) / 1000;
	}

	public String getUUID() {
		return sessionID.toString();
	}

}

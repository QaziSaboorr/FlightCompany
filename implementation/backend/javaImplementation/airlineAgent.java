package implementation.backend.javaImplementation;

public class airlineAgent extends user {

    private user agentUser;
    private String password;

    public airlineAgent(user agent, String password) {
        super(agent.getName(), agent.getBirthday(), agent.getAddress(), 
            agent.getPhone(), agent.getEAddress(), agent.gePersonID());
        agentUser= agent;
        this.password = password;
    }

    // Getters
    public user getAgentUser() {
        return this.agentUser;
    }

    public String getPassword() {
        return this.password;
    }

    // Setters
    public void setUser(user u) {
        this.agentUser = u;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    
}

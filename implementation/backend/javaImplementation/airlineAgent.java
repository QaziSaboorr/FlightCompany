package implementation.backend.javaImplementation;

public class airlineAgent extends person {

    private person agentPerson;
    private int employeeID;

    public airlineAgent(person agent, int employeeID) {
        super(agent.getName(), agent.getBirthday(), agent.getAddress(), agent.getPhone(), agent.getEAddress());
        agentPerson = agent;
        this.employeeID = employeeID;
    }

    
}

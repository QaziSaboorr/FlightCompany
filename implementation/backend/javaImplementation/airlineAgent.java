package implementation.backend.javaImplementation;

public class airlineAgent extends person {

    private person agentPerson;
    private int employeeID;

    public airlineAgent(person agent, int employeeID) {
        agentPerson = agent;
        this.employeeID = employeeID;
    }

    
}

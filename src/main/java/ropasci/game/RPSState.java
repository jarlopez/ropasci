package ropasci.game;

public class RPSState
{
    public enum State {
        OPEN, IN_PROGRESS, WAITING_FOR_PEERS, WAITING_FOR_SELF
    }

    public enum StateUpdate {
        ACTION_RECEIVED, ACTION_SENT, ALL_ACTIONS_RECEIVED
    }

    private State state;
    private RPSStateListener listener;
    private int numberOfActionsReceived;

    public RPSState(RPSStateListener listener) {
        this.listener = listener;
        this.state = State.OPEN;
        this.numberOfActionsReceived = 0;
    }

    public State getState(){
        return this.state;
    }

    public int getNumberOfActionsReceived()
    {
        return this.numberOfActionsReceived;
    }

    public void stateUpdate(StateUpdate stateUpdate)
    {
        State oldState = this.state;

        switch (this.state)
        {
            case OPEN:
                if(stateUpdate == StateUpdate.ACTION_RECEIVED)
                {
                    this.state = State.IN_PROGRESS;
                    this.numberOfActionsReceived++;
                }
                else if(stateUpdate == StateUpdate.ACTION_SENT)
                {
                    this.state = State.WAITING_FOR_PEERS;
                }
                break;
            case IN_PROGRESS:
                if(stateUpdate == StateUpdate.ACTION_RECEIVED)
                {
                    this.numberOfActionsReceived++;
                }
                else if(stateUpdate == StateUpdate.ACTION_SENT)
                {
                    this.state = State.WAITING_FOR_PEERS;
                }
                else if(stateUpdate == StateUpdate.ALL_ACTIONS_RECEIVED)
                {
                    this.state = State.WAITING_FOR_SELF;
                }
                break;
            case WAITING_FOR_PEERS:
                if(stateUpdate == StateUpdate.ACTION_RECEIVED)
                {
                    this.numberOfActionsReceived++;
                }
                else if(stateUpdate == StateUpdate.ALL_ACTIONS_RECEIVED)
                {
                    this.state = State.OPEN;
                    this.numberOfActionsReceived = 0;
                }
                break;
            case WAITING_FOR_SELF:
                if(stateUpdate == StateUpdate.ACTION_SENT)
                {
                    this.state = State.OPEN;
                    this.numberOfActionsReceived = 0;
                }
        }

        if(oldState != state)
        {
            this.listener.onGameStateChanged(this.state);
        }
    }
}

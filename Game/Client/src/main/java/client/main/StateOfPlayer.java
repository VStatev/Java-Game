package client.main;

import MessagesGameState.EPlayerGameState;

public enum StateOfPlayer {
	ShouldWait, ShouldActNext, Won, Lost;
	
	public static StateOfPlayer convert(EPlayerGameState state) {
    	switch(state) {
    		case ShouldWait : return StateOfPlayer.ShouldWait; 
    		case ShouldActNext : return StateOfPlayer.ShouldActNext;
    		case Won: return StateOfPlayer.Won;
    		default: return StateOfPlayer.Lost;
    	}
    }
}

package client.main;

import MessagesBase.EMove;

public enum Direction {
	Left, Right, Up, Down;
	
	/**
	 * Converting a given direction into an Emove object required for building a Move to send to Server
	 * map the corresponding value of a Direction into EMove
	 * @param d direction that a player wants to move to
	 * @return EMove object holding the same value as d
	 */
    public static EMove convert(Direction d) {
    	switch(d) {
    		case Left : return EMove.Left; 
    		case Right : return EMove.Right;
    		case Down : return EMove.Down;
    		default : return EMove.Up;
    	}
    }
}

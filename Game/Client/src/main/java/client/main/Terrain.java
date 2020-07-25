package client.main;

import java.util.Random;

import MessagesBase.ETerrain;

public enum Terrain {
	Mountain(2,2), Water(1000,1000), Grass(1,1);
	
	public final int requiredToLeave;
	public final int requiredToEnter;
	
	private Terrain(int stepsLeave, int stepsEnter) {
		this.requiredToLeave = stepsLeave;
		this.requiredToEnter = stepsEnter;
	}
	/**
	 * @return how many steps are required to be sent in order to leave a field made from this terrain
	 */
	public int getRequiredToLeave() {
		return this.requiredToLeave;
	}
	/**
	 * @return how many steps are required to be sent in order to enter a field made from this terrain
	 */
	public int getRequiredToEnter() {
		return this.requiredToEnter;
	}
	
	/**
	 * Method that return a terrain based on an index created at random
	 * used in generateMap()
	 * @return random terrain 
	 */
    public static Terrain getRandomTerrain() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }
    
    /**
     * Method that converts a Terrain into ETerrain in order a Map to be converted into HalfMap
     * Map current value of Terrain to ETerain
     * @param t Terrain that needs to be converted
     * @return ETerrain value corresponding to t
     */
    public static ETerrain convert(Terrain t) {
    	switch(t) {
    		case Mountain : return ETerrain.Mountain; 
    		case Water : return ETerrain.Water;
    		default : return ETerrain.Grass;
    	}
    }
}

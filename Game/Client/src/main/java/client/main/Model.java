package client.main;

public class Model {
	private Map map;
	private Boolean treasurePresent;
	private Boolean enemyFortressPresent;
	private StateOfPlayer state;
	private Point treasurePosition;
	private Point enemyFortressPosition;
	
	public Model(Map map) {
		this.map = map;
		this.treasurePresent = this.map.isTreasurePresent();
		if(this.treasurePresent) {
			Point temp = this.map.getTreasurePosition();
			this.treasurePosition = new Point(temp.getX(), temp.getY());
		}
		this.enemyFortressPresent = this.map.isEnemyFortressPresent();
		if(this.enemyFortressPresent) {
			Point temp = this.map.getEnemyFortressPosition();
			this.enemyFortressPosition = new Point(temp.getX(), temp.getY());
		}
	}

	
	public Point getTreasurePosition() {
		return this.treasurePosition;
	}
	public void setTreasurePresent(Boolean b) {
		if(b == true) {
			Point temp = this.map.getTreasurePosition();
			this.treasurePosition = new Point(temp.getX(), temp.getY());
		}
		this.treasurePresent = b;
	}
	
	public void setState(StateOfPlayer state) {
		this.state = state;
	}
	
	public void setEnemyFortressPresent(Boolean b) {
		if(b == true) {
			Point temp = this.map.getEnemyFortressPosition();
			this.enemyFortressPosition = new Point(temp.getX(), temp.getY());
		}
		this.enemyFortressPresent = b;
	}
	
	public Boolean getTreasurePresent() {
		return this.treasurePresent;
	}
	
	public Boolean getEnemyFortressPresent() {
		return this.enemyFortressPresent;
	}

	public Point getEnemyFortressPosition() {
		return enemyFortressPosition;
	}

}

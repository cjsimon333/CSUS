package com.mycompany.a2;

import com.codename1.ui.geom.Point2D;

// All opponents are moving and they all move the same way.
public abstract class Opponent extends GameObject implements IMovable {
	// Attributes
	// All opponents have integer attributes speed and direction,
	// which are used to define how they move through the world
	private int direction;
	private int speed;
	// Attribute Bounds
	// Each opponent size is chosen randomly when created, and constrained to 20 and 50
	static final int MIN_SIZE = 20;
	static final int MAX_SIZE = 50;
	// Direction is initialized to a random value, ranging between 0 and 359
	static final int MIN_DIRECTION = 0;
	static final int MAX_DIRECTION = 359;
	static final int MIN_SPEED 	   = 0;
	
	// Constructors
	public Opponent(GameWorld gw) {
		super(gw);
		this.setSize(R.nextInt(MIN_SIZE, MAX_SIZE));
		this.setDirection(R.nextInt(MIN_DIRECTION, MAX_DIRECTION));
		this.setSpeed(MIN_SPEED);
		this.setColor(DEFAULT_COLOR);
	}
	public Opponent(int size, GameWorld gw) {
		super(size, gw);
		this.setDirection(R.nextInt(MIN_DIRECTION, MAX_DIRECTION));
		this.setSpeed(MIN_SPEED);
	}
	public Opponent(int size, int direction, GameWorld gw) {
		super(size, gw);
		this.setDirection(direction);
		this.setSpeed(MIN_SPEED);
	}
	public Opponent(int size, int direction, int speed, GameWorld gw) {
		super(size, gw);
		this.setDirection(direction);
		this.setSpeed(speed);
	}
	
	// Accessors
	public int getDirection() {
		return direction;
	}
	public int getSpeed() {
		return speed;
	}
	
	// Mutators
	@Override
	public boolean setSize(int size) {
		// All opponents have a size which cannot be changed once the object is created
		return false;
	}
	public boolean setDirection(int direction) {
		boolean withinBounds = (MIN_DIRECTION <= direction && direction <= MAX_DIRECTION);
		if(withinBounds) this.direction = direction;
		return withinBounds;
	}
	public boolean setSpeed(int speed) {
		boolean withinBounds = 0 < speed;
		if(withinBounds) this.speed = speed;
		return withinBounds;
	}
	
	// Actions
	/*
	 * The direction of opponents indicates heading specified by a compass angle in degrees:
	 * 0 is north
	 * 90 is east
	 * 180 is south
	 * 270 is west
	 * See below for details on updating an opponent�s position when its move() method is invoked.
	 * @see com.mycompany.myapp.IMovable#move()
	 */
	public void move() {
		// Calculate the degree of the object based on it's location
		int degree = (this.getDirection() * 180) % 360;
		// Calculate the x and y components based on the object's speed and current location
		double x = this.getLocation().getX() + Math.cos(90 - degree) * this.getSpeed();
		double y = this.getLocation().getY() + Math.sin(90 - degree) * this.getSpeed();
		// Apply the new location
		this.setLocation(new Point2D(x, y));
	}
}

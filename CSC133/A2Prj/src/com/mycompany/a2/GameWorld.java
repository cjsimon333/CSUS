package com.mycompany.a2;

import java.util.Observable;
import java.util.StringTokenizer;

import com.codename1.maps.BoundingBox;
import com.codename1.maps.Coord;
import com.codename1.ui.geom.Point2D;

public class GameWorld extends Observable {
    // Helper Attributes
    static final Random R = Random.getInstance();
    
    // Using a global reference to the spaceship allows for us to reference it
    // without having to search for it in the GameObjects
    private static Spaceship SPACESHIP;

    // Attributes
    // The origin of the "world" (location (0, 0)) is at the lower left hand corner
    private int width       = 1024;
    private int height      = 768;
    
    // Game Specific Attributes
    // TODO Make this GameWorld class an abstract class that
    //      gets extended to include these game specific methods,
    //      variables and logic.
    //
    // The score of the game
    private int score       = 0;
    // The number of Astronauts and Aliens captured by the Spaceship
    int capturedAstronauts  = 0;
    int capturedAliens      = 0;
    // The number of remaining Astronauts and Aliens in the GameWorld
    int remainingAstronauts = 0;
    int remainingAliens     = 0;
    // The sound state
    boolean isSoundOn = true;
    // The GameObjects in the GameWorld
    private static GameObjectCollection GameObjects = new GameObjectCollection();
    
    // Constructors
    public GameWorld() {}
    public GameWorld(boolean init) {
        // Why not call init in here instead of in the Game() constructor?
        // @see com.mycompany.myapp.Game()
        if(init) init();
    }
    
    // Accessors
    public int getScore() {
		return score;
	}
    public int getCapturedAstronauts() {
    	return capturedAstronauts;
	}
    public int getCapturedAliens() {
    	return capturedAliens;
	}
    public int getRemainingAstronauts() {
    	return remainingAstronauts;
	}
    public int getRemainingAliens() {
    	return remainingAliens;
	}
    public int getWidth() {
		return width; 
	}
	public int getHeight() {
		return height; 
	}
    public boolean isSoundOn() {
    	return isSoundOn;
    }
    
    // Mutators
    public void setWidth(int width) {
		this.width = width; 
	}
	public void setHeight(int height) {
		this.height = height; 
	}
	
    /**
     * Creates the initial game objects and layout
     */
    public void init() {
        int initialAstronauts = remainingAstronauts = 4;
        int initialAliens     = remainingAliens     = 3;
        
        // Add the spaceship, astronauts and aliens
        this.addObject(SPACESHIP = Spaceship.getInstance(this));
        for(int a = 0; a < initialAstronauts; a++) {
            this.addObject(new Astronaut(this));
        }
        for(int a = 0; a < initialAliens; a++) {
            this.addObject(new Alien(this));
        }       
    }
    
    /**
     * Update function that runs on every iteration of the game
     */
    public void update() {
        IIterable i = GameObjects.getIterator();
    	// Apply properties to all GameObjects in the world
        // @TODO As per the assignment specification, create a custom iterator class
        // @TODO to loop through each object. See moveTo(String filter) for duplicate code
        while(i.hasNext()) {
        	Object obj = i.getNext();
            // Move all objects that can move.
            // Some game objects are moving, meaning that they provide an interface that allows other
            // objects to tell them to move. Telling a moving object to move() causes the object to update
            // its location.
            if(obj instanceof IMovable) {
                IMovable mObj = (IMovable)obj;
                mObj.move();
            }
        }
    }
    
    /**
     * Adds a new GameObject to the world
     * @return boolean
     */
    public final boolean addObject(GameObject o) {
        return GameObjects.add(o);
    }
    
    // MoveTo another destination GameObject
    public final boolean moveTo(GameObject o, GameObject destination) {
        // Exit prematurely if the destination object is null
        if(destination == null) return false;
        
        // Set the location of GameObject o to the location of the destination object
        o.setLocation(destination.getLocation());
        // o was successfully moved
        return true;
    }
    // MoveTo using GameObject filter
    public final boolean moveTo(GameObject o, String filter) {
        // Generate a list of the candidate objects according to the given filter
        GameObjectCollection foundObjects = GameWorld.find(filter);
        
        // Exit prematurely if no objects are found
        if(foundObjects == null || foundObjects.size() == 0) return false;
        
        // Set the location of GameObject o to the location of the randomly chosen object
        int randomIndex = R.nextInt(0, foundObjects.size() - 1);
        GameObject randomObject = foundObjects.get(randomIndex);
        o.setLocation(randomObject.getLocation());
        // o was successfully moved
        return true;
    }

    /**
     * Find multiple objects given a GameObject filter
     * @param  filter
     * @return AbstractList<GameObject>
     * 
     * @TODO Create filter parser to create more advanced queries with multiple GameObject type support
     * @link http://stackoverflow.com/q/12203003/2104168
     */
    private static final GameObjectCollection findMany(String filter, boolean multiple) {
        GameObjectCollection foundObjects = new GameObjectCollection();
        
        // Iterate through each object
        IIterable i = GameObjects.getIterator();
        while(i.hasNext()) {
        	GameObject obj = i.getNext();
            // Check for each filter in each object
            for(String objectFilter : split(filter, "|")) {
                if(obj.getType().equals(objectFilter)) {
                    foundObjects.add(obj);
                    if(!multiple) return foundObjects;
                }
            }
        }
        // No GameObject was found
        return foundObjects;
    }
    public static final GameObjectCollection find(String filter) {
        if(filter == "*") return GameWorld.findAll(); // Get all objects
        // Find and return multiple GamoeObjects from the list if applicable
        return GameWorld.findMany(filter, true);
    }
    // TODO: Create find N instead of 1 or many
    public static final GameObject findOne(String filter) {
        // Find and return one object from the list
        return (GameWorld.findMany(filter, false)).get(0);
    }
    private static final GameObjectCollection findAll() {
        return GameObjects;
    }
    private static final GameObject findRandom(String filter) {
        GameObjectCollection objects = GameWorld.find(filter);
        int index = R.nextInt(0, objects.size());
        GameObject randomObject = objects.get(index);
        return randomObject;
    }
    /**
     * Get a count of the number of objects returned 
     * @param  filter
     * @return int
     * 
     * @TODO Incorporate into existing functionality of find()
     *       for more code re-usability
     * @TODO Consider the benefits of storing the last filter
     *       and getting data from it, after the find is completed.
     *       This could potentially save one unnecessary and repetitive
     *       loops through each GameObject
     *       Example: Get the count of the last search
     */
    private static final int findCount(String filter) {
        return GameWorld.find(filter).size();
    }
    
    /**
     * Move the spaceship to a random Astronaut or Alien
     */
    public boolean moveSpaceshipToAstronaut() {
        // Assuming that we only have one and only one spaceship,
        // Transfer that spaceship to the location of a randomly selected astronaut
        return this.moveTo(SPACESHIP, "Astronaut");
    }
    public boolean moveSpaceshipToAlien() {
        // Transfer that spaceship to the location of a randomly selected alien
        return this.moveTo(SPACESHIP, "Alien");
    }

    /**
     * Shrinks and expands the size of the spaceship
     * @param  size
     * @return boolean
     */
    public boolean expand(int size) {
        // Increase the size of the spaceship
        return SPACESHIP.setSize(SPACESHIP.getSize() + size);
    }
    public boolean contract(int size) {
        // Shrink the size of the spaceship
        return SPACESHIP.setSize(SPACESHIP.getSize() - size);
    }

    /**
     * Check for a collision between the spaceship and another object
     * using their respective bounding boxes
     * @TODO Create interface ICollidable for GameObjects.
     * @TODO Create smart collision checking.
     *       An ICollidable that checks for collision between two object
     *       for each GameObject has a time complexity of O(n^n) every frame.
     *       Using a grid to check for objects that are both within the grid
     *       will reduce this time.
     */
    public void openDoor() {
        GameObject collider = SPACESHIP;
        BoundingBox colliderCollisionMask = collider.getCollisionMask();
        
        IIterable i = GameObjects.getIterator();
        while(i.hasNext()) {
        	GameObject o = i.getNext();
            // Don't check for collision for the colliding object
            if(o == collider) continue;
            Point2D oLocation = o.getLocation();
            Coord oCoord = new Coord(oLocation.getY(), oLocation.getX());
            if(colliderCollisionMask.contains(oCoord)) {
                // TODO checkCollisions within respective objects, not in the GameWorld itself
                if(collider.getType().equals("Alien")) {
                    // Subtract 10 points and remove the Alien
                    // TODO Create score handler within GameObject itself?
                    //      Would this be more modular than handling score in the GameWorld?
                    score -= 10;
                    capturedAliens++;
                    remainingAliens--;
                    o = null;
                }
                if(collider.getType().equals("Astronaut")) {
                    // Add score based on the Astronaut's current health and remove the Astronaut
                    // The more health, the better
                    // TODO Possibly look into GameObject constants for different score
                    //      multipliers for different events.
                    capturedAstronauts++;
                    remainingAstronauts--;
                	score += ((Astronaut) o).getHealth() * 10;
                    o = null;
                }
            }
        }
        this.setChanged();
		this.notifyObservers();
    }

    /**
     * Collide an Alien with another Alien or with an Astronaut
     * @return boolean
     */
    public boolean colAlien() {
        GameObjectCollection aliens = GameWorld.find("Alien");
        // There must be at least two aliens in the GameWorld
        if(aliens.size() < 2) return false;
        
        // Get a random alien
        Alien randomAlien = (Alien)GameWorld.findRandom("Alien");
        
        // Create a new alien in the location of the randomly selected alien
        Alien newAlien = new Alien(this);
        newAlien.setLocation(randomAlien.getLocation());
        return this.addObject(newAlien);
    }
    public boolean colAstronautAlien() {
        // Pick a random Astronaut to hit
        Astronaut randomAstronaut = (Astronaut)GameWorld.findRandom("Astronaut");
        return randomAstronaut.hit();
    }
    public boolean addAlien() {
    	this.addObject(new Alien(this));
    	remainingAliens++;
    	this.setChanged();
		this.notifyObservers();
    	return true;
    }
    /**
     * Print the points of game state values:
     * 1. Current score
     * 2. Number of astronauts rescued
     * 3. Number of aliens sneaked in to the spaceship
     * 4. Number of astronauts left in the world
     * 5. Number of aliens left in the world
     * Output should be appropriately labeled in easily readable format
     */
    public void printObjects() {
        // TODO Use printf for readability, though it doesn't seem to be implemented in codename1...
        System.out.println("Score:                " + score);
        System.out.println("Rescued Astronauts:   " + capturedAstronauts);
        System.out.println("Sneaked Aliens:       " + capturedAliens);
        System.out.println("Remaining Astronauts: " + GameWorld.findCount("Astronauts"));
        System.out.println("Remaining Aliens:     " + GameWorld.findCount("Aliens"));
    }

    /**
     * Print out all objects in the GameWorld
     */
    public void printMap() {
        IIterable i = GameObjects.getIterator();
    	while(i.hasNext()) {
    		GameObject obj = i.getNext();
    		// Print each object
            System.out.println(obj);
        }
    }
    
    /**
     * Move the spaceship
     * @param amount
     */
    public boolean moveSpaceshipUp(int amount) {
        return SPACESHIP.moveUp(amount);
    }
    public boolean moveSpaceshipDown(int amount) {
        return SPACESHIP.moveDown(amount);
    }
    public boolean moveSpaceshipLeft(int amount) {
        return SPACESHIP.moveLeft(amount);
    }
    public boolean moveSpaceshipRight(int amount) {
        return SPACESHIP.moveRight(amount);
    }
    
    /**
     * Toggle Sound
     */
    public boolean toggleSound() {
        isSoundOn = !isSoundOn;
        this.setChanged();
		this.notifyObservers();
        return isSoundOn;
    }
    
    /**
     * Exit the game
     */
    public boolean exit() {
        System.exit(0);
        return true;
    }
    
    // Helper Methods
    /**
     * Split a string into substrings given a delimiter
     * String.split() doesn't seem to be defined in some versions of J2ME
     * @link http://stackoverflow.com/q/2724745/2104168
     * @param  str
     * @param  delim
     * @return String[]
     */
    private final static String[] split(String str, String delim) {
        StringTokenizer stringTokenizer = new StringTokenizer( str, delim );
        String[] strArr = new String[stringTokenizer.countTokens()];
        int i = 0;
        while( stringTokenizer.hasMoreTokens() ) {
            strArr[i] = stringTokenizer.nextToken();
        }
        return strArr;
    }
}

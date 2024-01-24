import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.Arrays;

/**
 * A class representing shared characteristics of animals.
 * Each animal has a gender. 
 * It is possible for an animal to get an infection and heal.
 *
 * @version 2022.02.21
 */
enum Gender { 
    MALE, FEMALE;
}

public abstract class Animal extends Organism
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // to differentiate male and female.
    private Gender gender;    
    //the level of infection for each animal. 
    private int infection = 0; 
    //whether the animal is immune toward disease or not. 
    private boolean isImmune = false; 
    //max time each animal will be infected for. 
    private static final int MAX_INFECTION_TIME = 5;
    
    public Animal(Field field, Location location)
    {
        super(field, location);
        setGender(randomGender());    
    }

    /**
     * Look for each animal's food source adjacent to the current location.
     * Only the first food source found is eaten.
     */
    abstract protected Location findFood();

    /**
     * Generate a number representing the number of births, if it can breed.
     */
    abstract protected int breed();

    /**
     * Return the breeding age for this animal 
     */
    abstract protected int getBreedingAge();
 
    /**
     * Return the gender for this animal .
     * @return The animal's gender.
     */
    protected Gender getGender() 
    {
        return gender;
    }

    /**
     * Give this animal a new gender.
     * @param newGender The animal's new gender.
     */
    private void setGender(Gender newGender) 
    {
        this.gender = newGender;  
    }
    
    /**
     * Generate a random gender (either male or female) for this animal. 
     * @return Either female or male
     */
    private Gender randomGender() 
    {      
        Gender [] choice = Gender.values();
        int randomNumber = rand.nextInt(choice.length);
        return choice[randomNumber];
    }  

    /**
     * An animal can breed if the object at its adjacent location is an animal 
     * of the same species but different gender. Also, both animals need to be of 
     * breeding age. 
     * @return True if all conditions satisfied
     */
    protected boolean canBreed() 
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            //object at adjacent location needs to be an animal
            if (organism instanceof Animal) {
                Animal animal = (Animal) organism;
                //adjacent animal needs to be alive
                if (animal.isAlive()){
                    //same species for both animals 
                    if (sameSpecies(animal)) {
                        //different genders for both animals 
                        if (diffGender(animal)) {
                            //both animals need to be breeding age 
                            if (animal.ofBreedingAge() && ofBreedingAge()){
                                return true;                     
                            }
                        }
                    }
                }
            }
        }
        return false; 
    }

    /**
     * Check whether both animals are of different gender.
     * @return true if different gender, false if not.
     */        
    private boolean diffGender(Animal other) 
    {
        return !other.getGender().equals(gender);
    }

    /**
     * Check whether both animals are of different species.
     * @return true if same species, false if not.
     */        
    private boolean sameSpecies(Animal other) 
    {
        return other.getClass().equals(getClass());
    }

    /**
     * Check whether animals have reached breeding age.
     * @return true if animal of breeding age, false if not. 
     */
    private boolean ofBreedingAge()
    {
        return getAge() <= getBreedingAge();
    }   

    //disease
    /**
     * Infect animal if it is not immune. 
     */
    public void infect()
    {
        if(isAlive() && !isImmune) {
            infection = rand.nextInt(MAX_INFECTION_TIME); 
            if (infection == MAX_INFECTION_TIME) {
                setDead();
            }
            isImmune = true;
        }
    } 
    
    /**
     * Check whether animals are infected. 
     * @return true if infected, false if not
     */
    public boolean isInfected() {
        return infection > 0;
    }

    /**
     * Look for uninfected animals adjacent to the current location. 
     * If found, infect them.
     */
    protected void infectOthers()
    {
        Field field = getField();
        Location location = getLocation();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if(object instanceof Animal) {
                Animal other = (Animal) object;
                if(other.isAlive()){
                    other.infect();
                }
            }
        }
    }     

    /**
     * If animal is infected, execute the healing process. If infection reaches
     * zero, animal is healed. 
     */
    protected void heal() 
    {
        if (isInfected()) {
            infection --; 
        }      
    }
}


import java.util.ArrayList;
import java.util.HashMap;
/**
 * A class declaring what an Animal consumes 
 * using a HashMap
 *
 * @version 2016.02.29 (2)
 */
public class FoodChain
{
    //A HashMap of an animal and list of food it consumes
    private HashMap<String, ArrayList<String> >foodChain;
    private ArrayList<String> hyenaFood;
    private ArrayList<String> lionFood;
    private ArrayList<String> giraffeFood;
    private ArrayList<String> zebraFood;
    //Objects of Animal and Plant calsses
    private Zebra zebra;
    private Lion lion; 
    private Hyena hyena;
    private Giraffe giraffe;
    private Grass grass;
    private Tree tree;

    /**
     * Adding food of animals into respective ArrayLists
     * Mapping lists with corresponding Animal key 
     */
    public FoodChain()
    {
       
        
        hyenaFood = new ArrayList();
        hyenaFood.add("Zebra");
        hyenaFood.add("Giraffe");
        
       
        lionFood = new ArrayList();
        lionFood.add("Zebra");
        lionFood.add("Giraffe");

        zebraFood = new ArrayList();
        zebraFood.add("Grass");

        giraffeFood = new ArrayList();
        giraffeFood.add("Grass");
        giraffeFood.add("Tree");

        foodChain = new HashMap<>();
        foodChain.put("Hyena", hyenaFood);
        foodChain.put("Lion", lionFood);
        foodChain.put("Giraffe", giraffeFood);
        foodChain.put("Zebra", zebraFood);
    }

    /**
     * @param consumer Actor that is the predator 
     * @param provider Actor that is the prospective prey
     * @return true If Animal can consume the prey/plant
     */
    public boolean canEat(Actor consumer, Actor provider)
    {
       //Check if provider is edible (valid for plant)
       if(provider.isEdible())
       {
           //Check is provider is in consumer's food ArrayList
           ArrayList<String> possibleFood = foodChain.get(consumer.getClass().getSimpleName());
           if (possibleFood.contains(provider.getClass().getSimpleName()))
           {
               return true;
           }        
       }
       return false;
    }
    
    /**
     * @param actor Actor f
     * @return Start food value in Actor
     */
    public int getStartValue(Actor actor)
    {
        return getFood(actor,0);
    }
    
    /**
     * @return 200 as initial food value in Actor
     */
    public int getFood(Actor actor,int index)
    {
        return 300;
    }   
}

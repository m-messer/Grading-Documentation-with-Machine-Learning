package src;

/**
 * An Enum which dictates the allowed stats for Actors.
 * No information is held on any of these,
 * except that they are the only types of stats acknowledged by the simulation for the actors.
 *
 * @version 2021.03.03
 */
public enum StatTypes {
    BREEDING_AGE(), // The age at which an actor can start breeding
    MAX_AGE(), // The amx age for an actor, it dies when reaching this
    BREEDING_PROBABILITY(), // The chance for breeding to produce offsprings
    MAX_LITTER_SIZE(), // The maximum amount of offsprings an actor can have in one birth
    NUTRITIONAL_VALUE(), // The nutritional value this actor gives when it is eaten
    SIGHT(), // The distance at which an actor can look for something (for example food or mates)
    BITE(); // The damage a single bite of this animal can produce, usually used for herbivores when biting plants

    /**
     * Constructor for StatTypes, does nothing.
     */
    StatTypes(){}
}

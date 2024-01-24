package Main;

/**
 * Enumeration class SimulationStage
 * Represents the stages and steps that will be used to generate the Habitat in the simulation.
 *
 * @version 2022.02.19
 */
public enum SimulationStage
{
    //generation stages should happen in this order (top to bottom).
    ELEVATION_GENERATION,
    WEATHER_GENERATION,
    WATER_GENERATION,
    SATURATION_GENERATION,
    FLORA_GENERATION,
    FAUNA_GENERATION,
    SIMULATION
}

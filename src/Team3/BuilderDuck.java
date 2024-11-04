package Team3;

import battlecode.common.*;

public class BuilderDuck extends Duck {
    private enum State { SETUP, DEFENDING, EXPLORING }
    private State state = State.SETUP;
    private int trapCooldown = 0;
    private static final int SENSING_RADIUS = 10; // Adjust based on game settings

    public BuilderDuck(RobotController rc) {
        super(rc);
        skill = SkillType.BUILD;
    }

    @Override
    public void play() throws GameActionException {
        lookForFlag();

        // Perform actions based on game phase and resources
        if (rc.getRoundNum() < GameConstants.SETUP_ROUNDS) {
            setupDefensivePerimeter(); // Initial defensive setup
            state = State.DEFENDING; // Transition to DEFENDING state
        } else {
            switch (state) {
                case DEFENDING:
                    guardFlag();
                    adaptiveTrapPlacement(); // Trap placement based on nearby enemies
                    break;
                case EXPLORING:
                    gatherResources(); // Resource gathering in exploring mode
                    break;
            }
        }

        // Reduce cooldown at the end of the turn
        if (trapCooldown > 0) {
            trapCooldown--;
        }
        reduceCooldown();
    }

    private void setupDefensivePerimeter() throws GameActionException {
        MapLocation allyFlagLocation = rc.getLocation().add(allySpawnZoneDirection());

        for (Direction dir : Direction.allDirections()) {
            MapLocation targetLocation = allyFlagLocation.add(dir);

            // Create water barriers as a primary defense
            if (rc.canFill(targetLocation) && !hasCooldown()) {
                rc.fill(targetLocation);
                applyCooldown(GameConstants.FILL_COOLDOWN);
            }

            // Place explosive traps around the flag for additional protection
            if (rc.canBuild(TrapType.EXPLOSIVE, targetLocation) && rc.getCrumbs() >= GameConstants.FILL_COST && !hasCooldown()) {
                placeTrap(TrapType.EXPLOSIVE, targetLocation);
            }
        }
    }

    private void guardFlag() throws GameActionException {
        MapLocation flagLocation = rc.getLocation(); // Assuming the flag location is the robot's current location

        // Check for nearby enemies and adjust state based on threat level
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(SENSING_RADIUS, rc.getTeam().opponent());
        if (nearbyEnemies.length > 0) {
            // System.out.println("Enemies detected near flag. Guarding flag.");
            if (nearbyEnemies.length > 3) { // If enemies are overwhelming, switch to exploring
                state = State.EXPLORING;
            }
        }
    }

    private void adaptiveTrapPlacement() throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam().opponent());

        if (nearbyEnemies.length > 0) {
            for (RobotInfo enemy : nearbyEnemies) {
                MapLocation targetLocation = enemy.location;

                // Clear water if present at the target location to make space for traps
                if (!rc.sensePassability(targetLocation) && rc.canDig(targetLocation) && !hasCooldown()) {
                    rc.dig(targetLocation);
                    applyCooldown(GameConstants.DIG_COOLDOWN);
                } else {
                    // Place an explosive trap near enemies for effective damage
                    placeTrap(TrapType.EXPLOSIVE, targetLocation);
                }
                break; // Limit trap placement to one per turn for resource efficiency
            }
        } else {
            // No enemies nearby; focus on creating barriers or stun traps
            Direction randomDir = randomDirection();
            MapLocation targetLocation = rc.getLocation().add(randomDir);

            if (rc.getCrumbs() >= GameConstants.FILL_COST && rc.canBuild(TrapType.WATER, targetLocation) && !hasCooldown()) {
                placeTrap(TrapType.WATER, targetLocation); // Create water obstacles
            } else if (rc.getCrumbs() >= GameConstants.DIG_COST && rc.canBuild(TrapType.STUN, targetLocation) && !hasCooldown()) {
                placeTrap(TrapType.STUN, targetLocation); // Create stun traps to hinder enemies
            }
        }
    }

    private void gatherResources() throws GameActionException {
        // System.out.println("Exploring and gathering resources...");

        // Check for resources within sensing radius
        MapLocation resourceLocation = findNearestResource();
        if (resourceLocation != null) {
            moveToward(resourceLocation); // Move towards the resource location
        } else {
            moveInRandomDirection(); // Explore randomly if no resources are detected
        }
    }

    // Helper method to find the nearest resource (simplified example)
    private MapLocation findNearestResource() {
        // Placeholder: Implement logic to detect nearby resources and return location
        return null;
    }

    @Override
    public void collectCrumbs() throws GameActionException {
        MapLocation[] crumbLocations = rc.senseNearbyCrumbs(GameConstants.VISION_RADIUS_SQUARED);

        if (crumbLocations.length > 0) {
            moveToward(crumbLocations[0]); // Move towards the nearest crumb location
        } else {
            moveInRandomDirection(); // Explore randomly if no crumbs are nearby
        }
    }

    public void placeTrap(TrapType trapType, MapLocation targetLocation) throws GameActionException {
        if (rc.canBuild(trapType, targetLocation) && !hasCooldown()) {
            rc.build(trapType, targetLocation);
            applyCooldown(GameConstants.FILL_COOLDOWN); // Assuming FILL_COOLDOWN for trap building
            trapCooldown = 15; // Cooldown reset for trap placement
            // System.out.println("Placed " + trapType + " trap at: " + targetLocation);
        }
    }
}

package Team3;

import battlecode.common.*;

public class BuilderDuck extends Duck {
    private enum State { SETUP, DEFENDING, EXPLORING }
    private State state = State.SETUP;
    private int trapCooldown = 0;
    private int cooldown = 0;

    // Adjust based on game settings
    private static final int SENSING_RADIUS = 10;

    public BuilderDuck(RobotController rc) {
        super(rc, SkillType.BUILD);
    }

    @Override
    public void play() throws GameActionException {
        RobotController rc = getRobotController();
        super.setupPlay();

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
        RobotController rc = getRobotController();
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
        RobotController rc = getRobotController();
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
        RobotController rc = getRobotController();
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
            for (Direction randomDir : randomDirections()) {
                MapLocation targetLocation = rc.getLocation().add(randomDir);

                if (rc.getCrumbs() >= GameConstants.FILL_COST && rc.canBuild(TrapType.WATER, targetLocation) && !hasCooldown()) {
                    placeTrap(TrapType.WATER, targetLocation); // Create water obstacles
                    break;
                } else if (rc.getCrumbs() >= GameConstants.DIG_COST && rc.canBuild(TrapType.STUN, targetLocation) && !hasCooldown()) {
                    placeTrap(TrapType.STUN, targetLocation); // Create stun traps to hinder enemies
                    break;
                }
            }
        }
    }

    public int gatherResources() throws GameActionException {
        // System.out.println("Exploring and gathering resources...");

        // Check for resources within sensing radius
        MapLocation resourceLocation = findNearestResource();
        if (resourceLocation != null) {
            moveToward(resourceLocation); // Move towards the resource location
            return 0;
        } else {
            moveInRandomDirection(); // Explore randomly if no resources are detected
            return 1;
        }
    }

    // Helper method to find the nearest resource (simplified example)
    public MapLocation findNearestResource() {
        // Placeholder: Implement logic to detect nearby resources and return location
        return null;
    }

    public void placeTrap(TrapType trapType, MapLocation targetLocation) throws GameActionException {
        RobotController rc = getRobotController();
        if (rc.canBuild(trapType, targetLocation) && !hasCooldown()) {
            rc.build(trapType, targetLocation);
            applyCooldown(GameConstants.FILL_COOLDOWN); // Assuming FILL_COOLDOWN for trap building
            trapCooldown = 15; // Cooldown reset for trap placement
            // System.out.println("Placed " + trapType + " trap at: " + targetLocation);
        }
    }

    public boolean hasCooldown() {
        return cooldown > 0;
    }

    public int applyCooldown(final int amount) {
        cooldown += amount;
        return cooldown;
    }

    public int reduceCooldown() {
        if (cooldown > 0) {
            cooldown--;
        }
        return cooldown;
    }
}

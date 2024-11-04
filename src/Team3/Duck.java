package Team3;

import battlecode.common.*;
import java.util.Random;

public class Duck {
    RobotController rc;
    SkillType skill;
    private static final Random rng = new Random();
    private int cooldown = 0;
    protected static final int FLAG_DROP_TIME = GameConstants.FLAG_DROPPED_RESET_ROUNDS;
    protected static int flagTimer = 0;

    public Duck(RobotController rc) {
        this.rc = rc;
    }

    public void updateEnemyRobots() throws GameActionException {
        // Sensing methods can be passed in a radius of -1 to automatically
        // use the largest possible value.
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemyRobots.length != 0) {
            // Save an array of locations with enemy robots in them for future use.
            MapLocation[] enemyLocations = new MapLocation[enemyRobots.length];
            for (int i = 0; i < enemyRobots.length; i++) {
                enemyLocations[i] = enemyRobots[i].getLocation();
            }
            // Let the rest of our team know how many enemy robots we see!
            if (rc.canWriteSharedArray(0, enemyRobots.length)) {
                rc.writeSharedArray(0, enemyRobots.length);
                int numEnemies = rc.readSharedArray(0);
            }
        }
    }

    public void play() throws GameActionException {
        pickupFlag();

        while (rc.hasFlag() && rc.getRoundNum() >= GameConstants.SETUP_ROUNDS) {
            moveToward(allySpawnZoneDirection());
        }
        // Move and attack randomly if no objective.
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        if (rc.canMove(dir)) {
            rc.move(dir);
        } else if (rc.canAttack(nextLoc)) {
            rc.attack(nextLoc);
        }

        // Rarely attempt placing traps behind the robot.
        MapLocation prevLoc = rc.getLocation().subtract(dir);
        if (rc.canBuild(TrapType.EXPLOSIVE, prevLoc) && RobotPlayer.rng.nextInt() % 37 == 1)
            rc.build(TrapType.EXPLOSIVE, prevLoc);
        // We can also move our code into different methods or classes to better organize it!
        updateEnemyRobots();
    }

    public TrapType getRandomTrapType() {
        TrapType[] trapTypes = {TrapType.EXPLOSIVE, TrapType.WATER, TrapType.STUN};
        return trapTypes[rng.nextInt(trapTypes.length)];
    }

    public boolean hasCooldown() {
        return cooldown > 0;
    }

    public void applyCooldown(int amount) {
        cooldown += amount;
    }

    public void reduceCooldown() {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    public void collectCrumbs() throws GameActionException {
        MapLocation[] crumbLocations = rc.senseNearbyCrumbs(GameConstants.VISION_RADIUS_SQUARED);
        if (crumbLocations.length > 0) {
            moveToward(crumbLocations[0]);
        } else {
            moveInRandomDirection();
        }
    }

    public void placeTrap(TrapType trapType, MapLocation location) throws GameActionException {
        if (trapType == TrapType.EXPLOSIVE && rc.canBuild(TrapType.EXPLOSIVE, location) && !hasCooldown()) {
            rc.build(TrapType.EXPLOSIVE, location);
            applyCooldown(GameConstants.FILL_COOLDOWN);
        } else if (trapType == TrapType.STUN && rc.canBuild(TrapType.STUN, location) && !hasCooldown()) {
            rc.build(TrapType.STUN, location);
            applyCooldown(GameConstants.FILL_COOLDOWN);
        } else if (trapType == TrapType.WATER && rc.canFill(location) && !hasCooldown()) {
            rc.fill(location);
            applyCooldown(GameConstants.FILL_COOLDOWN);
        } else if (trapType == TrapType.NONE && rc.canDig(location) && !hasCooldown()) {
            rc.dig(location);
            applyCooldown(GameConstants.DIG_COOLDOWN);
        }
    }

//    public void lookForFlag() throws GameActionException {
//        FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());
//        for (FlagInfo flag : flags) {
//            if (rc.canPickupFlag(flag.getLocation())) {
//                rc.pickupFlag(flag.getLocation());
//                break;
//            }
//        }
//    }

    public void lookForFlag() throws GameActionException {
        if (rc.hasFlag()) {
            // Reset flag timer if the Duck is holding a flag
            flagTimer = 0;

            // Check for nearby enemies
            RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(GameConstants.ATTACK_RADIUS_SQUARED, rc.getTeam().opponent());

            // Drop the flag as a signal if enemies are close and dropping is allowed
            if (nearbyEnemies.length > 0 && rc.canDropFlag(rc.getLocation())) {
                rc.dropFlag(rc.getLocation());
                applyCooldown(GameConstants.PICKUP_DROP_COOLDOWN);
            }
        } else {
            // Increment flag timer when the Duck is not holding a flag
            flagTimer++;

            // Attempt to pick up a flag if the timer exceeds the threshold
            if (flagTimer >= FLAG_DROP_TIME) {
                // Sense flags for the team within unlimited range
                FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());
                FlagInfo closestFlag = null;
                int closestDistance = Integer.MAX_VALUE;

                // Find the nearest flag that can be picked up
                for (FlagInfo flag : flags) {
                    int distance = rc.getLocation().distanceSquaredTo(flag.getLocation());
                    if (distance < closestDistance && rc.canPickupFlag(flag.getLocation()) && !hasCooldown()) {
                        closestFlag = flag;
                        closestDistance = distance;
                    }
                }

                // Pick up the nearest flag if available
                if (closestFlag != null) {
                    rc.pickupFlag(closestFlag.getLocation());
                    //broadcastFlagLocation(closestFlag.getLocation());
                    applyCooldown(GameConstants.PICKUP_DROP_COOLDOWN);
                }

                // Reset the flag timer after attempting to pick up a flag
                flagTimer = 0;
            }
        }
    }


    public boolean moveAwayFrom(MapLocation location) throws GameActionException {
        Direction direction = rc.getLocation().directionTo(location).opposite();
        return moveToward(direction);
    }

    public boolean moveToward(MapLocation location) throws GameActionException {
        Direction direction = rc.getLocation().directionTo(location);
        return moveToward(direction);
    }

    public boolean moveToward(Direction direction) throws GameActionException {
        boolean didMove = false;
        if (rc.canFill(rc.getLocation().add(direction))) {
            rc.fill(rc.getLocation().add(direction));
        }
        if (rc.canMove(direction)) {
            didMove = true;
            rc.move(direction);
        }
        return didMove;
    }

    private Direction[] getPrioritizedDirections(Direction primaryDirection) {
        switch (primaryDirection) {
            case NORTH:
                return new Direction[]{Direction.NORTH, Direction.NORTHWEST, Direction.NORTHEAST, Direction.WEST, Direction.EAST};
            case SOUTH:
                return new Direction[]{Direction.SOUTH, Direction.SOUTHWEST, Direction.SOUTHEAST, Direction.WEST, Direction.EAST};
            case EAST:
                return new Direction[]{Direction.EAST, Direction.NORTHEAST, Direction.SOUTHEAST, Direction.NORTH, Direction.SOUTH};
            case WEST:
                return new Direction[]{Direction.WEST, Direction.NORTHWEST, Direction.SOUTHWEST, Direction.NORTH, Direction.SOUTH};
            case NORTHEAST:
                return new Direction[]{Direction.NORTHEAST, Direction.NORTH, Direction.EAST, Direction.NORTHWEST, Direction.SOUTHEAST};
            case NORTHWEST:
                return new Direction[]{Direction.NORTHWEST, Direction.NORTH, Direction.WEST, Direction.NORTHEAST, Direction.SOUTHWEST};
            case SOUTHEAST:
                return new Direction[]{Direction.SOUTHEAST, Direction.SOUTH, Direction.EAST, Direction.SOUTHWEST, Direction.NORTHEAST};
            case SOUTHWEST:
                return new Direction[]{Direction.SOUTHWEST, Direction.SOUTH, Direction.WEST, Direction.SOUTHEAST, Direction.NORTHWEST};
            default:
                return new Direction[]{primaryDirection};
        }
    }

    public static Direction randomDirection() {
        return Direction.allDirections()[RobotPlayer.rng.nextInt() % Direction.allDirections().length];
    }

    public void moveInRandomDirection() throws GameActionException {
        boolean didMove = false;
        while (!didMove) {
            Direction otherDirection = randomDirection();
            didMove = moveToward(otherDirection);
        }
    }

    public Direction allySpawnZoneDirection() {
        return rc.getLocation().directionTo(rc.getAllySpawnLocations()[0]);
    }

    public Direction enemySpawnZoneDirection() {
        return allySpawnZoneDirection().opposite();
    }

    public void pickupFlag() throws GameActionException {
        if (rc.canPickupFlag(rc.getLocation())) {
            rc.pickupFlag(rc.getLocation());
        }
    }
}

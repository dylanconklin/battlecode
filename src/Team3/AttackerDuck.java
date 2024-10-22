package Team3;

import battlecode.common.*;

public class AttackerDuck extends Duck {
    public AttackerDuck(RobotController rc) {
        super(rc);
        skill = SkillType.ATTACK;
    }

    @Override
    public void play() throws GameActionException {
        attack();
        lookForFlag();
        move();
    }

    public void attack() throws GameActionException {
        RobotInfo[] robotInfos = rc.senseNearbyRobots();
        Team rcTeam = rc.getTeam();
        for (RobotInfo robot : robotInfos) {
            if (robot.team != rcTeam && rc.canAttack(robot.location)) {
                rc.attack(robot.location);
            }
        }
    }

    public void move() throws GameActionException {
        while (rc.getRoundNum() >= GameConstants.SETUP_ROUNDS && (rc.hasFlag() || rc.getHealAmount() <= 300)) {
            // move toward ally spawn locations
            // TODO: don't move blindly toward locations[0]
            moveToward(allySpawnZoneDirection());
        }
        if (rc.getRoundNum() >= GameConstants.SETUP_ROUNDS) {
            // move toward adversary spawn locations
            // TODO: don't move blindly away from locations[0]
            moveToward(enemySpawnZoneDirection());
        }
    }
}





















/*package Team3;

import battlecode.common.*;
import java.util.*;

public class AttackerDuck extends Duck {

    static Direction direction;

    public AttackerDuck(RobotController rc) {
        super(rc);
        skill = SkillType.ATTACK;
    }

    @Override 
    public void play() throws GameActionException {
        super.play();
        // Attacker duck specifics (attack and movement logic)
        if (rc.getRoundNum() >= GameConstants.SETUP_ROUNDS) {
            attack();  // Engage enemies in range
            move();    // Navigate toward objectives
        }
    }

    // Attack nearby enemies
    public void attack() throws GameActionException {
        // Sense nearby enemies
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (nearbyEnemies.length > 0) {
            // Target enemyy in range
            MapLocation enemyLocation = nearbyEnemies[0].getLocation();
            if (rc.canAttack(enemyLocation)) {
                rc.attack(enemyLocation);  // Attack if within range
            }
        }
    }

    public void move() throws GameActionException {
        MapLocation[] locations = rc.getAllySpawnLocations();
        if (rc.hasFlag()) {
            // move toward ally spawn locations
            // TODO: don't move blindly toward locations[0]
            moveToward(locations[0]);
        } else {
            // move toward adversary spawn locations
            // TODO: don't move blindly away from locations[0]
            moveAwayFrom(locations[0]);
            // If not holding a flag, move toward enemy objectives or flags
            //MapLocation enemyFlagLocation = getEnemyFlagLocation();
            //moveToward(enemyFlagLocation);
        }
    }

    // Move toward location
    public static void moveToward(RobotController rc, MapLocation location) throws GameActionException {
        Direction direction = rc.getLocation().directionTo(location);
        if (rc.canMove(direction)) {
            rc.move(direction);
        } 
        else if (rc.canFill(rc.getLocation().add(direction))){
            rc.fill(rc.getLocation().add(direction));
        }

        else {
            Direction otherDirection = Direction.allDirections()[RobotPlayer.rng.nextInt(8)];
            if (rc.canMove(otherDirection)) {
                rc.move(otherDirection);
            }
            //for (Direction otherDirection : Direction.allDirections()) {
                //if (rc.canMove(otherDirection)) {
                   // rc.move(otherDirection);
               // }
            //}
        }
    }

    // Move a way from location
    public void moveAwayFrom(MapLocation location) throws GameActionException {
        Direction oppositeDirection = rc.getLocation().directionTo(location).opposite();
    
        if (rc.canMove(oppositeDirection)) {
            rc.move(oppositeDirection);
        } else {
            // Try all directions in case the opposite direction is blocked
            for (Direction otherDirection : Direction.allDirections()) {
                if (rc.canMove(otherDirection)) {
                    rc.move(otherDirection);
                    return;
                }
            }
        }
    }

    private static final int EXPLORE_ROUNDS = 150;

    public static void runSetup(RobotController rc) throws GameActionException {
        if(rc.getRoundNum() < EXPLORE_ROUNDS) {
            // pickup flag if possible
            FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());
            for(FlagInfo flag : flags){
                if(rc.canPickupFlag(flag.getLocation())){
                    rc.pickupFlag(flag.getLocation());
                    break;
                }
            }
            // explore randomly
            explore(rc);
        }
        else{
            // place flag if it far a way from other flag
            if(rc.senseLegalStartingFlagPlacement(rc.getLocation())){
                if(rc.canDropFlag(rc.getLocation())) rc.dropFlag(rc.getLocation());
            }
            // seach for nearby placed flag
            FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());
            FlagInfo target = null;
            for(FlagInfo flag : flags){
                if(flag.isPickedUp()){
                    target = flag;
                    break;
                }
            }

            // if there is a placed flag nearby, move and build traps
            if(target != null){

            }
        }
    }

    public static void explore(RobotController rc) throws GameActionException {
        // try to move towards crums or move in current direction
        // if can't continue in current direction, pick new random direction
        MapLocation[] crumLocs = rc.senseNearbyCrumbs(-1);
        if(crumLocs.length > 0){
            Direction crumbDirection = rc.getLocation().directionTo(crumLocs[0]);
            if(rc.canMove(crumbDirection)){
                rc.move(crumbDirection);
            }
        }

        if (rc.isMovementReady()){
            if(direction != null && rc.canMove(direction)) rc.move(direction);
                else{
                    direction = Direction.allDirections()[RobotPlayer.rng.nextInt(8)]; 
                }
        }
    }
}
*/
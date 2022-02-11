package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

import java.security.SecureRandom;

public class Bot {

    private static final int maxSpeed = 9;
    private List<Command> directionList = new ArrayList<>();
    private int countTweet = 0;

    private final Random random;

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    public Bot() {
        this.random = new SecureRandom();
        directionList.add(TURN_LEFT);
        directionList.add(TURN_RIGHT);
    }

    public Command run(GameState gameState) {
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;

        // Basic fix logic
        List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block, gameState);
        List<Object> nextBlocks = blocks.subList(0, 1);

        // Fix first if too damaged to move
        if (myCar.damage == 5) {
            return FIX;
        }
        // Accelerate first if going to slow
        if (myCar.speed <= 3) {
            return ACCELERATE;
        }

        // Basic fix logic
        if (myCar.damage >= 5) {
            return FIX;
        }

        // Basic avoidance logic
        if (blocks.contains(Terrain.MUD) || nextBlocks.contains(Terrain.WALL)) {
            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                return LIZARD;
            }
            if (nextBlocks.contains(Terrain.MUD) || nextBlocks.contains(Terrain.WALL)) {
                int i = random.nextInt(directionList.size());
                return directionList.get(i);
            }
        }

         // PUNYA LIZA: NGINDARIN OBSTACLE
        

        // PUNYA ADELLL : POWERUPSS
        // EMP
        if (hasPowerUp(PowerUps.EMP, myCar.powerups)) {
            if (opponent.speed > 5) {
                if (opponent.position.block > myCar.position.block) {
                    int yOp = opponent.position.lane;
                    int yMy = myCar.position.lane;
                    if (yMy == yOp || yMy == yOp + 1 || yMy == yOp - 1) {
                        return EMP;
                    }
                }
            }
        }

        int countTweet = 0;
        if (hasPowerUp(PowerUps.TWEET, myCar.powerups)) {
            if (opponent.speed > 5) {
                if (countTweet == 0) {
                    countTweet++;
                    return new TweetCommand(4, 76);
                } else {
                    int yOp = opponent.position.lane;
                    int xOp = opponent.position.block;
                    int speedOp = opponent.speed;
                    return new TweetCommand(yOp, xOp + speedOp + 1);
                }
            }
        }

        // BOOST
        if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            if (myCar.damage == 0) {
                if (!(blocks.contains(Terrain.MUD) || blocks.contains(Terrain.WALL)
                        || blocks.contains(Terrain.OIL_SPILL))) {
                    return BOOST;
                } else {
                    if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                        return BOOST;
                    } else {
                        if (myCar.position.lane == 2 || myCar.position.lane == 3) {
                            List<Object> leftBlocks = getBlocksInFront(myCar.position.lane - 1, myCar.position.block,
                                    gameState);
                            List<Object> rightBlocks = getBlocksInFront(myCar.position.lane + 1, myCar.position.block,
                                    gameState);
                            if ((!(leftBlocks.contains(Terrain.MUD) || leftBlocks.contains(Terrain.WALL)
                                    || leftBlocks.contains(Terrain.OIL_SPILL)))
                                    || (!(rightBlocks.contains(Terrain.MUD) || rightBlocks.contains(Terrain.WALL)
                                            || rightBlocks.contains(Terrain.OIL_SPILL)))) {
                                return BOOST;
                            }
                        } else if (myCar.position.lane == 1) {
                            List<Object> secondBlocks = getBlocksInFront(2, myCar.position.block, gameState);
                            if (!(secondBlocks.contains(Terrain.MUD) || secondBlocks.contains(Terrain.WALL)
                                    || secondBlocks.contains(Terrain.OIL_SPILL))) {
                                return BOOST;
                            }
                        } else if (myCar.position.lane == 4) {
                            List<Object> thirdBlocks = getBlocksInFront(3, myCar.position.block, gameState);
                            if (!(thirdBlocks.contains(Terrain.MUD) || thirdBlocks.contains(Terrain.WALL)
                                    || thirdBlocks.contains(Terrain.OIL_SPILL))) {
                                return BOOST;
                            }
                        }
                    }
                }
            }
        }

        if (hasPowerUp(PowerUps.OIL, myCar.powerups) && (myCar.position.block > opponent.position.block)) {
            return OIL;
        }

        
        // PUNYA LIZA: KIRI KANAN YEY
        // kalau di belakang lawan usahain ke tengah (harus empty)
        if (opponent.position.block > myCar.position.block) {
            System.out.println("TES BELOK 1");
            if (myCar.position.lane == 1) {
                List<Object> secondBlocks = getBlocksInFront(2, myCar.position.block, gameState);
                if (!(secondBlocks.contains(Terrain.MUD) || secondBlocks.contains(Terrain.WALL)
                        || secondBlocks.contains(Terrain.OIL_SPILL))) {
                    return TURN_RIGHT;
                }
            } else if (myCar.position.lane == 4) {
                List<Object> thirdBlocks = getBlocksInFront(3, myCar.position.block, gameState);
                if (!(thirdBlocks.contains(Terrain.MUD) || thirdBlocks.contains(Terrain.WALL)
                        || thirdBlocks.contains(Terrain.OIL_SPILL))) {
                    return TURN_LEFT;
                }
            }
        }
        // kalau di depan lawan usahain ke pinggir (harus empty)
        if (opponent.position.block < myCar.position.block) {
            System.out.println("TES BELOK 2");
            if (myCar.position.lane == 2) {
                List<Object> firstBlocks = getBlocksInFront(1, myCar.position.block, gameState);
                if (!(firstBlocks.contains(Terrain.MUD) || firstBlocks.contains(Terrain.WALL)
                        || firstBlocks.contains(Terrain.OIL_SPILL))) {
                    return TURN_LEFT;
                }
            } else if (myCar.position.lane == 3) {
                List<Object> fourthBlocks = getBlocksInFront(4, myCar.position.block, gameState);
                if (!(fourthBlocks.contains(Terrain.MUD) || fourthBlocks.contains(Terrain.WALL)
                        || fourthBlocks.contains(Terrain.OIL_SPILL))) {
                    return TURN_RIGHT;
                }
            }
        }
        


        System.out.println("HADEHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");

        
        return ACCELERATE;
    }

    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp : available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns
     * the amount of blocks that can be traversed at max speed.
     **/
    private List<Object> getBlocksInFront(int lane, int block, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + Bot.maxSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

}

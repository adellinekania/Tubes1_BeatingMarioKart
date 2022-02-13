package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

import java.security.SecureRandom;

public class Bot {

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

    // Command placeholder only
    private final static Command NOTHING = new DoNothingCommand();

    public Bot() {
        this.random = new SecureRandom();
        directionList.add(TURN_LEFT);
        directionList.add(TURN_RIGHT);
    }

    public Command run(GameState gameState) {
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;

        // Basic fix logic
        List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block, gameState, myCar.speed);
        List<Object> nextBlocks = blocks.subList(0, 1);

        // TES ADA CYBERTRUCK ATAU ENGGA
        System.out.println("ADA CYBERTRUCK?: " + checkCyberTruck(myCar.position.lane, myCar.position.block, gameState, myCar.speed));

        // PUNYA AFAN: #1 FIX
        if (myCar.damage >= 2) {
            return FIX;
        }

        // PUNYA AFAN: #2 ACCELERATE
        if (myCar.speed <= 3) {
            return ACCELERATE;
        }

        // PUNYA LIZA: #3 NGINDARIN OBSTACLE
        if (!blocksAreEmpty(blocks)) {
            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                System.out.println("TES NGEHINDAR make liza(rd)");
                return LIZARD;
            } else {
                if (myCar.position.lane == 1) {
                    List<Object> secondBlocks = getBlocksInFront(2, myCar.position.block, gameState, myCar.speed - 1);
                    if (blocksAreEmpty(secondBlocks)) {
                        System.out.println("TES NGEHINDAR ke 2");
                        return TURN_RIGHT;
                    }
                } else if (myCar.position.lane == 4) {
                    List<Object> thirdBlocks = getBlocksInFront(3, myCar.position.block, gameState, myCar.speed - 1);
                    if (blocksAreEmpty(thirdBlocks)) {
                        System.out.println("TES NGEHINDAR ke 3");
                        return TURN_LEFT;
                    }
                } else if (myCar.position.lane == 2) {
                    List<Object> leftBlocks = getBlocksInFront(1, myCar.position.block, gameState, myCar.speed - 1);
                    List<Object> rightBlocks = getBlocksInFront(3, myCar.position.block, gameState, myCar.speed - 1);
                    if (blocksAreEmpty(leftBlocks)) {
                        System.out.println("TES NGEHINDAR ke kiri");
                        return TURN_LEFT;
                    } else if (blocksAreEmpty(rightBlocks)) {
                        System.out.println("TES NGEHINDAR ke kanan");
                        return TURN_RIGHT;
                    }
                } else if (myCar.position.lane == 3) {
                    List<Object> leftBlocks = getBlocksInFront(2, myCar.position.block, gameState, myCar.speed - 1);
                    List<Object> rightBlocks = getBlocksInFront(4, myCar.position.block, gameState, myCar.speed - 1);
                    if (blocksAreEmpty(rightBlocks)) {
                        System.out.println("TES NGEHINDAR ke kanan");
                        return TURN_RIGHT;
                    } else if (blocksAreEmpty(leftBlocks)) {
                        System.out.println("TES NGEHINDAR ke kiri");
                        return TURN_LEFT;
                    }
                }
            }
        }

        // PUNYA AFAN: #4 Ngambil Powerups
        if (countPowerUp(PowerUps.LIZARD, myCar.powerups) < 5) {
            Command powaction = TakePowerUp(Terrain.LIZARD, PowerUps.LIZARD, blocks, myCar.position.lane,
                    myCar.position.block, gameState, myCar.speed);
            if (powaction != NOTHING) {
                return powaction;
            }
        } else if (countPowerUp(PowerUps.EMP, myCar.powerups) < 3) {
            Command powaction = TakePowerUp(Terrain.EMP, PowerUps.EMP, blocks, myCar.position.lane,
                    myCar.position.block, gameState, myCar.speed);
            if (powaction != NOTHING) {
                return powaction;
            }
        } else if (countPowerUp(PowerUps.TWEET, myCar.powerups) < 3) {
            Command powaction = TakePowerUp(Terrain.TWEET, PowerUps.TWEET, blocks, myCar.position.lane,
                    myCar.position.block, gameState, myCar.speed);
            if (powaction != NOTHING) {
                return powaction;
            }
        } else if (countPowerUp(PowerUps.BOOST, myCar.powerups) < 3) {
            Command powaction = TakePowerUp(Terrain.BOOST, PowerUps.BOOST, blocks, myCar.position.lane,
                    myCar.position.block, gameState, myCar.speed);
            if (powaction != NOTHING) {
                return powaction;
            }
        } else if (countPowerUp(PowerUps.OIL, myCar.powerups) < 3) {
            Command powaction = TakePowerUp(Terrain.OIL_POWER, PowerUps.OIL, blocks, myCar.position.lane,
                    myCar.position.block, gameState, myCar.speed);
            if (powaction != NOTHING) {
                return powaction;
            }
        }

        // PUNYA AFAN: #5 ACCELERATE
        if (myCar.speed <= 6) {
            return ACCELERATE;
        }

        // PUNYA ADELLL : POWERUPSS
        // TWEET
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

        // BOOST
        if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            if (myCar.damage == 0) {
                if (blocksAreEmpty(getBlocksInFront(myCar.position.lane, myCar.position.block,
                        gameState, 15))) {
                    return BOOST;
                }
            }
        }

        if (hasPowerUp(PowerUps.OIL, myCar.powerups) && (myCar.position.block > opponent.position.block)) {
            return OIL;
        }

        // PUNYA LIZA: KIRI KANAN YEY
        // kalau di belakang lawan usahain supaya lawan masuk ke range EMP (harus empty)
        if (opponent.position.block > myCar.position.block) {
            if (myCar.position.lane == 1) {
                List<Object> secondBlocks = getBlocksInFront(2, myCar.position.block, gameState, myCar.speed - 1);
                if (blocksAreEmpty(secondBlocks)) {
                    System.out.println("TES BELOK ke 2");
                    return TURN_RIGHT;
                }
            } else if (myCar.position.lane == 2 && opponent.position.lane == 4) {
                List<Object> thirdBlocks = getBlocksInFront(3, myCar.position.block, gameState, myCar.speed - 1);
                if (blocksAreEmpty(thirdBlocks)) {
                    System.out.println("TES BELOK ke 3");
                    return TURN_RIGHT;
                }
            } else if (myCar.position.lane == 3 && opponent.position.lane == 1) {
                List<Object> secondBlocks = getBlocksInFront(2, myCar.position.block, gameState, myCar.speed - 1);
                if (blocksAreEmpty(secondBlocks)) {
                    System.out.println("TES BELOK ke 2");
                    return TURN_LEFT;
                }
            } else if (myCar.position.lane == 4) {
                List<Object> thirdBlocks = getBlocksInFront(3, myCar.position.block, gameState, myCar.speed - 1);
                if (blocksAreEmpty(thirdBlocks)) {
                    System.out.println("TES BELOK ke 3");
                    return TURN_LEFT;
                }
            }
        }
        // kalau di depan lawan usahain kepinggir (harus empty)
        // bisa dipaksain lagi buat mastiin keluar dari range EMP lawan tapi ada
        // minusnya juga mungkin didiskusiin nanti :(
        if (opponent.position.block < myCar.position.block) {
            if (myCar.position.lane == 2 && opponent.position.lane != 4) {
                List<Object> firstBlocks = getBlocksInFront(1, myCar.position.block, gameState, myCar.speed - 1);
                if (blocksAreEmpty(firstBlocks)) {
                    System.out.println("TES BELOK ke 1");
                    return TURN_LEFT;
                }
            } else if (myCar.position.lane == 3 && opponent.position.lane != 1) {
                List<Object> fourthBlocks = getBlocksInFront(4, myCar.position.block, gameState, myCar.speed - 1);
                if (blocksAreEmpty(fourthBlocks)) {
                    System.out.println("TES BELOK ke 4");
                    return TURN_RIGHT;
                }
            }
        }

        System.out.println("HADEHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");

        return ACCELERATE;
    }

    // General Function to return action for taking power up
    private Command TakePowerUp(Terrain terrain, PowerUps powerup, List<Object> blocks, int lane, int block,
            GameState gameState, int speed) {
        if (blocksAreEmpty(blocks) && blocks.contains(terrain)) {
            System.out.println("POWERUP_FORWARD_" + powerup);
            return ACCELERATE;
        } else if (lane > 1) {
            List<Object> leftBlocks = getBlocksInFront(lane - 1, block, gameState, speed - 1);
            if (blocksAreEmpty(leftBlocks) && leftBlocks.contains(terrain)) {
                System.out.println("POWERUP_LEFT_" + powerup);
                return TURN_LEFT;
            } else {
                return NOTHING;
            }
        } else if (lane < 4) {
            List<Object> rightBlocks = getBlocksInFront(lane + 1, block, gameState, speed - 1);
            if (blocksAreEmpty(rightBlocks) && rightBlocks.contains(terrain)) {
                System.out.println("POWERUP_RIGHT_" + powerup);
                return TURN_RIGHT;
            } else {
                return NOTHING;
            }
        } else {
            return NOTHING;
        }
    }

    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp : available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    // Count how many powerup car has
    private int countPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        int counter = 0;
        for (PowerUps powerUp : available) {
            if (powerUp.equals(powerUpToCheck)) {
                counter++;
            }
        }
        return counter;
    }

    private Boolean blocksAreEmpty(List<Object> blocks) {
        return !(blocks.contains(Terrain.MUD) || blocks.contains(Terrain.WALL) || blocks.contains(Terrain.OIL_SPILL));
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns
     * the amount of blocks that can be traversed at max speed.
     **/
    private List<Object> getBlocksInFront(int lane, int block, GameState gameState, int maxLane) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + maxLane; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

    private boolean checkCyberTruck(int lane, int block, GameState gameState, int maxLane) {
        List<Lane[]> map = gameState.lanes;
        int startBlock = map.get(0)[0].position.block;
        boolean check = false;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + maxLane; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            if (laneList[i].cyberTruck) {
                check = true;
            }

        }
        return check;
    }

}

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
    private final Random random;

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    private final static Command NOTHING = new DoNothingCommand();

    public Bot() {
        this.random = new SecureRandom();
        directionList.add(TURN_LEFT);
        directionList.add(TURN_RIGHT);
    }

    public Command run(GameState gameState) {
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;

        // 1. Perbaiki mobil jika damage lebih dari sama dengan 2
        if (myCar.damage >= 2) {
            return FIX;
        }

        // 2. Accelerate jika kecepatan kurang dari sama dengan 3
        if (myCar.speed <= 3) {
            return ACCELERATE;
        }

        // 3. Menghindari Obstacle
        if (!blocksAreEmpty(myCar.position.lane, myCar.position.block, gameState, myCar.speed)) {
            // Gunakan lizard jika ada
            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                return LIZARD;
            } else {
                // Belok ke lane lain yang mungkin jika lane tersebut empty
                if (myCar.position.lane == 1) {
                    if (blocksAreEmpty(2, myCar.position.block, gameState, myCar.speed - 1)) {
                        return TURN_RIGHT;
                    }
                } else if (myCar.position.lane == 4) {
                    if (blocksAreEmpty(3, myCar.position.block, gameState, myCar.speed - 1)) {
                        return TURN_LEFT;
                    }
                } else if (myCar.position.lane == 2) {
                    if (blocksAreEmpty(1, myCar.position.block, gameState, myCar.speed - 1)) {
                        return TURN_LEFT;
                    } else if (blocksAreEmpty(3, myCar.position.block, gameState, myCar.speed - 1)) {
                        return TURN_RIGHT;
                    }
                } else if (myCar.position.lane == 3) {
                    if (blocksAreEmpty(4, myCar.position.block, gameState, myCar.speed - 1)) {
                        return TURN_RIGHT;
                    } else if (blocksAreEmpty(2, myCar.position.block, gameState, myCar.speed - 1)) {
                        return TURN_LEFT;
                    }
                }
            }
        }

        // 4. Mengambil powerups
        if (countPowerUp(PowerUps.LIZARD, myCar.powerups) < 5) {
            Command powaction = TakePowerUp(Terrain.LIZARD, PowerUps.LIZARD, myCar.position.lane,
                    myCar.position.block, gameState, myCar.speed);
            if (powaction != NOTHING) {
                return powaction;
            }
        } else if (countPowerUp(PowerUps.TWEET, myCar.powerups) < 3) {
            Command powaction = TakePowerUp(Terrain.TWEET, PowerUps.TWEET, myCar.position.lane,
                    myCar.position.block, gameState, myCar.speed);
            if (powaction != NOTHING) {
                return powaction;
            }
        } else if (countPowerUp(PowerUps.EMP, myCar.powerups) < 3) {
            Command powaction = TakePowerUp(Terrain.EMP, PowerUps.EMP, myCar.position.lane,
                    myCar.position.block, gameState, myCar.speed);
            if (powaction != NOTHING) {
                return powaction;
            }
        } else if (countPowerUp(PowerUps.BOOST, myCar.powerups) < 3) {
            Command powaction = TakePowerUp(Terrain.BOOST, PowerUps.BOOST, myCar.position.lane,
                    myCar.position.block, gameState, myCar.speed);
            if (powaction != NOTHING) {
                return powaction;
            }
        } else if (countPowerUp(PowerUps.OIL, myCar.powerups) < 3) {
            Command powaction = TakePowerUp(Terrain.OIL_POWER, PowerUps.OIL, myCar.position.lane,
                    myCar.position.block, gameState, myCar.speed);
            if (powaction != NOTHING) {
                return powaction;
            }
        }

        // 5. Accelerate jika kecepatan kurang dari sama dengan 6
        if (myCar.speed <= 6) {
            return ACCELERATE;
        }

        // 6. Gunakan porwerups
        // Menjatuhkan cybertruck di depan lawan
        if (hasPowerUp(PowerUps.TWEET, myCar.powerups)) {
            if (opponent.speed > 5) {
                int yOp = opponent.position.lane;
                int xOp = opponent.position.block;
                int speedOp = opponent.speed;
                return new TweetCommand(yOp, xOp + speedOp + 1);
            }
        }

        // Menmbakkan EMP jika berada di belakang lawan dan lawan berada dalam jangkauan
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

        // Menggunakan boost jika damage 0 agar speed maksimal
        if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            if (myCar.damage == 0) {
                if (blocksAreEmpty(myCar.position.lane, myCar.position.block, gameState, 15)) {
                    return BOOST;
                }
            }
        }

        // Menggunakan OIL jika berada di depan lawan
        if (hasPowerUp(PowerUps.OIL, myCar.powerups) && (myCar.position.block > opponent.position.block)) {
            return OIL;
        }

        // 7.
        // Jika berada di belakang lawan, belok jika perlu agar lawan masuk ke range EMP
        if (opponent.position.block > myCar.position.block) {
            if (myCar.position.lane == 1) {
                if (blocksAreEmpty(2, myCar.position.block, gameState, myCar.speed - 1)) {
                    return TURN_RIGHT;
                }
            } else if (myCar.position.lane == 2 && opponent.position.lane == 4) {
                if (blocksAreEmpty(3, myCar.position.block, gameState, myCar.speed - 1)) {
                    return TURN_RIGHT;
                }
            } else if (myCar.position.lane == 3 && opponent.position.lane == 1) {
                if (blocksAreEmpty(2, myCar.position.block, gameState, myCar.speed - 1)) {
                    return TURN_LEFT;
                }
            } else if (myCar.position.lane == 4) {
                if (blocksAreEmpty(3, myCar.position.block, gameState, myCar.speed - 1)) {
                    return TURN_LEFT;
                }
            }
        }
        // Jika berada di depan lawan, belok ke tepi untuk menghindari range EMP lawan
        if (opponent.position.block < myCar.position.block) {
            if (myCar.position.lane == 2 && opponent.position.lane != 4) {
                if (blocksAreEmpty(1, myCar.position.block, gameState, myCar.speed - 1)) {
                    return TURN_LEFT;
                }
            } else if (myCar.position.lane == 3 && opponent.position.lane != 1) {
                if (blocksAreEmpty(4, myCar.position.block, gameState, myCar.speed - 1)) {
                    return TURN_RIGHT;
                }
            }
        }

        return ACCELERATE;
    }

    // General Function to return action for taking power up
    private Command TakePowerUp(Terrain terrain, PowerUps powerup, int lane, int block,
            GameState gameState, int speed) {
        List<Object> blocks = getBlocksInFront(lane, block, gameState, speed);
        if (blocksAreEmpty(lane, block, gameState, speed) && blocks.contains(terrain)) {
            return ACCELERATE;
        } else if (lane > 1) {
            List<Object> leftBlocks = getBlocksInFront(lane - 1, block, gameState, speed - 1);
            if (blocksAreEmpty(lane - 1, block, gameState, speed - 1) && leftBlocks.contains(terrain)) {
                return TURN_LEFT;
            } else {
                return NOTHING;
            }
        } else if (lane < 4) {
            List<Object> rightBlocks = getBlocksInFront(lane + 1, block, gameState, speed - 1);
            if (blocksAreEmpty(lane + 1, block, gameState, speed - 1) && rightBlocks.contains(terrain)) {
                return TURN_RIGHT;
            } else {
                return NOTHING;
            }
        } else {
            return NOTHING;
        }
    }

    // Check if we have the powerUpToCheck
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

    // Check if the next blocks are empty
    private Boolean blocksAreEmpty(int lane, int block, GameState gameState, int forwardSpeed) {
        List<Object> blocks = getBlocksInFront(lane, block, gameState, forwardSpeed);
        return !(blocks.contains(Terrain.MUD) || blocks.contains(Terrain.WALL) || blocks.contains(Terrain.OIL_SPILL) ||
                checkCyberTruck(lane, block, gameState, forwardSpeed));
    }

    // Returns map of blocks and the objects in the for the current lanes, returns
    // the amount of blocks that can be traversed at max speed.
    private List<Object> getBlocksInFront(int lane, int block, GameState gameState, int forwardSpeed) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + forwardSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

    // Return true if there is cyberTruck in the next blocks
    private boolean checkCyberTruck(int lane, int block, GameState gameState, int forwardSpeed) {
        List<Lane[]> map = gameState.lanes;
        int startBlock = map.get(0)[0].position.block;
        boolean check = false;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + forwardSpeed; i++) {
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

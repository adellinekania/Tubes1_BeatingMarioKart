# "Overdrive" Bot Using Greedy Algorithm

## Table of Contents
- [General Information](#general-information)
- [Greedy Strategies](https://maven.apache.org/)
- [Requirements](#requirement)
- [Depedencies](#depedencies)
- [How To Build](#how-to-build)
- [How To Run](#how-to-run)
- [Authors](#authors)

## General Information
Overdrive is a racing game which compete 2 car bots. To win the match, each player must apply a certain strategy to be able to beat their opponent. This program builds a bot using the Greedy Algorithm, a strategy to optimize the condition of the car in each round that expect the car will win at the end.

## Greedy Strategies
The following is the strategy that we used after considering the actions that need to be prioritized based on the conditions that the car may face in a round.
1. Fix the car if damage ≥ 2
2. Accelerate the car if speed ≤ 3
3. Avoid obstacle if possible (using LIZARD or turn to the other empty lane)
4. Take powerups in the next possible blocks, if we don't have enough (minimum 5 for LIZARD and minimum 3 for others)
5. Accelerate the car if speed ≤ 6
6. Use the powerups,
    - TWEET, drop cybertuck on the next blocks of opponent to cause more damage
    - EMP, fire an emp wave if we are behind the opponent
    - BOOST, dramatically speed up the car if the damage = 0
    - OIL, create oil spills if we are ahead
7. If we are behind the opponent, turn to the 2nd or 3rd lane  so that the opponent will be in our EMP range, <br/>
or <br />
If we are ahead, turn to the 1st or 4th lane, to avoid opponent's EMP range
8. Accelerate if none of the above conditions are satisfy


## Requirement
- [Java 8 (minimum)](https://www.oracle.com/java/technologies/downloads/#java8)
- [IntelliJ  IDEA](https://www.jetbrains.com/idea/)
- [NodeJs](https://nodejs.org/en/download/)
- [Visual Studio Code (optional if you don't want to use IntelliJ)](https://code.visualstudio.com/)
- [Maven](https://maven.apache.org/)

To set up Maven in VS Code, you can follow [this instuction](https://code.visualstudio.com/docs/java/java-build).

## Dependencies
To implements this bot, first you need to download and extract the latest release [starter-pack.zip](https://github.com/EntelectChallenge/2020-Overdrive/releases/tag/2020.3.4) as the game engine.

## How To Build
- Clone this repository
    ```
    git clone https://github.com/adellinekania/Tubes1_BeatingMarioKart.git
    ```
Using Visual Studio Code:
- Copy the ```src``` folder in this repository to replace the ```src``` folder in ```starter-pack > starter-bots > java```
- You can rename the player for this bot in ```bot.json```, for example
    ```
    "nickName": "Beating MARIOO KARTTTTT",
    ```
- Edit the used bot in ```game-runner-config.json```
    ```
    "player-a": "./starter-bots/java"
    ```
- To compile, go to the maven project, ```java-starter-bot > Lifecycle``` then click the play button on ```install```

Using IntelliJ:



## How To Run
After you re-compile this bot within the program in starter-pack,
- If you are using Windows, simply double click the ```run.bat```
- If you are using Linux/Mac, run
    ```
    make run
    ```
    in the right directory

## Authors

<b>Beating Mario Kart</b>
| NIM       | Name                     |
| --------- | ------------------------ |
| 13520023  | Ahmad Alfani Handoyo     |
| 13520066  | Putri Nurhaliza          |
| 13520084  | Adelline Kania Setiyawan |

TUGAS BESAR I - Algorithm Strategies

Bandung Institute of Technology
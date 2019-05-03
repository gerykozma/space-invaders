# Space Invaders

<!-- TOC depthFrom:2 -->

- [1. General](#1-general)
- [2. Gameplay](#2-gameplay)
- [3. Controls](#3-controls)
- [4. Logs, scores, saves](#4-logs-scores-saves)
- [5. About the author](#5-about-the-author)

<!-- /TOC -->

## 1. General

This project is simple a re-make of the classic Space Invaders game from the arcade era.

> Note: this project is used for academic purpose only.

## 2. Gameplay

Manouver through the rain of enemy torpedoes and destroy the invading alien ships. The goal here is to destroy all waves of alien ships and secure the survival of Humanity.

## 3. Controls

- To controll your space ship use the `RIGHT` and `LEFT` arrows on the keyboard.
- To shoot a torpedo use the `SPACEBAR`.
- To pause the game hit `P` on the keyboard. To resume the game (only if the player is still alive) hit `P` again.
- To start a new game hit `R`.
- To save an on-going game hit `S` on the keyboard. To load a saved game stance hit `L` on the keyboard. [Read more](##4.-Logs,-scores,-saves)
- Hit `ESC` to close the application.

## 4. Logs, scores, saves

- Logs are rolled after a certain file size (1MB). The log files can be found near the executable (SpaceInvaders.log).
- Player scores are saved after each play-through. If a score is a new highscore the game will inform you via pop-up.
Scores are stored near the executable (PlayerScore.score).
- The player can save an on-going game by hitting the 'S' key on the keyboard and then choosing a path for the SavedGame.save file to be created. Similarily a game stance can be loaded by hitting the 'L' key then browsing the  SavedGame.save file.

## 5. About the author

- My name is Kozma Gergő Márk, I am a student at the [University of Debrecen, Faculty of Informatics](https://www.edu.unideb.hu/page.php?id=131). Also, I work at [National Instruments Hungary](http://hungary.ni.com/debrecen) as a C# developer. This assignment is my first major java implementation and I consider this a learning project. I had much fun making this, enjoy. :)
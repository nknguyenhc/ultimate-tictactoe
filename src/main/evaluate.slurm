#!/bin/bash
#SBATCH --output=result.google.gemma-2-2b-it.out
#SBATCH --error=result.google.gemma-2-2b-it.out
#SBATCH --time=02:00:00
#SBATCH --mem=32GB
#SBATCH --job-name=eval-google

java -jar ultimate-tictactoe.jar google.gemma-2-2b-it

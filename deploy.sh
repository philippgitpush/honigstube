gradle build

scp ./build/libs/honigstube.jar serverchen:/home/honigstube/data/plugins/honigstube.jar
ssh serverchen "docker exec -i honigstube-mc-1 rcon-cli plugman reload Honigstube && docker exec -i honigstube-mc-1 rcon-cli say 'Neue Plugin-Version geladen! ^-^'"

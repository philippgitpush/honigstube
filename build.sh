gradle build

cp ".\build\libs\honigstube.jar" ".\server\plugins\honigstube.jar"

docker exec -i spigot-dev-server-mc-1 rcon-cli plugman reload Honigstube
docker exec -i spigot-dev-server-mc-1 rcon-cli say Neue Plugin-Version geladen! ^-^

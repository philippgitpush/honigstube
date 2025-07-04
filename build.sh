gradle build
cp "e:\Development\honigstube\build\libs\honigstube-1.1.0.jar" "C:\Users\Philipp\Desktop\spigot-dev-server\data\plugins\honigstube-1.1.0.jar"
docker exec -i spigot-dev-server-mc-1 rcon-cli plugman reload all
docker exec -i spigot-dev-server-mc-1 rcon-cli say Plugins reloaded

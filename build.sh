sh & "C:\Libs\maven\bin\mvn.cmd" clean -f "e:\Development\honigstube\pom.xml"
sh & "C:\Libs\maven\bin\mvn.cmd" package -f "e:\Development\honigstube\pom.xml"
mv "e:\Development\honigstube\target\honigstube-1.0.0.jar" "C:\Users\Philipp\Desktop\spigot-dev-server\data\plugins\honigstube-1.0.0.jar"
docker exec -i spigot-dev-server-mc-1 rcon-cli reload confirm
docker exec -i spigot-dev-server-mc-1 rcon-cli say Reloaded Plugin

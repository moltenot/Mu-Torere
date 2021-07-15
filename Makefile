compile:
	javac MuTorere/*.java

run: compile
	java MuTorere/MuTorere NaivePlayer CustomPlayer

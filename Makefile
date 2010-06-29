all:
	javac ./src/*.java -d ./bin/ -deprecation
	
clean:
	rm -rf ./bin/*

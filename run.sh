COMPONENT=$(pwd)
mkdir bin 2> /dev/null
javac -d $COMPONENT/bin $COMPONENT/src/*.java
java -cp $COMPONENT/bin GeneticAlgorithm


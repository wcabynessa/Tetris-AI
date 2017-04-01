COMPONENT=$(pwd)
javac -d $COMPONENT/bin $COMPONENT/src/*.java
java -cp $COMPONENT/bin GeneticAlgorithm


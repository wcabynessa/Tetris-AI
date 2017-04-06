COMPONENT=$(pwd)
javac -d $COMPONENT/bin $COMPONENT/src/*.java
nohup java -cp $COMPONENT/bin GeneticAlgorithm  &

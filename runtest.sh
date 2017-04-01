COMPONENT=$(pwd)
javac -d $COMPONENT/bin $COMPONENT/src/*.java
java -cp $COMPONENT/bin TestWeights -0.510066 0.760666 -0.35663 -0.184483


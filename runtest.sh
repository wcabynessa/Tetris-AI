COMPONENT=$(pwd)
javac -d $COMPONENT/bin $COMPONENT/src/*.java
java -cp $COMPONENT/bin TestWeights -0.8367245813131511 0.7389812926550483 0.0018587911616203268 -0.12002884694473898 -0.13254433966738172


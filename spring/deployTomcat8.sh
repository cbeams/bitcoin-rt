
if [ -z "$TOMCAT8_HOME" ]; then
    echo -e "\n\nPlease set TOMCAT8_HOME\n\n"
    exit 1
fi

./gradlew build

rm -rf $TOMCAT8_HOME/webapps/bitcoin-spring*

cp build/libs/bitcoin-spring.war $TOMCAT8_HOME/webapps/

$TOMCAT8_HOME/bin/startup.sh

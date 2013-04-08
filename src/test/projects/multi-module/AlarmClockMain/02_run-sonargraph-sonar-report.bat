set LICENSE=C:/Users/Ingmar/.hello2morrow/SonargraphArchitect/SonargraphArchitect.license
set SONARGRAPH_GOAL=com.hello2morrow.sonargraph:maven-sonargraph-plugin:7.1.8:architect-report 
rem mvn -e clean package 
mvn -e %SONARGRAPH_GOAL% -Dsonargraph.file=./AlarmClock-3-levels.sonargraph -Dsonargraph.license=%LICENSE% -Dsonargraph.useSonargraphWorkspace=true -Dsonargraph.prepareForSonar=true -Dsonargraph.verbose=true
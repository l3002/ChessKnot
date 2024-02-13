## ChessKnot

ChessKnot is a Desktop-based Chess Application with a in-built companion to play with.

It uses MiniMax Algorithm from the Game Theory to calculate best potential moves when the chess companion is activated.

Additionally, It also provides several features like Board Flip, 
Highlight Legal Moves etc.

![image](https://github.com/l3002/ChessKnot/assets/111552820/caf7e6af-b484-48b4-aa87-bb7f3f25b3a0)

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the src folder maintains source code
- `icon`: the icon folder maintains icons used in GUI.

>Note: The binaries have not been included in this repository. If you want to execute the program compile it first with the following command.
>
>`mvn clean compile assembly:single`

## Dependency Management

Apache Maven is used for Dependency Management in this project and all the dependencies have been added to the pom.xml file.

## Building & Executing

In order to build the project, clone the directory with the following command in cmd or linux/macOs shell:

`git clone https://github.com/l3002/ChessKnot.git`

After changing the directory to the project, run the following command:

`mvn clean package assembly:single`

The above command will build the project in the target directory.

In order to execute the application (jar file) generated in the target directory, execute the following command:

In Linux/MacOS shell:

`java -jar target/ChessKnot-1.0-SNAPSHOT-jar-with-dependencies.jar`

In Windows cmd:

`java -jar target\ChessKnot-1.0-SNAPSHOT-jar-with-dependencies.jar`

>Note: Please make sure that maven and git is installed in your local system prior to the building and executing process.
>
>Execute the jar file with the following suffix:
>
>`jar-with-dependencies`
>
>Executing the other jar file will result in an error.

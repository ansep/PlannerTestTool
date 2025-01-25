# PDDL generator for planning tasks in tourism

The Planner Test Tool is a Java-based application that generates PDDL (Planning Domain Definition Language) domain and problem files for planning tasks in the tourism domain. It provides a user-friendly interface for defining the planning task requirements and generates the necessary PDDL files.

## Requirements

- Java JDK 21
- Maven
- Docker
- NetBeans 20+

## Getting Started

1. Clone the repository.
2. Install Java JDK 21.
3. Install Maven.
4. Install Docker.
5. Build the project with Maven: `mvn clean package`.
6. Run the program using the UI or the command line.

## Usage

### Generation of domain and problem PDDL files using the UI

To run the program with the GUI, simply execute the following command if you are using maven:

```sh
mvn clean javafx:run
```

If you are using the compiled jar file, execute the following command:

```sh
java -jar target/PlannerTests-2.0.jar
```

From the UI customize the options, select the target folder for generated PDDL files and click on _Genera PDDL_.

### Generation of domain and problem PDDL files using the command line

To run the program from the command line, you can use the following options:

- `-i` or `--input`: Input museum.properties file
- `-o` or `--output`: Directory where to save the PDDL files (required)
- `-r` or `--rooms`: Number of rooms
- `-a` or `--attractions`: Number of attractions
- `-l` or `--links`: Number of links between rooms (must be between 1 and 3)
- `-v` or `--visits`: Number of attractions to visit (required)

You must specify either an input museum properties file or provide the number of rooms, attractions, and links.

#### Example Command

If you are using maven, you can run the program with the following command:

```sh
mvn clean compile exec:java -Dexec.args="-o output_directory -r 10 -a 50 -l 3 -v 20"
```

If you are using the compiled jar file, you can run the program with the following command:

```sh
java -jar target/PlannerTests-2.0.jar -o output_directory -r 10 -a 50 -l 5 -v 20
```

This command will generate PDDL files with the specified number of rooms, attractions, links, and visits, and save them in the specified output directory.

#### Using an Input File

If you have an input `museum.properties` file, you can specify it with the `-i` option:

```sh
mvn clean compile exec:java -Dexec.args="-i path/to/museum.properties -o output_directory -v visits"
```

or

```sh
java -jar target/PlannerTests-2.0.jar -i path/to/museum.properties -o output_directory -v visits
```

In this case, the program will use the information from the input file to generate the PDDL files.

### Output

The program will generate the following files in the specified output directory:

- `domain.pddl`: The PDDL domain file
- `problem.pddl`: The PDDL problem file
- `generation_info.json`: A JSON file containing information about the generation process, including the number of rooms, attractions, links, visits, and the time taken to generate the files.
- `museum.properties`: The topology of the museum

If you want to use custom domain and problem files you must rename them to _domain.pddl_ and _problem.pddl_ and place them in the same folder.

At the end of any algorithm, you will find the generated output file _plan.sas_ in the same folder.

### Running the planner with Fast Downward

The following docker command gets in input the domain and problem files and generates the file _plan.sas_ by using Fast Downward planner. The planner can be changed by using the `--alias` flag with the desired algorithm. The `--search-time-limit` flag sets the time limit for the search algorithm in seconds and is required for some algorithms, as shown in the table below.
Replace `<LOCAL_PDDL_FOLDER>` with the directory where the PDDL files are stored and the `<ALGORITHM>` with one from the list below.

`docker run --rm -v "<LOCAL_PDDL_FOLDER>:/pddl" aibasel/downward [--search-time-limit <TIME_LIMIT>] --plan-file /pddl/plan.sas --alias <ALGORITHM> /pddl/domain.pddl /pddl/problem.pddl`

Available algorithms for the --alias flag:

| Algorithm                | Description                                                    | Needs `--search-time-limit` flag |
| ------------------------ | -------------------------------------------------------------- | -------------------------------- |
| lama                     | Lazy A\*                                                       | No                               |
| lama-first               | Lazy A\* with first solution                                   | No                               |
| seq-opt-bjolp            | Sequential optimization with BJO-like pruning                  | No                               |
| seq-opt-fdss-1           | Sequential optimization with Fast Downward single search       | Yes                              |
| seq-opt-fdss-2           | Sequential optimization with Fast Downward single search       | Yes                              |
| seq-opt-lmcut            | Sequential optimization with landmark-cut heuristic            | No                               |
| seq-opt-merge-and-shrink | Sequential optimization with merge-and-shrink heuristic        | Yes                              |
| seq-sat-fd-autotune-1    | Sequential satisficing with Fast Downward autotuned search     | No                               |
| seq-sat-fd-autotune-2    | Sequential satisficing with Fast Downward autotuned search     | No                               |
| seq-sat-fdss-1           | Sequential satisficing with Fast Downward single search        | Yes                              |
| seq-sat-fdss-2           | Sequential satisficing with Fast Downward single search        | Yes                              |
| seq-sat-fdss-2014        | Sequential satisficing with Fast Downward single search (2014) | Yes                              |
| seq-sat-fdss-2018        | Sequential satisficing with Fast Downward single search (2018) | Yes                              |
| seq-sat-lama-2011        | Sequential satisficing with Lazy A\* (2011)                    | No                               |

### Running the planner with Downward A\* Blind algorithm

Use the following command to run the planner with the Downward A\* Blind algorithm. Replace `<LOCAL_PDDL_FOLDER>` with the directory where the PDDL files are stored.

`docker run --rm -v "<LOCAL_PDDL_FOLDER>:/pddl" aibasel/downward /pddl/domain.pddl /pddl/problem.pddl --search "astar(blind())"`

### Running the planner with SymBA\* 2 algorithm

Use the following command to run the planner with the SymBA\* 2 algorithm. Replace `<LOCAL_PDDL_FOLDER>` with the directory where the PDDL files are stored.

`docker run --rm -v "<LOCAL_PDDL_FOLDER>:/pddl" ansep/symba2-64bit`

### Running the planner with Complementary2 algorithm

Use the following command to run the planner with the Complementary2 algorithm. Replace `<LOCAL_PDDL_FOLDER>` with the directory where the PDDL files are stored.

`docker run --rm -v "<LOCAL_PDDL_FOLDER>:/pddl" ansep/complementary2`

### Running the planner with Ragnarok algorithm

Use the following command to run the planner with the Ragnarok algorithm. Replace `<LOCAL_PDDL_FOLDER>` with the directory where the PDDL files are stored.

`docker run --rm -v "<LOCAL_PDDL_FOLDER>:/pddl" ansep/ragnarok`

## License

This project is licensed under the [MIT License](LICENSE).

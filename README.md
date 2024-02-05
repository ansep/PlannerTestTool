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
5. Open the project in NetBeans.
6. Build and run the project.

## Usage

### Generation of domain and problem PDDL files

From the UI customize the options, select the target folder for generated PDDL files and click on _Genera PDDL_

### Running the planner

The following docker command gets in input the domain and problem files and generates the file _plan.sas_. Replace `<LOCAL_PDDL_FOLDER>` with the directory where the PDDL files are stored and the `<ALGORITHM>` with one from the list below.

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

## License

This project is licensed under the [MIT License](LICENSE).

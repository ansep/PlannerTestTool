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

`docker run --rm -v "<LOCAL_PDDL_FOLDER>:/pddl" aibasel/downward --plan-file /pddl/plan.sas --alias <ALGORITHM> /pddl/domain.pddl /pddl/problem.pddl`

Available algorithms for the --alias flag:

    lama
    lama-first
    seq-opt-bjolp
    seq-opt-fdss-1
    seq-opt-fdss-2
    seq-opt-lmcut
    seq-opt-merge-and-shrink
    seq-sat-fd-autotune-1
    seq-sat-fd-autotune-2
    seq-sat-fdss-1
    seq-sat-fdss-2
    seq-sat-fdss-2014
    seq-sat-fdss-2018
    seq-sat-lama-2011

## License

This project is licensed under the [MIT License](LICENSE).

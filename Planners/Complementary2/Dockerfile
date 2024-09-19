FROM ubuntu:16.04 AS builder
RUN apt-get update && apt-get install -y git python cmake gcc-multilib g++-multilib
RUN git clone https://bitbucket.org/ipc2018-classical/team32.git
WORKDIR /team32
RUN ./build.py

FROM builder
VOLUME /pddl
COPY --from=builder /team32 /team32
WORKDIR /team32
ENTRYPOINT ["./fast-downward.py", "--plan-file", "/pddl/plan.sas", "/pddl/domain.pddl", "/pddl/problem.pddl", "--search", "astar(blind())"]
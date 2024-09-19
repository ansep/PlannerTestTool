FROM ubuntu:latest as builder
RUN apt-get update && apt-get install -y build-essential git
RUN git clone https://gitlab.com/atorralba_planners/symba-2014.git
RUN cd symba-2014 && git switch symba-2-64bits && ./build

FROM builder
VOLUME /pddl
RUN apt-get update && apt-get install -y python2.7 time gawk
COPY --from=builder /symba-2014 /symba-2014
WORKDIR /symba-2014
ENTRYPOINT [ "./plan", "/pddl/domain.pddl", "/pddl/problem.pddl" , "/pddl/plan.sas"]
FROM ubuntu:22.04 AS builder
RUN apt-get update && apt-get install -y --no-install-recommends \
    git cmake g++ make pypy3 autoconf automake default-jre-headless zlib1g-dev \
    autotools-dev bison ca-certificates flex gdb libboost-dev libboost-program-options-dev \
    && rm -rf /var/lib/apt/lists/*
RUN git clone https://github.com/ipc2023-classical/planner17.git /planner17
WORKDIR /planner17
COPY cplex_studio2211.linux_x86_64.bin /cplex.bin
RUN chmod +x /cplex.bin && \
    /cplex.bin -DLICENSE_ACCEPTED=TRUE -i silent
ENV DOWNWARD_CPLEX_ROOT=/opt/ibm/ILOG/CPLEX_Studio2211/cplex/
ENV DOWNWARD_CPLEX_CONCERT64=/opt/ibm/ILOG/CPLEX_Studio2211/concert/
RUN rm -rf */builds
WORKDIR /planner17/planners/decstar/third-party/bliss-0.73
RUN make clean && make && make lib
ENV PATH="/planner17/planners/decstar/third-party/bliss-0.73:$PATH"
WORKDIR /planner17/planners
RUN for planner in decstar scorpion symk powerlifted; do \
    cd ${planner} && pypy3 build.py && cd ..; \
    done
RUN strip --strip-all decstar/builds/release64/bin/downward \
    scorpion/builds/release/bin/downward scorpion/builds/release/bin/preprocess-h2 \
    symk/builds/release/bin/downward \
    powerlifted/builds/release/search/search

FROM ubuntu:22.04
COPY --from=builder /planner17/plan.py /plan.py
COPY --from=builder /planner17/driver /driver
COPY --from=builder /planner17/planners/decstar/builds/release64/bin/downward /planners/decstar/builds/release/bin/downward
COPY --from=builder /planner17/planners/decstar/third-party/bliss-0.73/ /planners/decstar/third-party/bliss-0.73/
COPY --from=builder /planner17/planners/scorpion/driver /planners/scorpion/
COPY --from=builder /planner17/planners/scorpion/fast-downward.py /planners/scorpion/
COPY --from=builder /planner17/planners/scorpion/builds/release/bin /planners/scorpion/builds/release/bin
COPY --from=builder /planner17/planners/scorpion/src/translate /planners/scorpion/src/translate
COPY --from=builder /planner17/planners/symk/builds/release/bin/downward /planners/symk/builds/release/bin/
COPY --from=builder /planner17/planners/powerlifted/driver /planners/powerlifted/
COPY --from=builder /planner17/planners/powerlifted/powerlifted.py /planners/powerlifted/
COPY --from=builder /planner17/planners/powerlifted/builds/release/search /planners/powerlifted/builds/release/search
COPY --from=builder /planner17/planners/powerlifted/builds/release/translator /planners/powerlifted/builds/release/translator
COPY --from=builder /opt/ibm/ILOG/CPLEX_Studio2211/cplex/ /opt/ibm/ILOG/CPLEX_Studio2211/cplex/
COPY --from=builder /opt/ibm/ILOG/CPLEX_Studio2211/concert/ /opt/ibm/ILOG/CPLEX_Studio2211/concert/
RUN apt-get update && apt-get install -y --no-install-recommends \
    pypy3 libboost-dev libboost-program-options-dev \
    && rm -rf /var/lib/apt/lists/*
RUN echo '#!/bin/bash\n\
    pypy3 /plan.py \
    --overall-memory-limit 6G \
    --overall-time-limit 30m \
    --translate-time-limit 15m \
    --transform-task /planners/scorpion/builds/release/bin/preprocess-h2 \
    --transform-task-options h2_time_limit,180 \
    --alias seq-opt-ragnarok \
    --plan-file "$3" \
    "$1" "$2"' > /plan && chmod +x /plan
ENTRYPOINT ["./plan", "/pddl/domain.pddl", "/pddl/problem.pddl", "/pddl/plan.sas"]
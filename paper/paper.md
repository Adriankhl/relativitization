---
title: 'Relativitization: an agent-based social simulation framework in 4D, relativistics spacetime'
tags:
  - Kotlin
  - agent-based simulation
  - interstellar society
  - special relativity
authors:
  - name: Lai Kwun Hang
    orcid: 0000-0003-0446-119X
    affiliation: 1
affiliations:
 - name: Centre for Science and Technology Studies, Leiden University, The Netherlands
   index: 1
date: 11 May 2023
bibliography: paper.bib

---

# Summary

`Relativitization` is a Kotlin project which provides a simulation framework for
agent-based modeling in 4D, relativistics spacetime.
Agent-based simulation is a popular technique in social science to explore
social phenomena in hypothetical societies.
We are interested in one particular type of hypothetical societies -
interstellar societies.
Because of the interstellar scale,
the simulated world should obey the physical laws imposed by special relativity.
`Relativitization`, or more precisely, the `universe-core` subproject,
provides a set of building blocks and enforces a specific way to implement a model,
such that some of the physical constraints are automatically satisfied.
Our framework makes it easier for people to build proper interstellar social model.

The source code is available at [https://github.com/Adriankhl/relativitization](https://github.com/Adriankhl/relativitization),
this repository contains a few subprojects of a turn-based strategy game built on top of the framework,
and only the `universe-core` subproject is relevant for this paper.
The documentation about the framework is provided at
[https://github.com/Adriankhl/relativitization-framework-doc](https://github.com/Adriankhl/relativitization-framework-doc).

# Statement of need

There are quite a few existing agent-based simulation frameworks,
e.g., NetLogo [@netlogo], Repast [@north2013complex], MESA [@masad2015mesa], and Agents.jl [@Agents.jl].
While these are useful libraries,
they are built for normal spacetime.
On an interstellar scale,
we should consider the time delay and time dilation effects of Minkowski spacetime:

* Time delay: information travels at the speed of light $c$, and everything should not travel faster than $c$,
* Time dilation: the clock of an moving object ticks slower, with a factor $\sqrt{1 - v^2 / c^2}$, 
  where $v$ is the moving speed of the object.

The framework 


# Algorithms
Test [@lai2022social]

![Test figure](./simulation-flow.svg)

# Projects

# Reference

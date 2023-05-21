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

There are a few existing agent-based simulation frameworks,
e.g., NetLogo [@netlogo], Repast [@north2013complex], MESA [@masad2015mesa], and Agents.jl [@Agents.jl].
While these are useful libraries,
they are built for normal spacetime.
On an interstellar scale,
we should consider the time delay and time dilation effects of Minkowski spacetime:

* Time delay: information travels at the speed of light $c$, and everything should not travel faster than $c$,
* Time dilation: the clock of an moving object ticks slower, with a factor of $\sqrt{1 - v^2 / c^2}$, 
  where $v$ is the moving speed of the object.

\autoref{fig:flow} illustrates how a model is simulated in our framework.
By limiting what an agent can see (view) and
how an agent can interact with other agents (command),
the time delay constraint is automatically satisfied.
By separating regular mechanisms, 
which are processed once per turn, 
and dilated mechanisms,
which are affected by time dilation and processed every one or more turns,
it is trivial to implement time dilation properly with our framework.
The mathematical and algorithmic details are described in [@lai2022social].

![Simulation framework overview\label{fig:flow}](./simulation-flow.svg)

Because of the time delay,
agents need to access the past states of other agents.
To prevent unintended modifications of the data,
we rely on a small library [KSerGen](https://github.com/Adriankhl/ksergen)
we developed to generate immutable copies of data classes.
Having an immutable version of the data also allows us
to easily paralellize a simulation.
Our framework supports parallel simulation painlessly,
no additional modification to the code is needed.
Since reproducibility is important in any simulation research,
the simulated result can be deterministic by setting a random seed,
even if the simulation is ran parallelly.

In summary, `Relativitization` is a flexible, performant agent-based simulation framework
which helps people to implement physically and computationally correct interstellar social models.

# Projects

Here are some of our projects based on the framework.

## Interstellar flocks

Classic flocking model, but the agents are spaceship propelled by photon rocket,
source code availalble at:
[https://github.com/Adriankhl/relativitization-model-flocking](https://github.com/Adriankhl/relativitization-model-flocking).

## Interstellar knowledge dynammics

Explore how knowledge is created and diffused in an interstellar society,
source code available at:
[https://github.com/Adriankhl/relativitization-model-knowledge-dynamics](https://github.com/Adriankhl/relativitization-model-knowledge-dynamics).

## Turn based strategy game

The rest of the 
[Relativitization repository](https://github.com/Adriankhl/relativitization),
consists of a turn-based strategy game (which is also a complex social model) 
with server-client architecture and graphical interface.


# Reference

package xyz.penpencil.competishun.data.model

// Model for State and City
data class StateCity(
    val states: List<State>
)

data class State(
    val name: String,
    val cities: List<String>
)



package ca.vgorcinschi.qandauser.impl.states

sealed trait UserState

case object LoggedInState extends UserState
case object LoggedOutState extends UserState
command is the data from client to server
message is the data from server to client
event generate by command or other event
communication of server component interact by event

each process of service has 2 type eventbus
1.global eventbus for other service
1.inner eventbus (e.g. gate eventbus) for itself(gate)

command and message just for gate
event for all component of server
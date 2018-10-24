# Enfuser NGSI

NGSI v2 compatible server for serving Enfuser air quality data.

Implementation is based on FIWARE-NGSI v2 Specification: https://orioncontextbroker.docs.apiary.io/

Limitations
===========

Server does not fully implement whole specification. Implementation does not support data modification, types, notifications, subscriptions, registrations or batch operations.

There are also number of limitations in specific methods:

List entities -endpoint 
-----------------------

Notes on the endpoint:

- Does not support following parameters: q, mq, metadata. 
- Only geo:distance orderBy is currently  supported


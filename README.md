# Enfuser NGSI

NGSI v2 compatible server for serving Enfuser air quality data.

Implementation is based on FIWARE-NGSI v2 Specification: https://orioncontextbroker.docs.apiary.io/

Limitations
===========

Server does not fully implement whole specification. Implementation does not support data modification, subscriptions or registrations.

There are also number of limitations in specific methods:

List entities -endpoint 
-----------------------
Does not support following parameters: q, mq, orderBy, metadata or options -parameters.


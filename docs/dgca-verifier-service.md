# European Digital Green Certificate Applications 
##DGCA-Verifier-Service

### Intention
The DGCA-Verifier-Service provides a template implementation for a member state backend service for a verifier application.

### General Overview
A general overview of how the different member state backends work together, can be seen in the following picture.

![DGCA overview](dgca_overview.png "DGCA Overview")

As you can see in the picture, each member state backend provides the services for it's own applications (e.g. verifier, issuer and wallet).
The member state synchronises the validation certificates over the [DGCGateway](https://https://github.com/eu-digital-green-certificates/dgc-gateway).

### Purpose and functionality of the DGCA-Verifier-Service
The verifier service basically caches the public keys that are distributed through the [DGCG](https://https://github.com/eu-digital-green-certificates/dgc-gateway) to the member states backends. 
The service provides the Trust List of certificates for the verifier apps. The apps can get the list to update their key store via an api.
To have an actual trust list the verifier service periodically polls the [DGCG](https://https://github.com/eu-digital-green-certificates/dgc-gateway) 
for the actual trust list. It therefore uses the functionality provided by the [DGC-lib](https://github.com/eu-digital-green-certificates/dgc-lib). 
It stores the received keys in a postgres db and removes revoked keys from it. The keys can than be requested by the verifier apps using the api described in the next section.



### API documentation

The api is described with [OpenApi v3](https://swagger.io). You can access the API documentation in your web browser, when you run the service :

    <base-url>/swagger

Which results in the following URL on your local machine:
http://localhost:8080/swagger

Or by downloading the OpenApi file in json format from the latest release:  
* [openapi.json](https://github.com/eu-digital-green-certificates/dgca-verifier-service/releases/latest/download/openapi.json)

You can than put the file in the openapi viewer of your choice. ([editor.swagger.io](https://editor.swagger.io) for example)


### Further Information
Further information can be found at [ec.europa.eu/health](https://ec.europa.eu/health/ehealth/covid-19_en)  
Especially at [Volume 4: Digital Green Certificate Applications](https://ec.europa.eu/health/sites/default/files/ehealth/docs/digital-green-certificates_v4_en.pdf)  
And the github repository of the [DGCG](https://https://github.com/eu-digital-green-certificates/dgc-gateway)

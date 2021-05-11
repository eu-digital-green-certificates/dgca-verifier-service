<h1 align="center">
    EU Digital Green Certificates Verifier Service
</h1>

<p align="center">
    <a href="/../../commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/eu-digital-green-certificates/dgca-verifier-service?style=flat"></a>
    <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/eu-digital-green-certificates/dgca-verifier-service?style=flat"></a>
    <a href="./LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat"></a>
</p>

<p align="center">
  <a href="#about">About</a> •
  <a href="#development">Development</a> •
  <a href="#documentation">Documentation</a> •
  <a href="#support-and-feedback">Support</a> •
  <a href="#how-to-contribute">Contribute</a> •
  <a href="#contributors">Contributors</a> •
  <a href="#licensing">Licensing</a>
</p>

## About

This repository contains the source code of the Digital Green Certificates Verifier Service.

The DGC Verifier Service is part of the national backends and caches the public keys that are distributed through the [DGCG](https://github.com/eu-digital-green-certificates/dgc-gateway). It is accessed by the DGC Verifier Apps ([Android](https://github.com/eu-digital-green-certificates/dgca-verifier-app-android), [iOS](https://github.com/eu-digital-green-certificates/dgca-verifier-app-ios)) to update the key store periodically.

## Development

### Prerequisites

- [Open JDK 11](https://openjdk.java.net)
- [Maven](https://maven.apache.org)
- [Docker](https://www.docker.com)
- An installation of the [DGCG](https://https://github.com/eu-digital-green-certificates/dgc-gateway)
- Keys to access the [DGCG](https://https://github.com/eu-digital-green-certificates/dgc-gateway) via the
  [DGCG Connector](https://github.com/eu-digital-green-certificates/dgc-lib) of the [DGC-lib](https://github.com/eu-digital-green-certificates/dgc-lib)
- Authenticate to [Github Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

#### Authenticating in to GitHub Packages

As some of the required libraries (and/or versions are pinned/available only from GitHub Packages) You need to authenticate
to [GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)
The following steps need to be followed

- Create [PAT](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token) with scopes:
  - `read:packages` for downloading packages
- Copy/Augment `~/.m2/settings.xml` with the contents of `settings.xml` present in this repository
  - Replace `${app.packages.username}` with your github username
  - Replace `${app.packages.password}` with the generated PAT
  
**Note:** The verifier service needs the connection to the gateway in order to run. There is no standalone version available.

For further information about the keys and certificates needed, please refer to the documentation of the 
[DGCG](https://https://github.com/eu-digital-green-certificates/dgc-gateway) and the 
[DGC-lib](https://github.com/eu-digital-green-certificates/dgc-lib)
  
### Build

Whether you cloned or downloaded the 'zipped' sources you will either find the sources in the chosen checkout-directory or get a zip file with the source code, which you can expand to a folder of your choice.

In either case open a terminal pointing to the directory you put the sources in. The local build process is described afterwards depending on the way you choose.


#### Build with maven
Building this project is done with maven.  

* Check [settings.xml](settings.xml) in the root folder of this git repository as example.  
  Copy the servers to your own `~/.m2/settings.xml` in order to connect the GitHub repositories we use in our code. Provide your GitHub username and access token (see [GitHub Help](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token)) under the variables suggested.

* Run the following command from the project root folder
```shell
mvn clean install
```
All required dependencies will be downloaded, the project build and the artifact stored in your local repository.
#### Run with docker
* Perform maven build as described above
* Place the keys and certificates named above into the ***certs*** folder.
* Adjust the values in the [docker-compose.yml](docker-compose.yml) file to fit the url for the gateway you use and 
  your keys and certificates you have to access it.
  ```yaml
  - DGC_GATEWAY_CONNECTOR_ENDPOINT=https://dgc-gateway.example.com
  - DGC_GATEWAY_CONNECTOR_TLSTRUSTSTORE_PATH=file:/ec/prod/app/san/dgc/tls_trust_store.p12
  - DGC_GATEWAY_CONNECTOR_TLSTRUSTSTORE_PASSWORD=dgcg-p4ssw0rd
  - DGC_GATEWAY_CONNECTOR_TLSKEYSTORE_ALIAS=1
  - DGC_GATEWAY_CONNECTOR_TLSKEYSTORE_PATH=file:/ec/prod/app/san/dgc/tls_key_store.p12
  - DGC_GATEWAY_CONNECTOR_TLSKEYSTORE_PASSWORD=dgcg-p4ssw0rd
  - DGC_GATEWAY_CONNECTOR_TRUSTANCHOR_ALIAS=ta
  - DGC_GATEWAY_CONNECTOR_TRUSTANCHOR_PATH=file:/ec/prod/app/san/dgc/trust_anchor.jks
  - DGC_GATEWAY_CONNECTOR_TRUSTANCHOR_PASSWORD=dgcg-p4ssw0rd
  ```
***Note:*** Leave the path as is and only change the file names, as the ***certs*** folder will be mapped to this folder inside the docker container.

* Run the following command from the project root folder

```shell
docker-compose up --build
```

After all containers have started, you will be able to reach the service on your [local machine](http://localhost:8080/api/docs) under port 8080.

## Documentation  

[OpenAPI Spec](https://eu-digital-green-certificates.github.io/dgca-verifier-service/)
 
[Service description](./docs/dgca-verifier-service.md)


## Support and feedback

The following channels are available for discussions, feedback, and support requests:

| Type                     | Channel                                                |
| ------------------------ | ------------------------------------------------------ |
| **Issues**    | <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/eu-digital-green-certificates/dgca-verifier-service?style=flat"></a>  |
| **Other requests**    | <a href="mailto:opensource@telekom.de" title="Email DGC Team"><img src="https://img.shields.io/badge/email-DGC%20team-green?logo=mail.ru&style=flat-square&logoColor=white"></a>   |

## How to contribute  

Contribution and feedback is encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](./CONTRIBUTING.md). By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Contributors  

Our commitment to open source means that we are enabling -in fact encouraging- all interested parties to contribute and become part of its developer community.

## Licensing

Copyright (C) 2021 T-Systems International GmbH and all other contributors

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.

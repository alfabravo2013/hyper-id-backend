# HyperID

## Hyperskill full stack project, backend REST API

[![Build Executables](https://github.com/alfabravo2013/hyper-id-backend/actions/workflows/native-artifact.yml/badge.svg?branch=master&event=workflow_dispatch)](https://github.com/alfabravo2013/hyper-id-backend/actions/workflows/native-artifact.yml)
[![Tests](https://github.com/alfabravo2013/hyper-id-backend/actions/workflows/tests.yml/badge.svg)](https://github.com/alfabravo2013/hyper-id-backend/actions/workflows/tests.yml)

### How to run

#### Option 1. Download an executable for your OS:

- [Windows_x86_64](https://github.com/alfabravo2013/hyper-id-backend/releases/download/v0.1/hyperid-Windows-snapshot-x86_64.zip)
- [Linux_x86_64](https://github.com/alfabravo2013/hyper-id-backend/releases/download/v0.1/hyperid-Linux-snapshot-x86_64.zip)
- [MacOS_x86_64](https://github.com/alfabravo2013/hyper-id-backend/releases/download/v0.1/hyperid-macOS-snapshot-x86_64.zip)

and run on your computer.

#### Option 2. Download the executable jar:

- [hyper-id-backend.jar](https://github.com/alfabravo2013/hyper-id-backend/releases/download/v0.1/hyper-id-backend.jar)

install JDK 17 and execute the following command in the directory where the jar file is located:

```bash
java -jar hyper-id-backend.jar
```

#### Option 3. Download the source code:

- [.zip](https://github.com/alfabravo2013/hyper-id-backend/archive/refs/tags/v0.1.zip) or 
- [.tar.gz](https://github.com/alfabravo2013/hyper-id-backend/archive/refs/tags/v0.1.tar.gz)

unzip it, install JDK 17 and execute the following command in the project root directory:

```bash
./gradlew bootRun
```

The server responds on `localhost:8080`

The landing page redirects to API docs (Swagger)
